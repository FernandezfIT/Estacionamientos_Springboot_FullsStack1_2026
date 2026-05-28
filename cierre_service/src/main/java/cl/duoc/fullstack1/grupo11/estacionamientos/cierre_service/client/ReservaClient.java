package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.ReservaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.MicroservicioNoDisponibleException;

@Component
public class ReservaClient {

    private final RestTemplate restTemplate;
    private final String reservaServiceBaseUrl;

    public ReservaClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.reserva.base-url}") String reservaServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.reservaServiceBaseUrl = reservaServiceBaseUrl;
    }

    public List<ReservaInternaResponse> listarReservas(String authorizationHeader) {
        try {
            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<ReservaInternaResponse[]> response = restTemplate.exchange(
                    reservaServiceBaseUrl,
                    HttpMethod.GET,
                    entity,
                    ReservaInternaResponse[].class
            );

            ReservaInternaResponse[] reservas = response.getBody();

            if (reservas == null) {
                return List.of();
            }

            return Arrays.asList(reservas);

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible conectar con reserva_service",
                    ex
            );
        }
    }

    public void eliminarReserva(
            Long idReserva,
            String authorizationHeader
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(reservaServiceBaseUrl)
                    .path("/{idReserva}")
                    .buildAndExpand(idReserva)
                    .toUriString();

            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    Void.class
            );

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible eliminar reserva en reserva_service",
                    ex
            );
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return new HttpEntity<>(headers);
    }
}