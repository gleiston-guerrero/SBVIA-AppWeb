package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that records a driving decision taken during a simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "decision")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Decision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idDecision;

    @Column(name = "action_taken", nullable = false, length = 255)
    private String accionRealizada;

    @Column(name = "result", nullable = false, length = 255)
    private String resultado;

    @Column(name = "reaction_time_ms")
    private Integer tiempoReaccionMs;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @Column(name = "position_x")
    private BigDecimal posicionX;

    @Column(name = "position_y")
    private BigDecimal posicionY;

    @Column(name = "note", length = 500)
    private String observacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_event_id")
    private RoadEvent roadEvent;
}
