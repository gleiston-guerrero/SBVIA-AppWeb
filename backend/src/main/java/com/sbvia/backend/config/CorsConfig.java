package com.sbvia.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración de CORS explícita requerida por OWASP.
 *
 * @author Keitho_
 */
@Configuration
public class CorsConfig {
    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:8080}")
    private String[] allowedOrigins;

    /**
     * Creates the CORS configuration source that applies the explicit CORS
     * policy required by OWASP. It allows only the origins configured in
     * {@code cors.allowed-origins}, permits the standard HTTP methods (GET,
     * POST, PUT, DELETE, OPTIONS) and the {@code Authorization},
     * {@code Content-Type} and {@code Accept} headers, exposes the
     * {@code Authorization} header to the client, enables credentials, and
     * registers the policy for every URL path of the application.
     *
     * @return a {@link org.springframework.web.cors.CorsConfigurationSource} applying the allowed-origins policy to all endpoints
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Orígenes permitidos dinámicamente
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        // Métodos permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Cabeceras permitidas
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        // Exponer cabeceras al cliente
        configuration.setExposedHeaders(List.of("Authorization"));
        // Permitir credenciales
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
