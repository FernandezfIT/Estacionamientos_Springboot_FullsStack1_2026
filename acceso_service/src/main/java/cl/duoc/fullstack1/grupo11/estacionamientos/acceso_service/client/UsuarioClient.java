package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.UsuarioInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.MicroservicioNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.UsuarioNoEncontradoException;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String usuarioServiceBaseUrl;

    public UsuarioClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.usuario.base-url}") String usuarioServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.usuarioServiceBaseUrl = usuarioServiceBaseUrl;
    }

    public UsuarioInternoResponse buscarUsuarioPorRut(String rut) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(usuarioServiceBaseUrl)
                    .path("/internal/rut/{rut}")
                    .buildAndExpand(rut)
                    .toUriString();

            return restTemplate.getForObject(url, UsuarioInternoResponse.class);

        } catch (HttpClientErrorException.NotFound ex) {
            throw new UsuarioNoEncontradoException("No existe un usuario con el RUT " + rut);

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException("No fue posible conectar con usuario_service", ex);
        }
    }
}