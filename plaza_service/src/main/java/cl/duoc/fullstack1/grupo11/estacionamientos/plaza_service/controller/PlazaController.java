package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.response.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.service.PlazaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/plazas")
@RequiredArgsConstructor
@Tag(name = "Plazas", description = "Operaciones para gestionar plazas de estacionamiento")
@SecurityRequirement(name = "bearer-jwt")
public class PlazaController {

    private final PlazaService plazaService;

    @Operation(
            summary = "Listar plazas",
            description = "Obtiene la lista completa de plazas de estacionamiento registradas."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plazas listadas correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PlazaResponse>> listarPlazas() {
        List<PlazaResponse> plazas = plazaService.listarPlazas();
        return ResponseEntity.ok(plazas);
    }

    @Operation(
            summary = "Buscar plaza por ID",
            description = "Obtiene una plaza específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plaza encontrada"),
            @ApiResponse(responseCode = "404", description = "Plaza no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idPlaza}")
    public ResponseEntity<PlazaResponse> obtenerPlazaPorId(
            @Parameter(description = "ID de la plaza", example = "1")
            @PathVariable Long idPlaza) {
        PlazaResponse plaza = plazaService.obtenerPlazaPorId(idPlaza);
        return ResponseEntity.ok(plaza);
    }

    @Operation(
            summary = "Buscar plaza por código",
            description = "Obtiene una plaza específica usando su código alfanumérico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plaza encontrada"),
            @ApiResponse(responseCode = "404", description = "Plaza no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/codigo/{codigoPlaza}")
    public ResponseEntity<PlazaResponse> obtenerPlazaPorCodigo(
            @Parameter(description = "Código de la plaza", example = "A01")
            @PathVariable String codigoPlaza) {
        PlazaResponse plaza = plazaService.obtenerPlazaPorCodigo(codigoPlaza);
        return ResponseEntity.ok(plaza);
    }

    @Operation(
            summary = "Actualizar estado de plaza",
            description = "Actualiza el estado de una plaza existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Estado de plaza actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Plaza no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{idPlaza}")
    public ResponseEntity<PlazaResponse> actualizarEstadoPlaza(
            @Parameter(description = "ID de la plaza", example = "1")
            @PathVariable Long idPlaza,
            @Valid @RequestBody PlazaUpdateRequest request
    ) {
        PlazaResponse plazaActualizada = plazaService.actualizarEstadoPlaza(idPlaza, request);
        return ResponseEntity.ok(plazaActualizada);
    }

    @Operation(
            summary = "Ocupar plaza por código",
            description = "Marca una plaza como ocupada usando su código."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plaza ocupada correctamente"),
            @ApiResponse(responseCode = "404", description = "Plaza no encontrada"),
            @ApiResponse(responseCode = "409", description = "Plaza no disponible"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/codigo/{codigoPlaza}/ocupar")
    public ResponseEntity<PlazaResponse> ocuparPlazaPorCodigo(
            @Parameter(description = "Código de la plaza", example = "A01")
            @PathVariable String codigoPlaza) {
        PlazaResponse plazaOcupada = plazaService.ocuparPlazaPorCodigo(codigoPlaza);
        return ResponseEntity.ok(plazaOcupada);
    }
}