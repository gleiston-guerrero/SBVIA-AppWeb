package com.sbvia.backend.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador global para el manejo de excepciones y formato de respuestas de error.
 * Implementa RFC 7807 (ProblemDetails).
 *
 * @author Keitho_
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    /**
     * Método público.
     *
     * @param ex a {@link com.sbvia.backend.exception.ResourceNotFoundException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    /**
     * Método público.
     *
     * @param ex a {@link com.sbvia.backend.exception.DuplicateEmailException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleDuplicateEmailException(DuplicateEmailException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    /**
     * Método público.
     *
     * @param ex a {@link java.lang.IllegalArgumentException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    /**
     * Método público.
     *
     * @param ex a {@link org.springframework.web.bind.MethodArgumentNotValidException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleValidationExceptions(MethodArgumentNotValidException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Errores de validación en la petición");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        problemDetail.setProperty("errores", errors);
        return problemDetail;
    }

    @ExceptionHandler(AuthenticationException.class)
    /**
     * Método público.
     *
     * @param ex a {@link org.springframework.security.core.AuthenticationException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o token expirado");
    }

    @ExceptionHandler(RateLimitExceededException.class)
    /**
     * Método público.
     *
     * @param ex a {@link com.sbvia.backend.exception.RateLimitExceededException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleRateLimitExceededException(RateLimitExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    /**
     * Método público.
     *
     * @param ex a {@link org.springframework.security.access.AccessDeniedException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso");
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    /**
     * Método público.
     *
     * @param ex a {@link org.springframework.web.HttpRequestMethodNotSupportedException} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado: " + ex.getMethod());
    }

    @ExceptionHandler(Exception.class)
    /**
     * Método público.
     *
     * @param ex a {@link java.lang.Exception} object
     * @return a {@link org.springframework.http.ProblemDetail} object
     */
    /** Javadoc for this element. */
    public ProblemDetail handleGlobalException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage());
    }
}
