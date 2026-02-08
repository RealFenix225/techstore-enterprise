package com.techstore.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "César (Tech Lead)",
                        email = "cesar@techstore.com",
                        url = "https://linkedin.com/in/tu-perfil"
                ),
                description = "API REST para la gestión de inventario, ventas y autenticación de TechStore Enterprise.",
                title = "TechStore Enterprise API",
                version = "1.0.0",
                license = @License(
                        name = "Proprietary",
                        url = "https://techstore.com"
                ),
                termsOfService = "https://techstore.com/terms"
        ),
        servers = {
                @Server(description = "Entorno Local", url = "http://localhost:8080"),
                @Server(
                        description = "Entorno Producción (Azure)",
                        url = "https://techstore-api-v2-axcreydeg0bvb3bj.spaincentral-01.azurewebsites.net"
                )
        },
        security = {
                @SecurityRequirement(name = "bearerAuth")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Authorization header using the Bearer scheme. Example: 'Bearer {token}'",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}