package com.techstore.dto;

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
private Long id;
@NotBlank(message = "The category name cannot be empty")
    @Size(min = 3, max = 50, message="The name must be between 3 and 50 characters")
    private String name;

private LocalDateTime createdAt;
private LocalDateTime updatedAt;
}
