package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.request.ReservaRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.response.ReservaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservas")
@RequiredArgsConstructor
public class ReservaController {

    private final ReservaService reservaService;

    @GetMapping
    public ResponseEntity<List<ReservaResponse>> listarReservas() {
        List<ReservaResponse> reservas = reservaService.listarReservas();
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/{idReserva}")
    public ResponseEntity<ReservaResponse> obtenerReservaPorId(@PathVariable Long idReserva) {
        ReservaResponse reserva = reservaService.obtenerReservaPorId(idReserva);
        return ResponseEntity.ok(reserva);
    }

    @PostMapping
    public ResponseEntity<ReservaResponse> crearReserva(
            @Valid @RequestBody ReservaRequest request
    ) {
        ReservaResponse reservaCreada = reservaService.crearReserva(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    @DeleteMapping("/{idReserva}")
    public ResponseEntity<Void> eliminarReserva(@PathVariable Long idReserva) {
        reservaService.eliminarReserva(idReserva);
        return ResponseEntity.noContent().build();
    }
}
