package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteCompletoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.service.ReporteService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @GetMapping("/resumen")
    public ResponseEntity<ReporteResumenResponse> obtenerReporteResumen(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReporteResumenResponse reporte = reporteService.generarReporteResumen(authorizationHeader);
        return ResponseEntity.ok(reporte);
    }

    @GetMapping("/completo")
    public ResponseEntity<ReporteCompletoResponse> obtenerReporteCompleto(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReporteCompletoResponse reporte = reporteService.generarReporteCompleto(authorizationHeader);
        return ResponseEntity.ok(reporte);
    }
}
