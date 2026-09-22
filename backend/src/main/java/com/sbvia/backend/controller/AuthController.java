package com.sbvia.backend.controller;

import com.sbvia.backend.dto.*;
import com.sbvia.backend.service.AuthService;
import com.sbvia.backend.service.LoginRateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * REST controller for authentication: registration, login, logout and token refresh.
 * Endpoints públicos: /api/auth/registro, /api/auth/login
 * Endpoints protegidos: /api/auth/logout, /api/auth/refresh, /api/users/me
 *
 * @author Keitho_
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de registro, login, logout y refresh token JWT")
@Slf4j
public class AuthController {
    private static final String ACCESS_COOKIE = "accessToken";
    private static final String REFRESH_COOKIE = "refreshToken";

    private final AuthService authService;
    private final LoginRateLimiter loginRateLimiter;

    /** Controls the cookie Secure flag. false in dev (HTTP), true in prod (HTTPS). */
    @Value("${security.cookie.secure:false}")
    private boolean cookieSecure;

    /**
     * POST /api/auth/registro — Registrar nuevo user.
     * Returns the created user, without its hash, and the access token.
     * The refresh token is delivered only in an HttpOnly cookie.
     *
     * @param request the data of the user to register (email, first name, last name, and so on)
     * @return an authentication response (AuthResponse) holding the access token and the created user
     */
    @PostMapping("/registro")
    @Operation(summary = "Registrar nuevo user", description = "Crea una cuenta y devuelve tokens JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    })
    /**
     * Registers a new user account. The registration is retried up to three
     * times when the auto-generated username collides with an existing one. On
     * success it sets the access token cookie for the whole site and the
     * refresh token cookie for the auth endpoints, hides the refresh token
     * from the response body, and returns the created user together with the
     * access token and HTTP 201 status.
     *
     * @param request the validated registration data (email, names, password and profile fields)
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.AuthResponse} with the access token and the created user
     */
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = null;
        int maxIntentos = 3;
        for (int intento = 1; intento <= maxIntentos; intento++) {
            try {
                response = authService.register(request);
                break;
            } catch (org.springframework.dao.DataIntegrityViolationException ex) {
                log.warn("Colisión de unicidad al generar nombre_usuario (intento {}/{}). Reintentando...",
                        intento, maxIntentos);
                if (intento == maxIntentos) {
                    throw ex;
                }
            }
        }
        ResponseCookie accessCookie = tokenCookie(ACCESS_COOKIE, response.getAccessToken(),
                response.getExpiresIn(), "/");
        ResponseCookie refreshCookie = tokenCookie(REFRESH_COOKIE, response.getRefreshToken(),
                authService.getRefreshExpirationSeconds(), "/api/auth");
        response.setRefreshToken(null);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(response);
    }

    /**
     * POST /api/auth/login — Autenticar user.
     * Returns the access token; the refresh token stays in an HttpOnly cookie.
     *
     * @param request the login data, including the identifier (email or username) and password
     * @param httpRequest the current HTTP request, used to get the client IP and to rate-limit failed attempts
     * @return an authentication response with the access token and the user details, setting the refresh token in a cookie
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica, devuelve el access token y establece el refresh token en cookie HttpOnly")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    /**
     * Authenticates a user with the given credentials. The client IP is used
     * to enforce the login rate limit (HTTP 429 when exceeded); failed
     * attempts are recorded and successful logins reset the counter. On
     * success the access token cookie is set for the whole site, the refresh
     * token cookie for the auth endpoints, the refresh token is removed from
     * the body, and the access token together with the user details is
     * returned.
     *
     * @param request the validated login data containing the identifier (email or username) and password
     * @param httpRequest the current HTTP request, used to obtain the client IP for rate limiting
     * @return a {@link org.springframework.http.ResponseEntity} carrying the {@link com.sbvia.backend.dto.AuthResponse} with the access token and user details
     */
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ip = clientIp(httpRequest);

        // OWASP A07: rate-limit failed authentication attempts per IP (429).
        try {
            loginRateLimiter.check(ip);
        } catch (com.sbvia.backend.exception.RateLimitExceededException ex) {
            log.warn("EVENTO_SEGURIDAD: login-bloqueado ip={} motivo=rate_limit app {}", ip, ex.getMessage());
            throw ex;
        }

        AuthResponse response;
        try {
            response = authService.login(request);
        } catch (org.springframework.security.core.AuthenticationException ex) {
            loginRateLimiter.recordFailure(ip);
            log.warn("EVENTO_SEGURIDAD: login-fallido identificador={} ip={} motivo=credenciales_invalidas",
                    request.getIdentificador(), ip);
            throw ex;
        }

        loginRateLimiter.reset(ip);
        log.info("EVENTO_SEGURIDAD: login-exitoso identificador={} ip={}", request.getIdentificador(), ip);

        ResponseCookie accessCookie = tokenCookie(ACCESS_COOKIE, response.getAccessToken(),
                response.getExpiresIn(), "/");
        ResponseCookie refreshCookie = tokenCookie(REFRESH_COOKIE, response.getRefreshToken(),
                authService.getRefreshExpirationSeconds(), "/api/auth");
        response.setRefreshToken(null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(response);
    }

    /**
     * POST /api/auth/logout — Cerrar sesión.
     * Adds the token JTI to the Redis blacklist.
     *
     * @param authHeader the authorization header holding the current access token in Bearer form (optional)
     * @param cookieToken the access token stored in the cookie (optional)
     * @return an HTTP response with no content (204) and instructions to clear the token cookies
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Revoca el token JWT agregando su JTI a Redis")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Sesión cerrada correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o ausente")
    })
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @CookieValue(value = "accessToken", required = false) String cookieToken) {
        
        String token = cookieToken;
        if (token == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }
        
        if (token != null) {
            authService.logout(token);
        }

        ResponseCookie accessCookie = tokenCookie(ACCESS_COOKIE, "", 0, "/");
        ResponseCookie refreshCookie = tokenCookie(REFRESH_COOKIE, "", 0, "/api/auth");

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .build();
    }

    /**
     * POST /api/auth/refresh — Emitir nuevo accessToken.
     * Uses the refresh token without re-authenticating.
     *
     * @param cookieRefreshToken the refresh token taken from the HttpOnly cookie (optional)
     * @param request an object holding the refresh token when it is sent in the request body (optional)
     * @return a response with the new access token and the session details, refreshing the refresh token cookie
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Emite un nuevo accessToken usando el refreshToken")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token refrescado"),
        @ApiResponse(responseCode = "403", description = "Refresh token inválido o expirado")
    })
    public ResponseEntity<AuthResponse> refresh(
            @CookieValue(value = REFRESH_COOKIE, required = false) String cookieRefreshToken,
            @RequestBody(required = false) RefreshTokenRequest request) {
        String refreshToken = (cookieRefreshToken != null && !cookieRefreshToken.isBlank())
                ? cookieRefreshToken
                : (request != null ? request.getRefreshToken() : null);
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new org.springframework.security.authentication.BadCredentialsException("Refresh token ausente o no proporcionado");
        }

        AuthResponse response = authService.refresh(refreshToken);
        ResponseCookie accessCookie = tokenCookie(ACCESS_COOKIE, response.getAccessToken(),
                response.getExpiresIn(), "/");
        ResponseCookie refreshCookie = tokenCookie(REFRESH_COOKIE, response.getRefreshToken(),
                authService.getRefreshExpirationSeconds(), "/api/auth");
        response.setRefreshToken(null);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString(), refreshCookie.toString())
                .body(response);
    }

    private ResponseCookie tokenCookie(String name, String value, long maxAgeSeconds, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite("Strict")
                .path(path)
                .maxAge(maxAgeSeconds)
                .build();
    }

    /**
     * Gets the client IP address, honouring the X-Forwarded-For header
     * when the request arrives through a proxy (the nginx frontend).
     */
    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
