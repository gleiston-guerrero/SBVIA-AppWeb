package com.sbvia.backend.controller;

import com.sbvia.backend.dto.DrivingMetricsRequest;
import com.sbvia.backend.dto.DrivingResultDTO;
import com.sbvia.backend.dto.FeedbackIaResponse;
import com.sbvia.backend.dto.SimulationDTO;
import com.sbvia.backend.dto.EndSimulationRequest;
import com.sbvia.backend.service.FeedbackService;
import com.sbvia.backend.service.SimulationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.List;

/**
 * REST endpoints for starting, tracking and scoring driving simulations.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/simulations")
@RequiredArgsConstructor
@Tag(name = "Simulations", description = "Endpoints de prácticas de simulations")
@SecurityRequirement(name = "bearerAuth")
public class SimulationController {
    private final SimulationService simulationService;
    private final FeedbackService retroalimentacionService;

    /**
     * POST /api/simulations/iniciar/{scenarioId} — Iniciar simulación.
     * Creates a new driving practice based on the given scenario.
     *
     * @param scenarioId the identifier of the scenario to use in the simulation
     * @param authentication the security context with the current user data
     * @return an HTTP response with the SimulationDTO representing the started practice
     */
    @PostMapping("/iniciar/{scenarioId}")
    @Operation(summary = "Iniciar simulación", description = "Crea una práctica en progreso para el user autenticado")
    public ResponseEntity<SimulationDTO> start(
            @PathVariable Integer scenarioId,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.startSimulation(authentication.getName(), scenarioId));
    }

    /**
     * POST /api/simulations/{simulationId}/finalizar — Finalizar simulación.
     * Closes a practice in progress, recording the final score obtained.
     *
     * @param simulationId the unique identifier of the simulation to finish
     * @param request the object with the closing data, such as the final score
     * @param authentication the security context of the authenticated user
     * @return an HTTP response with the SimulationDTO updated to the completed state
     */
    @PostMapping("/{simulationId}/finalizar")
    @Operation(summary = "Finalizar simulación", description = "Registra el puntaje y genera el resultado de la práctica")
    public ResponseEntity<SimulationDTO> finish(
            @PathVariable Integer simulationId,
            @Valid @RequestBody EndSimulationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.finishSimulation(
                authentication.getName(), simulationId, request.finalScore()));
    }

    /**
     * POST /api/simulations/{simulationId}/conduccion/finalizar — Finalizar conducción 2D.
     * Receives the driving metrics from the simulator and computes the final results.
     *
     * @param simulationId the identifier of the matching simulation
     * @param request the metrics gathered while driving (collisions, time, and so on)
     * @param authentication the security context of the user
     * @return an HTTP response with the DrivingResultDTO, including scores and penalties
     */
    @PostMapping("/{simulationId}/conduccion/finalizar")
    @Operation(summary = "Finalizar conducción 2D", description = "Registra las métricas del simulador, calcula el puntaje en el servidor y persiste los resultados")
    public ResponseEntity<DrivingResultDTO> finishDriving(
            @PathVariable Integer simulationId,
            @Valid @RequestBody DrivingMetricsRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.finishDriving(
                authentication.getName(), simulationId, request));
    }

    /**
     * GET /api/simulations/{simulationId}/feedback — Obtener retroalimentación.
     * Generates or retrieves a performance report for the simulation, using the local engine or the AI.
     *
     * @param simulationId the identifier of the simulation being queried
     * @param authentication the security context of the authenticated user
     * @return an HTTP response with the FeedbackIaResponse holding the recommendations
     */
    @GetMapping("/{simulationId}/feedback")
    @Operation(summary = "Obtener retroalimentación", description = "Devuelve el informe de desempeño de una simulación propia (motor local o IA externa)")
    public ResponseEntity<FeedbackIaResponse> feedback(
            @PathVariable Integer simulationId,
            Authentication authentication) {
        return ResponseEntity.ok(retroalimentacionService.generateReport(
                authentication.getName(), simulationId));
    }

    /**
     * GET /api/simulations/mis-practicas - Practice history.
     * Returns every practice carried out by the current user.
     *
     * @param authentication the security context with the user identity
     * @return an HTTP response with a list of SimulationDTO objects belonging to the user
     */
    @GetMapping("/mis-practicas")
    @Operation(summary = "Obtener mis prácticas", description = "Devuelve el historial de simulations del user autenticado")
    public ResponseEntity<List<SimulationDTO>> getMyPractices(Authentication authentication) {
        String email = authentication.getName();
        List<SimulationDTO> practicas = simulationService.getMyPractices(email);
        return ResponseEntity.ok(practicas);
    }

    /**
     * GET /api/simulations - List every simulation.
     * Allows administrators and instructors to review every practice in the system.
     *
     * @return an HTTP response with the full list of SimulationDTO objects
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'INSTRUCTOR')")
    @Operation(summary = "Obtener todas las simulations", description = "Devuelve todas las simulations para supervisión de administradores, instructores y auditores")
    public ResponseEntity<List<SimulationDTO>> getAll() {
        List<SimulationDTO> practicas = simulationService.getAll();
        return ResponseEntity.ok(practicas);
    }

    /**
     * GET /api/simulations/estadisticas — Obtener estadísticas globales.
     * Computes aggregated metrics of simulator usage across the whole system.
     *
     * @return an HTTP response with the StatisticsDTO holding the averages and totals
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas globales", description = "Calcula el total de prácticas y promedios globales de forma eficiente")
    public ResponseEntity<com.sbvia.backend.dto.StatisticsDTO> getGlobalStatistics() {
        return ResponseEntity.ok(simulationService.getGlobalStatistics());
    }
}
