package com.sbvia.backend.service;

import com.sbvia.backend.dto.DrivingMetricsRequest;
import com.sbvia.backend.dto.DrivingResultDTO;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.dto.SimulationDTO;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.SimulationState;
import com.sbvia.backend.entity.Infraction;
import com.sbvia.backend.entity.PerformanceMetric;
import com.sbvia.backend.entity.SeverityLevel;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.entity.TrainingSession;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.MetricType;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.entity.Vehicle;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.ScenarioRepository;
import com.sbvia.backend.repository.SimulationStateRepository;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import com.sbvia.backend.repository.SeverityLevelRepository;
import com.sbvia.backend.repository.TrafficRuleRepository;
import com.sbvia.backend.repository.TrainingSessionRepository;
import com.sbvia.backend.repository.SimulationRepository;
import com.sbvia.backend.repository.MetricTypeRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.stream.Collectors;

/**
 * Business logic for running simulations and scoring a practice.
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SimulationService {

    /**
     * Rule codes from the catalogue that map exactly to the 2D simulator.
     * The penalties for these two types are read from `regla_transito.penalizacion_base`
     * (single source); collision, lane departure and distance use constants because the catalogue
     * has no equivalent rules yet (proposal: RT-006 to RT-008 in a future migration).
     */
    public static final String REGLA_EXCESO = "RT-002";
    /** Constant <code>REGLA_SEMAFORO="RT-001"</code> */
    public static final String REGLA_SEMAFORO = "RT-001";

    /** Fixed per-episode deductions for types with no catalogue rule. */
    public static final BigDecimal PENAL_COLISION = new BigDecimal("20");
    /** Constant <code>PENAL_SALIDA</code> */
    public static final BigDecimal PENAL_SALIDA = new BigDecimal("10");
    /** Constant <code>PENAL_DISTANCIA</code> */
    public static final BigDecimal PENAL_DISTANCIA = new BigDecimal("8");

    private final SimulationRepository simulacionRepository;
    private final UserRepository usuarioRepository;
    private final ScenarioRepository escenarioRepository;
    private final PerformanceMetricRepository metricaDesempenoRepository;
    private final InfractionRepository infraccionRepository;
    private final SimulationStateRepository estadoSimulacionRepository;
    private final MetricTypeRepository tipoMetricaRepository;
    private final TrafficRuleRepository reglaTransitoRepository;
    private final SeverityLevelRepository nivelGravedadRepository;
    private final TrainingSessionRepository sesionEntrenamientoRepository;
    private final VehicleRepository vehiculoRepository;
    private final FeedbackService retroalimentacionService;

    /**
     * Starts a new simulation for the given user and active scenario, creating an open
     * training session and assigning the first available active vehicle.
     *
     * @param email the email of the user starting the simulation
     * @param scenarioId the id of the active scenario to practice
     * @return the DTO of the created simulation in progress
     */
    public SimulationDTO startSimulation(String email, Integer scenarioId) {
        User user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado"));
        Scenario scenario = escenarioRepository.findById(scenarioId)
                .filter(Scenario::isActivo)
                .orElseThrow(() -> new ResourceNotFoundException("Scenario activo no encontrado"));
        SimulationState enProgreso = estadoSimulacionRepository.findByName("EN_PROGRESO")
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta el estado EN_PROGRESO"));
        Vehicle vehicle = vehiculoRepository.findFirstByActivoTrueOrderByIdVehiculoAsc()
                .orElseThrow(() -> new IllegalStateException("No existe un vehículo activo para iniciar la simulación"));

        // The trg_validar_usuario_sesion trigger requires a valid session whose
        // user matches the one of the simulation.
        TrainingSession sesion = sesionEntrenamientoRepository.save(TrainingSession.builder()
                .user(user)
                .estado("ABIERTA")
                .objetivo("Práctica de conducción")
                .build());

        Simulation simulation = Simulation.builder()
                .startDate(LocalDate.now())
                .finalScore(BigDecimal.ZERO)
                .user(user)
                .scenario(scenario)
                .vehicle(vehicle)
                .simulationState(enProgreso)
                .trainingSession(sesion)
                .build();
        return mapToDTO(simulacionRepository.save(simulation));
    }

    /**
     * Finalizes an owned, not yet completed simulation by setting its end date and final
     * score and marking it as completed.
     *
     * @param email the email of the user who owns the simulation
     * @param simulationId the id of the simulation to finish
     * @param finalScore the final score to record for the simulation
     * @return the DTO of the finalized simulation
     */
    public SimulationDTO finishSimulation(String email, Integer simulationId, BigDecimal finalScore) {
        Simulation simulation = simulacionRepository.findById(simulationId)
                .orElseThrow(() -> new ResourceNotFoundException("Simulación no encontrada"));
        if (!simulation.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("La simulación pertenece a otro user");
        }
        if (simulation.isCompleted()) {
            throw new IllegalArgumentException("La simulación ya fue finalizada");
        }

        simulation.setEndDate(LocalDate.now());
        simulation.setFinalScore(finalScore);
        simulation.setCompleted(true);
        return mapToDTO(simulacionRepository.save(simulation));
    }

    /**
     * Finishes a 2D simulator run with the metrics reported by the frontend.
     * The score is computed on the server (the client never imposes it) and the metrics
     * are persisted in `metrica_desempeno` and `infraction` within the same transaction.
     *
     * @param email a {@link java.lang.String} object
     * @param simulationId a {@link java.lang.Integer} object
     * @param metricas a {@link com.sbvia.backend.dto.DrivingMetricsRequest} object
     * @return a {@link com.sbvia.backend.dto.DrivingResultDTO} object
     */
    public DrivingResultDTO finishDriving(String email, Integer simulationId, DrivingMetricsRequest metricas) {
        Simulation simulation = simulacionRepository.findById(simulationId)
                .orElseThrow(() -> new ResourceNotFoundException("Simulación no encontrada"));
        if (!simulation.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("La simulación pertenece a otro user");
        }
        if (simulation.isCompleted()) {
            throw new IllegalArgumentException("La simulación ya fue finalizada");
        }
        if (metricas.velocidadMaxima().compareTo(metricas.velocidadPromedio()) < 0) {
            throw new IllegalArgumentException("La velocidad máxima no puede ser menor que la promedio");
        }

        TrafficRule reglaExceso = reglaTransitoRepository.findByCodigo(REGLA_EXCESO)
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta la regla " + REGLA_EXCESO));
        TrafficRule reglaSemaforo = reglaTransitoRepository.findByCodigo(REGLA_SEMAFORO)
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta la regla " + REGLA_SEMAFORO));
        SeverityLevel moderada = nivelGravedadRepository.findByName("MODERADA")
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta el nivel MODERADA"));
        SeverityLevel grave = nivelGravedadRepository.findByName("GRAVE")
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta el nivel GRAVE"));
        SimulationState completed = estadoSimulacionRepository.findByName("COMPLETADA")
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta el estado COMPLETADA"));

        int totalInfracciones = metricas.excesosVelocidad() + metricas.colisiones()
                + metricas.salidasCarril() + metricas.semaforosIgnorados() + metricas.distanciaInsegura();

        BigDecimal descuento = reglaExceso.getPenalizacionBase().multiply(BigDecimal.valueOf(metricas.excesosVelocidad()))
                .add(reglaSemaforo.getPenalizacionBase().multiply(BigDecimal.valueOf(metricas.semaforosIgnorados())))
                .add(PENAL_COLISION.multiply(BigDecimal.valueOf(metricas.colisiones())))
                .add(PENAL_SALIDA.multiply(BigDecimal.valueOf(metricas.salidasCarril())))
                .add(PENAL_DISTANCIA.multiply(BigDecimal.valueOf(metricas.distanciaInsegura())));
        BigDecimal puntaje = BigDecimal.valueOf(100).subtract(descuento).max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal cumplimiento = BigDecimal.valueOf(100 - Math.min(100, totalInfracciones * 10));

        saveMetric(simulation, "VELOCIDAD_PROMEDIO", metricas.velocidadPromedio(), "Promedio del simulador 2D");
        saveMetric(simulation, "TOTAL_INFRACCIONES", BigDecimal.valueOf(totalInfracciones), "Conteo del simulador 2D");
        saveMetric(simulation, "PUNTAJE_SEGURIDAD", puntaje, "Calculado en el servidor");
        saveMetric(simulation, "PORCENTAJE_CUMPLIMIENTO", cumplimiento, "Calculado en el servidor");

        if (metricas.excesosVelocidad() > 0) {
            saveInfraction(simulation, null, reglaExceso, moderada,
                    metricas.excesosVelocidad() + " exceso(s) de velocidad en el simulador 2D",
                    reglaExceso.getPenalizacionBase().multiply(BigDecimal.valueOf(metricas.excesosVelocidad())));
        }
        if (metricas.semaforosIgnorados() > 0) {
            saveInfraction(simulation, null, reglaSemaforo, grave,
                    metricas.semaforosIgnorados() + " semáforo(s) en rojo ignorado(s) en el simulador 2D",
                    reglaSemaforo.getPenalizacionBase().multiply(BigDecimal.valueOf(metricas.semaforosIgnorados())));
        }

        // This save happens AFTER the infractions on purpose: the
        // trg_recalcular_puntaje_infraccion recomputes puntaje_final with a formula
        // that is partial (it only adds penalizacion_aplicada over the persisted rows, and the
        // catalogue has no rules for collision, departure or distance yet). The value
        // computed by the server (5 types) is the source of truth and must be written
        // last. Proposal: rules RT-006 to RT-008, plus persisting all 5 types.
        simulation.setEndDate(LocalDate.now());
        simulation.setFinalScore(puntaje);
        simulation.setDurationSeconds(metricas.durationSeconds());
        simulation.setCompleted(true);
        simulation.setSimulationState(completed);
        simulation.setObservations("{\"origen\":\"SIMULADOR_2D\",\"velocidadMaxima\":"
                + metricas.velocidadMaxima() + ",\"durationSeconds\":" + metricas.durationSeconds()
                + ",\"excesos\":" + metricas.excesosVelocidad() + ",\"colisiones\":" + metricas.colisiones()
                + ",\"salidas\":" + metricas.salidasCarril() + ",\"semaforos\":" + metricas.semaforosIgnorados()
                + ",\"distancia\":" + metricas.distanciaInsegura()
                + ",\"respetados\":" + metricas.respetados() + "}");
        Simulation simulacionFinalizada = simulacionRepository.save(simulation);

        FeedbackIaResponse informe =
                retroalimentacionService.generateAndSave(email, simulacionFinalizada.getSimulationId());

        return DrivingResultDTO.builder()
                .simulation(mapToDTO(simulation))
                .feedback(informe)
                .build();
    }

    /**
     * Returns the simulations of the given user ordered by id descending.
     *
     * @param email the email of the user whose simulations are requested
     * @return the list of the user's simulation DTOs, newest first
     */
    public List<SimulationDTO> getMyPractices(String email) {
        User user = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));

        List<Simulation> simulations = simulacionRepository
                .findByUser_UserIdOrderBySimulationIdDesc(user.getUserId());

        return simulations.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Returns all simulations ordered by id descending.
     *
     * @return the list of all simulation DTOs, newest first
     */
    public List<SimulationDTO> getAll() {
        return simulacionRepository.findAllByOrderBySimulationIdDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * <p>getGlobalStatistics.</p>
     *
     * @return a {@link com.sbvia.backend.dto.StatisticsDTO} object
     */
    public com.sbvia.backend.dto.StatisticsDTO getGlobalStatistics() {
        Object[] result = simulacionRepository.getGlobalStats();
        if (result == null || result[0] == null || result.length == 0 || ((Object[]) result[0])[0] == null) {
            return new com.sbvia.backend.dto.StatisticsDTO(0, 0, 0);
        }
        Object[] row = (Object[]) result[0];
        long total = row[0] != null ? ((Number) row[0]).longValue() : 0;
        int promedio = row[1] != null ? (int) Math.round(((Number) row[1]).doubleValue()) : 0;
        long aprobadas = row[2] != null ? ((Number) row[2]).longValue() : 0;
        
        int tasaAprobacion = total > 0 ? (int) Math.round((double) aprobadas * 100 / total) : 0;

        return com.sbvia.backend.dto.StatisticsDTO.builder()
                .totalPracticas(total)
                .promedioGlobal(promedio)
                .tasaAprobacionGlobal(tasaAprobacion)
                .build();
    }

    private void saveMetric(Simulation simulation, String tipo, BigDecimal value, String observacion) {
        MetricType metricType = tipoMetricaRepository.findByName(tipo)
                .orElseThrow(() -> new IllegalStateException("Catálogo incompleto: falta el tipo " + tipo));
        metricaDesempenoRepository.save(PerformanceMetric.builder()
                .value(value)
                .observacion(observacion)
                .simulation(simulation)
                .metricType(metricType)
                .build());
    }

    private void saveInfraction(Simulation simulation, com.sbvia.backend.entity.Decision decision,
            TrafficRule regla, SeverityLevel gravedad, String description, BigDecimal penalizacion) {
        infraccionRepository.save(Infraction.builder()
                .description(description)
                .penalizacionAplicada(penalizacion)
                .simulation(simulation)
                .decision(decision)
                .trafficRule(regla)
                .severityLevel(gravedad)
                .build());
    }

    private SimulationDTO mapToDTO(Simulation simulation) {
        return SimulationDTO.builder()
                .simulationId(simulation.getSimulationId())
                .startDate(simulation.getStartDate())
                .endDate(simulation.getEndDate())
                .finalScore(simulation.getFinalScore())
                .completed(simulation.isCompleted())
                .scenarioId(simulation.getScenario() != null ? simulation.getScenario().getScenarioId() : null)
                .scenarioName(simulation.getScenario() != null ? simulation.getScenario().getName() : "N/A")
                .userId(simulation.getUser() != null ? simulation.getUser().getUserId() : null)
                .username(simulation.getUser() != null
                        ? simulation.getUser().getFirstName() + " " + simulation.getUser().getLastName()
                        : "N/A")
                .userEmail(simulation.getUser() != null ? simulation.getUser().getEmail() : null)
                .build();
    }
}
