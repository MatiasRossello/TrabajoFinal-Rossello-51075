package com.example.utn.dnaRecord.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mutant Detector API")
                        .version("1.0.0")
                        .description("API REST para detectar si un humano es mutante basándose en su secuencia de ADN. " +
                                   "Un humano es mutante si se encuentra más de una secuencia de cuatro letras iguales " +
                                   "de forma horizontal, vertical o diagonal en su matriz de ADN NxN. " +
                                   "\n\n**Características:**\n" +
                                   "- Validación de secuencias de ADN\n" +
                                   "- Detección optimizada con early termination\n" +
                                   "- Caché de resultados mediante hash SHA-256\n" +
                                   "- Estadísticas de verificaciones realizadas"));
    }
}