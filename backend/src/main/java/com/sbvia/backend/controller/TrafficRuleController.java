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
     * GET /api/reglas-transito — Listar reglas de tránsito.
     * Devuelve todas las reglas de evaluación configuradas en el sistema.
     *
     * @return lista completa de objetos TrafficRuleDTO
     */
    @GetMapping
    @Operation(summary = "Listar reglas de tránsito")
    public List<TrafficRuleDTO> list() { return trafficRuleService.list(); }

    /**
     * POST /api/reglas-transito — Crear nueva regla.
     * Registra una nueva regla de tránsito en el sistema.
     *
     * @param dto el objeto que contiene los datos de la nueva regla
     * @return una respuesta HTTP con el TrafficRuleDTO recién creado
     */
    @PostMapping
    @Operation(summary = "Registrar una regla de tránsito")
    public ResponseEntity<TrafficRuleDTO> create(@Valid @RequestBody TrafficRuleDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(trafficRuleService.create(dto));
    }

    /**
     * PUT /api/reglas-transito/{id} — Actualizar regla.
     * Modifica los parámetros de una regla de tránsito existente.
     *
     * @param id el identificador de la regla a actualizar
     * @param dto los nuevos datos de la regla
     * @return el objeto TrafficRuleDTO con los datos actualizados
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar una regla de tránsito")
    public TrafficRuleDTO update(@PathVariable Integer id, @Valid @RequestBody TrafficRuleDTO dto) {
        return trafficRuleService.update(id, dto);
    }

    /**
     * DELETE /api/reglas-transito/{id} — Eliminar regla.
     * Elimina físicamente o lógicamente una regla de tránsito del sistema.
     *
     * @param id el identificador de la regla a eliminar
     * @return una respuesta HTTP sin contenido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una regla de tránsito")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        trafficRuleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
