package com.techstore.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorDto {
    private String message;
    private String details;
    private LocalDateTime timestamp;
    private int code;
}
