package com.sbvia.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "backup")
@Getter
@Setter
public class Backup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "size_bytes")
    private Long sizeBytes;

    @Column(name = "details")
    private String details;

    @Column(name = "mode", nullable = false)
    private String mode;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "comment")
    private String comment;
}
