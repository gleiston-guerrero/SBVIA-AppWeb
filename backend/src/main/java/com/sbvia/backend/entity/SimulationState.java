package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines the lifecycle state of a simulation.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "simulation_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SimulationState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idEstadoSimulacion;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_final_state", nullable = false)
    @Builder.Default
    private boolean esEstadoFinal = false;
}
