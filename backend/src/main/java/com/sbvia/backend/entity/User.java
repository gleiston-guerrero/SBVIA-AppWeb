package com.sbvia.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer userId;

    @Column(name = "nombres", nullable = false, length = 255)
    private String firstName;

    @Column(name = "apellidos", nullable = false, length = 255)
    private String lastName;

    @Column(name = "nombre_usuario", unique = true, length = 100)
    private String username;

    @Column(name = "correo", nullable = false, unique = true, length = 255)
    private String email;

    @JsonIgnore
    @Column(name = "contrasena_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "telefono", length = 20)
    private String phone;

    @Column(name = "fecha_nacimiento")
    private LocalDate birthDate;

    @Column(name = "fecha_registro", nullable = false)
    @Builder.Default
    private LocalDate registrationDate = LocalDate.now();

    @Column(name = "ultimo_acceso")
    private Instant lastAccess;

    @Column(name = "intentos_fallidos", nullable = false)
    @Builder.Default
    private Integer failedAttempts = 0;

    @Column(name = "cuenta_bloqueada", nullable = false)
    @Builder.Default
    private boolean accountLocked = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_rol", nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_estado_usuario", nullable = false)
    @Builder.Default
    private UserState userState = new UserState();
}
