package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.VehiculoInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.MicroservicioNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.VehiculoNoEncontradoException;

@Component
public class VehiculoClient {

    private final RestTemplate restTemplate;
    private final String vehiculoServiceBaseUrl;

    public VehiculoClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.vehiculo.base-url}") String vehiculoServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.vehiculoServiceBaseUrl = vehiculoServiceBaseUrl;
    }

    public VehiculoInternoResponse buscarVehiculoPorPatente(
            String patente,
            String authorizationHeader
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(vehiculoServiceBaseUrl)
                    .path("/patente/{patente}")
                    .buildAndExpand(patente)
                    .toUriString();

            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<VehiculoInternoResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    VehiculoInternoResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new VehiculoNoEncontradoException("No existe un vehículo con la patente " + patente);

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException("No fue posible conectar con vehiculo_service", ex);
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return new HttpEntity<>(headers);
    }
}