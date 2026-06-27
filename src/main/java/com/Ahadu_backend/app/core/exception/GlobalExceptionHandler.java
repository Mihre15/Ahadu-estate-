package com.Ahadu_backend.app.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. This catches your registration error ("Email already exists")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // 2. This catches WRONG PASSWORDS during login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized", "The password you entered is incorrect.");
    }

    // 3. This catches WRONG EMAILS / MISSING USERS during login
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Not Found", "No account found matching this email address.");
    }

    // Helper method to keep things DRY (Don't Repeat Yourself)
    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}