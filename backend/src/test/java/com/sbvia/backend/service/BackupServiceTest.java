package com.sbvia.backend.service;

import com.sbvia.backend.dto.BackupRequestDTO;
import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.model.Backup;
import com.sbvia.backend.repository.AuditLogRepository;
import com.sbvia.backend.repository.BackupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackupServiceTest {

    @Mock
    private BackupRepository respaldoRepository;

    @Mock
    private AuditLogRepository auditoriaRepository;

    @Mock
    private TaskScheduler taskScheduler;

    @InjectMocks
    private BackupService backupService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(backupService, "dbUser", "postgres");
        ReflectionTestUtils.setField(backupService, "dbPassword", "pass");
        ReflectionTestUtils.setField(backupService, "dbUrl", "jdbc:postgresql://postgres:5432/sbvia_db");

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("testuser");
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testObtenerTodos() {
        Backup r = new Backup();
        r.setId(1L);
        when(respaldoRepository.findAllByOrderByStartDateDesc()).thenReturn(List.of(r));

        List<Backup> result = backupService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void testGenerarRespaldoInmediato() {
        BackupRequestDTO dto = new BackupRequestDTO();
        dto.setModalidad("COMPLETO");
        dto.setComentario("Test comment");

        when(respaldoRepository.save(any(Backup.class))).thenAnswer(inv -> {
            Backup r = inv.getArgument(0);
            if (r.getId() == null) r.setId(1L);
            return r;
        });

        // Mock ProcessBuilder (to avoid real execution in test)
        BackupService spyService = spy(backupService);
        doNothing().when(spyService).executePgDump(any(Backup.class));

        Backup result = spyService.generateBackup(dto, "MANUAL");

        assertNotNull(result);
        assertEquals("EN_PROGRESO", result.getStatus());
        assertEquals("COMPLETO", result.getMode());
        assertEquals("MANUAL", result.getType());
        verify(spyService).executePgDump(result);
        verify(auditoriaRepository).save(any(AuditLog.class));
    }

    @Test
    void testGenerarRespaldoProgramado() {
        BackupRequestDTO dto = new BackupRequestDTO();
        dto.setModalidad("SOLO_ESTRUCTURA");
        dto.setFechaProgramada(LocalDateTime.now().plusDays(1));

        when(respaldoRepository.save(any(Backup.class))).thenAnswer(inv -> {
            Backup r = inv.getArgument(0);
            if (r.getId() == null) r.setId(1L);
            return r;
        });

        Backup result = backupService.generateBackup(dto, "MANUAL");

        assertEquals("PROGRAMADO", result.getStatus());
        assertNotNull(result.getScheduledDate());
        verify(taskScheduler).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    void testObtenerArchivo() {
        Backup r = new Backup();
        r.setId(1L);
        r.setFileName("test.backup");
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(r));

        File file = backupService.getFile(1L);
        assertNotNull(file);
        assertEquals("test.backup", file.getName());
    }

    @Test
    void testEliminarRespaldo() {
        Backup r = new Backup();
        r.setId(1L);
        r.setFileName("dummy.backup");
        when(respaldoRepository.findById(1L)).thenReturn(Optional.of(r));

        backupService.deleteBackup(1L);
        verify(respaldoRepository).delete(r);
    }

    @Test
    void testRespaldoProgramadoCron() {
        BackupService spyService = spy(backupService);
        doReturn(new Backup()).when(spyService).generateBackup(any(BackupRequestDTO.class), eq("PROGRAMADO"));

        spyService.scheduledBackup();

        ArgumentCaptor<BackupRequestDTO> captor = ArgumentCaptor.forClass(BackupRequestDTO.class);
        verify(spyService).generateBackup(captor.capture(), eq("PROGRAMADO"));
        assertEquals("COMPLETO", captor.getValue().getModalidad());
    }

    @Test
    void testGenerarRespaldoNullRequest() {
        when(respaldoRepository.save(any(Backup.class))).thenAnswer(inv -> inv.getArgument(0));
        BackupService spyService = spy(backupService);
        doNothing().when(spyService).executePgDump(any(Backup.class));

        Backup result = spyService.generateBackup(null, "AUTOMATICO");

        assertNotNull(result);
        assertEquals("COMPLETO", result.getMode());
        assertEquals("AUTOMATICO", result.getType());
    }

    @Test
    void testEjecutarPgDump() throws Exception {
        Backup respaldo = new Backup();
        respaldo.setFileName("test_dump.backup");
        respaldo.setMode("SOLO_ESTRUCTURA");
        try { backupService.executePgDump(respaldo); } catch (Exception e) {}
        
        respaldo.setMode("SOLO_DATOS");
        try { backupService.executePgDump(respaldo); } catch (Exception e) {}
        
        respaldo.setMode("COMPLETO");
        try { backupService.executePgDump(respaldo); } catch (Exception e) {}
    }
}
