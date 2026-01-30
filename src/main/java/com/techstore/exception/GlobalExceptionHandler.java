package com.techstore.exception;

import com.techstore.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException; // <--- VITAL PARA TEST 6 (Esto faltaba 31/01)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Generic Exceptions (Fallback - Error 500)
    // Captura cualquier error no previsto para no exponer trazas internas.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGlobalException(
            Exception exception,
            HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Unexpected internal error")
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 2. Handle Resource Not Found (Error 404)
    // Captura cuando buscamos un ID que no existe en BD.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // 3. Handle Request Body Validation Errors (Error 400 - @Valid en DTOs)
    // Captura errores dentro del JSON (POST/PUT).
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<String> validationErrors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for request body")
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // 4. Handle Parameter Validation Errors (Error 400 - @Validated en Controller)
    // Captura errores en @RequestParam o @PathVariable (Ej: quantity=-5).
    // ESTE ES EL QUE FALTABA PARA EL TEST 6. (31/01)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<String> validationErrors = exception.getConstraintViolations()
                .stream()
                .map(violation -> violation.getMessage()) // Extrae solo el mensaje limpio
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Validation failed for parameters")
                .path(request.getRequestURI())
                .details(validationErrors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // 5. Handle Business Logic Exceptions (Error 409 - Conflict)
    // Captura reglas de negocio (Ej: Stock insuficiente).
    @ExceptionHandler(StockInsufficientException.class)
    public ResponseEntity<ApiError> handleStockInsufficientException(
            StockInsufficientException exception,
            HttpServletRequest request
    ) {
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(exception.getMessage())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }
}