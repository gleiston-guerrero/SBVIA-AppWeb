package com.sbvia.backend.controller;

import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuditControllerTest {

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuditController controller;

    @Test
    void testObtenerAuditoria() {
        when(auditService.getAuditLogs(any(), any(), any(), any(), any())).thenReturn(List.of(new AuditLog()));
        ResponseEntity<List<AuditLog>> res = controller.getAuditLogs(null, null, null, null, null);
        assertEquals(200, res.getStatusCode().value());
        assertFalse(res.getBody().isEmpty());
    }

    @Test
    void testDescargarReportePdf() {
        when(auditService.generatePdfReport(any(), any(), any(), any(), any())).thenReturn(new byte[]{1, 2, 3});
        ResponseEntity<byte[]> res = controller.downloadPdfReport(null, null, null, null, null);
        assertEquals(200, res.getStatusCode().value());
        assertEquals("application/pdf", res.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertNotNull(res.getBody());
    }
}
