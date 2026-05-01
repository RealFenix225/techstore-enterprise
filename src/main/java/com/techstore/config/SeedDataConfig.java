package com.techstore.config;

import com.techstore.model.Role;
import com.techstore.model.User;
import com.techstore.model.avicola.AvClienteMercado;
import com.techstore.model.avicola.AvEstadoCuenta;
import com.techstore.model.avicola.AvJornadaDiaria;
import com.techstore.model.enums.EstadoJornada;
import com.techstore.repository.UserRepository;
import com.techstore.repository.avicola.AvClienteMercadoRepository;
import com.techstore.repository.avicola.AvEstadoCuentaRepository;
import com.techstore.repository.avicola.AvJornadaDiariaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeedDataConfig {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AvClienteMercadoRepository avClienteMercadoRepository;
    private final AvJornadaDiariaRepository avJornadaDiariaRepository;
    private final AvEstadoCuentaRepository avEstadoCuentaRepository;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {

            // --- Usuario General ---
            if (userRepository.findByEmail("general@techstore.com").isEmpty()) {
                User admin = new User();
                admin.setEmail("general@techstore.com");
                admin.setPassword(passwordEncoder.encode("password123"));
                admin.setRole(Role.ADMIN);
                admin.setFirstName("Cesar");
                admin.setLastName("General");

                userRepository.save(admin);
                log.info("USUARIO 'GENERAL' CREADO EN LA BASE DE DATOS");
            } else {
                log.info("El usuario 'General' ya existe en la DB");
            }

            // --- Cliente "El Chino" ---
            AvClienteMercado elChino = avClienteMercadoRepository.findAll()
                    .stream()
                    .filter(c -> "El Chino".equals(c.getNombreAlias()))
                    .findFirst()
                    .orElseGet(() -> {
                        AvClienteMercado nuevoCliente = new AvClienteMercado();
                        nuevoCliente.setNombreAlias("El Chino");
                        AvClienteMercado saved = avClienteMercadoRepository.save(nuevoCliente);
                        log.info("Cliente 'El Chino' creado con ID: {}", saved.getId());
                        return saved;
                    });

            // --- Jornada Diaria de hoy ---
            LocalDate hoy = LocalDate.now();
            AvJornadaDiaria jornadaHoy = avJornadaDiariaRepository.findAll()
                    .stream()
                    .filter(j -> hoy.equals(j.getFechaOperativa()))
                    .findFirst()
                    .orElseGet(() -> {
                        AvJornadaDiaria nuevaJornada = new AvJornadaDiaria();
                        nuevaJornada.setFechaOperativa(hoy);
                        nuevaJornada.setEstado(EstadoJornada.ABIERTA);
                        AvJornadaDiaria saved = avJornadaDiariaRepository.save(nuevaJornada);
                        log.info("Jornada diaria creada para fecha: {}", hoy);
                        return saved;
                    });

            // --- EstadoCuenta vinculando El Chino + Jornada de hoy ---
            boolean estadoCuentaExiste = avEstadoCuentaRepository.findAll()
                    .stream()
                    .anyMatch(ec ->
                            ec.getCliente() != null && ec.getCliente().getId().equals(elChino.getId()) &&
                                    ec.getJornada() != null && ec.getJornada().getId().equals(jornadaHoy.getId())
                    );

            if (!estadoCuentaExiste) {
                AvEstadoCuenta estadoCuenta = new AvEstadoCuenta();
                estadoCuenta.setCliente(elChino);
                estadoCuenta.setJornada(jornadaHoy);
                estadoCuenta.setDescuentoRectificacion(BigDecimal.ZERO);

                AvEstadoCuenta savedEstadoCuenta = avEstadoCuentaRepository.save(estadoCuenta);
                log.info("UUID_MOCK_FRONTEND: {}", savedEstadoCuenta.getId());
            } else {
                log.info("El EstadoCuenta para 'El Chino' en la jornada de hoy ya existe.");
            }
        };
    }
}