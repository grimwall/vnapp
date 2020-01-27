package com.aveng.vnapp.service.exception.mapper;

import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

    private SpringExceptionHandler springExceptionHandler;

    public GlobalExceptionHandler(SpringExceptionHandler springExceptionHandler) {
        this.springExceptionHandler = springExceptionHandler;
    }

    @ExceptionHandler(value = { ApplicationException.class })
    public ResponseEntity<ApiResponse<String>> handleApplicationException(final ApplicationException ex,
        final WebRequest request) {
        return mapApplicationException(ex);
    }

    public ResponseEntity<ApiResponse<String>> mapApplicationException(final ApplicationException exception) {
        String errorMessage = Objects.toString(exception.getMessage(), "OOPS! error");

        if (exception.getLevel() != null) {
            switch (exception.getLevel()) {
                case ERROR:
                    log.error(errorMessage, exception);
                    break;
                case WARN:
                    log.warn(errorMessage, exception);
                    break;
                case INFO:
                    log.info(errorMessage, exception);
                    break;
                case DEBUG:
                    log.debug(errorMessage, exception);
                    break;
                case TRACE:
                    log.trace(errorMessage, exception);
                    break;
            }
        } else {
            log.error(errorMessage, exception);
        }

        ApiResponse<String> apiResponse = ApiResponse.<String>builder().message(errorMessage)
            .responseCode(exception.getStatus())
            .data(ExceptionUtils.getStackTrace(exception))
            .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(exception.getStatus()));
    }

    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {

        try {
            ResponseEntity<Object> responseEntity = springExceptionHandler.handleException(ex, request);
            log.warn("Spring specific exception", ex);
            return responseEntity;
        } catch (Exception e) {
            //A non spring specific exception
            log.error("Unhandled exception", ex);

            ApiResponse<String> apiResponse = ApiResponse.<String>builder().message("Unhandled exception")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(ExceptionUtils.getStackTrace(ex))
                .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
