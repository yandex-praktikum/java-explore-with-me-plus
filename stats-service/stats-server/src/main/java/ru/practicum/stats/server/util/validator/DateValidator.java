package ru.practicum.stats.server.util.validator;

import ru.practicum.stats.server.exceptions.IncorrectDateException;

import java.time.LocalDateTime;

public class DateValidator {

    public static void validateStartAndEndDate(final LocalDateTime start, final LocalDateTime end) {
        validateDateNotInFuture(start);
        if (end.isBefore(start) || start.isAfter(end)) {
            throw new IncorrectDateException("Start date must be before end date.");
        }
    }

    public static void validateDateNotInFuture(final LocalDateTime time) {
        if (LocalDateTime.now().isBefore(time)) {
            throw new IncorrectDateException("Date must not be in the future. " + time.toString());
        }
    }
}
