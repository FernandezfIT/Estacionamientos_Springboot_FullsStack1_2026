package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.ReporteResumenResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.MicroservicioNoDisponibleException;

@Component
public class ReporteClient {

    private final RestTemplate restTemplate;
    private final String reporteServiceBaseUrl;

    public ReporteClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.reporte.base-url}") String reporteServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.reporteServiceBaseUrl = reporteServiceBaseUrl;
    }

    public ReporteResumenResponse obtenerResumen(String authorizationHeader) {
        try {
            String url = UriComponentsBuilder
                    .fromUriString(reporteServiceBaseUrl)
                    .path("/resumen")
                    .toUriString();

            HttpEntity<Void> entity = crearHttpEntityConToken(authorizationHeader);

            ResponseEntity<ReporteResumenResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    ReporteResumenResponse.class
            );

            return response.getBody();

        } catch (RestClientException ex) {
            throw new MicroservicioNoDisponibleException(
                    "No fue posible conectar con reporte_service",
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