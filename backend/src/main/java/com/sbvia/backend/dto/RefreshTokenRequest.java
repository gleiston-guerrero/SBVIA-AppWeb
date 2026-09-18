package com.sbvia.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para la solicitud de refresh token.
 *
 * @author Keitho_
 */
@Data
public class RefreshTokenRequest {
    /** Default constructor for RefreshTokenRequest. */
    public RefreshTokenRequest() {}

    @NotBlank(message = "El refresh token es obligatorio")
    private String refreshToken;
}
