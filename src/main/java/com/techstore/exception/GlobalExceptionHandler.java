package com.techstore.exception;

import com.techstore.dto.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice //Este es el vigía total
public class GlobalExceptionHandler {
    //Esto manera recursos no encontrados (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorDto> handleResourceNotFoundException(
            ResourceNotFoundException exception,
            WebRequest webRequest
    ) {
        ErrorDto errorDto = ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .message(exception.getMessage())
                .details(webRequest.getDescription(false)) //false para no inicializar headers sensibles
                .code(HttpStatus.NOT_FOUND.value())
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }

    //Esto maneja cualquier otro error no esperado (500)
    //Esto eevita que el usuario vea "NullPointerException" o cosas raras.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handGlobalException(
            Exception exception,
            WebRequest webRequest
    ) {
        ErrorDto errorDto = ErrorDto.builder()
                .timestamp(LocalDateTime.now())
                .message("Internal Server Error: Ocurrió un error inesperado.")
                .details(exception.getMessage()) //En prod, a veces ocultamos esto
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build();
        return new ResponseEntity<>(errorDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Manejar errores de validación (400)
    //Este método extrae cada campo que falló y su mensaje
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            WebRequest webRequest
    ) {
        Map<String, String> errors = new HashMap();

        //Iteramos sobre todos los errores que encontró Spring
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        //Creamos una respuesta personalizada (podríamos reusar ErrorDto, pero un Map es más claro aquí para múltiples campos)
        Map<String, Object> response = new HashMap();
        response.put("timestamp", LocalDateTime.now());
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Error de validación en los datos de entrada");
        response.put("errors", errors); //Aquí va la lista de campos malos

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}