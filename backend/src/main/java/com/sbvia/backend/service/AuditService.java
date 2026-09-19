package com.sbvia.backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.sbvia.backend.entity.AuditLog;
import com.sbvia.backend.repository.AuditLogRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>AuditService class.</p>
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository repository;

    /**
     * Searches for audit log entries that match the given optional filters, newest first.
     * A {@code null} or empty filter value means that criterion is not applied.
     *
     * @param table the exact table name to filter by, or {@code null}/empty to match any table
     * @param operation the exact operation to filter by, or {@code null}/empty to match any operation
     * @param user a case-insensitive partial match on the application user, or {@code null}/empty to match any user
     * @param startDate the minimum creation date, inclusive, or {@code null} for no lower bound
     * @param endDate the maximum creation date, inclusive, or {@code null} for no upper bound
     * @return the matching audit log entries ordered by creation date descending
     */
    public List<AuditLog> getAuditLogs(String table, String operation, String user, LocalDateTime startDate, LocalDateTime endDate) {
        Specification<AuditLog> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (table != null && !table.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("tableName"), table));
            }
            if (operation != null && !operation.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("operation"), operation));
            }
            if (user != null && !user.isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("appUser")), "%" + user.toLowerCase() + "%"));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * Builds a PDF report with the audit log entries matching the given optional filters.
     * The report lists the applied filters and a table with each entry's date, table,
     * operation, user, and previous and new data.
     *
     * @param table the exact table name to filter by, or {@code null}/empty to match any table
     * @param operation the exact operation to filter by, or {@code null}/empty to match any operation
     * @param user a case-insensitive partial match on the application user, or {@code null}/empty to match any user
     * @param startDate the minimum creation date, inclusive, or {@code null} for no lower bound
     * @param endDate the maximum creation date, inclusive, or {@code null} for no upper bound
     * @return a byte array containing the generated PDF report
     */
    public byte[] generatePdfReport(String table, String operation, String user, LocalDateTime startDate, LocalDateTime endDate) {
        List<AuditLog> records = getAuditLogs(table, operation, user, startDate, endDate);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Título
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph titulo = new Paragraph("SBVIA - Reporte de Auditoría de Base de Datos", fontTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(20f);
            document.add(titulo);
            
            // Subtítulo con filtros
            Font fontSub = FontFactory.getFont(FontFactory.HELVETICA, 10);
            document.add(new Paragraph("Filtros aplicados:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
            document.add(new Paragraph("Tabla: " + (table != null ? table : "Todas") + 
                                       " | Operación: " + (operation != null ? operation : "Todas") + 
                                       " | User: " + (user != null ? user : "Todos"), fontSub));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), fontSub));
            document.add(new Paragraph("\n"));

            // Tabla
            PdfPTable pdfTable = new PdfPTable(6);
            pdfTable.setWidthPercentage(100);
            pdfTable.setWidths(new float[]{1.5f, 2f, 1.5f, 2.5f, 3f, 3f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            String[] headers = {"Fecha", "Tabla", "Operación", "User", "Datos Antiguos", "Datos Nuevos"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(new java.awt.Color(200, 200, 200));
                cell.setPadding(5);
                pdfTable.addCell(cell);
            }

            Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            for (AuditLog log : records) {
                pdfTable.addCell(new Phrase(log.getCreatedAt() != null ? log.getCreatedAt().format(dtf) : "", rowFont));
                pdfTable.addCell(new Phrase(log.getTableName() != null ? log.getTableName() : "", rowFont));
                pdfTable.addCell(new Phrase(log.getOperation() != null ? log.getOperation() : "", rowFont));
                pdfTable.addCell(new Phrase(log.getAppUser() != null ? log.getAppUser() : (log.getDbUser() != null ? log.getDbUser() : ""), rowFont));
                
                String ant = log.getPreviousData() != null ? log.getPreviousData() : "-";
                String nue = log.getNewData() != null ? log.getNewData() : "-";
                // Truncar si es muy largo para el PDF
                if(ant.length() > 200) ant = ant.substring(0, 197) + "...";
                if(nue.length() > 200) nue = nue.substring(0, 197) + "...";

                pdfTable.addCell(new Phrase(ant, rowFont));
                pdfTable.addCell(new Phrase(nue, rowFont));
            }

            document.add(pdfTable);
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }
}
