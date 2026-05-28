package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.request.ReservaRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.response.ReservaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.exception.PlazaServiceException;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.exception.ReservaNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.model.Reserva;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final PlazaClient plazaClient;
    private final MovimientoClient movimientoClient;

    @Transactional
    public ReservaResponse crearReserva(ReservaRequest request, String authorizationHeader) {
        //PlazaResponse plaza = plazaClient.obtenerPlaza(request.getIdPlaza());
        PlazaResponse plaza = plazaClient.obtenerPlaza(
            request.getIdPlaza(),
            authorizationHeader
        );

        if (plaza == null) {
            throw new IllegalArgumentException(
                    "No existe una plaza con ID " + request.getIdPlaza());
        }

        if (!"Disponible".equals(plaza.getEstado())) {
            throw new IllegalArgumentException(
                    "La plaza " + plaza.getCodigoPlaza() + " no está disponible. Estado actual: " + plaza.getEstado());
        }

        //PlazaResponse plazaActualizada = plazaClient.actualizarEstadoPlaza(
        //        request.getIdPlaza(), "Reservada");

        PlazaResponse plazaActualizada = plazaClient.actualizarEstadoPlaza(
            request.getIdPlaza(),
            "Reservada",
            authorizationHeader
        );

        if (plazaActualizada == null) {
            throw new PlazaServiceException(
                    "No se pudo actualizar el estado de la plaza " + request.getIdPlaza());
        }

        Reserva reserva = new Reserva();
        reserva.setRutReserva(request.getRutReserva().trim());
        reserva.setPatente(request.getPatente().trim());
        reserva.setIdPlaza(request.getIdPlaza());

        Reserva reservaGuardada = reservaRepository.save(reserva);

        log.info("Reserva creada: plaza {} (ID {}) reservada por {} - patente {}",
                plaza.getCodigoPlaza(), request.getIdPlaza(),
                request.getRutReserva(), request.getPatente());

        registrarMovimientoReserva(reservaGuardada, plaza.getCodigoPlaza(), authorizationHeader);

        return mapToReservaResponse(reservaGuardada);
    }

    @Transactional(readOnly = true)
    public List<ReservaResponse> listarReservas() {
        return reservaRepository.findAll()
                .stream()
                .map(this::mapToReservaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ReservaResponse obtenerReservaPorId(Long idReserva) {
        Reserva reserva = buscarReservaPorId(idReserva);
        return mapToReservaResponse(reserva);
    }

    @Transactional
    public void eliminarReserva(Long idReserva) {
        Reserva reserva = buscarReservaPorId(idReserva);
        reservaRepository.delete(reserva);

        log.info("Reserva ID {} eliminada - plaza ID {}", idReserva, reserva.getIdPlaza());
    }

    private void registrarMovimientoReserva(
            Reserva reserva,
            String codigoPlaza,
            String authorizationHeader
    ) {
        try {
            MovimientoCreateRequest movimientoRequest = new MovimientoCreateRequest(
                    "RESERVA",
                    null,
                    reserva.getRutReserva(),
                    null,
                    reserva.getPatente(),
                    reserva.getIdPlaza(),
                    codigoPlaza,
                    reserva.getIdReserva(),
                    "reserva_service",
                    "Reserva de visita registrada correctamente"
            );

            movimientoClient.registrarMovimiento(movimientoRequest, authorizationHeader);

        } catch (Exception ex) {
            log.warn(
                    "No fue posible registrar movimiento de reserva para idReserva {}. Motivo: {}",
                    reserva.getIdReserva(),
                    ex.getMessage()
            );
        }
    }

    private Reserva buscarReservaPorId(Long idReserva) {
        return reservaRepository.findById(idReserva)
                .orElseThrow(() -> new ReservaNoEncontradaException(
                        "No existe una reserva con ID " + idReserva));
    }

    private ReservaResponse mapToReservaResponse(Reserva reserva) {
        return new ReservaResponse(
                reserva.getIdReserva(),
                reserva.getRutReserva(),
                reserva.getPatente(),
                reserva.getIdPlaza(),
                reserva.getFechaReserva()
        );
    }
}
