package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.MovimientoResponse;

@Component
public class MovimientoClient {

    private final RestTemplate restTemplate;
    private final String movimientoServiceBaseUrl;

    public MovimientoClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.movimiento.base-url}") String movimientoServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.movimientoServiceBaseUrl = movimientoServiceBaseUrl;
    }

    public MovimientoResponse registrarMovimiento(
            MovimientoCreateRequest request,
            String authorizationHeader
    ) {
        try {
            HttpEntity<MovimientoCreateRequest> entity = crearHttpEntityConToken(
                    request,
                    authorizationHeader
            );

            ResponseEntity<MovimientoResponse> response = restTemplate.exchange(
                    movimientoServiceBaseUrl,
                    HttpMethod.POST,
                    entity,
                    MovimientoResponse.class
            );

            return response.getBody();

        } catch (RestClientException ex) {
            throw ex;
        }
    }

    private HttpEntity<MovimientoCreateRequest> crearHttpEntityConToken(
            MovimientoCreateRequest request,
            String authorizationHeader
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return new HttpEntity<>(request, headers);
    }
}