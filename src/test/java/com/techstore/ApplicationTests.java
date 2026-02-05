package com.techstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.techstore.config.JwtService;
import com.techstore.model.Role;
import com.techstore.model.User;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootTest
class ApplicationTests {
    @Autowired
    private JwtService jwtService;

    @Test
    void contextLoads() {
        // Nada aquí
    }

    @Test
    void testTokenGeneration() {
        // 1. Simular un usuario
        User user = User.builder()
                .email("general@techstore.com")
                .password("password123") // No importa para el token
                .role(Role.ADMIN)
                .build();

        // 2. Generar Token
        String token = jwtService.generateToken(user);

        // 3. Imprimir resultado
        System.out.println("-----------------------------------------------------------");
        System.out.println("TOKEN GENERADO CON ÉXITO:");
        System.out.println(token);
        System.out.println("-----------------------------------------------------------");
    }

}
