package com.sbvia.backend.controller;

import com.sbvia.backend.dto.TrafficRuleDTO;
import com.sbvia.backend.service.TrafficRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints that manage the traffic rules used by the simulator.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/reglas-transito")
@RequiredArgsConstructor
@Tag(name = "Reglas de tránsito", description = "Administración de normativas del motor de evaluación")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
public class TrafficRuleController {
    private final TrafficRuleService trafficRuleService;

    /**
     * GET /api/reglas-transito - List traffic rules.
     * Returns every assessment rule configured in the system.
     *
     * @return the full list of TrafficRuleDTO objects
     */
    @GetMapping
    @Operation(summary = "Listar reglas de tránsito")
    public List<TrafficRuleDTO> list() { return trafficRuleService.list(); }

    /**
     * POST /api/reglas-transito - Create a new rule.
     * Registers a new traffic rule in the system.
     *
     * @param dto the object holding the data of the new rule
     * @return an HTTP response with the newly created TrafficRuleDTO
     */
    @PostMapping
    @Operation(summary = "Registrar una regla de tránsito")
    public ResponseEntity<TrafficRuleDTO> create(@Valid @RequestBody TrafficRuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trafficRuleService.create(dto));
    }

    /**
     * PUT /api/reglas-transito/{id} - Update a rule.
     * Updates the parameters of an existing traffic rule.
     *
     * @param id the identifier of the rule to update
     * @param dto the new rule data
     * @return the TrafficRuleDTO with the updated data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una regla de tránsito")
    public TrafficRuleDTO update(@PathVariable Integer id, @Valid @RequestBody TrafficRuleDTO dto) {
        return trafficRuleService.update(id, dto);
    }

    /**
     * DELETE /api/reglas-transito/{id} - Delete a rule.
     * Deletes a traffic rule from the system, physically or logically.
     *
     * @param id the identifier of the rule to delete
     * @return an HTTP response with no content
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una regla de tránsito")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        trafficRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
