package com.mymusic.backend.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle 429 from music provider
    @ExceptionHandler(HttpClientErrorException.TooManyRequests.class)
    public ResponseEntity<Map<String, String>> handleRateLimit(
            HttpClientErrorException.TooManyRequests ex) {

        Map<String, String> error = new HashMap<>();

        error.put("error", "RATE_LIMIT");
        error.put("message", "Music service is temporarily busy. Please try again.");

        return ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(error);
    }

    // Handle local JioSaavn API being offline/unreachable
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<Map<String, String>> handleConnectionError(
            ResourceAccessException ex) {

        Map<String, String> error = new HashMap<>();

        error.put("error", "MUSIC_SERVICE_UNAVAILABLE");
        error.put("message", "Music service is currently unavailable.");

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(error);
    }

    // Handle unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGeneralError(
            Exception ex) {

        ex.printStackTrace();

        Map<String, String> error = new HashMap<>();

        error.put("error", "INTERNAL_ERROR");
        error.put("message", "Something went wrong.");
 
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}