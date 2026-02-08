package com.techstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Model representing product details for UI and API interaction")
public class ProductDto {

    @Schema(description = "Unique identifier (Auto-generated)", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Commercial product name", example = "Lenovo Legion 5 Pro")
    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Product name contains invalid characters (Only letters, numbers, spaces and hyphens allowed)")
    private String name;

    @Schema(description = "Detailed product description", example = "Gaming laptop with RTX 3060, 16GB RAM, 512GB SSD")
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @Schema(description = "Unit price in EUR", example = "1250.50")
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid (expected format: X.XX)")
    private BigDecimal price;

    @Schema(description = "Available units in warehouse", example = "50")
    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // --- RELACIONES (Lectura) ---
    @Schema(description = "Category name (Read-only)", example = "Laptops", accessMode = Schema.AccessMode.READ_ONLY)
    private String categoryName;

    @Schema(description = "Provider name (Read-only)", example = "Lenovo Official", accessMode = Schema.AccessMode.READ_ONLY)
    private String providerName;

    // --- RELACIONES (Escritura - IDs) ---
    @Schema(description = "ID of the category this product belongs to", example = "2")
    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @Schema(description = "ID of the provider supplying this product", example = "5")
    @NotNull(message = "Provider ID is required")
    @Positive(message = "Provider ID must be a positive number")
    private Long providerId;

    // --- AUDITORÍA ---
    @Schema(description = "Creation timestamp", example = "2026-02-08T10:00:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;

    @Schema(description = "Last update timestamp", example = "2026-02-08T12:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime updatedAt;
}