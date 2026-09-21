package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/**
 * JPA entity that records a change made to a tracked table.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "audit_log")
@Getter
@Setter
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "table_name", nullable = false)
    private String tableName;

    @Column(name = "operation", nullable = false)
    private String operation;

    @Column(name = "db_user", nullable = false)
    private String dbUser;

    @Column(name = "app_user")
    private String appUser;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "previous_data", columnDefinition = "jsonb")
    private String previousData;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "new_data", columnDefinition = "jsonb")
    private String newData;
}
