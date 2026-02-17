package com.example.tinyurl.exception;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidUrl(InvalidUrlException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Bad Request",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleShortUrlNotFound(ShortUrlNotFoundException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Not Found",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "Something went wrong"
                ));
    }

    @ExceptionHandler(UrlAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUrlAlreadyExists(UrlAlreadyExistsException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "Conflict",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(UrlExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleUrlExpired(UrlExpiredException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.GONE)
                .body(Map.of(
                        "error", "Gone",
                        "message", ex.getMessage()
                ));
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRateLimit(RateLimitExceededException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body(Map.of(
                        "error", "Too Many Requests",
                        "message", ex.getMessage()
                ));
    }


    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<?> handleOptimisticLock(OptimisticLockException ex, HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "Conflict",
                        "message", "Concurrent update detected. Please retry."

                ));
    }

}
