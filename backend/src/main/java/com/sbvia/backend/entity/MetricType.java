package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

/**
 * JPA entity that defines a type of performance metric.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "metric_type")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class MetricType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idTipoMetrica;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "unit", length = 50)
    private String unidadMedida;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "min_value")
    private BigDecimal valorMinimo;

    @Column(name = "max_value")
    private BigDecimal valorMaximo;
}
