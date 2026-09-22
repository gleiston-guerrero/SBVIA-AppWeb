package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * JPA entity that records an event that occurred on the road during a simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "road_event")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class RoadEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idEventoVial;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "risk_level", nullable = false)
    private Integer nivelRiesgo;

    @Column(name = "occurred_at", nullable = false)
    @Builder.Default
    private Instant fechaHora = Instant.now();

    @Column(name = "position_x")
    private BigDecimal posicionX;

    @Column(name = "position_y")
    private BigDecimal posicionY;

    @Column(name = "vehicle_speed_kmh")
    private BigDecimal velocidadVehiculoKmh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;
}
