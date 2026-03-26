package com.sportradar.sportevents.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        List<String> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, null);
    }

    public static ErrorResponse ofFields(int status, String error, String message, List<String> fieldErrors) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, fieldErrors);
    }
}
