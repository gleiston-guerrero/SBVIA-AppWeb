package com.sbvia.backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Configuración principal de Spring Security 6.
 * Define la cadena de filtros SecurityFilterChain con:
 * - Autenticación stateless (JWT, sin sesiones)
 * - BCrypt encoder con costo 12 (OWASP)
 * - Cabeceras HTTP de seguridad (X-Content-Type-Options, X-Frame-Options, CSP)
 * - CORS configurado explícitamente
 * - Roles con @PreAuthorize habilitado
 *
 * @author Keitho_
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;
    private final CsrfTokenIssuerFilter csrfTokenIssuerFilter;
    private final CsrfTokenRepository csrfTokenRepository;
    private final UserDetailsService userDetailsService;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    /**
     * Builds the main Spring Security filter chain of the application. It
     * configures CSRF protection with the double-submit cookie pattern required
     * by the Angular frontend, hardens the HTTP security headers
     * (X-Content-Type-Options, X-Frame-Options, HSTS, XSS protection and CSP),
     * delegates CORS handling, registers the custom authentication entry point
     * and access denied handler, enforces stateless sessions for JWT
     * authentication, and sets the authorization rules: the authentication and
     * Swagger endpoints are public, GET on scenarios requires authentication,
     * the mutating scenario operations require the ADMINISTRADOR authority and
     * every other request requires authentication. Finally it registers the DAO
     * authentication provider and inserts the JWT and CSRF issuing filters
     * around the standard username/password authentication filter.
     *
     * @param http the {@link org.springframework.security.config.annotation.web.builders.HttpSecurity} builder used to configure the chain
     * @return the fully configured {@link org.springframework.security.web.SecurityFilterChain}
     * @throws java.lang.Exception if the security configuration cannot be built
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        
        http
                // La autenticación viaja en cookies, por lo que las operaciones mutables
                // requieren el patrón double-submit cookie compatible con Angular.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/registro", "/api/auth/refresh"))

                // Cabeceras HTTP de seguridad (OWASP A05)
                .headers(headers -> headers
                        .contentTypeOptions(contentType -> {})  // X-Content-Type-Options: nosniff
                        .frameOptions(frame -> frame.deny())     // X-Frame-Options: DENY
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000))      // Strict-Transport-Security
                        .xssProtection(xss -> xss
                                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                        .contentSecurityPolicy(csp ->
                                csp.policyDirectives("default-src 'self'; frame-ancestors 'none';"))
                )

                // CORS — delegado a CorsConfig
                .cors(cors -> {})

                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))

                // Sesiones stateless (JWT). La rotación del token CSRF por
                // autenticación se neutraliza en StatelessCsrfTokenRepository.
                // Ver ADR-009.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Reglas de autorización
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/docs/**", "/api/swagger-ui/**",
                                "/api/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // CRUD de scenarios: GET es público para users autenticados,
                        // POST/PUT/DELETE requiere ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/scenarios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        // Todo lo demás requiere autenticación
                        .anyRequest().authenticated()
                )

                // Proveedor de autenticación
                .authenticationProvider(authenticationProvider())

                // Agregar filtro JWT antes del filtro de autenticación estándar
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(csrfTokenIssuerFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder con BCrypt costo 12 (OWASP A02: Fallas criptográficas).
     *
     * @return a {@link org.springframework.security.crypto.password.PasswordEncoder} object
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Creates the authentication provider that validates credentials against
     * the users stored in the database. It is backed by a
     * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider} wired with the
     * application user details service and the BCrypt password encoder.
     *
     * @return a configured {@link org.springframework.security.authentication.AuthenticationProvider} for database-backed authentication
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * <p>authenticationManager.</p>
     *
     * @param config a {@link org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration} object
     * @return a {@link org.springframework.security.authentication.AuthenticationManager} object
     * @throws java.lang.Exception if any.
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
