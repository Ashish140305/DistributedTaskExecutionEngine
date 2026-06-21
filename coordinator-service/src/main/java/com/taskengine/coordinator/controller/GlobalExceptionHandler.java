package com.taskengine.coordinator.controller;

import com.taskengine.common.exception.JobNotFoundException;
import com.taskengine.common.exception.NoAvailableWorkerException;
import com.taskengine.common.exception.WorkerNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler for REST controllers.
 *
 * <p>Converts domain exceptions to structured JSON error responses
 * with appropriate HTTP status codes.
 *
 * @author TaskEngine Team
 * @since 1.0.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JobNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleJobNotFound(JobNotFoundException ex) {
        log.warn("Job not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Job not found", ex.getMessage());
    }

    @ExceptionHandler(WorkerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleWorkerNotFound(WorkerNotFoundException ex) {
        log.warn("Worker not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, "Worker not found", ex.getMessage());
    }

    @ExceptionHandler(NoAvailableWorkerException.class)
    public ResponseEntity<Map<String, Object>> handleNoAvailableWorker(NoAvailableWorkerException ex) {
        log.warn("No available workers: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, "No available workers", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", ex.getMessage());
    }

    /**
     * Builds a structured error response.
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now().toString(),
                "status", status.value(),
                "error", error,
                "message", message != null ? message : "No details available"
        );
        return ResponseEntity.status(status).body(body);
    }
}
