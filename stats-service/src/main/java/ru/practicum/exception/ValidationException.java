package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when business validation rules are violated.
 * <p>
 * Used for domain-specific validation failures beyond basic input validation.
 * Results in HTTP 400 Bad Request response.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    /**
     * Constructs a new ValidationException with the specified message.
     *
     * @param message detailed error message describing the validation failure
     */
    public ValidationException(String message) {
        super(message);
    }
}