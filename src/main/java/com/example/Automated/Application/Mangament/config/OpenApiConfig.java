package com.example.Automated.Application.Mangament.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "API", version = "1.0", description = "Information"),
       servers = {


               @Server(url = "http://localhost:8080", description = "Local Server URL"),
//              @Server(url = "https://manage-and-automate-aviation-academy-application-production.up.railway.app", description = "Railway Server URL"),
//                @Server(url = "https://manage-and-automate-aviation-academy.onrender.com", description = "Render Server URL"),
       },
        security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "api", scheme = "bearer", type = SecuritySchemeType.HTTP, in = SecuritySchemeIn.HEADER, bearerFormat = "JWT")
public class OpenApiConfig {
}
