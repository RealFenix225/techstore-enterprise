package com.techstore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


@Configuration
@EnableJpaAuditing // Esto enciende las cámaras de vigilancia
public class AuditConfig {

}
