package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.configuration;


import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "usuarioRestTemplate")
    public RestTemplate usuarioRestTemplate(
            RestTemplateBuilder builder,
            @Value("${app.services.usuario.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${app.services.usuario.read-timeout-ms}") int readTimeoutMs
    ) {
        return builder
                .connectTimeout(Duration.ofMillis(connectTimeoutMs))
                .readTimeout(Duration.ofMillis(readTimeoutMs))
                .build();
    }
}