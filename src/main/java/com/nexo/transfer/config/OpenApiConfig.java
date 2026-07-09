package com.nexo.transfer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Documentación OpenAPI de la API. springdoc genera el spec y el Swagger UI a partir
 * de los controladores; acá se completa la metadata y se declara el esquema de
 * seguridad (bearer) para que la doc sea el contrato de referencia de web y mobile.
 */
@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI nexoOpenAPI() {
    final String schemeName = "bearerAuth";
    return new OpenAPI()
        .info(new Info()
            .title("Nexo Transfer API")
            .description("API de transferencias ficticias del ecosistema Nexo Finanzas. "
                + "Datos y usuarios son ficticios; no representan ninguna entidad real.")
            .version("0.1.0")
            .contact(new Contact().name("Nexo Finanzas (portfolio)"))
            .license(new License().name("MIT")))
        .addSecurityItem(new SecurityRequirement().addList(schemeName))
        .components(new Components().addSecuritySchemes(schemeName, new SecurityScheme()
            .name(schemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")));
  }
}
