package com.techstore.config;

import com.techstore.model.Role;
import com.techstore.model.User;
import com.techstore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            // Verificamos si ya existe el usuario para no duplicarlo
            if (userRepository.findByEmail("general@techstore.com").isEmpty()) {

                User admin = new User();
                admin.setEmail("general@techstore.com"); // El mismo email del token
                admin.setPassword(passwordEncoder.encode("password123")); // Contraseña encriptada
                admin.setRole(Role.ADMIN);
                admin.setFirstName("Cesar");
                admin.setLastName("General");

                userRepository.save(admin);
                log.info("USUARIO 'GENERAL' CREADO EN LA BASE DE DATOS");
            } else {
                log.info("El usuario 'General' ya existe en la DB");
            }
        };
    }
}