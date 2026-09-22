package com.sbvia.backend.exception;

/**
 * Raised when registering an email that already exists in the database.
 *
 * @author Keitho_
 */
public class DuplicateEmailException extends RuntimeException {
    /**
     * Constructs an exception indicating that an email address already exists
     * in the database.
     *
     * @param message the detail message explaining which email is duplicated
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
