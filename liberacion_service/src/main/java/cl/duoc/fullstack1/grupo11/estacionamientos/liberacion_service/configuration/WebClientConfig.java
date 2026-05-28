package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "plazaWebClient")
    public WebClient plazaWebClient(
            @Value("${app.services.plaza.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${app.services.plaza.read-timeout-ms}") int readTimeoutMs
    ) {
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectTimeoutMs)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(
                                Math.toIntExact(Duration.ofMillis(readTimeoutMs).getSeconds())))
                        .addHandlerLast(new WriteTimeoutHandler(
                                Math.toIntExact(Duration.ofMillis(readTimeoutMs).getSeconds())))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
