package com.sbvia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.Feedback;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import com.sbvia.backend.repository.FeedbackRepository;
import com.sbvia.backend.repository.SimulationRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.service.feedback.DrivingData;
import com.sbvia.backend.service.feedback.AiUnavailableException;
import com.sbvia.backend.service.feedback.ExternalAiFeedbackService;
import com.sbvia.backend.service.feedback.LocalFeedbackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @Mock
    private SimulationRepository simulacionRepository;
    @Mock
    private UserRepository usuarioRepository;
    @Mock
    private PerformanceMetricRepository metricaDesempenoRepository;
    @Mock
    private InfractionRepository infraccionRepository;
    @Mock
    private FeedbackRepository retroalimentacionRepository;
    @Mock
    private LocalFeedbackService motorLocal;
    @Mock
    private ExternalAiFeedbackService proveedorExterno;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private FeedbackService servicioReal() {
        return new FeedbackService(simulacionRepository, usuarioRepository,
                metricaDesempenoRepository, infraccionRepository, retroalimentacionRepository,
                motorLocal, proveedorExterno, objectMapper);
    }

    private Simulation simulacionPropia() {
        User user = User.builder().userId(7).email("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().scenarioId(3).name("Centro urbano").build();
        return Simulation.builder().simulationId(21).user(user).scenario(escenario)
                .finalScore(new BigDecimal("88.00")).durationSeconds(100)
                .observations("{\"velocidadMaxima\":70,\"excesos\":1,\"colisiones\":0,"
                        + "\"salidas\":0,\"semaforos\":0,\"distancia\":0,\"respetados\":1}")
                .completed(true).build();
    }

    @Test
    void usesLocalEngineWhenExternalIsDisabled() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUser_UserIdOrderBySimulationIdDesc(7)).thenReturn(List.of());
        when(proveedorExterno.isEnabled()).thenReturn(false);
        FeedbackIaResponse local = FeedbackIaResponse.builder()
                .origen("IA_LOCAL").nivelRiesgo("MEDIO").build();
        when(motorLocal.generate(any(DrivingData.class))).thenReturn(local);

        FeedbackIaResponse informe = servicioReal().generateReport("conductor@sbvia.test", 21);

        assertThat(informe.getOrigen()).isEqualTo("IA_LOCAL");
        verify(proveedorExterno, never()).generate(any());
    }

    @Test
    void usesLocalEngineWhenExternalFails() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUser_UserIdOrderBySimulationIdDesc(7)).thenReturn(List.of());
        when(proveedorExterno.isEnabled()).thenReturn(true);
        when(proveedorExterno.generate(any(DrivingData.class)))
                .thenThrow(new AiUnavailableException("timeout"));
        FeedbackIaResponse local = FeedbackIaResponse.builder()
                .origen("IA_LOCAL").nivelRiesgo("MEDIO").build();
        when(motorLocal.generate(any(DrivingData.class))).thenReturn(local);

        FeedbackIaResponse informe = servicioReal().generateReport("conductor@sbvia.test", 21);

        assertThat(informe.getOrigen()).isEqualTo("IA_LOCAL");
    }

    @Test
    void savesFeedbackOnGenerateAndSave() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUser_UserIdOrderBySimulationIdDesc(7)).thenReturn(List.of());
        when(proveedorExterno.isEnabled()).thenReturn(false);
        when(motorLocal.generate(any(DrivingData.class))).thenReturn(
                FeedbackIaResponse.builder().resumen("Bien")
                        .recomendaciones(List.of("A", "B", "C")).origen("IA_LOCAL").build());
        when(retroalimentacionRepository.save(any(Feedback.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        servicioReal().generateAndSave("conductor@sbvia.test", 21);

        verify(retroalimentacionRepository).save(any(Feedback.class));
    }

    @Test
    void preventsViewingAnotherUsersReport() {
        User otro = User.builder().email("otro@sbvia.test").build();
        Simulation simulation = Simulation.builder().simulationId(21).user(otro).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulation));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> servicioReal().generateReport("conductor@sbvia.test", 21))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
