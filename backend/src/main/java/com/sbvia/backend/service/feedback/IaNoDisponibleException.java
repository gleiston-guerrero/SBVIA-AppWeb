package com.sbvia.backend.service.feedback;

public class IaNoDisponibleException extends RuntimeException {

    public IaNoDisponibleException(String message) {
        super(message);
    }

    public IaNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
