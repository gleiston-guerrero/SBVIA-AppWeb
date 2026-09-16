package com.sbvia.backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tipo_vehiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_vehiculo")
    private Integer idTipoVehiculo;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "descripcion", length = 255)
    private String description;

    @Column(name = "requiere_licencia", length = 10)
    private String licenciaRequerida;
}
