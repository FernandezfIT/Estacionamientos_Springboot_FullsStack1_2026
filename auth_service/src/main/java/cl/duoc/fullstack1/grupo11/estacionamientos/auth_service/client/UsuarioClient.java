package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.dto.UsuarioAuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.exception.UsuarioServiceEx;

@Component
public class UsuarioClient {

    private final RestTemplate restTemplate;
    private final String usuarioServiceBaseUrl;

    public UsuarioClient(
            @Qualifier("usuarioRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.usuario.base-url}") String usuarioServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.usuarioServiceBaseUrl = usuarioServiceBaseUrl;
    }

    public Optional<UsuarioAuthResponse> buscarPorEmail(String email) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(usuarioServiceBaseUrl)
                    .path("/auth/email/{email}")
                    .buildAndExpand(email)
                    .toUriString();

            UsuarioAuthResponse usuario = restTemplate.getForObject(url, UsuarioAuthResponse.class);

            return Optional.ofNullable(usuario);

        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();

        } catch (RestClientException ex) {
            throw new UsuarioServiceEx("No fue posible consultar usuario_service", ex);
        }
    }
}