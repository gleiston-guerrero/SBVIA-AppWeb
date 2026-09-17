package com.sbvia.backend.controller;

import com.sbvia.backend.dto.StatisticsDTO;
import com.sbvia.backend.dto.EndSimulationRequest;
import com.sbvia.backend.dto.DrivingMetricsRequest;
import com.sbvia.backend.dto.DrivingResultDTO;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.dto.SimulationDTO;
import com.sbvia.backend.service.FeedbackService;
import com.sbvia.backend.service.SimulationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SimulationControllerTest {

    @Mock
    private SimulationService simulationService;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private SimulationController controller;

    @Test
    void testIniciar() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(simulationService.iniciarSimulacion("user", 1)).thenReturn(new SimulationDTO());

        ResponseEntity<SimulationDTO> res = controller.iniciar(1, auth);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testFinalizar() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(simulationService.finalizarSimulacion(eq("user"), eq(1), any(BigDecimal.class))).thenReturn(new SimulationDTO());

        ResponseEntity<SimulationDTO> res = controller.finalizar(1, new EndSimulationRequest(BigDecimal.valueOf(100)), auth);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testFinalizarConduccion() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(simulationService.finalizarConduccion(eq("user"), eq(1), any(DrivingMetricsRequest.class))).thenReturn(new DrivingResultDTO());

        DrivingMetricsRequest request = new DrivingMetricsRequest(
            0, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0, 0, 0, 0, 0
        );
        
        ResponseEntity<DrivingResultDTO> res = controller.finalizarConduccion(1, request, auth);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testFeedback() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(feedbackService.generateReport("user", 1)).thenReturn(new FeedbackIaResponse());

        ResponseEntity<FeedbackIaResponse> res = controller.feedback(1, auth);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testObtenerMisPracticas() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user");
        when(simulationService.getMyPractices("user")).thenReturn(List.of(new SimulationDTO()));

        ResponseEntity<List<SimulationDTO>> res = controller.getMyPractices(auth);
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testObtenerTodas() {
        when(simulationService.getAll()).thenReturn(List.of(new SimulationDTO()));
        ResponseEntity<List<SimulationDTO>> res = controller.getAll();
        assertEquals(200, res.getStatusCode().value());
    }

    @Test
    void testObtenerEstadisticasGlobales() {
        when(simulationService.getGlobalStatistics()).thenReturn(new StatisticsDTO(1, 100, 50));
        ResponseEntity<StatisticsDTO> res = controller.getGlobalStatistics();
        assertEquals(200, res.getStatusCode().value());
    }
}
