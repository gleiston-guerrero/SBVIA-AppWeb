package com.sbvia.backend.controller;

import com.sbvia.backend.dto.BackupRequestDTO;
import com.sbvia.backend.model.Backup;
import com.sbvia.backend.service.BackupService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BackupControllerTest {

    @Mock
    private BackupService backupService;

    @InjectMocks
    private BackupController controller;

    @Test
    void testList() {
        when(backupService.getAll()).thenReturn(List.of(new Backup()));
        List<Backup> res = controller.list();
        assertFalse(res.isEmpty());
    }

    @Test
    void testCreate() {
        when(backupService.generateBackup(any(), any())).thenReturn(new Backup());
        Backup res = controller.generate(new BackupRequestDTO());
        assertNotNull(res);
    }

    @Test
    void testDownload() throws IOException {
        File tempFile = File.createTempFile("test", ".backup");
        tempFile.deleteOnExit();
        when(backupService.getFile(1L)).thenReturn(tempFile);
        
        ResponseEntity<Resource> res = controller.download(1L);
        assertEquals(200, res.getStatusCode().value());
        assertNotNull(res.getBody());
    }
    
    @Test
    void testDownloadNotFound() {
        File nonExistentFile = new File("doesnotexist12345.backup");
        when(backupService.getFile(1L)).thenReturn(nonExistentFile);
        
        ResponseEntity<Resource> res = controller.download(1L);
        assertEquals(404, res.getStatusCode().value());
    }

    @Test
    void testDelete() {
        doNothing().when(backupService).deleteBackup(1L);
        ResponseEntity<Void> res = controller.delete(1L);
        assertEquals(204, res.getStatusCode().value());
    }
}
