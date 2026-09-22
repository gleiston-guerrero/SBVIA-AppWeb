package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a type of road used by a scenario.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "road_type")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class RoadType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idTipoVia;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "reference_speed_kmh")
    private Integer velocidadReferencialKmh;
}
