package com.sbvia.backend.service.feedback;

/**
 * <p>AiUnavailableException class.</p>
 *
 * @author Keitho_
 */
public class AiUnavailableException extends RuntimeException {

    /**
     * Constructs an exception indicating that the AI feedback service is
     * currently unavailable.
     *
     * @param message the detail message describing the unavailability
     */
    public AiUnavailableException(String message) {
        super(message);
    }

    /**
     * Constructs an exception indicating that the AI feedback service is
     * unavailable, with the underlying cause.
     *
     * @param message the detail message describing the unavailability
     * @param cause the underlying cause of the unavailability
     */
    public AiUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
