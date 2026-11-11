package com.poppe.poppetechhub.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Poppe Tech Hub API",
                version = "v1",
                description = "API do backend do Poppe Tech Hub."
        )
)
@SecurityScheme(
        name = "basicAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "basic",
        description = "Autenticação HTTP Basic. Em desenvolvimento, use o usuário gerado no log de inicialização."
)
public class OpenApiConfig {

}
