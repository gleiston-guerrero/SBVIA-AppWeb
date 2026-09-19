package com.sbvia.backend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DeferredCsrfToken;

/**
 * {@link CsrfTokenRepository} para backend JWT stateless. Delega todo en el
 * repositorio de cookie salvo el borrado del token, que se ignora.
 *
 * <p>Spring Security borra la cookie XSRF-TOKEN en cada request autenticado
 * ({@code CsrfAuthenticationStrategy}, rotación pensada para login con sesión)
 * y nada la vuelve a emitir, así que el double-submit nunca se estabiliza y
 * todos los POST/PUT/DELETE responden 403. Sin sesiones no hay fijación de
 * sesión que mitigar con esa rotación; la validación encabezado == cookie
 * sigue activa. Ver ADR-009.
 */
final class StatelessCsrfTokenRepository implements CsrfTokenRepository {

    private final CsrfTokenRepository delegate;

    StatelessCsrfTokenRepository(CsrfTokenRepository delegate) {
        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     *
     * Generates a new CSRF token by delegating to the wrapped cookie-based
     * repository.
     *
     * @param request the current HTTP request
     * @return the newly generated {@link org.springframework.security.web.csrf.CsrfToken}
     */
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return delegate.generateToken(request);
    }

    /**
     * {@inheritDoc}
     *
     * Saves the given CSRF token by delegating to the wrapped repository. A
     * null token is ignored so that the token rotation triggered by Spring
     * Security after authentication cannot erase the cookie in a stateless
     * session (see ADR-009).
     *
     * @param token the CSRF token to save, or null to leave the stored token untouched
     * @param request the current HTTP request
     * @param response the current HTTP response
     */
    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        if (token != null) {
            delegate.saveToken(token, request, response);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Loads the CSRF token associated with the current request by delegating to
     * the wrapped cookie-based repository.
     *
     * @param request the current HTTP request
     * @return the stored {@link org.springframework.security.web.csrf.CsrfToken}, or null if none exists
     */
    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        return delegate.loadToken(request);
    }

    /**
     * {@inheritDoc}
     *
     * Loads a deferred CSRF token for the current request/response pair by
     * delegating to the wrapped cookie-based repository.
     *
     * @param request the current HTTP request
     * @param response the current HTTP response
     * @return the {@link org.springframework.security.web.csrf.DeferredCsrfToken} resolved from the wrapped repository
     */
    @Override
    public DeferredCsrfToken loadDeferredToken(HttpServletRequest request, HttpServletResponse response) {
        return delegate.loadDeferredToken(request, response);
    }
}
