package com.sbvia.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Issues the XSRF-TOKEN cookie when the client does not have one yet. Angular
 * mirrors it in the X-XSRF-TOKEN header (double-submit cookie) for
 * mutating operations. It runs before the chain so that the response
 * is not committed yet. See ADR-009.
 */
@Component
@RequiredArgsConstructor
final class CsrfTokenIssuerFilter extends OncePerRequestFilter {

    private final CsrfTokenRepository csrfTokenRepository;

    /** {@inheritDoc} */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        if (csrfTokenRepository.loadToken(request) == null) {
            csrfTokenRepository.saveToken(csrfTokenRepository.generateToken(request), request, response);
        }
        filterChain.doFilter(request, response);
    }
}
