package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response.TipoVehiculoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service.TipoVehiculoService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/tipos-vehiculos")
@RequiredArgsConstructor
public class TipoVehiculoController {

    private final TipoVehiculoService tipoVehiculoService;

    @GetMapping
    public ResponseEntity<List<TipoVehiculoResponse>> listarTiposVehiculo() {
        List<TipoVehiculoResponse> tiposVehiculo = tipoVehiculoService.listarTiposVehiculo();
        return ResponseEntity.ok(tiposVehiculo);
    }

    @GetMapping("/{idTipoVehiculo}")
    public ResponseEntity<TipoVehiculoResponse> obtenerTipoVehiculoPorId(
            @PathVariable Long idTipoVehiculo
    ) {
        TipoVehiculoResponse tipoVehiculo = tipoVehiculoService.obtenerTipoVehiculoPorId(idTipoVehiculo);
        return ResponseEntity.ok(tipoVehiculo);
    }
}