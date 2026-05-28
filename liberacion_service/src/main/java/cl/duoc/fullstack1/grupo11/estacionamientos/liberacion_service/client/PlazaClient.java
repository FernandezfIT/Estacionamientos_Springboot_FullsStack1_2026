package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client;

import java.util.Map;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.exception.PlazaServiceException;

@Component
public class PlazaClient {

    private final WebClient webClient;

    public PlazaClient(
            @Qualifier("plazaWebClient") WebClient webClient,
            @Value("${app.services.plaza.base-url}") String baseUrl
    ) {
        this.webClient = webClient.mutate()
                .baseUrl(baseUrl)
                .build();
    }

    public PlazaResponse obtenerPlaza(
            Long idPlaza,
            String authorizationHeader
    ) {
        try {
            return webClient.get()
                    .uri("/{id}", idPlaza)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(PlazaResponse.class)
                    .block();

        } catch (WebClientResponseException.NotFound ex) {
            return null;

        } catch (Exception ex) {
            throw new PlazaServiceException("No fue posible consultar plaza_service", ex);
        }
    }

    public PlazaResponse actualizarEstadoPlaza(
            Long idPlaza,
            String estado,
            String authorizationHeader
    ) {
        try {
            return webClient.put()
                    .uri("/{id}", idPlaza)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .bodyValue(Map.of("estado", estado))
                    .retrieve()
                    .bodyToMono(PlazaResponse.class)
                    .block();

        } catch (WebClientResponseException.NotFound ex) {
            return null;

        } catch (Exception ex) {
            throw new PlazaServiceException("No fue posible actualizar la plaza en plaza_service", ex);
        }
    }
}