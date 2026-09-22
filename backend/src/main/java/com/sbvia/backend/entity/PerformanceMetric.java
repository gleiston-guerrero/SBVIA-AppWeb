package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that stores a single measured performance value.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "performance_metric")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class PerformanceMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idMetrica;

    @Column(name = "metric_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @Column(name = "note", length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metric_type_id", nullable = false)
    private MetricType metricType;
}
