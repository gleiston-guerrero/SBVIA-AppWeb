package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * JPA entity that stores the feedback issued for a practice.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "feedback")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idRetroalimentacion;

    @Column(name = "comment", nullable = false, length = 1000)
    private String comentario;

    @Column(name = "recommendation", length = 1000)
    private String recomendacion;

    @Column(name = "source", nullable = false, length = 50)
    @Builder.Default
    private String origen = "SISTEMA";

    @Column(name = "generated_at", nullable = false)
    @Builder.Default
    private Instant fechaGeneracion = Instant.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "simulation_id", nullable = false)
    private Simulation simulation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "road_behavior_id")
    private DrivingBehavior drivingBehavior;
}
