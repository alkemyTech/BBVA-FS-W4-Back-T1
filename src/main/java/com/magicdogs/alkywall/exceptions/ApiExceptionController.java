package com.magicdogs.alkywall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiExceptionHandler> handleApiException(ApiException ex) {
        var apiErrorHandler = new ApiExceptionHandler(ex.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(apiErrorHandler);
    }
}
