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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios, roles y consultas internas")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // ENDPOINT para listar todos los vehículos
    @Operation(
        summary = "Listar vehículos",
        description = "Obtiene todos los vehículos registrados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículos listados correctamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
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

    // ENDPOINT para buscar vehículo por patente
    @Operation(
        summary = "Buscar vehículo por patente",
        description = "Obtiene un vehículo usando su patente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
        @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
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

   
    @Operation(
        summary = "Crear vehículo",
        description = "Registra un vehículo asociado a un usuario existente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Vehículo creado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario o tipo de vehículo no encontrado"),
        @ApiResponse(responseCode = "409", description = "Patente duplicada"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
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