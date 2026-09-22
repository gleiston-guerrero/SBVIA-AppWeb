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
 * Main Spring Security 6 configuration.
 * It defines the SecurityFilterChain with:
 * - Autenticación stateless (JWT, sin sesiones)
 * - BCrypt encoder with cost 12 (OWASP)
 * - Security HTTP headers (X-Content-Type-Options, X-Frame-Options, CSP)
 * - CORS configurado explícitamente
 * - Roles with @PreAuthorize enabled
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
                // Authentication travels in cookies, so mutating operations
                // require the double-submit cookie pattern that Angular supports.
                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(requestHandler)
                        .ignoringRequestMatchers("/api/auth/login", "/api/auth/registro", "/api/auth/refresh"))

                // Security HTTP headers (OWASP A05)
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

                // Stateless sessions (JWT). The CSRF token rotation on
                // authentication is neutralised in StatelessCsrfTokenRepository.
                // Ver ADR-009.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos (sin autenticación)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/docs/**", "/api/swagger-ui/**",
                                "/api/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        // Scenario CRUD: GET is public for authenticated users,
                        // POST/PUT/DELETE requiere ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/scenarios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/scenarios/**").hasAuthority("ADMINISTRADOR")
                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // Authentication provider
                .authenticationProvider(authenticationProvider())

                // Add the JWT filter before the standard authentication filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(csrfTokenIssuerFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder with BCrypt cost 12 (OWASP A02: cryptographic failures).
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
