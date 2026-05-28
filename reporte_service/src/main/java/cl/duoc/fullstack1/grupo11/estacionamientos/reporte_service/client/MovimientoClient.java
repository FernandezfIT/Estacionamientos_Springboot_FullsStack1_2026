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

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.exception.MicroservicioNoDisponibleException;

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

    public List<MovimientoResponse> listarMovimientos(String authorizationHeader) {
        try {
            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<MovimientoResponse[]> response = restTemplate.exchange(
                    movimientoServiceBaseUrl,
                    HttpMethod.GET,
                    entity,
                    MovimientoResponse[].class
            );

            MovimientoResponse[] movimientos = response.getBody();
            return movimientos != null ? Arrays.asList(movimientos) : List.of();

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible obtener los movimientos desde movimiento_service", ex);
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }
}
