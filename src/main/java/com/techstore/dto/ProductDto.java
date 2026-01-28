package com.techstore.dto;

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
public class ProductDto {

    private Long id;

    @NotBlank(message = "Product name is required")
    @Size(min = 3, max = 100, message = "Product name must be between 3 and 100 characters")
    // Pattern: Only letters, numbers, spaces, and hyphens allowed to prevent XSS or weird inputs
    @Pattern(regexp = "^[a-zA-Z0-9\\s\\-]+$", message = "Product name contains invalid characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price format is invalid (expected format: X.XX)")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    private String categoryName;
    private String providerName;

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number")
    private Long categoryId;

    @NotNull(message = "Provider ID is required")
    @Positive(message = "Provider ID must be a positive number")
    private Long providerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}