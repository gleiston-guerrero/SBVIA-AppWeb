package com.sbvia.backend.service;

import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.model.Respaldo;
import com.sbvia.backend.dto.BackupRequestDTO;
import com.sbvia.backend.repository.AuditLogRepository;
import com.sbvia.backend.repository.RespaldoRepository;
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
public class RespaldoService {

    private static final Logger logger = LoggerFactory.getLogger(RespaldoService.class);

    private final RespaldoRepository respaldoRepository;
    private final AuditLogRepository auditoriaRepository;
    private final TaskScheduler taskScheduler;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${DB_URL:jdbc:postgresql://postgres:5432/sbvia_db}")
    private String dbUrl;

    private final String backupDir = "/app/backups";

    public RespaldoService(RespaldoRepository respaldoRepository, 
                           AuditLogRepository auditoriaRepository,
                           TaskScheduler taskScheduler) {
        this.respaldoRepository = respaldoRepository;
        this.auditoriaRepository = auditoriaRepository;
        this.taskScheduler = taskScheduler;
        
        File dir = new File(backupDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public List<Respaldo> obtenerTodos() {
        return respaldoRepository.findAllByOrderByFechaInicioDesc();
    }

    public Respaldo generarRespaldo(BackupRequestDTO request, String tipo) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = "sbvia_backup_" + timestamp + ".backup";

        Respaldo respaldo = new Respaldo();
        respaldo.setNombreArchivo(filename);
        respaldo.setTipo(tipo);
        respaldo.setModalidad(request != null && request.getModalidad() != null ? request.getModalidad() : "COMPLETO");
        respaldo.setComentario(request != null ? request.getComentario() : "");
        respaldo.setFechaInicio(LocalDateTime.now());
        
        if (request != null && request.getFechaProgramada() != null && request.getFechaProgramada().isAfter(LocalDateTime.now())) {
            respaldo.setEstado("PROGRAMADO");
            respaldo.setFechaProgramada(request.getFechaProgramada());
            respaldo = respaldoRepository.save(respaldo);
            
            final Respaldo resFinal = respaldo;
            taskScheduler.schedule(() -> {
                resFinal.setEstado("EN_PROGRESO");
                respaldoRepository.save(resFinal);
                ejecutarPgDump(resFinal);
            }, Date.from(request.getFechaProgramada().atZone(ZoneId.systemDefault()).toInstant()));
        } else {
            respaldo.setEstado("EN_PROGRESO");
            respaldo = respaldoRepository.save(respaldo);
            ejecutarPgDump(respaldo);
        }

        // Registrar en Auditoría
        registrarAuditoria(respaldo);

        return respaldo;
    }

    private void registrarAuditoria(Respaldo respaldo) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String currentUser = auth != null ? auth.getName() : "SISTEMA";

            AuditLog auditoria = new AuditLog();
            auditoria.setTableName("respaldo");
            auditoria.setOperation("BACKUP");
            auditoria.setDbUser(dbUser);
            auditoria.setAppUser(currentUser);
            auditoria.setNewData("{\"archivo\": \"" + respaldo.getNombreArchivo() + "\", \"modalidad\": \"" + respaldo.getModalidad() + "\", \"tipo\": \"" + respaldo.getTipo() + "\"}");
            
            auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            logger.error("Error al registrar auditoría de respaldo", e);
        }
    }

    @Async
    protected void ejecutarPgDump(Respaldo respaldo) {
        String outputPath = backupDir + "/" + respaldo.getNombreArchivo();
        
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

        if ("SOLO_ESTRUCTURA".equals(respaldo.getModalidad())) {
            command.add("-s");
        } else if ("SOLO_DATOS".equals(respaldo.getModalidad())) {
            command.add("-a");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Map<String, String> env = processBuilder.environment();
        env.put("PGPASSWORD", dbPassword);

        try {
            logger.info("Iniciando respaldo de base de datos: {}", outputPath);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            respaldo.setEndDate(LocalDateTime.now());

            if (exitCode == 0) {
                File file = new File(outputPath);
                if (file.exists()) {
                    respaldo.setTamanioBytes(file.length());
                    respaldo.setEstado("COMPLETADO");
                    respaldo.setDetalles("Respaldo completado exitosamente.");
                    logger.info("Respaldo completado: {}", outputPath);
                } else {
                    respaldo.setEstado("FALLIDO");
                    respaldo.setDetalles("Archivo no encontrado tras finalizar pg_dump.");
                }
            } else {
                respaldo.setEstado("FALLIDO");
                respaldo.setDetalles("pg_dump devolvió código de error: " + exitCode);
            }

        } catch (IOException | InterruptedException e) {
            respaldo.setEstado("FALLIDO");
            respaldo.setEndDate(LocalDateTime.now());
            respaldo.setDetalles("Excepción: " + e.getMessage());
            logger.error("Excepción durante respaldo", e);
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        }

        respaldoRepository.save(respaldo);
    }

    public File obtenerArchivo(Long id) {
        Respaldo respaldo = respaldoRepository.findById(id).orElseThrow(() -> new RuntimeException("Respaldo no encontrado"));
        return new File(backupDir + "/" + respaldo.getNombreArchivo());
    }

    public void eliminarRespaldo(Long id) {
        Respaldo respaldo = respaldoRepository.findById(id).orElseThrow(() -> new RuntimeException("Respaldo no encontrado"));
        File file = new File(backupDir + "/" + respaldo.getNombreArchivo());
        if (file.exists()) {
            file.delete();
        }
        respaldoRepository.delete(respaldo);
    }

    @Scheduled(cron = "0 0 2 * * ?")
    public void respaldoProgramado() {
        logger.info("Ejecutando respaldo automático programado...");
        BackupRequestDTO dto = new BackupRequestDTO();
        dto.setModalidad("COMPLETO");
        dto.setComentario("Respaldo diario automático");
        generarRespaldo(dto, "PROGRAMADO");
    }
}
