package com.techstore.dto.auth;

import com.techstore.model.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @Schema(description = "Nombre del usuario", example = "César")
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private Role role;
}