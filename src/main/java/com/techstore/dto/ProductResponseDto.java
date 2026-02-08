package com.techstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductResponseDto {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "Monitor 24 pulgadas")
    private String name;

    @Schema(description = "Descripción breve", example = "Panel IPS, 75Hz, HDMI")
    private String description;

    @Schema(description = "Precio actual", example = "150.00")
    private BigDecimal price;

    @Schema(description = "Stock disponible", example = "25")
    private Integer stock;

    @Schema(description = "Categoría asociada", example = "Monitores")
    private String categoryName;

    @Schema(description = "Proveedor asociado", example = "Samsung")
    private String providerName;

    @Schema(description = "ID de la categoría", example = "3")
    private Long categoryId;

    @Schema(description = "ID del proveedor", example = "7")
    private Long providerId;

    @Schema(description = "Fecha de creación", example = "2026-02-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de actualización", example = "2026-02-05T16:00:00")
    private LocalDateTime updatedAt;
}