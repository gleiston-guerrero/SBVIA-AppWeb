package com.sbvia.backend.security;

import com.sbvia.backend.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.util.WebUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT filter that runs once per HTTP request.
 * It extends Spring Security's OncePerRequestFilter.
 *
 * Flujo:
 * 1. It takes the token from the Authorization: Bearer [token] header
 * 2. It validates the signature and the expiry with JwtService.validateToken()
 * 3. It queries Redis to check that the JTI is not in the blacklist
 * 4. It sets the UsernamePasswordAuthenticationToken in the SecurityContextHolder
 *
 * @author Keitho_
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService tokenBlacklistService;

    /** {@inheritDoc} */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = null;
        
        // 1. Try to take the token from the HttpOnly cookie
        Cookie cookie = WebUtils.getCookie(request, "accessToken");
        if (cookie != null) {
            jwt = cookie.getValue();
        } 
        
        // 2. With no cookie, fall back to the Authorization header (for Postman and Swagger)
        if (jwt == null) {
            final String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            }
        }

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Take the email from the token
            final String email = jwtService.extractEmail(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 3. Query Redis: check that the JTI is not in the blacklist
                String jti = jwtService.extractJti(jwt);
                if (tokenBlacklistService.isTokenBlacklisted(jti)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                // 4. Load the UserDetails from the database
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 5. Validar firma y expiración
                if (jwtService.validateToken(jwt, userDetails)) {
                    // 6. Set the SecurityContext
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Invalid token: no authentication is set and the chain continues
            logger.debug("Token JWT inválido: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
