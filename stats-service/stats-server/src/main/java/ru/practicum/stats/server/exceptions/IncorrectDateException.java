package ru.practicum.stats.server.exceptions;

public class IncorrectDateException extends RuntimeException {
    public IncorrectDateException(final String message) {
        super(message);
    }
}
