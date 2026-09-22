package com.sbvia.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication response DTO.
 * The refreshToken field is used internally between service and controller, but the
 * controller removes it before serialising the HTTP response.
 * It carries no @Entity annotations.
 *
 * @author Keitho_
 */
@Data
@Builder
@AllArgsConstructor
@lombok.NoArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String tokenType;
    private UserDTO user;
}
