package com.sbvia.backend.exception;

/**
 * Raised when a client exceeds the allowed number of
 * login attempts (OWASP A07). It maps to HTTP 429 Too Many Requests
 * through {@link com.sbvia.backend.exception.GlobalExceptionHandler}.
 *
 * @author Keitho_
 */
public class RateLimitExceededException extends RuntimeException {

    /**
     * Constructs an exception indicating that the client exceeded the allowed
     * number of login attempts, mapped to HTTP 429 Too Many Requests.
     *
     * @param message the detail message describing the rate-limit violation
     */
    public RateLimitExceededException(String message) {
        super(message);
    }
}
