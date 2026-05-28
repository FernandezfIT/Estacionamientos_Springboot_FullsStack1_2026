package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.exception.MicroservicioNoDisponibleException;

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

    public List<PlazaResponse> listarPlazas(String authorizationHeader) {
        try {
            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<PlazaResponse[]> response = restTemplate.exchange(
                    plazaServiceBaseUrl,
                    HttpMethod.GET,
                    entity,
                    PlazaResponse[].class
            );

            PlazaResponse[] plazas = response.getBody();
            return plazas != null ? Arrays.asList(plazas) : List.of();

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible obtener las plazas desde plaza_service", ex);
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
}
