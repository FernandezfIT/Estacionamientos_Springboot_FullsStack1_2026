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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/vehiculos")
@RequiredArgsConstructor
@Tag(name = "Vehículos", description = "Operaciones para gestionar vehículos registrados")
@SecurityRequirement(name = "bearer-jwt")
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // ENDPOINT QUE LISTA TODOS LOS VEHÍCULOS REGISTRADOS
    @Operation(
            summary = "Listar vehículos",
            description = "Obtiene todos los vehículos registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículos listados correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<VehiculoResponse>> listarVehiculos() {
        List<VehiculoResponse> vehiculos = vehiculoService.listarVehiculos();
        return ResponseEntity.ok(vehiculos);
    }

    // ENDPOINT QUE BUSCA UN VEHÍCULO POR SU ID
    @Operation(
            summary = "Buscar vehículo por ID",
            description = "Obtiene un vehículo específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorId(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long idVehiculo
    ) {
        VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorId(idVehiculo);
        return ResponseEntity.ok(vehiculo);
    }

    // ENDPOINT QUE BUSCA UN VEHÍCULO POR PATENTE
    @Operation(
            summary = "Buscar vehículo por patente",
            description = "Obtiene un vehículo usando su patente."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<VehiculoResponse> obtenerVehiculoPorPatente(
            @Parameter(description = "Patente del vehículo", example = "ABCD12")
            @PathVariable String patente
    ) {
        VehiculoResponse vehiculo = vehiculoService.obtenerVehiculoPorPatente(patente);
        return ResponseEntity.ok(vehiculo);
    }

    // ENDPOINT QUE LISTA LOS VEHÍCULOS ASOCIADOS A UN USUARIO
    @Operation(
            summary = "Listar vehículos por usuario",
            description = "Obtiene todos los vehículos asociados a un usuario específico."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículos encontrados"),
            @ApiResponse(responseCode = "404", description = "Usuario o vehículos no encontrados"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<VehiculoResponse>> listarVehiculosPorUsuario(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Long idUsuario
    ) {
        List<VehiculoResponse> vehiculos = vehiculoService.listarVehiculosPorUsuario(idUsuario);
        return ResponseEntity.ok(vehiculos);
    }

    // ENDPOINT QUE CREA UN NUEVO VEHÍCULO
    @Operation(
            summary = "Crear vehículo",
            description = "Registra un vehículo asociado a un usuario existente."
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

    // ENDPOINT QUE ACTUALIZA UN VEHÍCULO EXISTENTE
    @Operation(
            summary = "Actualizar vehículo",
            description = "Actualiza los datos de un vehículo existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Vehículo, usuario o tipo de vehículo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Patente duplicada"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{idVehiculo}")
    public ResponseEntity<VehiculoResponse> actualizarVehiculo(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long idVehiculo,
            @Valid @RequestBody VehiculoUpdateRequest request
    ) {
        VehiculoResponse vehiculoActualizado = vehiculoService.actualizarVehiculo(idVehiculo, request);
        return ResponseEntity.ok(vehiculoActualizado);
    }

    // ENDPOINT QUE ELIMINA UN VEHÍCULO EXISTENTE
    @Operation(
            summary = "Eliminar vehículo",
            description = "Elimina un vehículo existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{idVehiculo}")
    public ResponseEntity<Void> eliminarVehiculo(
            @Parameter(description = "ID del vehículo", example = "1")
            @PathVariable Long idVehiculo
    ) {
        vehiculoService.eliminarVehiculo(idVehiculo);
        return ResponseEntity.noContent().build();
    }
}