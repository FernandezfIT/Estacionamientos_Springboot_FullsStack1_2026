package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
            .info(new Info()
        .title("API usuario_service")
        .version("1.0")
        .description("CRUD mantenedor de Usuarios"))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
        .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                            new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
        );   
    }
}
