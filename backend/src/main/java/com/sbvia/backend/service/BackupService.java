package com.sbvia.backend.service;

import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.model.Backup;
import com.sbvia.backend.dto.BackupRequestDTO;
import com.sbvia.backend.repository.AuditLogRepository;
import com.sbvia.backend.repository.BackupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

@Service
public class BackupService {

    private static final Logger logger = LoggerFactory.getLogger(BackupService.class);

    private final BackupRepository backupRepository;
    private final AuditLogRepository auditLogRepository;
    private final TaskScheduler taskScheduler;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${DB_URL:jdbc:postgresql://postgres:5432/sbvia_db}")
    private String dbUrl;

    private final String backupDir = "/app/backups";

    public BackupService(BackupRepository backupRepository, 
                           AuditLogRepository auditLogRepository,
                           TaskScheduler taskScheduler) {
        this.backupRepository = backupRepository;
        this.auditLogRepository = auditLogRepository;
        this.taskScheduler = taskScheduler;
        
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<Backup> getAll() {
        return backupRepository.findAllByOrderByStartDateDesc();
    }

    public Backup generateBackup(BackupRequestDTO request, String type) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "sbvia_backup_" + timestamp + ".backup";

        Backup backup = new Backup();
        backup.setFileName(filename);
        backup.setType(type);
        backup.setMode(request != null && request.getModalidad() != null ? request.getModalidad() : "COMPLETO");
        backup.setComment(request != null ? request.getComentario() : "");
        backup.setStartDate(LocalDateTime.now());
        
        if (request != null && request.getFechaProgramada() != null && request.getFechaProgramada().isAfter(LocalDateTime.now())) {
            backup.setStatus("PROGRAMADO");
            backup.setScheduledDate(request.getFechaProgramada());
            backup = backupRepository.save(backup);
            
            final Backup finalBackup = backup;
            taskScheduler.schedule(() -> {
                finalBackup.setStatus("EN_PROGRESO");
                backupRepository.save(finalBackup);
                executePgDump(finalBackup);
            }, Date.from(request.getFechaProgramada().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            backup.setStatus("EN_PROGRESO");
            backup = backupRepository.save(backup);
            executePgDump(backup);
        }

        // Registrar en Auditoría
        registerAuditLog(backup);

        return backup;
    }

    private void registerAuditLog(Backup backup) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth != null ? auth.getName() : "SISTEMA";

            AuditLog auditLog = new AuditLog();
            auditLog.setTableName("backup");
            auditLog.setOperation("BACKUP");
            auditLog.setDbUser(dbUser);
            auditLog.setAppUser(currentUser);
            auditLog.setNewData("{\"archivo\": \"" + backup.getFileName() + "\", \"modalidad\": \"" + backup.getMode() + "\", \"tipo\": \"" + backup.getType() + "\"}");
            
            auditLogRepository.save(auditLog);
        } catch (Exception e) {
            logger.error("Error al registrar auditoría de respaldo", e);
        }
    }

    @Async
    protected void executePgDump(Backup backup) {
        String outputPath = backupDir + "/" + backup.getFileName();
        
        String host = "postgres"; 
        String dbName = "sbvia_db";
        
        if (dbUrl.contains("://")) {
            String cleanUrl = dbUrl.substring(dbUrl.indexOf("://") + 3);
            if (cleanUrl.contains("/")) {
                String[] parts = cleanUrl.split("/");
                host = parts[0].split(":")[0];
                dbName = parts[1].split("\\?")[0];
            }
        }

        List<String> command = new ArrayList<>(List.of(
            "pg_dump",
            "-h", host,
            "-U", dbUser,
            "-d", dbName,
            "-F", "c",
            "-f", outputPath
        ));

        if ("SOLO_ESTRUCTURA".equals(backup.getMode())) {
            command.add("-s");
        } else if ("SOLO_DATOS".equals(backup.getMode())) {
            command.add("-a");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Map<String, String> env = processBuilder.environment();
        env.put("PGPASSWORD", dbPassword);

        try {
            logger.info("Iniciando respaldo de base de datos: {}", outputPath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            backup.setEndDate(LocalDateTime.now());

            if (exitCode == 0) {
                File file = new File(outputPath);
                if (file.exists()) {
                    backup.setSizeBytes(file.length());
                    backup.setStatus("COMPLETADO");
                    backup.setDetails("Respaldo completado exitosamente.");
                    logger.info("Respaldo completado: {}", outputPath);
                } else {
                    backup.setStatus("FALLIDO");
                    backup.setDetails("Archivo no encontrado tras finalizar pg_dump.");
                }
            } else {
                backup.setStatus("FALLIDO");
                backup.setDetails("pg_dump devolvió código de error: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            backup.setStatus("FALLIDO");
            backup.setEndDate(LocalDateTime.now());
            backup.setDetails("Excepción: " + e.getMessage());
            logger.error("Excepción durante respaldo", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        backupRepository.save(backup);
    }

    public File getFile(Long id) {
        Backup backup = backupRepository.findById(id).orElseThrow(() -> new RuntimeException("Respaldo no encontrado"));
        return new File(backupDir + "/" + backup.getFileName());
    }

    public void deleteBackup(Long id) {
        Backup backup = backupRepository.findById(id).orElseThrow(() -> new RuntimeException("Respaldo no encontrado"));
        File file = new File(backupDir + "/" + backup.getFileName());
        if (file.exists()) {
            file.delete();
        }
        backupRepository.delete(backup);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void scheduledBackup() {
        logger.info("Ejecutando respaldo automático programado...");
        BackupRequestDTO dto = new BackupRequestDTO();
        dto.setModalidad("COMPLETO");
        dto.setComentario("Respaldo diario automático");
        generateBackup(dto, "PROGRAMADO");
    }
}
