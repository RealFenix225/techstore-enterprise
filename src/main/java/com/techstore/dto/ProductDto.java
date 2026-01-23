package com.techstore.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "Product name cannot be empty.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 character")
    private String name;

    @Size(max = 255, message = "Description is longer.")
    private String description;

    @NotNull(message="The price is mandatory")
    @Positive(message ="The price must be greater than zero. ")
    private BigDecimal price;

    @Min(value = 0, message = "The stock cannot be negative.")
    private Integer stock;
    private String categoryName;
    private String providerName;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    @NotNull(message = "Provider IS is required")
    private Long providerId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
