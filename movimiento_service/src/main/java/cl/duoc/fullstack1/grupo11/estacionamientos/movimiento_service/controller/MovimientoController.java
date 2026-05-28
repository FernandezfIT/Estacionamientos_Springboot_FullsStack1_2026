package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.controller;

import java.util.List;

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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/movimientos")
@RequiredArgsConstructor
public class MovimientoController {

    private final MovimientoService movimientoService;

    // ENDPOINT que crea un movimiento
    @PostMapping
    public ResponseEntity<MovimientoResponse> crearMovimiento(
            @Valid @RequestBody MovimientoCreateRequest request
    ) {
        MovimientoResponse movimientoCreado = movimientoService.crearMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoCreado);
    }

    // ENDPOINT que muestra todos los movimientos
    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos() {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientos();
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT para buscar movimiento por id
    @GetMapping("/{idMovimiento}")
    public ResponseEntity<MovimientoResponse> obtenerMovimientoPorId(
            @PathVariable Long idMovimiento
    ) {
        MovimientoResponse movimiento = movimientoService.obtenerMovimientoPorId(idMovimiento);
        return ResponseEntity.ok(movimiento);
    }

    // EDPOINT que muestra todos los movimientos asociados a un tipo (Acceso, liberacion, reserva, etc)
    @GetMapping("/tipo/{tipoMovimiento}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorTipo(
            @PathVariable String tipoMovimiento
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorTipo(tipoMovimiento);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los movimientos asociados a un RUT
    @GetMapping("/rut/{rutUsuario}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorRut(
            @PathVariable String rutUsuario
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorRut(rutUsuario);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los moviemientos asociados a una patente
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorPatente(
            @PathVariable String patente
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorPatente(patente);
        return ResponseEntity.ok(movimientos);
    }

    // EDNPOINT que muestra todos los movimientos asociados a una plaza
    @GetMapping("/plaza/{codigoPlaza}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorCodigoPlaza(
            @PathVariable String codigoPlaza
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorCodigoPlaza(codigoPlaza);
        return ResponseEntity.ok(movimientos);
    }

    // ENDPOINT que muestra todos los movimientos generados por un tipo de microservicio
    @GetMapping("/origen/{servicioOrigen}")
    public ResponseEntity<List<MovimientoResponse>> listarMovimientosPorServicioOrigen(
            @PathVariable String servicioOrigen
    ) {
        List<MovimientoResponse> movimientos = movimientoService.listarMovimientosPorServicioOrigen(servicioOrigen);
        return ResponseEntity.ok(movimientos);
    }
}