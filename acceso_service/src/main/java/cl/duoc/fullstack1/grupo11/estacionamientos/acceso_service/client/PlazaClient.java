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

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.MicroservicioNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.PlazaNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.PlazaNoEncontradaException;

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

    public PlazaInternaResponse buscarPlazaPorCodigo(
            String codigoPlaza,
            String authorizationHeader
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(plazaServiceBaseUrl)
                    .path("/codigo/{codigoPlaza}")
                    .buildAndExpand(codigoPlaza)
                    .toUriString();

            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<PlazaInternaResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    PlazaInternaResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new PlazaNoEncontradaException("No existe una plaza con código " + codigoPlaza);

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException("No fue posible conectar con plaza_service", ex);
        }
    }

    public PlazaInternaResponse ocuparPlazaPorCodigo(
            String codigoPlaza,
            String authorizationHeader
    ) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(plazaServiceBaseUrl)
                    .path("/codigo/{codigoPlaza}/ocupar")
                    .buildAndExpand(codigoPlaza)
                    .toUriString();

            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<PlazaInternaResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    PlazaInternaResponse.class
            );

            return response.getBody();

        } catch (HttpClientErrorException.NotFound ex) {
            throw new PlazaNoEncontradaException("No existe una plaza con código " + codigoPlaza);

        } catch (HttpClientErrorException.Conflict ex) {
            throw new PlazaNoDisponibleException("La plaza " + codigoPlaza + " no está disponible");

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException("No fue posible conectar con plaza_service", ex);
        }
    }

    private HttpEntity<Void> crearHttpEntityConToken(String authorizationHeader) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return new HttpEntity<>(headers);
    }
}