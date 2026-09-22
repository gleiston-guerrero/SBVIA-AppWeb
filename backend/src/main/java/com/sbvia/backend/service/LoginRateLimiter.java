package com.sbvia.backend.service;

import com.sbvia.backend.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login attempt rate limiter (OWASP A07: identification and authentication
 * failures). It counts failed attempts per IP address within a
 * sliding window and, once the threshold is exceeded, temporarily blocks new attempts
 * with HTTP 429 (Too Many Requests).
 *
 * <p>The thresholds are injected from external configuration, with defaults:
 * {@code security.login.max-attempts} (5 by default) and
 * {@code security.login.lock-duration-seconds} (60 s by default).</p>
 *
 * @author Keitho_
 */
@Service
public class LoginRateLimiter {

    private static final int DEFAULT_MAX_ATTEMPTS = 5;
    private static final long DEFAULT_LOCK_SECONDS = 60L;

    private final int maxAttempts;
    private final long lockSeconds;
    private final ConcurrentHashMap<String, Attempts> attemptsByIp = new ConcurrentHashMap<>();

    /**
     * <p>Constructor for LoginRateLimiter.</p>
     *
     * @param maxAttempts a int
     * @param lockSeconds a long
     */
    public LoginRateLimiter(
            @Value("${security.login.max-attempts:5}") int maxAttempts,
            @Value("${security.login.lock-duration-seconds:60}") long lockSeconds) {
        this.maxAttempts = Math.max(1, maxAttempts);
        this.lockSeconds = Math.max(1, lockSeconds);
    }

    /**
     * Checks whether the IP has already exceeded the allowed number of failed attempts.
     * Throws {@link com.sbvia.backend.exception.RateLimitExceededException} (HTTP 429) when it is blocked.
     *
     * @param ip a {@link java.lang.String} object
     */
    public void check(String ip) {
        if (ip == null || ip.isBlank()) {
            return;
        }
        Attempts att = attemptsByIp.get(ip);
        if (att != null && att.failed >= maxAttempts) {
            long now = Instant.now().getEpochSecond();
            if (now - att.firstFailedAt < lockSeconds) {
                throw new RateLimitExceededException(
                        "Demasiados intentos fallidos. Intente de nuevo en unos segundos.");
            }
            attemptsByIp.remove(ip);
        }
    }

    /**
     * Records a failed attempt for the IP. It implements a sliding window:
     * when the last failure was more than {@link #lockSeconds} seconds ago, the counter
     * is reset.
     *
     * @param ip a {@link java.lang.String} object
     */
    public void recordFailure(String ip) {
        if (ip == null || ip.isBlank()) {
            return;
        }
        long now = Instant.now().getEpochSecond();
        attemptsByIp.compute(ip, (k, att) -> {
            if (att == null || now - att.firstFailedAt >= lockSeconds) {
                return new Attempts(now, 1);
            }
            att.failed++;
            return att;
        });
    }

    /**
     * Clears the IP counter after a successful login.
     *
     * @param ip a {@link java.lang.String} object
     */
    public void reset(String ip) {
        if (ip == null || ip.isBlank()) {
            return;
        }
        attemptsByIp.remove(ip);
    }

    private static final class Attempts {
        final long firstFailedAt;
        int failed;

        Attempts(long firstFailedAt, int failed) {
            this.firstFailedAt = firstFailedAt;
            this.failed = failed;
        }
    }
}
