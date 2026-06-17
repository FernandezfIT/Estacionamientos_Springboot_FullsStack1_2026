package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.controller;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.request.AccesoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.response.AccesoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.service.AccesoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/accesos")
@RequiredArgsConstructor
@Tag(name = "Accesos", description = "Operaciones para registrar y consultar accesos al estacionamiento")
public class AccesoController {

    private final AccesoService accesoService;

    // ENDPOINT que genera acceso
    @Operation(
        summary = "Registrar acceso",
        description = "Registra el ingreso de un vehículo al estacionamiento. Valida usuario, vehículo y plaza disponible"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Acceso registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "404", description = "Usuario, vehículo o plaza no encontrado"),
        @ApiResponse(responseCode = "409", description = "La plaza no está disponible"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<AccesoResponse> registrarAcceso(
            @Valid @RequestBody AccesoCreateRequest request,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
    ) {
        AccesoResponse accesoRegistrado = accesoService.registrarAcceso(request, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(accesoRegistrado);
    }

    // ENDPOINT que lista los accesos existentes
    @Operation(
        summary = "Listar accesos",
        description = "Obtiene todos los accesos registrados"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accesos listados correctamente"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping
    public ResponseEntity<List<AccesoResponse>> listarAccesos() {
        List<AccesoResponse> accesos = accesoService.listarAccesos();
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra acceso por id
    @Operation(
        summary = "Buscar accesos por ID",
        description = "Obtiene acceso identificado por nro de ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accesos encontrados"),
        @ApiResponse(responseCode = "404", description = "Acceso no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idAcceso}")
    public ResponseEntity<AccesoResponse> obtenerAccesoPorId(@PathVariable Long idAcceso) {
        AccesoResponse acceso = accesoService.obtenerAccesoPorId(idAcceso);
        return ResponseEntity.ok(acceso);
    }

    // ENDPINT que muestra los accesos de un rut
    @Operation(
        summary = "Buscar accesos por RUT",
        description = "Obtiene los accesos asociados a un RUT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accesos encontrados"),
        @ApiResponse(responseCode = "404", description = "Acceso no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/rut/{rut}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorRut(@PathVariable String rut) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorRut(rut);
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra los accesos de una patente
    @Operation(
        summary = "Buscar accesos por patente",
        description = "Obtiene los accesos asociados a una patente"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accesos encontrados"),
        @ApiResponse(responseCode = "404", description = "Acceso no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/patente/{patente}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorPatente(@PathVariable String patente) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorPatente(patente);
        return ResponseEntity.ok(accesos);
    }

    // ENDPOINT que muestra los accessos de una plaza
    @Operation(
        summary = "Buscar accesos por plaza",
        description = "Obtiene los accesos asociados a una plaza"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accesos encontrados"),
        @ApiResponse(responseCode = "404", description = "Acceso no encontrado"),
        @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/plaza/{codigoPlaza}")
    public ResponseEntity<List<AccesoResponse>> listarAccesosPorCodigoPlaza(
            @PathVariable String codigoPlaza
    ) {
        List<AccesoResponse> accesos = accesoService.listarAccesosPorCodigoPlaza(codigoPlaza);
        return ResponseEntity.ok(accesos);
    }
}