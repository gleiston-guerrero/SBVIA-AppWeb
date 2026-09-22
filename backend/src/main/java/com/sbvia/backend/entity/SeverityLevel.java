package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines how severe an infraction is.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "severity_level")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class SeverityLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idNivelGravedad;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "severity_value", nullable = false)
    private Integer value;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "penalty_multiplier", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private java.math.BigDecimal multiplicadorPenalizacion = java.math.BigDecimal.ONE;
}
