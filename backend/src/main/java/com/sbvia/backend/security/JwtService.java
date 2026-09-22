package com.sbvia.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * Service that generates and validates JWT tokens with jjwt 0.12.x.
 * It signs tokens with HS256 and a secret key of at least 256 bits.
 * Every token carries a unique JTI (JWT ID) to support the Redis blacklist.
 *
 * @author Keitho_
 */
@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secretKey;

    @Value("${security.jwt.expiration-ms}")
    private long accessExpirationMs;

    @Value("${security.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Value("${security.jwt.issuer}")
    private String jwtIssuer;

    @Value("${security.jwt.audience}")
    private String jwtAudience;

    /**
     * Generates an access JWT with custom claims.
     *
     * @param userDetails a {@link org.springframework.security.core.userdetails.UserDetails} object
     * @param userId a {@link java.lang.Long} object
     * @param role a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String generateAccessToken(UserDetails userDetails, Long userId, String role) {
        return buildToken(
                Map.of(
                        "email", userDetails.getUsername(),
                        "role", role,
                        "type", "access"
                ),
                String.valueOf(userId),
                accessExpirationMs
        );
    }

    /**
     * Generates a refresh JWT.
     *
     * @param userDetails a {@link org.springframework.security.core.userdetails.UserDetails} object
     * @param userId a {@link java.lang.Long} object
     * @return a {@link java.lang.String} object
     */
    public String generateRefreshToken(UserDetails userDetails, Long userId) {
        return buildToken(
                Map.of("type", "refresh"),
                String.valueOf(userId),
                refreshExpirationMs
        );
    }

    /**
     * Builds a JWT signed with HS256.
     */
    private String buildToken(Map<String, Object> extraClaims, String subject, long expirationMs) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .claims(extraClaims)
                .issuer(jwtIssuer)
                .subject(subject)
                .audience().add(jwtAudience).and()
                .id(UUID.randomUUID().toString())
                .notBefore(now)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    /**
     * Extracts the subject (the user ID) from the token.
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String extractSubject(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts the email from the token.
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).get("email", String.class);
    }

    /**
     * Extracts the JTI (JWT ID) from the token, used for the Redis blacklist.
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String extractJti(String token) {
        return extractClaim(token, Claims::getId);
    }

    /**
     * Extracts the token expiry date.
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.util.Date} object
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts the issuer claim (iss) from the given JWT token.
     *
     * @param token the JWT token from which the issuer claim is extracted
     * @return the issuer value declared in the token, or {@code null} if the claim is absent
     */
    public String extractIssuer(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    /**
     * <p>extractAudience.</p>
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.util.Set} object
     */
    public java.util.Set<String> extractAudience(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    /**
     * Extracts the not-before claim (nbf) from the given JWT token.
     *
     * @param token the JWT token from which the not-before claim is extracted
     * @return the date before which the token must not be accepted, or {@code null} if the claim is absent
     */
    public Date extractNotBefore(String token) {
        return extractClaim(token, Claims::getNotBefore);
    }

    /**
     * Extracts the token type (access or refresh).
     *
     * @param token a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public String extractTokenType(String token) {
        return extractAllClaims(token).get("type", String.class);
    }

    /**
     * Validates the token: correct signature, not expired,
     * and the username matching the UserDetails.
     *
     * @param token a {@link java.lang.String} object
     * @param userDetails a {@link org.springframework.security.core.userdetails.UserDetails} object
     * @return a boolean
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String email = extractEmail(token);
            return (email != null && email.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Gets the remaining time to expiry in milliseconds.
     * Used to set the Redis TTL when the token is revoked.
     *
     * @param token a {@link java.lang.String} object
     * @return a long
     */
    public long getExpirationRemainingMs(String token) {
        Date expiration = extractExpiration(token);
        return expiration.getTime() - System.currentTimeMillis();
    }

    /**
     * Returns the configured lifetime of the access tokens issued by this service.
     *
     * @return the access token expiration time in milliseconds
     */
    public long getAccessExpirationMs() {
        return accessExpirationMs;
    }

    /**
     * Returns the configured lifetime of the refresh tokens issued by this service.
     *
     * @return the refresh token expiration time in milliseconds
     */
    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .requireIssuer(jwtIssuer)
                .requireAudience(jwtAudience)
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
