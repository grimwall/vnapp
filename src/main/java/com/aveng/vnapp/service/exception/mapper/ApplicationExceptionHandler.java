package com.aveng.vnapp.service.exception.mapper;

import java.util.Objects;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.aveng.vnapp.service.exception.ApplicationException;
import com.aveng.vnapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * @author apaydin
 */
@Component
@Slf4j
public class ApplicationExceptionHandler {

    /**
     * Will log and map an {@link ApplicationException} and return a meaningful response
     *
     * @param exception
     * @return an {@link ApiResponse} with meaningful error messages
     */
    public ResponseEntity<ApiResponse<String>> mapApplicationException(final ApplicationException exception) {

        //todo introduce message localization of error messages here
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

        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
            .message(errorMessage)
            .responseCode(exception.getStatus())
            .data(ExceptionUtils.getStackTrace(exception))
            .build();

        return new ResponseEntity<>(apiResponse, HttpStatus.valueOf(exception.getStatus()));
    }
}
