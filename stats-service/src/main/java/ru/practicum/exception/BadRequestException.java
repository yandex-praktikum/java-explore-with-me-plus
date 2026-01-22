package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client request is malformed or invalid.
 * <p>
 * Results in HTTP 400 Bad Request response.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {

    /**
     * Constructs a new BadRequestException with the specified message.
     *
     * @param message detailed error message
     */
    public BadRequestException(String message) {
        super(message);
    }
}