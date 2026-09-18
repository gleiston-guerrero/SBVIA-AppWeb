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
     * Método público.
     *
     * @param message a {@link java.lang.String} object
     */
    public RateLimitExceededException(String message) {
        super(message);
    }
}
