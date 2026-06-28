package com.genc.ctds.trialprotocol.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Converts business-rule violations (invalid status transitions, missing entities,
 * site work on a non-approved protocol, etc.) into clean HTTP 400 JSON responses
 * like { "error": "..." } so the frontend can show the message to the user.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }
}

