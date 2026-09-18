package com.sbvia.backend.exception;

/**
 * Excepción lanzada al intentar registrar un email que ya existe en la BD.
 *
 * @author Keitho_
 */
public class DuplicateEmailException extends RuntimeException {
    /**
     * Método público.
     *
     * @param message a {@link java.lang.String} object
     */
    public DuplicateEmailException(String message) {
        super(message);
    }
}
