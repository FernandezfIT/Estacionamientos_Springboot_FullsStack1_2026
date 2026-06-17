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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
@Tag(name = "Movimientos", description = "Operaciones para registrar y consultar movimientos del sistema")
public class MovimientoController {

    private final MovimientoService movimientoService;

    // ENDPOINT que crea un movimiento
    @Operation(
        summary = "Crear movimiento",
        description = "Registra un movimiento en la bitácora central del sistema"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Movimiento creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<MovimientoResponse> crearMovimiento(
            @Valid @RequestBody MovimientoCreateRequest request
    ) {
        MovimientoResponse movimientoCreado = movimientoService.crearMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    // ENDPOINT que muestra todos los movimientos
    @Operation(
        summary = "Listar movimientos",
        description = "Obtiene todos los movimientos registrados"
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

    // ENDPOINT para listar movimientos por fecha
    @Operation(
        summary = "Listar movimientos por Fecha",
        description = "Obtiene movimientos filtrados por fecha"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/fecha/{fechaMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorFecha(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaMovimiento
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorFecha(fechaMovimiento);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT para buscar movimiento por id
    @Operation(
        summary = "Buscar movimientos por ID",
        description = "Obtiene movimientos filtrados por ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(
            @PathVariable Long idMovimiento
    ) {
        MovimientoResponse movimiento = movimientoService.obtenerMovimientoPorId(idMovimiento);
        return ResponseEntity.ok(movimiento);
    }

    // EDPOINT que muestra todos los movimientos asociados a un tipo (Acceso, liberacion, reserva, etc)
    @Operation(
        summary = "Listar movimientos por tipo",
        description = "Obtiene movimientos filtrados por tipo, por ejemplo ACCESO, RESERVA o SALIDA"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorTipo(
            @PathVariable String tipoMovimiento
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorTipo(tipoMovimiento);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los movimientos asociados a un RUT
        @Operation(
        summary = "Listar movimientos por RUT",
        description = "Obtiene movimientos filtrados por RUT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/rut/{rutUsuario}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorRut(
            @PathVariable String rutUsuario
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorRut(rutUsuario);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los moviemientos asociados a una patente
    @Operation(
        summary = "Listar movimientos por Patente",
        description = "Obtiene movimientos filtrados por Patente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorPatente(
            @PathVariable String patente
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorPatente(patente);
        return ResponseEntity.ok(movimientos);
    }

    // EDNPOINT que muestra todos los movimientos asociados a una plaza
    @Operation(
        summary = "Listar movimientos por Plaza",
        description = "Obtiene movimientos filtrados por Plaza"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/plaza/{codigoPlaza}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorCodigoPlaza(
            @PathVariable String codigoPlaza
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorCodigoPlaza(codigoPlaza);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los movimientos generados por un tipo de microservicio
    @Operation(
        summary = "Listar movimientos por Origen",
        description = "Obtiene movimientos filtrados por Origen"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Movimientos encontrados"),
        @ApiResponse(responseCode = "404", description = "Movimiento no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/origen/{servicioOrigen}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorServicioOrigen(
            @PathVariable String servicioOrigen
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorServicioOrigen(servicioOrigen);
        return ResponseEntity.ok(movimientos);
    }
}