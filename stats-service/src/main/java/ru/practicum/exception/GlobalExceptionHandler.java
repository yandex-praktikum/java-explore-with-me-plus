package ru.practicum.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for the Statistics Service.
 * <p>
 * Provides centralized exception handling across all REST controllers.
 * Converts exceptions to appropriate HTTP responses with consistent
 * error message format. All errors are logged for debugging purposes.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== Custom Application Exceptions ====================

    /**
     * Handles custom validation exceptions from business logic.
     */
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handles illegal argument exceptions (e.g., invalid date ranges).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handles resource not found exceptions.
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(NotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handles access denied exceptions.
     */
    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbiddenException(ForbiddenException ex) {
        log.warn("Forbidden: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handles conflict exceptions (e.g., duplicate resources).
     */
    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflictException(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    /**
     * Handles bad request exceptions from controllers.
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleBadRequestException(BadRequestException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return Map.of("error", ex.getMessage());
    }

    // ==================== Spring Validation Exceptions ====================

    /**
     * Handles @Valid annotation failures on request body DTOs.
     * Collects all field validation errors into a structured response.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());
        
        log.warn("Validation errors: {}", errors);
        
        return Map.of(
            "errors", errors,
            "message", "Validation failed",
            "timestamp", LocalDateTime.now(),
            "status", "BAD_REQUEST"
        );
    }

    /**
     * Handles constraint violations on @RequestParam and @PathVariable.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleConstraintViolationException(ConstraintViolationException ex) {
        List<String> errors = ex.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", 
                    violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());
        
        log.warn("Constraint violations: {}", errors);
        
        return Map.of(
            "errors", errors,
            "message", "Constraint validation failed",
            "timestamp", LocalDateTime.now(),
            "status", "BAD_REQUEST"
        );
    }

    // ==================== Request Parsing Exceptions ====================

    /**
     * Handles malformed JSON in request body.
     * Provides detailed error message for format issues.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = "Invalid JSON in request body";
        
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            message = String.format("Invalid format for field '%s': expected %s", 
                ife.getPath().get(0).getFieldName(), ife.getTargetType().getSimpleName());
        }
        
        log.warn("Invalid JSON: {}", message);
        return Map.of("error", message);
    }

    /**
     * Handles missing required request parameters.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = String.format("Required parameter is missing: %s", ex.getParameterName());
        log.warn("Missing parameter: {}", ex.getParameterName());
        return Map.of("error", message);
    }

    /**
     * Handles type mismatch in request parameters (e.g., string instead of number).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Parameter '%s' must be of type '%s'", 
            ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        log.warn("Type mismatch: {}", message);
        return Map.of("error", message);
    }

    // ==================== Database Exceptions ====================

    /**
     * Handles database constraint violations (e.g., unique key conflicts).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMessage(), ex);
        return Map.of("error", "Data integrity violation");
    }

    // ==================== Fallback Handler ====================

    /**
     * Catches all unhandled exceptions as a safety net.
     * Returns generic error to avoid exposing internal details.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return Map.of("error", "Internal server error");
    }
}