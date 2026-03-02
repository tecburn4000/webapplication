package com.example.webapplication.exception.ftp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequestMapping("/ftp")
public class FtpExceptionHandler {

    public static final String STATUS = "status";
    public static final String MESSAGE = "message";
    public static final String TIMESTAMP = "timestamp";
    public static final String ERROR = "error";

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, Object>> handleIOException(IOException e) {
        return getMapResponseEntity(e, "IO Exception occurred", "SFTP operation failed: ");
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException e) {
        return getMapResponseEntity(e, "File size exceeded", "File size exceeds maximum allowed size");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException e) {
        return getMapResponseEntity(e, "Invalid argument", "Invalid request: ");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception e) {
        return getMapResponseEntity(e, "Unexpected error occurred", "An unexpected error occurred");
    }

    private static ResponseEntity<Map<String, Object>> getMapResponseEntity(Exception e, String errorType, String message) {
        log.error(errorType, e);
        Map<String, Object> response = new HashMap<>();
        response.put(STATUS, ERROR);
        response.put(MESSAGE, message + e.getMessage());
        response.put(TIMESTAMP, System.currentTimeMillis());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

