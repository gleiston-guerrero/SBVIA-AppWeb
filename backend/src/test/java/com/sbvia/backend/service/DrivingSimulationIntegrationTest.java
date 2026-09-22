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
 * Checks that finishDriving persists the simulation, its metrics and its infractions
 * in a real database (H2) within a single transaction.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DrivingSimulationIntegrationTest {

    @Autowired
    private SimulationService simulationService;

    @Autowired
    private PerformanceMetricRepository metricaDesempenoRepository;

    @Autowired
    private InfractionRepository infraccionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void finishesDrivingAndPersistsMetricsAndInfractions() {
        Role rol = Role.builder().name("PARTICIPANTE").description("Conductor").build();
        entityManager.persist(rol);
        UserState activo = UserState.builder().name("ACTIVO").description("Habilitada").build();
        entityManager.persist(activo);
        User user = User.builder()
                .firstName("Test").lastName("Conductor").email("conductor.it@sbvia.test")
                .passwordHash("hash").role(rol).userState(activo).build();
        entityManager.persist(user);
        RoadType via = RoadType.builder().name("Urbana").build();
        entityManager.persist(via);
        DifficultyLevel nivel = DifficultyLevel.builder().name("Intermedio").value(2).build();
        entityManager.persist(nivel);
        WeatherType clima = WeatherType.builder().name("Despejado").build();
        entityManager.persist(clima);
        Scenario escenario = Scenario.builder()
                .name("Pista IT").trafficDensity("MEDIA")
                .roadType(via).difficultyLevel(nivel).weatherType(clima).build();
        entityManager.persist(escenario);
        VehicleType vehicleType = VehicleType.builder()
                .name("AUTOMOVIL").licenciaRequerida("B").build();
        entityManager.persist(vehicleType);
        Vehicle vehicle = Vehicle.builder()
                .name("Vehículo IT").transmision("MANUAL")
                .vehicleType(vehicleType).build();
        entityManager.persist(vehicle);
        Simulation simulation = Simulation.builder()
                .user(user).scenario(escenario).vehicle(vehicle)
                .finalScore(BigDecimal.ZERO).build();
        entityManager.persist(simulation);
        entityManager.persist(SimulationState.builder().name("COMPLETADA").build());
        entityManager.persist(MetricType.builder().name("VELOCIDAD_PROMEDIO").build());
        entityManager.persist(MetricType.builder().name("TOTAL_INFRACCIONES").build());
        entityManager.persist(MetricType.builder().name("PUNTAJE_SEGURIDAD").build());
        entityManager.persist(MetricType.builder().name("PORCENTAJE_CUMPLIMIENTO").build());
        entityManager.persist(TrafficRule.builder().codigo("RT-001").nombre("Exceso de velocidad").categoria("GRAVE").penalizacionBase(new BigDecimal("10.00")).activa(true).build());
        entityManager.persist(TrafficRule.builder().codigo("RT-002").nombre("Paso de semáforo en rojo").categoria("MUY_GRAVE").penalizacionBase(new BigDecimal("20.00")).activa(true).build());
        entityManager.persist(SeverityLevel.builder().name("MODERADA").value(4).build());
        entityManager.persist(SeverityLevel.builder().name("GRAVE").value(7).build());
        entityManager.flush();

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                120, new BigDecimal("45.50"), new BigDecimal("72.00"), 2, 1, 1, 1, 1, 0);
        DrivingResultDTO resultado = simulationService.finishDriving(
                "conductor.it@sbvia.test", simulation.getSimulationId(), metricas);

        // 100 - (2*15 + 1*20 + 1*20 + 1*10 + 1*8) = 12
        assertThat(resultado.getSimulation().getFinalScore()).isEqualByComparingTo("12.00");
        assertThat(resultado.getFeedback().getNivelRiesgo()).isEqualTo("ALTO");
        assertThat(resultado.getFeedback().getRecomendaciones()).hasSize(3);
        assertThat(resultado.getFeedback().getOrigen()).isEqualTo("IA_LOCAL");
        assertThat(metricaDesempenoRepository.findBySimulation_SimulationId(simulation.getSimulationId()))
                .hasSize(4);
        assertThat(infraccionRepository.findBySimulation_SimulationId(simulation.getSimulationId()))
                .hasSize(2);
        entityManager.flush();
        entityManager.clear();
        Simulation recargada = entityManager.find(Simulation.class, simulation.getSimulationId());
        assertThat(recargada.isCompleted()).isTrue();
        assertThat(recargada.getDurationSeconds()).isEqualTo(120);
        assertThat(recargada.getSimulationState().getName()).isEqualTo("COMPLETADA");
    }
}
