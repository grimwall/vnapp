package com.aveng.vnapp.service.exception;

import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Our base ApplicationException for all custom exceptions
 */
@Data
@NoArgsConstructor
public class ApplicationException extends RuntimeException {

    private int status = HttpStatus.INTERNAL_SERVER_ERROR.value();

    private Level level = Level.ERROR;

    public ApplicationException(HttpStatus status, String message) {
        super(message);
        this.status = status.value();
    }

    public ApplicationException(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status.value();
    }

    public ApplicationException(HttpStatus status, String message, Level level) {
        super(message);
        this.status = status.value();
        this.level = level;
    }

    public ApplicationException(HttpStatus status, String message, Throwable cause, Level level) {
        super(message, cause);
        this.status = status.value();
        this.level = level;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

}
