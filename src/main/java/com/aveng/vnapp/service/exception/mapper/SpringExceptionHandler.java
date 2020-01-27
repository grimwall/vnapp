package com.aveng.vnapp.service.exception.mapper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.aveng.vnapp.web.rest.model.ApiResponse;

import lombok.extern.slf4j.Slf4j;

/**
 * Handle all spring specific exceptions here
 *
 * @author apaydin
 */
@Component
@Slf4j
public class SpringExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    @NonNull
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {
        String errorMessage = "Malformed JSON request";

        ApiResponse<String> apiResponse = ApiResponse.<String>builder().message(errorMessage)
            .responseCode(status.value())
            .data(ExceptionUtils.getStackTrace(ex))
            .build();
        return new ResponseEntity<>(apiResponse, status);
    }

    @Override
    @NonNull
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
        HttpHeaders headers, HttpStatus status, WebRequest request) {

        List<String> validationErrors =
            ex.getBindingResult().getAllErrors().stream().map(mapToErrorMessage()).collect(Collectors.toList());

        ApiResponse<List<String>> apiResponse = ApiResponse.<List<String>>builder().message("Invalid object")
            .responseCode(status.value())
            .data(validationErrors)
            .build();

        return new ResponseEntity<>(apiResponse, status);
    }

    private Function<ObjectError, String> mapToErrorMessage() {
        return objectError -> {
            if (objectError instanceof FieldError) {
                FieldError fieldError = (FieldError) objectError;
                return "Field error in object '" + fieldError.getObjectName() + "' on field '" + fieldError.getField()
                    + "': rejected value [" + ObjectUtils.nullSafeToString(fieldError.getRejectedValue()) + "]; "
                    + fieldError.getDefaultMessage();
            } else {
                return "Object error in object '" + objectError.getObjectName() + "', "
                    + objectError.getDefaultMessage();
            }
        };
    }
}
