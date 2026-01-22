package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a request conflicts with the current state.
 * <p>
 * Typically used for duplicate entries or constraint violations.
 * Results in HTTP 409 Conflict response.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /**
     * Constructs a new ConflictException with the specified message.
     *
     * @param message detailed error message describing the conflict
     */
    public ConflictException(String message) {
        super(message);
    }
}