package com.techstore.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configuración CSRF (Cross-Site Request Forgery)
                // En APIs REST modernas se desactiva.
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Gestión de Sesiones (Session Management)
                // Para ser Stateless (sin memoria).
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3. Autorización de Rutas (El Portero)
                .authorizeHttpRequests(auth -> auth
                        // Hoy cerramos todo a cal y canto.
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}