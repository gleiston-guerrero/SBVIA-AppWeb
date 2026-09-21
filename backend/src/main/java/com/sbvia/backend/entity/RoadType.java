package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA entity that defines a type of road used by a scenario.
 *
 * @author Keitho_
 */
@Entity
@Table(name = "tipo_via")
@Data
@AllArgsConstructor
@Builder
@lombok.NoArgsConstructor
public class RoadType {
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
