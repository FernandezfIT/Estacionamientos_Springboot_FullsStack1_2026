package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.MicroservicioNoDisponibleException;

@Component
public class PlazaClient {

    private final RestTemplate restTemplate;
    private final String plazaServiceBaseUrl;

    public PlazaClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.plaza.base-url}") String plazaServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.plazaServiceBaseUrl = plazaServiceBaseUrl;
    }

    public List<PlazaInternaResponse> listarPlazas(String authorizationHeader) {
        try {
            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<PlazaInternaResponse[]> response = restTemplate.exchange(
                    plazaServiceBaseUrl,
                    HttpMethod.GET,
                    entity,
                    PlazaInternaResponse[].class
            );

            PlazaInternaResponse[] plazas = response.getBody();

            if (plazas == null) {
                return List.of();
            }

            return Arrays.asList(plazas);

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible conectar con plaza_service",
                    ex
            );
        }
    }

    public PlazaInternaResponse actualizarEstadoPlaza(
            Long idPlaza,
            String estado,
            String authorizationHeader
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(plazaServiceBaseUrl)
                    .path("/{idPlaza}")
                    .buildAndExpand(idPlaza)
                    .toUriString();

            PlazaUpdateRequest request = new PlazaUpdateRequest(estado);

            HttpEntity<PlazaUpdateRequest> entity = crearHttpEntityConTokenYBody(
                    request,
                    authorizationHeader
            );

            ResponseEntity<PlazaInternaResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    PlazaInternaResponse.class
            );

            return response.getBody();

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible actualizar plaza en plaza_service",
                    ex
            );
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return new HttpEntity<>(headers);
    }

    private HttpEntity<PlazaUpdateRequest> crearHttpEntityConTokenYBody(
            PlazaUpdateRequest request,
            String authorizationHeader
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, headers);
    }
}