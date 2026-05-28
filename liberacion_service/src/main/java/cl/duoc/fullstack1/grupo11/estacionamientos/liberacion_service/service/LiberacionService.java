package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.request.LiberacionRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.response.LiberacionResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.exception.LiberacionNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.exception.PlazaServiceException;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.model.Liberacion;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.repository.LiberacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiberacionService {

    private final LiberacionRepository liberacionRepository;
    private final PlazaClient plazaClient;
    private final MovimientoClient movimientoClient;

    @Transactional
    public LiberacionResponse liberarPlaza(LiberacionRequest request, String authorizationHeader) {
        PlazaResponse plaza = plazaClient.obtenerPlaza(request.getIdPlaza());

        if (plaza == null) {
            throw new IllegalArgumentException(
                    "No existe una plaza con ID " + request.getIdPlaza());
        }

        String estadoActual = plaza.getEstado();
        if (!"Ocupada".equals(estadoActual) && !"Reservada".equals(estadoActual)) {
            throw new IllegalArgumentException(
                    "La plaza " + plaza.getCodigoPlaza() + " no está ocupada ni reservada. Estado actual: " + estadoActual);
        }

        PlazaResponse plazaActualizada = plazaClient.actualizarEstadoPlaza(
                request.getIdPlaza(), "Disponible");

        if (plazaActualizada == null) {
            throw new PlazaServiceException(
                    "No se pudo actualizar el estado de la plaza " + request.getIdPlaza());
        }

        Liberacion liberacion = new Liberacion();
        liberacion.setIdPlaza(request.getIdPlaza());

        Liberacion liberacionGuardada = liberacionRepository.save(liberacion);

        log.info("Plaza {} (ID {}) liberada. Estado anterior: {}",
                plaza.getCodigoPlaza(), request.getIdPlaza(), estadoActual);

        registrarMovimientoSalida(liberacionGuardada, plaza.getCodigoPlaza(), authorizationHeader);

        return mapToLiberacionResponse(liberacionGuardada);
    }

    @Transactional(readOnly = true)
    public List<LiberacionResponse> listarLiberaciones() {
        return liberacionRepository.findAll()
                .stream()
                .map(this::mapToLiberacionResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public LiberacionResponse obtenerLiberacionPorId(Long idLiberacion) {
        Liberacion liberacion = buscarLiberacionPorId(idLiberacion);
        return mapToLiberacionResponse(liberacion);
    }

    private void registrarMovimientoSalida(
            Liberacion liberacion,
            String codigoPlaza,
            String authorizationHeader
    ) {
        try {
            MovimientoCreateRequest movimientoRequest = new MovimientoCreateRequest(
                    "SALIDA",
                    null,
                    null,
                    null,
                    null,
                    liberacion.getIdPlaza(),
                    codigoPlaza,
                    liberacion.getIdLiberacion(),
                    "liberacion_service",
                    "Salida registrada y plaza liberada correctamente"
            );

            movimientoClient.registrarMovimiento(movimientoRequest, authorizationHeader);

        } catch (Exception ex) {
            log.warn(
                    "No fue posible registrar movimiento de salida para idLiberacion {}. Motivo: {}",
                    liberacion.getIdLiberacion(),
                    ex.getMessage()
            );
        }
    }

    private Liberacion buscarLiberacionPorId(Long idLiberacion) {
        return liberacionRepository.findById(idLiberacion)
                .orElseThrow(() -> new LiberacionNoEncontradaException(
                        "No existe una liberación con ID " + idLiberacion));
    }

    private LiberacionResponse mapToLiberacionResponse(Liberacion liberacion) {
        return new LiberacionResponse(
                liberacion.getIdLiberacion(),
                liberacion.getIdPlaza(),
                liberacion.getFechaLiberacion()
        );
    }
}
