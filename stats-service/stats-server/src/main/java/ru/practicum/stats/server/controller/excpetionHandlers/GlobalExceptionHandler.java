package ru.practicum.stats.server.controller.excpetionHandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.stats.server.exceptions.IncorrectDateException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IncorrectDateException.class)
    public ResponseEntity<Object> handleNotFoundException(final IncorrectDateException exception) {
        log.warn(exception.getMessage(), exception);
        return sendResponse(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleStatsException(final RuntimeException exception) {
        log.error(exception.getMessage(), exception);
        return sendResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Object> sendResponse(final String message, final HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }
}
