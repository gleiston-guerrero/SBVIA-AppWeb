package com.sbvia.backend.controller;

import com.sbvia.backend.model.Respaldo;
import com.sbvia.backend.service.RespaldoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;

@RestController
@RequestMapping("/api/respaldos")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
public class RespaldoController {

    private final RespaldoService respaldoService;

    public RespaldoController(RespaldoService respaldoService) {
        this.respaldoService = respaldoService;
    }

    /**
     * GET /api/respaldos — Listar respaldos.
     * Retorna un listado de todos los respaldos generados en el sistema (Solo Admin).
     *
     * @return una lista de entidades Respaldo
     */
    @GetMapping
    public List<Respaldo> listar() {
        return respaldoService.obtenerTodos();
    }

    /**
     * POST /api/respaldos/generar — Generar respaldo manual.
     * Dispara la creación de un nuevo backup de la base de datos bajo demanda (Solo Admin).
     *
     * @param request el objeto con opciones de configuración para el respaldo
     * @return el registro del Respaldo generado
     */
    @PostMapping("/generar")
    public Respaldo generar(@RequestBody com.sbvia.backend.dto.RespaldoRequestDTO request) {
        return respaldoService.generarRespaldo(request, "MANUAL");
    }

    /**
     * GET /api/respaldos/descargar/{id} — Descargar archivo de respaldo.
     * Permite obtener el archivo físico (.sql, .dump) de un backup existente (Solo Admin).
     *
     * @param id el identificador del respaldo a descargar
     * @return una respuesta HTTP con el archivo como recurso descargable, o 404 si no existe
     */
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> descargar(@PathVariable Long id) {
        File file = respaldoService.obtenerArchivo(id);
        
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Resource resource = new FileSystemResource(file);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * DELETE /api/respaldos/{id} — Eliminar respaldo.
     * Borra el registro de la base de datos y su archivo físico correspondiente (Solo Admin).
     *
     * @param id el identificador del respaldo a eliminar
     * @return una respuesta HTTP sin contenido
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        respaldoService.eliminarRespaldo(id);
        return ResponseEntity.noContent().build();
    }
}
