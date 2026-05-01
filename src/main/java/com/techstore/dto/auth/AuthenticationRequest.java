package com.techstore.dto.auth;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {

    @Schema(description = "User's password", example = "password123")

    @NotBlank
    String password;

    @Schema(description = "User's email address", example = "admin@techstore.com")

    @Email
    @NotBlank
    private String email;
}