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
import com.sbvia.backend.service.feedback.DatosConduccion;
import com.sbvia.backend.service.feedback.IaNoDisponibleException;
import com.sbvia.backend.service.feedback.FeedbackIaExternaService;
import com.sbvia.backend.service.feedback.FeedbackLocalService;
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
    private FeedbackLocalService motorLocal;
    @Mock
    private FeedbackIaExternaService proveedorExterno;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private FeedbackService servicioReal() {
        return new FeedbackService(simulacionRepository, usuarioRepository,
                metricaDesempenoRepository, infraccionRepository, retroalimentacionRepository,
                motorLocal, proveedorExterno, objectMapper);
    }

    private Simulation simulacionPropia() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().idEscenario(3).name("Centro urbano").build();
        return Simulation.builder().idSimulacion(21).usuario(usuario).escenario(escenario)
                .finalScore(new BigDecimal("88.00")).durationSeconds(100)
                .observations("{\"velocidadMaxima\":70,\"excesos\":1,\"colisiones\":0,"
                        + "\"salidas\":0,\"semaforos\":0,\"distancia\":0,\"respetados\":1}")
                .completed(true).build();
    }

    @Test
    void usaElMotorLocalCuandoElExternoNoEstaHabilitado() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUsuario_IdUsuarioOrderByIdSimulacionDesc(7)).thenReturn(List.of());
        when(proveedorExterno.habilitado()).thenReturn(false);
        FeedbackIaResponse local = FeedbackIaResponse.builder()
                .origen("IA_LOCAL").nivelRiesgo("MEDIO").build();
        when(motorLocal.generar(any(DatosConduccion.class))).thenReturn(local);

        FeedbackIaResponse informe = servicioReal().generarInforme("conductor@sbvia.test", 21);

        assertThat(informe.getOrigen()).isEqualTo("IA_LOCAL");
        verify(proveedorExterno, never()).generar(any());
    }

    @Test
    void usaElMotorLocalCuandoElExternoFalla() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUsuario_IdUsuarioOrderByIdSimulacionDesc(7)).thenReturn(List.of());
        when(proveedorExterno.habilitado()).thenReturn(true);
        when(proveedorExterno.generar(any(DatosConduccion.class)))
                .thenThrow(new IaNoDisponibleException("timeout"));
        FeedbackIaResponse local = FeedbackIaResponse.builder()
                .origen("IA_LOCAL").nivelRiesgo("MEDIO").build();
        when(motorLocal.generar(any(DatosConduccion.class))).thenReturn(local);

        FeedbackIaResponse informe = servicioReal().generarInforme("conductor@sbvia.test", 21);

        assertThat(informe.getOrigen()).isEqualTo("IA_LOCAL");
    }

    @Test
    void guardaLaFeedbackAlGenerarYGuardar() {
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacionPropia()));
        when(metricaDesempenoRepository.findBySimulation_SimulationId(21)).thenReturn(List.of());
        when(simulacionRepository.findByUsuario_IdUsuarioOrderByIdSimulacionDesc(7)).thenReturn(List.of());
        when(proveedorExterno.habilitado()).thenReturn(false);
        when(motorLocal.generar(any(DatosConduccion.class))).thenReturn(
                FeedbackIaResponse.builder().resumen("Bien")
                        .recomendaciones(List.of("A", "B", "C")).origen("IA_LOCAL").build());
        when(retroalimentacionRepository.save(any(Feedback.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        servicioReal().generarYGuardar("conductor@sbvia.test", 21);

        verify(retroalimentacionRepository).save(any(Feedback.class));
    }

    @Test
    void impideVerElInformeDeOtroUsuario() {
        User otro = User.builder().correo("otro@sbvia.test").build();
        Simulation simulacion = Simulation.builder().idSimulacion(21).usuario(otro).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        org.assertj.core.api.Assertions.assertThatThrownBy(
                        () -> servicioReal().generarInforme("conductor@sbvia.test", 21))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
