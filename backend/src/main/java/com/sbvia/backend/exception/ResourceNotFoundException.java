package com.sbvia.backend.exception;

/**
 * Excepción lanzada cuando un recurso (ej. user, scenario) no es encontrado en la BD.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Método público.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
