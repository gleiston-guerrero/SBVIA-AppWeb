package com.sbvia.backend.controller;

import com.sbvia.backend.dto.MetricasConduccionRequest;
import com.sbvia.backend.dto.ResultadoConduccionDTO;
import com.sbvia.backend.dto.RetroalimentacionIaResponse;
import com.sbvia.backend.dto.SimulacionDTO;
import com.sbvia.backend.dto.FinalizarSimulacionRequest;
import com.sbvia.backend.service.RetroalimentacionService;
import com.sbvia.backend.service.SimulacionService;
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

@RestController
@RequestMapping("/api/simulaciones")
@RequiredArgsConstructor
@Tag(name = "Simulaciones", description = "Endpoints de prácticas de simulaciones")
@SecurityRequirement(name = "bearerAuth")
public class SimulacionController {

    private final SimulacionService simulacionService;
    private final RetroalimentacionService retroalimentacionService;

    /**
     * POST /api/simulaciones/iniciar/{idEscenario} — Iniciar simulación.
     * Crea una nueva práctica de conducción basada en el escenario especificado.
     *
     * @param idEscenario el identificador del escenario a utilizar en la simulación
     * @param authentication el contexto de seguridad con los datos del usuario actual
     * @return una respuesta HTTP con el objeto SimulacionDTO que representa la práctica iniciada
     */
    @PostMapping("/iniciar/{idEscenario}")
    @Operation(summary = "Iniciar simulación", description = "Crea una práctica en progreso para el usuario autenticado")
    public ResponseEntity<SimulacionDTO> iniciar(
            @PathVariable Integer idEscenario,
            Authentication authentication) {
        return ResponseEntity.ok(simulacionService.iniciarSimulacion(authentication.getName(), idEscenario));
    }

    /**
     * POST /api/simulaciones/{idSimulacion}/finalizar — Finalizar simulación.
     * Concluye una práctica en progreso registrando el puntaje final obtenido.
     *
     * @param idSimulacion el identificador único de la simulación a finalizar
     * @param request el objeto con los datos de finalización, como el puntaje final
     * @param authentication el contexto de seguridad del usuario autenticado
     * @return una respuesta HTTP con el objeto SimulacionDTO actualizado a estado completado
     */
    @PostMapping("/{idSimulacion}/finalizar")
    @Operation(summary = "Finalizar simulación", description = "Registra el puntaje y genera el resultado de la práctica")
    public ResponseEntity<SimulacionDTO> finalizar(
            @PathVariable Integer idSimulacion,
            @Valid @RequestBody FinalizarSimulacionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulacionService.finalizarSimulacion(
                authentication.getName(), idSimulacion, request.puntajeFinal()));
    }

    /**
     * POST /api/simulaciones/{idSimulacion}/conduccion/finalizar — Finalizar conducción 2D.
     * Recibe las métricas de la conducción en el simulador y calcula los resultados finales.
     *
     * @param idSimulacion el identificador de la simulación correspondiente
     * @param request las métricas obtenidas durante la conducción (colisiones, tiempo, etc.)
     * @param authentication el contexto de seguridad del usuario
     * @return una respuesta HTTP con el ResultadoConduccionDTO que incluye puntajes y penalizaciones
     */
    @PostMapping("/{idSimulacion}/conduccion/finalizar")
    @Operation(summary = "Finalizar conducción 2D", description = "Registra las métricas del simulador, calcula el puntaje en el servidor y persiste los resultados")
    public ResponseEntity<ResultadoConduccionDTO> finalizarConduccion(
            @PathVariable Integer idSimulacion,
            @Valid @RequestBody MetricasConduccionRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(simulacionService.finalizarConduccion(
                authentication.getName(), idSimulacion, request));
    }

    /**
     * GET /api/simulaciones/{idSimulacion}/retroalimentacion — Obtener retroalimentación.
     * Genera o recupera un informe de desempeño sobre la simulación (usando motor local o IA).
     *
     * @param idSimulacion el identificador de la simulación consultada
     * @param authentication el contexto de seguridad del usuario autenticado
     * @return una respuesta HTTP con el objeto RetroalimentacionIaResponse que contiene las recomendaciones
     */
    @GetMapping("/{idSimulacion}/retroalimentacion")
    @Operation(summary = "Obtener retroalimentación", description = "Devuelve el informe de desempeño de una simulación propia (motor local o IA externa)")
    public ResponseEntity<RetroalimentacionIaResponse> retroalimentacion(
            @PathVariable Integer idSimulacion,
            Authentication authentication) {
        return ResponseEntity.ok(retroalimentacionService.generarInforme(
                authentication.getName(), idSimulacion));
    }

    /**
     * GET /api/simulaciones/mis-practicas — Historial de simulaciones.
     * Retorna todas las prácticas realizadas por el usuario actual.
     *
     * @param authentication el contexto de seguridad con la identidad del usuario
     * @return una respuesta HTTP con una lista de objetos SimulacionDTO pertenecientes al usuario
     */
    @GetMapping("/mis-practicas")
    @Operation(summary = "Obtener mis prácticas", description = "Devuelve el historial de simulaciones del usuario autenticado")
    public ResponseEntity<List<SimulacionDTO>> obtenerMisPracticas(Authentication authentication) {
        String email = authentication.getName();
        List<SimulacionDTO> practicas = simulacionService.obtenerMisPracticas(email);
        return ResponseEntity.ok(practicas);
    }

    /**
     * GET /api/simulaciones — Listar todas las simulaciones.
     * Permite a los administradores o instructores revisar todas las prácticas del sistema.
     *
     * @return una respuesta HTTP con la lista completa de objetos SimulacionDTO
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR', 'INSTRUCTOR')")
    @Operation(summary = "Obtener todas las simulaciones", description = "Devuelve todas las simulaciones para supervisión de administradores, instructores y auditores")
    public ResponseEntity<List<SimulacionDTO>> obtenerTodas() {
        List<SimulacionDTO> practicas = simulacionService.obtenerTodas();
        return ResponseEntity.ok(practicas);
    }

    /**
     * GET /api/simulaciones/estadisticas — Obtener estadísticas globales.
     * Calcula métricas agregadas del uso del simulador a nivel de todo el sistema.
     *
     * @return una respuesta HTTP con el objeto EstadisticasDTO que contiene los promedios y totales
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasAnyAuthority('ADMINISTRADOR')")
    @Operation(summary = "Obtener estadísticas globales", description = "Calcula el total de prácticas y promedios globales de forma eficiente")
    public ResponseEntity<com.sbvia.backend.dto.EstadisticasDTO> obtenerEstadisticasGlobales() {
        return ResponseEntity.ok(simulacionService.obtenerEstadisticasGlobales());
    }
}
