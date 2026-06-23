package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.controller;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.request.ReservaRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.response.ReservaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
@Tag(name = "Reservas", description = "Operaciones para gestionar reservas")
@SecurityRequirement(name = "bearer-jwt")
public class ReservaController {

    private final ReservaService reservaService;

    @Operation(
            summary = "Listar reservas",
            description = "Obtiene la lista completa de reservas registradas en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservas listadas correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarReservas() {
        List<ReservaResponse> reservas = reservaService.listarReservas();
        return ResponseEntity.ok(reservas);
    }

    @Operation(
            summary = "Buscar reserva por ID",
            description = "Obtiene una reserva específica usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reserva encontrada"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponse> obtenerReservaPorId(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long idReserva) {
        ReservaResponse reserva = reservaService.obtenerReservaPorId(idReserva);
        return ResponseEntity.ok(reserva);
    }

    @Operation(
            summary = "Crear reserva",
            description = "Registra una nueva reserva en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reserva creada correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Conflicto - plaza no disponible"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(
            @Valid @RequestBody ReservaRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        ReservaResponse reservaCreada = reservaService.crearReserva(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    @Operation(
            summary = "Eliminar reserva",
            description = "Elimina una reserva existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reserva eliminada correctamente"),
            @ApiResponse(responseCode = "404", description = "Reserva no encontrada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> eliminarReserva(
            @Parameter(description = "ID de la reserva", example = "1")
            @PathVariable Long idReserva) {
        reservaService.eliminarReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
