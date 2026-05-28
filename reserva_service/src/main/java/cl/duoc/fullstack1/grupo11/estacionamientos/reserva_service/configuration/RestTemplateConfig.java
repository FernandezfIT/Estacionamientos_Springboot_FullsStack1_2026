package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "microserviciosRestTemplate")
    public RestTemplate microserviciosRestTemplate(
            @Value("${app.services.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${app.services.read-timeout-ms}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return new RestTemplate(requestFactory);
    }
}
