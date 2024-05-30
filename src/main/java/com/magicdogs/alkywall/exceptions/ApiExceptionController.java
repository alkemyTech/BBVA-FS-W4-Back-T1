package com.magicdogs.alkywall.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ApiExceptionController {

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<ApiExceptionHandler> handleApiException(ApiException ex) {
        var apiErrorHandler = new ApiExceptionHandler(ex.getStatus().value(), ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(apiErrorHandler);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        var errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        var errorMessage = "Dato ingresado inválido: " + ex.getMessage();
        return ResponseEntity.badRequest().body(errorMessage);
    }
}
