package com.techstore.service;

import com.techstore.config.JwtService;
import com.techstore.dto.auth.AuthenticationRequest;
import com.techstore.dto.auth.AuthenticationResponse;
import com.techstore.dto.auth.RegisterRequest;
import com.techstore.model.Role;
import com.techstore.model.User;
import com.techstore.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    // --- TEST 1: REGISTRO EXITOSO ---
    @Test
    @DisplayName("Should register user and return JWT token")
    void shouldRegisterUser_whenRequestIsValid() {
        // ARRANGE
        RegisterRequest request = RegisterRequest.builder()
                .firstname("Cesar")
                .lastname("Tech")
                .email("cesar@techstore.com")
                .password("password123")
                .role(Role.USER)
                .build();

        // Simulamos la encriptación
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        // Simulamos la generación del token
        when(jwtService.generateToken(any(User.class))).thenReturn("mock-jwt-token");

        // ACT
        AuthenticationResponse response = authenticationService.register(request);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");

        // Verificamos que se guardó el usuario en la BD
        verify(repository).save(any(User.class));
    }

    // --- TEST 2: LOGIN EXITOSO ---
    @Test
    @DisplayName("Should authenticate user and return JWT token")
    void shouldAuthenticateUser_whenCredentialsAreCorrect() {
        // ARRANGE
        AuthenticationRequest request = AuthenticationRequest.builder()
                .email("cesar@techstore.com")
                .password("password123")
                .build();

        User mockUser = User.builder()
                .email("cesar@techstore.com")
                .password("encodedPassword") // En BD estaría encriptada
                .build();

        // 1. Simulamos que findByEmail encuentra al usuario
        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(mockUser));

        // 2. Simulamos que el token se genera
        when(jwtService.generateToken(mockUser)).thenReturn("mock-jwt-token");

        // ACT
        AuthenticationResponse response = authenticationService.authenticate(request);

        // ASSERT
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("mock-jwt-token");

        // CRÍTICO: Verificar que SE LLAMÓ al AuthenticationManager real
        // Esto confirma que Spring Security hizo su trabajo de verificar la password
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}