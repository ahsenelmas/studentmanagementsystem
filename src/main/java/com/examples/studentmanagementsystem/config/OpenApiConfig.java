package com.examples.studentmanagementsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management System API")
                        .version("1.0")
                        .description("REST API for managing students, courses, schedules, and enrollments")
                        .contact(new Contact()
                                .name("Ahsen")
                                .email("your-email@example.com"))
                        .license(new License()
                                .name("Educational Use")));
    }
}