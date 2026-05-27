package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.response.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.service.PlazaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/plazas")
@RequiredArgsConstructor
public class PlazaController {

    private final PlazaService plazaService;

    @GetMapping
    public ResponseEntity<List<PlazaResponse>> listarPlazas() {
        List<PlazaResponse> plazas = plazaService.listarPlazas();
        return ResponseEntity.ok(plazas);
    }

    @GetMapping("/{idPlaza}")
    public ResponseEntity<PlazaResponse> obtenerPlazaPorId(@PathVariable Long idPlaza) {
        PlazaResponse plaza = plazaService.obtenerPlazaPorId(idPlaza);
        return ResponseEntity.ok(plaza);
    }

    @GetMapping("/codigo/{codigoPlaza}")
    public ResponseEntity<PlazaResponse> obtenerPlazaPorCodigo(@PathVariable String codigoPlaza) {
        PlazaResponse plaza = plazaService.obtenerPlazaPorCodigo(codigoPlaza);
        return ResponseEntity.ok(plaza);
    }

    @PutMapping("/{idPlaza}")
    public ResponseEntity<PlazaResponse> actualizarEstadoPlaza(
            @PathVariable Long idPlaza,
            @Valid @RequestBody PlazaUpdateRequest request
    ) {
        PlazaResponse plazaActualizada = plazaService.actualizarEstadoPlaza(idPlaza, request);
        return ResponseEntity.ok(plazaActualizada);
    }

    @PutMapping("/codigo/{codigoPlaza}/ocupar")
    public ResponseEntity<PlazaResponse> ocuparPlazaPorCodigo(@PathVariable String codigoPlaza) {
        PlazaResponse plazaOcupada = plazaService.ocuparPlazaPorCodigo(codigoPlaza);
        return ResponseEntity.ok(plazaOcupada);
    }
}