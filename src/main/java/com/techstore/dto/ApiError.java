package com.techstore.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
// @JsonInclude: CRÍTICO. Si 'details' es null, no aparecerá en el JSON.
// Esto limpia la respuesta para errores simples (como un 404 o 500) que no tienen lista de validaciones.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String correlationId;
    private LocalDateTime timestamp;    // Fecha y hora del error
    private Integer status;             // Código HTTP (400, 404, 500)
    private String error;               // Nombre del error (e.g. "Bad Request")
    private String message;             // Mensaje descriptivo
    private String path;                // La URL que falló (e.g. "/api/products")

    // Lista de errores específicos.
    // Usada cuando fallan validaciones múltiples (ej: precio negativo Y nombre vacío a la vez)
    private List<String> details;
}