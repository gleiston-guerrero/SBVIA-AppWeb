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
 * REST endpoints for creating, listing and downloading database backups.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/respaldos")
@PreAuthorize("hasAuthority('ADMINISTRADOR')")
public class BackupController {

    private final BackupService backupService;

    /**
     * Constructs the backup controller with the service that handles backup
     * listing, generation, file download and deletion.
     *
     * @param backupService the {@link com.sbvia.backend.service.BackupService} used to manage the backups
     */
    public BackupController(BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * GET /api/respaldos — Listar respaldos.
     * Returns a list of every backup created in the system (Admin only).
     *
     * @return a list of Backup entities
     */
    @GetMapping
    public List<Backup> list() {
        return backupService.getAll();
    }

    /**
     * POST /api/respaldos/generar - Create a manual backup.
     * Triggers the creation of a new database backup on demand (Admin only).
     *
     * @param request the object with the configuration options for the backup
     * @return the record of the created backup
     */
    @PostMapping("/generar")
    public Backup generate(@RequestBody com.sbvia.backend.dto.BackupRequestDTO request) {
        return backupService.generateBackup(request, "MANUAL");
    }

    /**
     * GET /api/respaldos/descargar/{id} - Download a backup file.
     * Retrieves the physical file (.sql, .dump) of an existing backup (Admin only).
     *
     * @param id the identifier of the backup to download
     * @return an HTTP response with the file as a downloadable resource, or 404 if it does not exist
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
     * DELETE /api/respaldos/{id} - Delete a backup.
     * Deletes the database record and its physical file (Admin only).
     *
     * @param id the identifier of the backup to delete
     * @return an HTTP response with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        backupService.deleteBackup(id);
        return ResponseEntity.noContent().build();
    }
}
