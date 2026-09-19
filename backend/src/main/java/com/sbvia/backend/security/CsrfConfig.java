package com.sbvia.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRepository;

/**
 * Repositorio CSRF compartido entre la configuración de seguridad y el filtro
 * de emisión, para que el token emitido sea el mismo que se valida.
 * Vive en su propia clase para evitar una referencia circular con
 * SecurityConfig. Envuelto para ignorar el borrado por rotación (ver
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
