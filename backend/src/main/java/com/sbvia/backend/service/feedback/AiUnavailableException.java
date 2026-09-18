package com.sbvia.backend.service.feedback;

public class AiUnavailableException extends RuntimeException {

    /**
     * Método público.
     */
    public AiUnavailableException(String message) {
        super(message);
    }

    /**
     * Método público.
     */
    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
