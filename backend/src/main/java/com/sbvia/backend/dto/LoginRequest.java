package com.sbvia.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la petición de inicio de sesión.
 * Permite autenticarse mediante 'identificador' (que puede ser el name de user o el email electrónico).
 * Mantiene compatibilidad total con peticiones existentes que envían 'email'.
 *
 * @author Keitho_
 */
@Data
public class LoginRequest {
    private String identificador;
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Método público.
     *
     * @return a {@link java.lang.String} object
     */
    public String getIdentificador() {
        if (identificador != null && !identificador.isBlank()) {
            return identificador.trim();
        }
        if (email != null && !email.isBlank()) {
            return email.trim();
        }
        return "";
    }

    /**
     * Método público.
     *
     * @param identificador a {@link java.lang.String} object
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Método público.
     *
     * @return a {@link java.lang.String} object
     */
    public String getEmail() {
        return getIdentificador();
    }

    /**
     * Método público.
     *
     * @param email a {@link java.lang.String} object
     */
    public void setEmail(String email) {
        this.email = email;
        if (this.identificador == null || this.identificador.isBlank()) {
            this.identificador = email;
        }
    }
}
