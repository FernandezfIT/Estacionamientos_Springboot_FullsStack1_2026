package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.request.CierreEjecutarRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.response.CierreResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.service.CierreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cierres")
@RequiredArgsConstructor
public class CierreController {

    private final CierreService cierreService;

    // ENDPOINT que ejecuta el cierre
    @PostMapping("/ejecutar")
    public ResponseEntity<CierreResponse> ejecutarCierre(
            @Valid @RequestBody(required = false) CierreEjecutarRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        CierreResponse cierreEjecutado = cierreService.ejecutarCierre(
                request,
                authorizationHeader
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cierreEjecutado);
    }

    // ENDPOINT que lista historico de cierres
    @GetMapping
    public ResponseEntity<List<CierreResponse>> listarCierres() {
        List<CierreResponse> cierres = cierreService.listarCierres();
        return ResponseEntity.ok(cierres);
    }

    // ENDPOINT para buscar cierre por ID
    @GetMapping("/{idCierre}")
    public ResponseEntity<CierreResponse> obtenerCierrePorId(
            @PathVariable Long idCierre
    ) {
        CierreResponse cierre = cierreService.obtenerCierrePorId(idCierre);
        return ResponseEntity.ok(cierre);
    }

    //ENDPOINT para buscar cierre por fecha
    @GetMapping("/fecha/{fechaCierre}")
    public ResponseEntity<List<CierreResponse>> listarCierresPorFecha(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaCierre
    ) {
        List<CierreResponse> cierres = cierreService.listarCierresPorFecha(fechaCierre);
        return ResponseEntity.ok(cierres);
    }
}