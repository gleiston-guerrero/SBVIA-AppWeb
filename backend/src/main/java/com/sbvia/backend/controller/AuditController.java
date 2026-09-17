package com.sbvia.backend.controller;

import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuditController {

    private final AuditService auditService;

    /**
     * GET /api/auditoria — Listar registros de auditoría.
     * Permite consultar la bitácora con filtros opcionales (Solo Admin).
     *
     * @param tabla name de la tabla afectada a filtrar
     * @param operation tipo de operación realizada (INSERT, UPDATE, DELETE)
     * @param user name o identificador del user que realizó la acción
     * @param fechaInicio fecha y hora de inicio para el rango de búsqueda
     * @param endDate fecha y hora de fin para el rango de búsqueda
     * @return una respuesta HTTP con la lista de objetos AuditLog
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<AuditLog> registros = auditService.getAuditLogs(tabla, operation, user, fechaInicio, endDate);
        return ResponseEntity.ok(registros);
    }

    /**
     * GET /api/auditoria/reporte/pdf — Descargar reporte PDF de auditoría.
     * Genera un archivo PDF con los registros de la bitácora según los filtros dados (Solo Admin).
     *
     * @param tabla name de la tabla afectada a filtrar
     * @param operation tipo de operación realizada
     * @param user name o identificador del user
     * @param fechaInicio fecha y hora inicial
     * @param endDate fecha y hora final
     * @return una respuesta HTTP que contiene el archivo PDF como array de bytes para su descarga
     */
    @GetMapping("/reporte/pdf")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<byte[]> downloadPdfReport(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        byte[] pdfBytes = auditService.generatePdfReport(tabla, operation, user, fechaInicio, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_auditoria.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
