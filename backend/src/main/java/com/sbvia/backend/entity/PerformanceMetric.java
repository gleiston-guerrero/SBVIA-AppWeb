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
@Table(name = "metrica_desempeno")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class PerformanceMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_metrica")
    private Integer idMetrica;

    @Column(name = "valor", nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "fecha_hora", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @Column(name = "observacion", length = 255)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_simulacion", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tipo_metrica", nullable = false)
    private MetricType metricType;
}
