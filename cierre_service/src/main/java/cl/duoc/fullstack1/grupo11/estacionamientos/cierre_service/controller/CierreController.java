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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cierres")
@RequiredArgsConstructor
@Tag(name = "Cierres", description = "Operaciones para ejecutar y consultar cierres operativos")
public class CierreController {

    private final CierreService cierreService;

    // ENDPOINT que ejecuta el cierre
    @Operation(
        summary = "Ejecutar cierre operativo",
        description = "Libera plazas ocupadas, elimina reservas existentes, consume reporte diario y guarda un resumen histórico"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Cierre ejecutado correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
        @ApiResponse(responseCode = "403", description = "No autorizado"),
        @ApiResponse(responseCode = "503", description = "Microservicio dependiente no disponible")
    })
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
    @Operation(
        summary = "Listar cierres",
        description = "Obtiene el historial completo de cierres ejecutados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cierres listados correctamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<CierreResponse>> listarCierres() {
        List<CierreResponse> cierres = cierreService.listarCierres();
        return ResponseEntity.ok(cierres);
    }

    // ENDPOINT para buscar cierre por ID
        @Operation(
        summary = "Buscar Cierre por ID",
        description = "Obtiene cierre filtrado por ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cierres listados correctamente"),
        @ApiResponse(responseCode = "404", description = "Cierre no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idCierre}")
    public ResponseEntity<CierreResponse> obtenerCierrePorId(
            @PathVariable Long idCierre
    ) {
        CierreResponse cierre = cierreService.obtenerCierrePorId(idCierre);
        return ResponseEntity.ok(cierre);
    }

    //ENDPOINT para buscar cierre por fecha
        @Operation(
        summary = "Buscar Cierre por Fecha",
        description = "Obtiene cierre filtrado por Fecha"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cierres listados correctamente"),
        @ApiResponse(responseCode = "404", description = "Cierre no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/fecha/{fechaCierre}")
    public ResponseEntity<List<CierreResponse>> listarCierresPorFecha(
            @Parameter(description = "Fecha del cierre", example = "2026-06-11")
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaCierre
    ) {
        List<CierreResponse> cierres = cierreService.listarCierresPorFecha(fechaCierre);
        return ResponseEntity.ok(cierres);
    }
}