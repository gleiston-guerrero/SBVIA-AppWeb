package com.sbvia.backend.exception;

/**
 * Raised when a resource, such as a user or a scenario, is not found in the database.
 *
 * @author Keitho_
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs an exception indicating that a requested resource was not
     * found in the database.
     *
     * @param message the detail message identifying the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
