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
 * REST controller for scenario CRUD.
 * Shows Spring Data JPA with pagination and role-based security.
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
     * GET /api/scenarios - List scenarios with pagination and optional filters.
     * Accessible to any authenticated user (ROLE_USER, ROLE_ADMIN, ROLE_INSTRUCTOR).
     * Filtros: roadType, difficultyLevel, clima (todos opcionales).
     *
     * @param roadType optional filter by road type (urban, rural, and so on)
     * @param difficultyLevel optional filter by difficulty level (1, 2, 3...)
     * @param clima optional filter by weather conditions (clear, rain, and so on)
     * @param pageable object holding the pagination settings (size, page, sorting)
     * @return a page of ScenarioDTO objects matching the search criteria
     */
    @GetMapping
    @Operation(summary = "Listar scenarios", description = "Lista scenarios activos con paginación y filtros opcionales (roadType, difficultyLevel, clima)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista devuelta exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    public ResponseEntity<Page<ScenarioDTO>> list(
            @RequestParam(required = false) String roadType,
            @RequestParam(required = false) Integer difficultyLevel,
            @RequestParam(required = false) String clima,
            Pageable pageable) {
        Page<ScenarioDTO> page = scenarioService.findFiltered(roadType, difficultyLevel, clima, pageable);
        return ResponseEntity.ok(page);
    }

    /**
     * GET /api/scenarios/{id} - Get one specific scenario.
     * Accessible to any authenticated user.
     *
     * @param id the unique identifier of the requested scenario
     * @return an HTTP response with the ScenarioDTO for that ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener scenario por ID", description = "Devuelve los detalles de un scenario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Scenario devuelto exitosamente"),
        @ApiResponse(responseCode = "404", description = "Scenario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    /**
     * Returns the scenario identified by the given ID. Any authenticated user
     * may call this endpoint.
     *
     * @param id the unique identifier of the scenario to retrieve
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.ScenarioDTO} that matches the ID
     */
    public ResponseEntity<ScenarioDTO> findById(@PathVariable Integer id) {
        ScenarioDTO dto = scenarioService.findById(id);
        return ResponseEntity.ok(dto);
    }

    /**
     * POST /api/scenarios - Create a new scenario.
     * Accessible to administrators only.
     *
     * @param dto the data of the new scenario to register
     * @return an HTTP response with the ScenarioDTO of the newly created scenario, including its generated ID
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
     * Creates a new scenario from the validated data and returns it with its
     * generated ID and HTTP 201 status. Only users with the ADMINISTRADOR
     * authority may call this endpoint.
     *
     * @param dto the validated scenario data used to create the new scenario
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.ScenarioDTO} of the newly created scenario
     */
    public ResponseEntity<ScenarioDTO> create(@Valid @RequestBody ScenarioDTO dto) {
        ScenarioDTO creado = scenarioService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    /**
     * PUT /api/scenarios/{id} - Update a scenario.
     * Accessible to administrators only.
     *
     * @param id the unique identifier of the scenario to update
     * @param dto an object with the updated scenario data
     * @return an HTTP response with the ScenarioDTO reflecting the changes
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
     * DELETE /api/scenarios/{id} - Delete a scenario (soft delete).
     * Accessible to administrators only.
     *
     * @param id the unique identifier of the scenario to deactivate or delete
     * @return an HTTP 204 (No Content) response confirming the deletion
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
     * Soft-deletes the scenario identified by the given ID and returns HTTP
     * 204 (No Content) on success. Only users with the ADMINISTRADOR authority
     * may call this endpoint.
     *
     * @param id the unique identifier of the scenario to soft-delete
     * @return a {@link org.springframework.http.ResponseEntity} with HTTP 204 (No Content) confirming the deletion
     */
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        scenarioService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
