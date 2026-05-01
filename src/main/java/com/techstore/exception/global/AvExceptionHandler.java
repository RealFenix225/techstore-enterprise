package com.techstore.exception.global;

import com.techstore.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Handler específico del módulo avícola.
 * Reutiliza ApiError del proyecto base para no romper el contrato de la API.
 * Se registra como un @RestControllerAdvice adicional; Spring Boot los fusiona.
 */
@RestControllerAdvice
public class AvExceptionHandler {

    @ExceptionHandler(AvBusinessException.class)
    public ResponseEntity<ApiError> handleAvBusinessException(
            AvBusinessException ex, HttpServletRequest request) {

        ApiError apiError = ApiError.builder()
                .correlationId(MDC.get("correlationId"))
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNPROCESSABLE_ENTITY.value())
                .error("Avicola Business Rule Violation")
                .message("[" + ex.getCodigoError() + "] " + ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(apiError);
    }
}
