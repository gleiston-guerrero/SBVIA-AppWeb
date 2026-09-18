package com.sbvia.backend.service.feedback;

/**
 * <p>AiUnavailableException class.</p>
 *
 * @author Keitho_
 */
public class AiUnavailableException extends RuntimeException {

    /**
     * Método público.
     *
     * @param message a {@link java.lang.String} object
     */
    public AiUnavailableException(String message) {
        super(message);
    }

    /**
     * Método público.
     *
     * @param message a {@link java.lang.String} object
     * @param cause a {@link java.lang.Throwable} object
     */
    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
