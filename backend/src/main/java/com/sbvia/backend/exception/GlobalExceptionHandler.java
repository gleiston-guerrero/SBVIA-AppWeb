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
 * Global handler for exceptions and for formatting error responses.
 * Implementa RFC 7807 (ProblemDetails).
 *
 * @author Keitho_
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Handles {@link com.sbvia.backend.exception.ResourceNotFoundException} by returning an RFC 7807
     * problem detail with HTTP 404 (Not Found) and the exception message.
     *
     * @param ex the exception thrown when a requested resource does not exist
     * @return a {@link org.springframework.http.ProblemDetail} describing the 404 error
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /**
     * Handles {@link com.sbvia.backend.exception.DuplicateEmailException} by returning an RFC 7807
     * problem detail with HTTP 409 (Conflict) and the exception message.
     *
     * @param ex the exception thrown when an email is already registered
     * @return a {@link org.springframework.http.ProblemDetail} describing the 409 error
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ProblemDetail handleDuplicateEmailException(DuplicateEmailException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /**
     * Handles {@link java.lang.IllegalArgumentException} by returning an RFC 7807
     * problem detail with HTTP 400 (Bad Request) and the exception message.
     *
     * @param ex the exception thrown when a method receives an invalid argument
     * @return a {@link org.springframework.http.ProblemDetail} describing the 400 error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /**
     * Handles {@link org.springframework.web.bind.MethodArgumentNotValidException} raised when request body
     * validation fails. It returns an RFC 7807 problem detail with HTTP 400
     * (Bad Request) and attaches an "errores" property mapping each invalid
     * field name to its validation error message.
     *
     * @param ex the exception carrying the binding result with the field validation errors
     * @return a {@link org.springframework.http.ProblemDetail} describing the validation failures per field
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
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

    /**
     * Handles {@link org.springframework.security.core.AuthenticationException} by returning an RFC 7807
     * problem detail with HTTP 401 (Unauthorized) and a message indicating
     * that the credentials are invalid or the token has expired.
     *
     * @param ex the exception thrown when authentication fails
     * @return a {@link org.springframework.http.ProblemDetail} describing the 401 error
     */
    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Credenciales inválidas o token expirado");
    }

    /**
     * Handles {@link com.sbvia.backend.exception.RateLimitExceededException} by returning an RFC 7807
     * problem detail with HTTP 429 (Too Many Requests) and the exception
     * message describing the rate limit breach.
     *
     * @param ex the exception thrown when a rate limit such as the login attempt limit is exceeded
     * @return a {@link org.springframework.http.ProblemDetail} describing the 429 error
     */
    @ExceptionHandler(RateLimitExceededException.class)
    public ProblemDetail handleRateLimitExceededException(RateLimitExceededException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage());
    }

    /**
     * Handles {@link org.springframework.security.access.AccessDeniedException} by returning an RFC 7807
     * problem detail with HTTP 403 (Forbidden) and a message stating that the
     * caller lacks permission for the requested resource.
     *
     * @param ex the exception thrown when an authenticated user lacks the required authority
     * @return a {@link org.springframework.http.ProblemDetail} describing the 403 error
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDeniedException(AccessDeniedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, "No tiene permisos para acceder a este recurso");
    }

    /**
     * Handles {@link org.springframework.web.HttpRequestMethodNotSupportedException} by returning an
     * RFC 7807 problem detail with HTTP 405 (Method Not Allowed) and a message
     * identifying the HTTP method that is not supported.
     *
     * @param ex the exception thrown when a request uses an HTTP method the endpoint does not support
     * @return a {@link org.springframework.http.ProblemDetail} describing the 405 error
     */
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP no soportado: " + ex.getMethod());
    }

    /**
     * Catches any exception that is not handled by a more specific handler and
     * returns an RFC 7807 problem detail with HTTP 500 (Internal Server Error)
     * and a message including the exception text.
     *
     * @param ex the unexpected exception thrown while processing the request
     * @return a {@link org.springframework.http.ProblemDetail} describing the 500 error
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGlobalException(Exception ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor: " + ex.getMessage());
    }
}
