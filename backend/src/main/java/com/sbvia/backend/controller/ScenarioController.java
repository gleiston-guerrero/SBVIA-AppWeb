package com.sbvia.backend.controller;

import com.sbvia.backend.dto.ScenarioDTO;
import com.sbvia.backend.service.ScenarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para el CRUD de Scenarios.
 * Demuestra el uso de Spring Data JPA con paginación y seguridad basada en roles.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/scenarios")
@RequiredArgsConstructor
@Tag(name = "Scenarios", description = "CRUD de scenarios de simulación vial")
@SecurityRequirement(name = "bearerAuth")
public class ScenarioController {
    private final ScenarioService scenarioService;

    /**
     * GET /api/scenarios — Listar scenarios con paginación y filtros opcionales.
     * Accesible por cualquier user autenticado (ROLE_USER, ROLE_ADMIN, ROLE_INSTRUCTOR).
     * Filtros: roadType, difficultyLevel, clima (todos opcionales).
     *
     * @param roadType filtro opcional para buscar por el tipo de vía (urbana, rural, etc.)
     * @param difficultyLevel filtro opcional para buscar por nivel de dificultad (1, 2, 3...)
     * @param clima filtro opcional para buscar por condiciones climáticas (despejado, lluvia, etc.)
     * @param pageable objeto que contiene la configuración de paginación (tamaño, página, ordenamiento)
     * @return una página de objetos ScenarioDTO que coinciden con los criterios de búsqueda
     */
    @GetMapping
    @Operation(summary = "Listar scenarios", description = "Lista scenarios activos con paginación y filtros opcionales (roadType, difficultyLevel, clima)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<Page<ScenarioDTO>> listar(
            @RequestParam(required = false) String roadType,
            @RequestParam(required = false) Integer difficultyLevel,
            @RequestParam(required = false) String clima,
            Pageable pageable) {
        Page<ScenarioDTO> page = scenarioService.findFiltered(roadType, difficultyLevel, clima, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/scenarios/{id} — Obtener un scenario específico.
     * Accesible por cualquier user autenticado.
     *
     * @param id el identificador único del scenario solicitado
     * @return una respuesta HTTP con el objeto ScenarioDTO correspondiente al ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener scenario por ID", description = "Devuelve los detalles de un scenario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Scenario devuelto exitosamente"),
        @ApiResponse(responseCode = "404", description = "Scenario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    /**
     * Método público.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object
     */
    public ResponseEntity<ScenarioDTO> findById(@PathVariable Integer id) {
        ScenarioDTO dto = scenarioService.findById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/scenarios — Crear un nuevo scenario.
     * Solo accesible por administradores.
     *
     * @param dto los datos del nuevo scenario a registrar
     * @return una respuesta HTTP con el objeto ScenarioDTO del scenario recién creado, incluyendo su ID generado
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Crear scenario", description = "Crea un nuevo scenario. Requiere ADMINISTRADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Scenario creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado (requiere ROLE_ADMIN)")
    })
    /**
     * Método público.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object
     */
    public ResponseEntity<ScenarioDTO> create(@Valid @RequestBody ScenarioDTO dto) {
        ScenarioDTO creado = scenarioService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/scenarios/{id} — Actualizar un scenario.
     * Solo accesible por administradores.
     *
     * @param id el identificador único del scenario a modificar
     * @param dto un objeto con los datos actualizados del scenario
     * @return una respuesta HTTP con el objeto ScenarioDTO reflejando los cambios
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Actualizar scenario", description = "Actualiza un scenario existente. Requiere ADMINISTRADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Scenario actualizado"),
        @ApiResponse(responseCode = "404", description = "Scenario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    public ResponseEntity<ScenarioDTO> update(
            @PathVariable Integer id,
            @Valid @RequestBody ScenarioDTO dto) {
        ScenarioDTO actualizado = scenarioService.update(id, dto);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * DELETE /api/scenarios/{id} — Eliminar (soft delete) un scenario.
     * Solo accesible por administradores.
     *
     * @param id el identificador único del scenario a desactivar/eliminar
     * @return una respuesta HTTP 204 (Sin contenido) confirmando la eliminación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    @Operation(summary = "Eliminar scenario", description = "Soft delete de un scenario. Requiere ADMINISTRADOR")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Scenario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Scenario no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    /**
     * Método público.
     *
     * @return a {@link org.springframework.http.ResponseEntity} object
     */
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        scenarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
