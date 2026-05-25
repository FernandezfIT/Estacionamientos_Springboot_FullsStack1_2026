package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response.VehiculoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service.VehiculoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
public class VehiculoController {

    private final VehiculoService vehiculoService;

    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listarVehiculos() {
        List<VehiculoResponse> vehiculos = vehiculoService.listarVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorId(
            @PathVariable Long idVehiculo
    ) {
        VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorId(idVehiculo);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorPatente(
            @PathVariable String patente
    ) {
        VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorPatente(patente);
        return ResponseEntity.ok(vehiculo);
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<VehiculoResponse>> listarVehiculosPorUsuario(
            @PathVariable Long idUsuario
    ) {
        List<VehiculoResponse> vehiculos = vehiculoService.listarVehiculosPorUsuario(idUsuario);
        return ResponseEntity.ok(vehiculos);
    }

    @PostMapping
    public ResponseEntity<VehiculoResponse> crearVehiculo(
            @Valid @RequestBody VehiculoCreateRequest request
    ) {
        VehiculoResponse vehiculoCreado = vehiculoService.crearVehiculo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoCreado);
    }

    @PutMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponse> actualizarVehiculo(
            @PathVariable Long idVehiculo,
            @Valid @RequestBody VehiculoUpdateRequest request
    ) {
        VehiculoResponse vehiculoActualizado = vehiculoService.actualizarVehiculo(idVehiculo, request);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<Void> eliminarVehiculo(
            @PathVariable Long idVehiculo
    ) {
        vehiculoService.eliminarVehiculo(idVehiculo);
        return ResponseEntity.noContent().build();
    }
}