package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.request.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.response.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.service.MovimientoService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Operaciones para consultar movimientos del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class MovimientoController {

    private final MovimientoService movimientoService;

    // ENDPOINT INTERNO USADO POR OTROS MICROSERVICIOS PARA REGISTRAR MOVIMIENTOS. NO SE MUESTRA EN SWAGGER.
    @Hidden
    @PostMapping
    public ResponseEntity<MovimientoResponse> crearMovimiento(
            @Valid @RequestBody MovimientoCreateRequest request
    ) {
        MovimientoResponse movimientoCreado = movimientoService.crearMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    // ENDPOINT QUE LISTA TODOS LOS MOVIMIENTOS REGISTRADOS
    @Operation(
            summary = "Listar movimientos",
            description = "Obtiene todos los movimientos registrados en la bitácora central."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos listados correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos() {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR FECHA
    @Operation(
            summary = "Listar movimientos por fecha",
            description = "Obtiene movimientos filtrados por una fecha específica."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/fecha/{fechaMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorFecha(
            @Parameter(description = "Fecha del movimiento", example = "2026-06-11")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaMovimiento
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorFecha(fechaMovimiento);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE BUSCA UN MOVIMIENTO POR SU ID
    @Operation(
            summary = "Buscar movimiento por ID",
            description = "Obtiene un movimiento específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(
            @Parameter(description = "ID del movimiento", example = "1")
            @PathVariable Long idMovimiento
    ) {
        MovimientoResponse movimiento = movimientoService.obtenerMovimientoPorId(idMovimiento);
        return ResponseEntity.ok(movimiento);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR TIPO
    @Operation(
            summary = "Listar movimientos por tipo",
            description = "Obtiene movimientos filtrados por tipo. Ejemplos: ACCESO, RESERVA o SALIDA."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorTipo(
            @Parameter(description = "Tipo de movimiento", example = "ACCESO")
            @PathVariable String tipoMovimiento
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorTipo(tipoMovimiento);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR RUT DE USUARIO O VISITA
    @Operation(
            summary = "Listar movimientos por RUT",
            description = "Obtiene movimientos filtrados por RUT de usuario o visita."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/rut/{rutUsuario}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorRut(
            @Parameter(description = "RUT del usuario o visita", example = "33333333-3")
            @PathVariable String rutUsuario
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorRut(rutUsuario);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR PATENTE
    @Operation(
            summary = "Listar movimientos por patente",
            description = "Obtiene movimientos filtrados por patente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorPatente(
            @Parameter(description = "Patente del vehículo", example = "ABCD12")
            @PathVariable String patente
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorPatente(patente);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR CÓDIGO DE PLAZA
    @Operation(
            summary = "Listar movimientos por plaza",
            description = "Obtiene movimientos filtrados por código de plaza."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/plaza/{codigoPlaza}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorCodigoPlaza(
            @Parameter(description = "Código de la plaza", example = "P30")
            @PathVariable String codigoPlaza
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorCodigoPlaza(codigoPlaza);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT QUE LISTA MOVIMIENTOS POR MICROSERVICIO DE ORIGEN
    @Operation(
            summary = "Listar movimientos por servicio de origen",
            description = "Obtiene movimientos filtrados por el microservicio que los generó."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/origen/{servicioOrigen}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorServicioOrigen(
            @Parameter(description = "Servicio de origen", example = "acceso_service")
            @PathVariable String servicioOrigen
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorServicioOrigen(servicioOrigen);
        return ResponseEntity.ok(movimientos);
    }
}