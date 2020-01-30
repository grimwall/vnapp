package com.aveng.vnapp.service.exception.mapper;

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

/**
 * All exceptions and their logging requirements are first handled here.
 * Add more Exception handlers for each custom exception
 *
 * @author apaydin
 */
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GlobalExceptionHandler {

    private SpringExceptionHandler springExceptionHandler;
    private ApplicationExceptionHandler applicationExceptionHandler;

    public GlobalExceptionHandler(SpringExceptionHandler springExceptionHandler,
        ApplicationExceptionHandler applicationExceptionHandler) {
        this.springExceptionHandler = springExceptionHandler;
        this.applicationExceptionHandler = applicationExceptionHandler;
    }

    @ExceptionHandler(value = { ApplicationException.class })
    public ResponseEntity<ApiResponse<String>> handleApplicationException(final ApplicationException ex,
        final WebRequest request) {
        return applicationExceptionHandler.mapApplicationException(ex);
    }

    /**
     * All exceptions except custom ones are handled here
     *
     * @param ex the exception to be handled
     * @param request We pass this to the default spring handler
     * @return A meaningful response
     */
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleAll(final Exception ex, final WebRequest request) {

        try {
            ResponseEntity<Object> responseEntity = springExceptionHandler.handleException(ex, request);
            log.warn("Spring specific exception", ex);
            return responseEntity;
        } catch (Exception e) {
            //A non spring specific exception, last stop for all exceptions
            log.error("Unhandled exception", ex);

            ApiResponse<String> apiResponse = ApiResponse.<String>builder().message("Unhandled exception")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(ExceptionUtils.getStackTrace(ex))
                .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
