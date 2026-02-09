package com.techstore.controller;

import com.techstore.dto.auth.AuthenticationRequest;
import com.techstore.dto.auth.AuthenticationResponse;
import com.techstore.dto.auth.RegisterRequest;
import com.techstore.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // IMPORTANTE
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j // Habilita el logger
@Tag(name = "Authentication", description = "Endpoints para registro y autenticación de usuarios (JWT)")
public class AuthController {

    private final AuthenticationService service;

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        log.info("Registering new user: {}", request.getEmail());
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    @Operation(summary = "Iniciar sesión")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "403", description = "Credenciales inválidas")
    })
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        log.info("Authentication attempt for user: {}", request.getEmail());
        return ResponseEntity.ok(service.authenticate(request));
    }
}