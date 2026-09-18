package com.sbvia.backend.controller;

import com.sbvia.backend.model.Backup;
import com.sbvia.backend.service.BackupService;
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

/**
 * <p>BackupController class.</p>
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/respaldos")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
public class BackupController {

    private final BackupService backupService;

    /**
     * Método público.
     *
     * @param backupService a {@link com.sbvia.backend.service.BackupService} object
     */
    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * GET /api/respaldos — Listar respaldos.
     * Retorna un listado de todos los respaldos generados en el sistema (Solo Admin).
     *
     * @return una lista de entidades Backup
     */
    @GetMapping
    public List<Backup> list() {
        return backupService.getAll();
    }

    /**
     * POST /api/respaldos/generar — Generar respaldo manual.
     * Dispara la creación de un nuevo backup de la base de datos bajo demanda (Solo Admin).
     *
     * @param request el objeto con opciones de configuración para el respaldo
     * @return el registro del Backup generado
     */
    @PostMapping("/generar")
    public Backup generate(@RequestBody com.sbvia.backend.dto.BackupRequestDTO request) {
        return backupService.generateBackup(request, "MANUAL");
    }

    /**
     * GET /api/respaldos/descargar/{id} — Descargar archivo de respaldo.
     * Permite obtener el archivo físico (.sql, .dump) de un backup existente (Solo Admin).
     *
     * @param id el identificador del respaldo a descargar
     * @return una respuesta HTTP con el archivo como recurso descargable, o 404 si no existe
     */
    @GetMapping("/descargar/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        File file = backupService.getFile(id);
        
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
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        backupService.deleteBackup(id);
        return ResponseEntity.noContent().build();
    }
}
