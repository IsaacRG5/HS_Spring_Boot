package com.eventify.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * Personaliza los metadatos que se muestran en la interfaz de Swagger UI
 * (titulo, version, descripcion) para los endpoints de Eventify.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI eventifyOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Eventify API")
                        .description("API interna para el registro y consulta de Venues y Events "
                                + "del catalogo de Eventify (arquitectura base Spring MVC).")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo Eventify")
                                .email("soporte@eventify.com")));
    }

}
