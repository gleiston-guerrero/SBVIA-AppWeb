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
     * Crea una nueva práctica de conducción basada en el scenario especificado.
     *
     * @param scenarioId el identificador del scenario a utilizar en la simulación
     * @param authentication el contexto de seguridad con los datos del user actual
     * @return una respuesta HTTP con el objeto SimulationDTO que representa la práctica iniciada
     */
    @PostMapping("/iniciar/{scenarioId}")
    @Operation(summary = "Iniciar simulación", description = "Crea una práctica en progreso para el user autenticado")
    public ResponseEntity<SimulationDTO> iniciar(
            @PathVariable Integer scenarioId,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.iniciarSimulacion(authentication.getName(), scenarioId));
    }

    /**
     * POST /api/simulations/{simulationId}/finalizar — Finalizar simulación.
     * Concluye una práctica en progreso registrando el puntaje final obtenido.
     *
     * @param simulationId el identificador único de la simulación a finalizar
     * @param request el objeto con los datos de finalización, como el puntaje final
     * @param authentication el contexto de seguridad del user autenticado
     * @return una respuesta HTTP con el objeto SimulationDTO actualizado a estado completado
     */
    @PostMapping("/{simulationId}/finalizar")
    @Operation(summary = "Finalizar simulación", description = "Registra el puntaje y genera el resultado de la práctica")
    public ResponseEntity<SimulationDTO> finalizar(
            @PathVariable Integer simulationId,
            @Valid @RequestBody EndSimulationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.finalizarSimulacion(
                authentication.getName(), simulationId, request.finalScore()));
    }

    /**
     * POST /api/simulations/{simulationId}/conduccion/finalizar — Finalizar conducción 2D.
     * Recibe las métricas de la conducción en el simulador y calcula los resultados finales.
     *
     * @param simulationId el identificador de la simulación correspondiente
     * @param request las métricas obtenidas durante la conducción (colisiones, tiempo, etc.)
     * @param authentication el contexto de seguridad del user
     * @return una respuesta HTTP con el DrivingResultDTO que incluye puntajes y penalizaciones
     */
    @PostMapping("/{simulationId}/conduccion/finalizar")
    @Operation(summary = "Finalizar conducción 2D", description = "Registra las métricas del simulador, calcula el puntaje en el servidor y persiste los resultados")
    public ResponseEntity<DrivingResultDTO> finalizarConduccion(
            @PathVariable Integer simulationId,
            @Valid @RequestBody DrivingMetricsRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulationService.finalizarConduccion(
                authentication.getName(), simulationId, request));
    }

    /**
     * GET /api/simulations/{simulationId}/feedback — Obtener retroalimentación.
     * Genera o recupera un informe de desempeño sobre la simulación (usando motor local o IA).
     *
     * @param simulationId el identificador de la simulación consultada
     * @param authentication el contexto de seguridad del user autenticado
     * @return una respuesta HTTP con el objeto FeedbackIaResponse que contiene las recomendaciones
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
     * GET /api/simulations/mis-practicas — Historial de simulations.
     * Retorna todas las prácticas realizadas por el user actual.
     *
     * @param authentication el contexto de seguridad con la identidad del user
     * @return una respuesta HTTP con una lista de objetos SimulationDTO pertenecientes al user
     */
    @GetMapping("/mis-practicas")
    @Operation(summary = "Obtener mis prácticas", description = "Devuelve el historial de simulations del user autenticado")
    public ResponseEntity<List<SimulationDTO>> getMyPractices(Authentication authentication) {
        String email = authentication.getName();
        List<SimulationDTO> practicas = simulationService.getMyPractices(email);
        return ResponseEntity.ok(practicas);
    }

    /**
     * GET /api/simulations — Listar todas las simulations.
     * Permite a los administradores o instructores revisar todas las prácticas del sistema.
     *
     * @return una respuesta HTTP con la lista completa de objetos SimulationDTO
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
     * Calcula métricas agregadas del uso del simulador a nivel de todo el sistema.
     *
     * @return una respuesta HTTP con el objeto StatisticsDTO que contiene los promedios y totales
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas globales", description = "Calcula el total de prácticas y promedios globales de forma eficiente")
    public ResponseEntity<com.sbvia.backend.dto.StatisticsDTO> getGlobalStatistics() {
        return ResponseEntity.ok(simulationService.getGlobalStatistics());
    }
}
