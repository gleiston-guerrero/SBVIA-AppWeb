package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * <p>RoadType class.</p>
 *
 * @author Keitho_
 */
@Entity
@Table(name = "tipo_via")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoadType {
    /** Default constructor for RoadType. */
    public RoadType() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_via")
    private Integer idTipoVia;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "velocidad_referencial_kmh")
    private Integer velocidadReferencialKmh;
}
