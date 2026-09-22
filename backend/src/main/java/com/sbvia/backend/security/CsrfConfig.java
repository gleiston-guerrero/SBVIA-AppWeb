package com.sbvia.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * CSRF repository shared between the security configuration and the issuing
 * filter, so that the token issued is the same one that is validated.
 * It lives in its own class to avoid a circular reference with
 * SecurityConfig. Wrapped to ignore deletion on rotation (see
 * StatelessCsrfTokenRepository y ADR-009).
 *
 * @author Keitho_
 */
@Configuration
public class CsrfConfig {
    /**
     * Creates the shared CSRF token repository used both by the security
     * configuration and by the token issuing filter, so that the token issued
     * to the client is the same one that gets validated. The cookie-based
     * repository is wrapped in a stateless variant that ignores the token
     * deletion performed by Spring Security's per-authentication rotation (see
     * ADR-009).
     *
     * @return a {@link org.springframework.security.web.csrf.CsrfTokenRepository} backed by a non-HTTP-only cookie and wrapped for stateless use
     */
    @Bean
    public CsrfTokenRepository csrfTokenRepository() {
        return new StatelessCsrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
    }
}
