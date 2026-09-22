package com.sbvia.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service that manages the JWT token blacklist in Redis.
 * It stores the JTI (JWT ID) of revoked tokens with a TTL equal
 * to the token expiry, so that they remove themselves from Redis.
 *
 * Decisión documentada en ADR-003: jwt-redis.
 *
 * @author Keitho_
 */
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * Adds a JTI to the Redis blacklist with a TTL in milliseconds.
     * When the TTL expires, Redis removes the entry automatically.
     *
     * @param jti a {@link java.lang.String} object
     * @param expirationMs a long
     */
    public void blacklistToken(String jti, long expirationMs) {
        String key = BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, "revoked", expirationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Checks whether a JTI is in the blacklist.
     * Queried by JwtAuthFilter on every request before authorising.
     *
     * @param jti a {@link java.lang.String} object
     * @return a boolean
     */
    public boolean isTokenBlacklisted(String jti) {
        String key = BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
