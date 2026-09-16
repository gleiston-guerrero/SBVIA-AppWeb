package com.sbvia.backend.service;

import com.sbvia.backend.dto.DrivingMetricsRequest;
import com.sbvia.backend.dto.DrivingResultDTO;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.SimulationState;
import com.sbvia.backend.entity.UserState;
import com.sbvia.backend.entity.DifficultyLevel;
import com.sbvia.backend.entity.SeverityLevel;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.entity.Role;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.WeatherType;
import com.sbvia.backend.entity.MetricType;
import com.sbvia.backend.entity.RoadType;
import com.sbvia.backend.entity.VehicleType;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.entity.Vehicle;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifica que finalizarConduccion persiste simulación, métricas e infracciones
 * en una base real (H2) dentro de una sola transacción.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SimulacionConduccionIntegracionTest {

    @Autowired
    private SimulationService simulacionService;

    @Autowired
    private PerformanceMetricRepository metricaDesempenoRepository;

    @Autowired
    private InfractionRepository infraccionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void finalizaConduccionYPersisteMetricasEInfracciones() {
        Role rol = Role.builder().name("PARTICIPANTE").descripcion("Conductor").build();
        entityManager.persist(rol);
        UserState activo = UserState.builder().name("ACTIVO").descripcion("Habilitada").build();
        entityManager.persist(activo);
        User usuario = User.builder()
                .nombres("Test").apellidos("Conductor").correo("conductor.it@sbvia.test")
                .contrasenaHash("hash").rol(rol).estadoUsuario(activo).build();
        entityManager.persist(usuario);
        RoadType via = RoadType.builder().name("Urbana").build();
        entityManager.persist(via);
        DifficultyLevel nivel = DifficultyLevel.builder().name("Intermedio").value(2).build();
        entityManager.persist(nivel);
        WeatherType clima = WeatherType.builder().name("Despejado").build();
        entityManager.persist(clima);
        Scenario escenario = Scenario.builder()
                .name("Pista IT").densidadTrafico("MEDIA")
                .roadType(via).difficultyLevel(nivel).weatherType(clima).build();
        entityManager.persist(escenario);
        VehicleType tipoVehiculo = VehicleType.builder()
                .name("AUTOMOVIL").licenciaRequerida("B").build();
        entityManager.persist(tipoVehiculo);
        Vehicle vehiculo = Vehicle.builder()
                .name("Vehículo IT").transmision("MANUAL")
                .tipoVehiculo(tipoVehiculo).build();
        entityManager.persist(vehiculo);
        Simulation simulacion = Simulation.builder()
                .usuario(usuario).escenario(escenario).vehiculo(vehiculo)
                .finalScore(BigDecimal.ZERO).build();
        entityManager.persist(simulacion);
        entityManager.persist(SimulationState.builder().name("COMPLETADA").build());
        entityManager.persist(MetricType.builder().name("VELOCIDAD_PROMEDIO").build());
        entityManager.persist(MetricType.builder().name("TOTAL_INFRACCIONES").build());
        entityManager.persist(MetricType.builder().name("PUNTAJE_SEGURIDAD").build());
        entityManager.persist(MetricType.builder().name("PORCENTAJE_CUMPLIMIENTO").build());
        entityManager.persist(TrafficRule.builder().codigo("RT-001").name("Semáforo")
                .categoria("SENALIZACION").penalizacionBase(new BigDecimal("20.00")).build());
        entityManager.persist(TrafficRule.builder().codigo("RT-002").name("Velocidad")
                .categoria("VELOCIDAD").penalizacionBase(new BigDecimal("15.00")).build());
        entityManager.persist(SeverityLevel.builder().name("MODERADA").value(4).build());
        entityManager.persist(SeverityLevel.builder().name("GRAVE").value(7).build());
        entityManager.flush();

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                120, new BigDecimal("45.50"), new BigDecimal("72.00"), 2, 1, 1, 1, 1, 0);
        DrivingResultDTO resultado = simulacionService.finalizarConduccion(
                "conductor.it@sbvia.test", simulacion.getIdSimulacion(), metricas);

        // 100 - (2*15 + 1*20 + 1*20 + 1*10 + 1*8) = 12
        assertThat(resultado.getSimulacion().getFinalScore()).isEqualByComparingTo("12.00");
        assertThat(resultado.getFeedback().getNivelRiesgo()).isEqualTo("ALTO");
        assertThat(resultado.getFeedback().getRecomendaciones()).hasSize(3);
        assertThat(resultado.getFeedback().getOrigen()).isEqualTo("IA_LOCAL");
        assertThat(metricaDesempenoRepository.findBySimulation_SimulationId(simulacion.getIdSimulacion()))
                .hasSize(4);
        assertThat(infraccionRepository.findBySimulation_SimulationId(simulacion.getIdSimulacion()))
                .hasSize(2);
        entityManager.flush();
        entityManager.clear();
        Simulation recargada = entityManager.find(Simulation.class, simulacion.getIdSimulacion());
        assertThat(recargada.isCompleted()).isTrue();
        assertThat(recargada.getDurationSeconds()).isEqualTo(120);
        assertThat(recargada.getSimulationState().getName()).isEqualTo("COMPLETADA");
    }
}
