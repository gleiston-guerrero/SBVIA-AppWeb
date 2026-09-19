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
     * Returns the login identifier, trimming whitespace and falling back to the
     * email address when no identifier is set.
     *
     * @return the trimmed identifier used for authentication, or an empty string
     *         if neither the identifier nor the email is set
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
     * Sets the login identifier (username or email) used for authentication.
     *
     * @param identificador the identifier to use for login
     */
    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    /**
     * Returns the email address used for login, resolved through the shared
     * identifier logic for compatibility with legacy requests.
     *
     * @return the email address used for authentication
     */
    public String getEmail() {
        return getIdentificador();
    }

    /**
     * Sets the email address used for login and backfills the identifier with
     * it when no identifier is set.
     *
     * @param email the email address to use for login
     */
    public void setEmail(String email) {
        this.email = email;
        if (this.identificador == null || this.identificador.isBlank()) {
            this.identificador = email;
        }
    }
}
