package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that records a driving infraction committed during a simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "infraction")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Infraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idInfraccion;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "applied_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal penalizacionAplicada;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decision_id")
    private Decision decision;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "traffic_rule_id", nullable = false)
    private TrafficRule trafficRule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "severity_level_id", nullable = false)
    private SeverityLevel severityLevel;
}
