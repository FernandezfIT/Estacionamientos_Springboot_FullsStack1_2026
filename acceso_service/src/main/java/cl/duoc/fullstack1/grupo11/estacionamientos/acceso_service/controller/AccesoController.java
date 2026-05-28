package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.request.AccesoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.response.AccesoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.service.AccesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    // ENDPOINT que genera acceso
    @PostMapping
    public ResponseEntity<AccesoResponse> registrarAcceso(
            @Valid @RequestBody AccesoCreateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        AccesoResponse accesoRegistrado = accesoService.registrarAcceso(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(accesoRegistrado);
    }

    // ENDPOINT que lista los accesos existentes
    @GetMapping
    public ResponseEntity<List<AccesoResponse>> listarAccesos() {
        List<AccesoResponse> accesos = accesoService.listarAccesos();
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra acceso por id
    @GetMapping("/{idAcceso}")
    public ResponseEntity<AccesoResponse> obtenerAccesoPorId(@PathVariable Long idAcceso) {
        AccesoResponse acceso = accesoService.obtenerAccesoPorId(idAcceso);
        return ResponseEntity.ok(acceso);
    }

    // ENDPINT que muestra los accesos de un rut
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorRut(@PathVariable String rut) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorRut(rut);
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra los accesos de una patente
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorPatente(@PathVariable String patente) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorPatente(patente);
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra los accessos de una plaza
    @GetMapping("/plaza/{codigoPlaza}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorCodigoPlaza(
            @PathVariable String codigoPlaza
    ) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorCodigoPlaza(codigoPlaza);
        return ResponseEntity.ok(accesos);
    }
}