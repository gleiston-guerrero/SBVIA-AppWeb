package com.sbvia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.entity.Infraction;
import com.sbvia.backend.entity.PerformanceMetric;
import com.sbvia.backend.entity.Feedback;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.exception.ResourceNotFoundException;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import com.sbvia.backend.repository.FeedbackRepository;
import com.sbvia.backend.repository.SimulationRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.service.feedback.DatosConduccion;
import com.sbvia.backend.service.feedback.IaNoDisponibleException;
import com.sbvia.backend.service.feedback.FeedbackIaExternaService;
import com.sbvia.backend.service.feedback.FeedbackLocalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Orquesta la retroalimentación de una conducción: reconstruye las métricas
 * desde la BD, calcula el historial del conductor, intenta el proveedor externo
 * de IA y usa el motor local de reglas como respaldo garantizado.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FeedbackService {

    private final SimulationRepository simulacionRepository;
    private final UserRepository usuarioRepository;
    private final PerformanceMetricRepository metricaDesempenoRepository;
    private final InfractionRepository infraccionRepository;
    private final FeedbackRepository retroalimentacionRepository;
    private final FeedbackLocalService motorLocal;
    private final FeedbackIaExternaService proveedorExterno;
    private final ObjectMapper objectMapper;

    public FeedbackIaResponse generarInforme(String email, Integer simulationId) {
        DatosConduccion datos = construirDatos(email, simulationId);
        return generarConRespaldo(datos);
    }

    public FeedbackIaResponse generarYGuardar(String email, Integer simulationId) {
        Simulation simulation = cargarPropia(email, simulationId);
        DatosConduccion datos = construirDatos(simulation);
        FeedbackIaResponse informe = generarConRespaldo(datos);
        retroalimentacionRepository.save(Feedback.builder()
                .comentario(truncar(informe.getResumen()))
                .recomendacion(truncar(String.join("; ", informe.getRecomendaciones())))
                .origen(informe.getOrigen())
                .simulation(simulation)
                .build());
        return informe;
    }

    private FeedbackIaResponse generarConRespaldo(DatosConduccion datos) {
        if (proveedorExterno.habilitado()) {
            try {
                FeedbackIaResponse externa = proveedorExterno.generar(datos);
                if (externa.getComparacion() == null) {
                    externa.setComparacion(motorLocal.comparar(datos));
                }
                return externa;
            } catch (IaNoDisponibleException e) {
                log.warn("Proveedor externo de IA no disponible, se usa el motor local: {}", e.getMessage());
            }
        }
        return motorLocal.generar(datos);
    }

    private Simulation cargarPropia(String email, Integer simulationId) {
        Simulation simulation = simulacionRepository.findById(simulationId)
                .orElseThrow(() -> new ResourceNotFoundException("Simulación no encontrada"));
        if (!simulation.getUser().getEmail().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("La simulación pertenece a otro user");
        }
        return simulation;
    }

    private DatosConduccion construirDatos(String email, Integer simulationId) {
        return construirDatos(cargarPropia(email, simulationId));
    }

    private DatosConduccion construirDatos(Simulation simulation) {
        Map<String, BigDecimal> metricas = new java.util.HashMap<>();
        for (PerformanceMetric m : metricaDesempenoRepository
                .findBySimulation_SimulationId(simulation.getSimulationId())) {
            if (m.getMetricType() != null && m.getValue() != null) {
                metricas.put(m.getMetricType().getName(), m.getValue());
            }
        }
        Map<?, ?> snapshot = leerSnapshot(simulation.getObservations());
        int excesos = entero(snapshot.get("excesos"));
        int colisiones = entero(snapshot.get("colisiones"));
        int salidas = entero(snapshot.get("salidas"));
        int semaforos = entero(snapshot.get("semaforos"));
        int distancia = entero(snapshot.get("distancia"));
        int respetados = entero(snapshot.get("respetados"));

        User user = simulation.getUser();
        List<BigDecimal> previos = new ArrayList<>();
        for (Simulation s : simulacionRepository
                .findByUsuario_IdUsuarioOrderByIdSimulacionDesc(user.getUserId())) {
            if (s.isCompleted() && !s.getSimulationId().equals(simulation.getSimulationId())
                    && s.getFinalScore() != null) {
                previos.add(s.getFinalScore());
            }
        }
        BigDecimal promedio = previos.isEmpty() ? BigDecimal.ZERO
                : previos.stream().reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(previos.size()), 2, RoundingMode.HALF_UP);
        BigDecimal mejor = previos.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);

        return new DatosConduccion(
                simulation.getDurationSeconds() != null ? simulation.getDurationSeconds() : 0,
                metricas.getOrDefault("VELOCIDAD_PROMEDIO", BigDecimal.ZERO),
                numero(snapshot.get("velocidadMaxima")),
                excesos, colisiones, salidas, semaforos, respetados, distancia,
                simulation.getFinalScore() != null ? simulation.getFinalScore() : BigDecimal.ZERO,
                simulation.getScenario() != null ? simulation.getScenario().getName() : "el scenario",
                previos.size(), promedio, mejor);
    }

    @SuppressWarnings("unchecked")
    private Map<?, ?> leerSnapshot(String observations) {
        if (observations == null || !observations.trim().startsWith("{")) {
            return Map.of();
        }
        try {
            return objectMapper.readValue(observations, Map.class);
        } catch (Exception e) {
            log.warn("No se pudo interpretar el snapshot de métricas: {}", e.getMessage());
            return Map.of();
        }
    }

    private int entero(Object value) {
        if (value instanceof Number n) return n.intValue();
        return 0;
    }

    private BigDecimal numero(Object value) {
        if (value instanceof Number n) return new BigDecimal(n.toString());
        return BigDecimal.ZERO;
    }

    private String truncar(String texto) {
        if (texto == null) return "";
        return texto.length() <= 1000 ? texto : texto.substring(0, 1000);
    }
}
