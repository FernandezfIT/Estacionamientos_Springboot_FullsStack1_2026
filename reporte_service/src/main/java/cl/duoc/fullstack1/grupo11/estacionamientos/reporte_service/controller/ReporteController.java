package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteCompletoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.service.ReporteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reportes")
@RequiredArgsConstructor
@Tag(name = "Reportes", description = "Operaciones para generar reportes del estacionamiento")
@SecurityRequirement(name = "bearer-jwt")
public class ReporteController {

    private final ReporteService reporteService;

    @Operation(
            summary = "Obtener reporte resumen",
            description = "Genera un reporte resumen del estacionamiento con datos generales."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/resumen")
    public ResponseEntity<ReporteResumenResponse> obtenerReporteResumen(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReporteResumenResponse reporte = reporteService.generarReporteResumen(authorizationHeader);
        return ResponseEntity.ok(reporte);
    }

    @Operation(
            summary = "Obtener reporte completo",
            description = "Genera un reporte completo del estacionamiento con todos los detalles."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/completo")
    public ResponseEntity<ReporteCompletoResponse> obtenerReporteCompleto(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReporteCompletoResponse reporte = reporteService.generarReporteCompleto(authorizationHeader);
        return ResponseEntity.ok(reporte);
    }

    @Operation(
            summary = "Obtener reporte resumen por fecha",
            description = "Genera un reporte resumen filtrado por una fecha específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reporte generado correctamente"),
            @ApiResponse(responseCode = "400", description = "Fecha inválida"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/resumen/fecha/{fecha}")
    public ResponseEntity<ReporteResumenResponse> obtenerReporteResumenPorFecha(
            @Parameter(description = "Fecha del reporte (formato ISO: yyyy-MM-dd)", example = "2026-06-23")
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fecha,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReporteResumenResponse reporte = reporteService.generarReporteResumenPorFecha(
                fecha,
                authorizationHeader
        );
    
        return ResponseEntity.ok(reporte);
    }
}
