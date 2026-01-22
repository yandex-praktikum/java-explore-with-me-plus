package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when access to a resource is denied.
 * <p>
 * Used when authentication is valid but authorization fails.
 * Results in HTTP 403 Forbidden response.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    /**
     * Constructs a new ForbiddenException with the specified message.
     *
     * @param message detailed error message explaining the access denial
     */
    public ForbiddenException(String message) {
        super(message);
    }
}