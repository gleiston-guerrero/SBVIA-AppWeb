package com.sbvia.backend.exception;

/**
 * Excepción lanzada cuando un recurso (ej. user, scenario) no es encontrado en la BD.
 *
 * @author Keitho_
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Método público.
     *
     * @param message a {@link java.lang.String} object
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
