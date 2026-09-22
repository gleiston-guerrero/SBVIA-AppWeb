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

/**
 * REST endpoints that expose the audit log to administrators.
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/auditoria")
@RequiredArgsConstructor
public class AuditController {
    private final AuditService auditService;

    /**
     * GET /api/auditoria - List audit records.
     * Allows querying the audit log with optional filters (Admin only).
     *
     * @param tabla name of the affected table to filter by
     * @param operation type of operation performed (INSERT, UPDATE, DELETE)
     * @param user name or identifier of the user who performed the action
     * @param endDate end date and time of the search range
     * @return an HTTP response with the list of AuditLog objects
     * @param startDate a {@link java.time.LocalDateTime} object
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<AuditLog>> getAuditLogs(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        List<AuditLog> registros = auditService.getAuditLogs(tabla, operation, user, startDate, endDate);
        return ResponseEntity.ok(registros);
    }

    /**
     * GET /api/auditoria/reporte/pdf - Download the audit PDF report.
     * Generates a PDF file with the audit log records matching the given filters (Admin only).
     *
     * @param tabla name of the affected table to filter by
     * @param operation type of operation performed
     * @param user name or identifier of the user
     * @param endDate fecha y hora final
     * @return an HTTP response containing the PDF file as a byte array for download
     * @param startDate a {@link java.time.LocalDateTime} object
     */
    @GetMapping("/reporte/pdf")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<byte[]> downloadPdfReport(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String user,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        
        byte[] pdfBytes = auditService.generatePdfReport(tabla, operation, user, startDate, endDate);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_auditoria.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
