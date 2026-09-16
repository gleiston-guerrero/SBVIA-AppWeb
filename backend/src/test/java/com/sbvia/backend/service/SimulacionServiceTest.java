package com.sbvia.backend.service;

import com.sbvia.backend.dto.DrivingMetricsRequest;
import com.sbvia.backend.dto.DrivingResultDTO;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.dto.SimulationDTO;
import com.sbvia.backend.entity.Scenario;
import com.sbvia.backend.entity.SimulationState;
import com.sbvia.backend.entity.TrainingSession;
import com.sbvia.backend.entity.Infraction;
import com.sbvia.backend.entity.PerformanceMetric;
import com.sbvia.backend.entity.SeverityLevel;
import com.sbvia.backend.entity.TrafficRule;
import com.sbvia.backend.entity.Simulation;
import com.sbvia.backend.entity.MetricType;
import com.sbvia.backend.entity.User;
import com.sbvia.backend.entity.Vehicle;
import com.sbvia.backend.repository.SimulationStateRepository;
import com.sbvia.backend.repository.InfractionRepository;
import com.sbvia.backend.repository.PerformanceMetricRepository;
import com.sbvia.backend.repository.SeverityLevelRepository;
import com.sbvia.backend.repository.TrafficRuleRepository;
import com.sbvia.backend.repository.TrainingSessionRepository;
import com.sbvia.backend.repository.SimulationRepository;
import com.sbvia.backend.repository.ScenarioRepository;
import com.sbvia.backend.repository.MetricTypeRepository;
import com.sbvia.backend.repository.UserRepository;
import com.sbvia.backend.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class SimulacionServiceTest {

    @Mock
    private SimulationRepository simulacionRepository;

    @Mock
    private UserRepository usuarioRepository;

    @Mock
    private ScenarioRepository escenarioRepository;

    @Mock
    private PerformanceMetricRepository metricaDesempenoRepository;

    @Mock
    private InfractionRepository infraccionRepository;

    @Mock
    private SimulationStateRepository estadoSimulacionRepository;

    @Mock
    private MetricTypeRepository tipoMetricaRepository;

    @Mock
    private TrafficRuleRepository reglaTransitoRepository;

    @Mock
    private SeverityLevelRepository nivelGravedadRepository;

    @Mock
    private TrainingSessionRepository sesionEntrenamientoRepository;

    @Mock
    private VehicleRepository vehiculoRepository;

    @Mock
    private FeedbackService retroalimentacionService;

    @InjectMocks
    private SimulationService simulacionService;

    @Test
    void iniciaUnaSimulacionParaElUsuarioAutenticado() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().idEscenario(3).name("Intersección urbana").activo(true).build();
        SimulationState enProgreso = SimulationState.builder()
                .idEstadoSimulacion(2).name("EN_PROGRESO").build();
        Vehicle vehiculo = Vehicle.builder().idVehiculo(1).name("Vehículo de práctica").activo(true).build();
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(escenarioRepository.findById(3)).thenReturn(Optional.of(escenario));
        when(estadoSimulacionRepository.findByNombre("EN_PROGRESO")).thenReturn(Optional.of(enProgreso));
        when(vehiculoRepository.findFirstByActivoTrueOrderByIdVehiculoAsc()).thenReturn(Optional.of(vehiculo));
        when(sesionEntrenamientoRepository.save(any(TrainingSession.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(simulacionRepository.save(any(Simulation.class))).thenAnswer(invocacion -> {
            Simulation guardada = invocacion.getArgument(0);
            guardada.setIdSimulacion(21);
            return guardada;
        });

        SimulationDTO resultado = simulacionService.iniciarSimulacion(usuario.getCorreo(), 3);

        assertThat(resultado.getIdSimulacion()).isEqualTo(21);
        assertThat(resultado.getFinalScore()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(resultado.getFechaInicio()).isEqualTo(LocalDate.now());
        assertThat(resultado.getIdEscenario()).isEqualTo(3);
        org.mockito.Mockito.verify(simulacionRepository).save(org.mockito.ArgumentMatchers.argThat(simulacion ->
                simulacion.getSimulationState() == enProgreso && simulacion.getVehiculo() == vehiculo));
    }

    @Test
    void rechazaIniciarUnaSimulacionConEscenarioInactivo() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().idEscenario(3).activo(false).build();
        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(escenarioRepository.findById(3)).thenReturn(Optional.of(escenario));

        assertThatThrownBy(() -> simulacionService.iniciarSimulacion(usuario.getCorreo(), 3))
                .isInstanceOf(com.sbvia.backend.exception.ResourceNotFoundException.class)
                .hasMessage("Scenario activo no encontrado");
    }

    @Test
    void finalizaLaSimulacionYApruebaConSetentaPuntos() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(usuario)
                .finalScore(BigDecimal.ZERO).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));
        when(simulacionRepository.save(simulacion)).thenReturn(simulacion);

        SimulationDTO resultado = simulacionService.finalizarSimulacion(
                usuario.getCorreo(), 21, new BigDecimal("70"));

        assertThat(resultado.getFinalScore()).isEqualByComparingTo("70");
        assertThat(resultado.getEndDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void impideFinalizarLaPracticaDeOtroUsuario() {
        User propietario = User.builder().correo("propietario@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(propietario).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        assertThatThrownBy(() -> simulacionService.finalizarSimulacion(
                "otro@sbvia.test", 21, new BigDecimal("80")))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    @Test
    void impideFinalizarDosVecesLaMismaPractica() {
        User usuario = User.builder().correo("conductor@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(usuario).completed(true).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        assertThatThrownBy(() -> simulacionService.finalizarSimulacion(
                usuario.getCorreo(), 21, new BigDecimal("90")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La simulación ya fue finalizada");
    }

    @Test
    void obtieneLasPracticasDelUsuarioConSuEscenario() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().idEscenario(3).name("Intersección urbana").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(11)
                .usuario(usuario)
                .escenario(escenario)
                .completed(true)
                .finalScore(new BigDecimal("92.50"))
                .build();

        when(usuarioRepository.findByCorreo(usuario.getCorreo())).thenReturn(Optional.of(usuario));
        when(simulacionRepository.findByUsuario_IdUsuarioOrderByIdSimulacionDesc(7)).thenReturn(List.of(simulacion));

        List<SimulationDTO> resultado = simulacionService.obtenerMisPracticas(usuario.getCorreo());

        assertThat(resultado).singleElement().satisfies(dto -> {
            assertThat(dto.getIdSimulacion()).isEqualTo(11);
            assertThat(dto.getIdEscenario()).isEqualTo(3);
            assertThat(dto.getNombreEscenario()).isEqualTo("Intersección urbana");
            assertThat(dto.getFinalScore()).isEqualByComparingTo("92.50");
            assertThat(dto.getIdUsuario()).isEqualTo(7);
            assertThat(dto.getCorreoUsuario()).isEqualTo("conductor@sbvia.test");
        });
    }

    @Test
    void rechazaLaConsultaCuandoElUsuarioNoExiste() {
        when(usuarioRepository.findByCorreo("desconocido@sbvia.test")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> simulacionService.obtenerMisPracticas("desconocido@sbvia.test"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User no encontrado");
    }

    @Test
    void finalizaConduccionCalculandoElPuntajeEnElServidor() {
        User usuario = User.builder().idUsuario(7).correo("conductor@sbvia.test").build();
        Scenario escenario = Scenario.builder().idEscenario(3).name("Centro urbano").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(usuario).escenario(escenario).completed(false).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));
        when(reglaTransitoRepository.findByCodigo("RT-002")).thenReturn(Optional.of(
                TrafficRule.builder().idReglaTransito(2).codigo("RT-002").penalizacionBase(new BigDecimal("15.00")).build()));
        when(reglaTransitoRepository.findByCodigo("RT-001")).thenReturn(Optional.of(
                TrafficRule.builder().idReglaTransito(1).codigo("RT-001").penalizacionBase(new BigDecimal("20.00")).build()));
        when(nivelGravedadRepository.findByNombre("MODERADA")).thenReturn(Optional.of(
                SeverityLevel.builder().idNivelGravedad(2).name("MODERADA").build()));
        when(nivelGravedadRepository.findByNombre("GRAVE")).thenReturn(Optional.of(
                SeverityLevel.builder().idNivelGravedad(3).name("GRAVE").build()));
        when(estadoSimulacionRepository.findByNombre("COMPLETADA")).thenReturn(Optional.of(
                SimulationState.builder().idEstadoSimulacion(4).name("COMPLETADA").build()));
        when(tipoMetricaRepository.findByNombre(any(String.class))).thenAnswer(invocacion ->
                Optional.of(MetricType.builder().name(invocacion.getArgument(0)).build()));
        when(simulacionRepository.save(any(Simulation.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
        when(metricaDesempenoRepository.save(any(PerformanceMetric.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(infraccionRepository.save(any(Infraction.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                120, new BigDecimal("45.50"), new BigDecimal("72.00"), 2, 1, 1, 1, 1, 0);
        when(retroalimentacionService.generarYGuardar(usuario.getCorreo(), 21)).thenReturn(
                FeedbackIaResponse.builder().puntaje(new BigDecimal("12.00")).origen("IA_LOCAL").build());
        DrivingResultDTO resultado = simulacionService.finalizarConduccion(usuario.getCorreo(), 21, metricas);

        // 100 - (2*15 + 1*20 + 1*20 + 1*10 + 1*8) = 100 - 88 = 12
        assertThat(resultado.getSimulacion().getFinalScore()).isEqualByComparingTo("12.00");
        assertThat(resultado.getSimulacion().getEndDate()).isEqualTo(LocalDate.now());
        assertThat(resultado.getFeedback().getOrigen()).isEqualTo("IA_LOCAL");
        assertThat(simulacion.getDurationSeconds()).isEqualTo(120);
        assertThat(simulacion.isCompleted()).isTrue();
        org.mockito.Mockito.verify(metricaDesempenoRepository, org.mockito.Mockito.times(4))
                .save(any(PerformanceMetric.class));
        org.mockito.Mockito.verify(infraccionRepository, org.mockito.Mockito.times(2))
                .save(any(Infraction.class));
    }

    @Test
    void impideFinalizarConduccionDeOtroUsuario() {
        User propietario = User.builder().correo("propietario@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(propietario).completed(false).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                60, new BigDecimal("40.00"), new BigDecimal("55.00"), 0, 0, 0, 0, 0, 0);

        assertThatThrownBy(() -> simulacionService.finalizarConduccion("otro@sbvia.test", 21, metricas))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    @Test
    void impideFinalizarDosVecesLaConduccion() {
        User usuario = User.builder().correo("conductor@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(usuario).completed(true).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                60, new BigDecimal("40.00"), new BigDecimal("55.00"), 0, 0, 0, 0, 0, 0);

        assertThatThrownBy(() -> simulacionService.finalizarConduccion(usuario.getCorreo(), 21, metricas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La simulación ya fue finalizada");
    }

    @Test
    void rechazaMetricasConMaximaMenorQueElPromedio() {
        User usuario = User.builder().correo("conductor@sbvia.test").build();
        Simulation simulacion = Simulation.builder()
                .idSimulacion(21).usuario(usuario).completed(false).build();
        when(simulacionRepository.findById(21)).thenReturn(Optional.of(simulacion));

        DrivingMetricsRequest metricas = new DrivingMetricsRequest(
                60, new BigDecimal("50.00"), new BigDecimal("40.00"), 0, 0, 0, 0, 0, 0);

        assertThatThrownBy(() -> simulacionService.finalizarConduccion(usuario.getCorreo(), 21, metricas))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("La velocidad máxima no puede ser menor que la promedio");
    }

    @Test
    void listaTodasLasPracticasAunqueNoTenganEscenario() {
        Simulation simulacion = Simulation.builder()
                .idSimulacion(15)
                .build();
        when(simulacionRepository.findAllByOrderByIdSimulacionDesc()).thenReturn(List.of(simulacion));

        List<SimulationDTO> resultado = simulacionService.obtenerTodas();

        assertThat(resultado).singleElement().satisfies(dto -> {
            assertThat(dto.getIdEscenario()).isNull();
            assertThat(dto.getNombreEscenario()).isEqualTo("N/A");
        });
    }
}
