package ru.practicum.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * <p>
 * Results in HTTP 404 Not Found response.
 * </p>
 *
 * @author Explore With Me Team
 * @version 1.0
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with the specified message.
     *
     * @param message detailed error message describing the missing resource
     */
    public NotFoundException(String message) {
        super(message);
    }
}