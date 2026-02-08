package com.techstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryDto {

    @Schema(description = "ID de la categoría", example = "10")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Periféricos")
    @NotBlank(message = "The category name cannot be empty")
    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 characters")
    private String name;

    @Schema(description = "Fecha de creación", example = "2026-01-01T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización", example = "2026-01-02T10:00:00")
    private LocalDateTime updatedAt;
}