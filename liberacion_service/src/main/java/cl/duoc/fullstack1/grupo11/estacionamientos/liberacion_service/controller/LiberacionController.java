package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.request.LiberacionRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.response.LiberacionResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.service.LiberacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/liberaciones")
@RequiredArgsConstructor
public class LiberacionController {

    private final LiberacionService liberacionService;

    @GetMapping
    public ResponseEntity<List<LiberacionResponse>> listarLiberaciones() {
        List<LiberacionResponse> liberaciones = liberacionService.listarLiberaciones();
        return ResponseEntity.ok(liberaciones);
    }

    @GetMapping("/{idLiberacion}")
    public ResponseEntity<LiberacionResponse> obtenerLiberacionPorId(@PathVariable Long idLiberacion) {
        LiberacionResponse liberacion = liberacionService.obtenerLiberacionPorId(idLiberacion);
        return ResponseEntity.ok(liberacion);
    }

    @PostMapping
    public ResponseEntity<LiberacionResponse> liberarPlaza(
            @Valid @RequestBody LiberacionRequest request
    ) {
        LiberacionResponse liberacionCreada = liberacionService.liberarPlaza(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(liberacionCreada);
    }
}
