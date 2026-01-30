package com.techstore.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// No utilizaré 400 (Bad Request) porque está bien formado.
// Usaré 409 (Conflict) porque el estado actual del recurso impide la acción.
@ResponseStatus(HttpStatus.CONFLICT)
public class StockInsufficientException extends RuntimeException {

    public StockInsufficientException(String message) {
        super(message);
    }
}