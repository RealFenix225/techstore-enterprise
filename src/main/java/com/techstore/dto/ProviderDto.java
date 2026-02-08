package com.techstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProviderDto {

    @Schema(description = "ID del proveedor", example = "55")
    private Long id;

    @Schema(description = "Nombre o Razón Social del proveedor", example = "Logitech S.A.")
    @NotBlank(message = "Provider name is required")
    private String name;

    @Schema(description = "Identificador Fiscal (CIF/NIF/RUC)", example = "B-98765432")
    @NotBlank(message = "Tax ID is required")
    private String taxId;

    @Schema(description = "Fecha de registro", example = "2025-11-15T08:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Última modificación", example = "2025-12-01T14:20:00")
    private LocalDateTime updatedAt;
}