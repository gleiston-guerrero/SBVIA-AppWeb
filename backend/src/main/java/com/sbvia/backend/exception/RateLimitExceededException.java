package com.sbvia.backend.exception;

/**
 * Excepción lanzada cuando un cliente supera el número permitido de intentos
 * de inicio de sesión (OWASP A07). Se traduce a HTTP 429 Too Many Requests
 * mediante {@link com.sbvia.backend.exception.GlobalExceptionHandler}.
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
