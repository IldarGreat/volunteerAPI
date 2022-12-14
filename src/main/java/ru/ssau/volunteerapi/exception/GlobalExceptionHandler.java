package ru.ssau.volunteerapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND,e.getMessage(),e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiError> handleNotFoundException(SQLException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage(),e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException e) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST,e.getMessage(),e);
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
