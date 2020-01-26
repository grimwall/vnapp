package com.aveng.vnapp.service.exception.mapper;

import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ApplicationExceptionMapper extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { ApplicationException.class, RuntimeException.class })
    protected ResponseEntity<ApiResponse> handleConflict(RuntimeException ex, WebRequest request) {
        return toResponse(ex);
    }

    public ResponseEntity<ApiResponse> toResponse(RuntimeException exception) {

        if (exception instanceof ApplicationException) {
            ApplicationException ex = (ApplicationException) exception;
            String errorMessage = Objects.toString(exception.getMessage(), "OOPS! error");

            if (ex.getLevel() != null) {
                switch (ex.getLevel()) {
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

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                .message(errorMessage)
                .responseCode(ex.getStatus())
                .data(ExceptionUtils.getStackTrace(ex))
                .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(ex.getStatus()));
        } else {
            log.error("Unhandled exception", exception);

            ApiResponse<Object> apiResponse = ApiResponse.builder()
                .message("Unhandled exception")
                .responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .data(ExceptionUtils.getStackTrace(exception))
                .build();

            return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

    }

}
