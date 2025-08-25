package com.example.Web_sale_app.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(value = HttpClientErrorException.Unauthorized.class)
    public ResponseEntity<?> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
