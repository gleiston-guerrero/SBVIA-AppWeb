package com.sbvia.backend.controller;

import com.sbvia.backend.entity.BitacoraAuditoria;
import com.sbvia.backend.service.AuditoriaService;
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
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    /**
     * GET /api/auditoria — Listar registros de auditoría.
     * Permite consultar la bitácora con filtros opcionales (Solo Admin).
     *
     * @param tabla nombre de la tabla afectada a filtrar
     * @param operacion tipo de operación realizada (INSERT, UPDATE, DELETE)
     * @param usuario nombre o identificador del usuario que realizó la acción
     * @param fechaInicio fecha y hora de inicio para el rango de búsqueda
     * @param fechaFin fecha y hora de fin para el rango de búsqueda
     * @return una respuesta HTTP con la lista de objetos BitacoraAuditoria
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<List<BitacoraAuditoria>> obtenerAuditoria(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        List<BitacoraAuditoria> registros = auditoriaService.obtenerAuditoria(tabla, operacion, usuario, fechaInicio, fechaFin);
        return ResponseEntity.ok(registros);
    }

    /**
     * GET /api/auditoria/reporte/pdf — Descargar reporte PDF de auditoría.
     * Genera un archivo PDF con los registros de la bitácora según los filtros dados (Solo Admin).
     *
     * @param tabla nombre de la tabla afectada a filtrar
     * @param operacion tipo de operación realizada
     * @param usuario nombre o identificador del usuario
     * @param fechaInicio fecha y hora inicial
     * @param fechaFin fecha y hora final
     * @return una respuesta HTTP que contiene el archivo PDF como array de bytes para su descarga
     */
    @GetMapping("/reporte/pdf")
    @PreAuthorize("hasAuthority('ADMINISTRADOR')")
    public ResponseEntity<byte[]> descargarReportePdf(
            @RequestParam(required = false) String tabla,
            @RequestParam(required = false) String operacion,
            @RequestParam(required = false) String usuario,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        
        byte[] pdfBytes = auditoriaService.generarReportePdf(tabla, operacion, usuario, fechaInicio, fechaFin);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "reporte_auditoria.pdf");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }
}
