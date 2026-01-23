package com.techstore.dto;

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

    private Long id;

    @NotBlank(message = "Provider name is required")
    private String name;

    @NotBlank(message = "Tax ID is required")
    private String taxId; // El identificador fiscal

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}