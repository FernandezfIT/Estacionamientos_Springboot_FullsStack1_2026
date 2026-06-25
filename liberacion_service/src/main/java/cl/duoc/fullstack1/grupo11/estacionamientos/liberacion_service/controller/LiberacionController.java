package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.controller;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.request.LiberacionRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.response.LiberacionResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.service.LiberacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/liberaciones")
@RequiredArgsConstructor
@Tag(name = "Liberaciones", description = "Operaciones para gestionar liberaciones de plazas")
@SecurityRequirement(name = "bearer-jwt")
public class LiberacionController {

    private final LiberacionService liberacionService;

    @Operation(
            summary = "Listar liberaciones",
            description = "Obtiene la lista completa de liberaciones registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liberaciones listadas correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<LiberacionResponse>> listarLiberaciones() {
        List<LiberacionResponse> liberaciones = liberacionService.listarLiberaciones();
        return ResponseEntity.ok(liberaciones);
    }

    @Operation(
            summary = "Buscar liberación por ID",
            description = "Obtiene una liberación específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liberación encontrada"),
            @ApiResponse(responseCode = "404", description = "Liberación no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idLiberacion}")
    public ResponseEntity<LiberacionResponse> obtenerLiberacionPorId(
            @Parameter(description = "ID de la liberación", example = "1")
            @PathVariable Long idLiberacion) {
        LiberacionResponse liberacion = liberacionService.obtenerLiberacionPorId(idLiberacion);
        return ResponseEntity.ok(liberacion);
    }

    @Operation(
            summary = "Liberar plaza",
            description = "Registra la liberación de una plaza de estacionamiento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Liberación creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Plaza o movimiento no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<LiberacionResponse> liberarPlaza(
            @Valid @RequestBody LiberacionRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        LiberacionResponse liberacionCreada = liberacionService.liberarPlaza(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(liberacionCreada);
    }
}
