package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.service;


import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.request.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.response.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.exception.MovimientoNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.model.Movimiento;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    @Transactional
    public MovimientoResponse crearMovimiento(MovimientoCreateRequest request) {
        Movimiento movimiento = new Movimiento();

        movimiento.setTipoMovimiento(normalizarTipoMovimiento(request.getTipoMovimiento()));

        movimiento.setIdUsuario(request.getIdUsuario());
        movimiento.setRutUsuario(normalizarTextoOpcional(request.getRutUsuario()));

        movimiento.setIdVehiculo(request.getIdVehiculo());
        movimiento.setPatente(normalizarPatenteOpcional(request.getPatente()));

        movimiento.setIdPlaza(request.getIdPlaza());
        movimiento.setCodigoPlaza(normalizarCodigoPlazaOpcional(request.getCodigoPlaza()));

        movimiento.setIdReferencia(request.getIdReferencia());
        movimiento.setServicioOrigen(normalizarTexto(request.getServicioOrigen()));
        movimiento.setDescripcion(normalizarTextoOpcional(request.getDescripcion()));

        Movimiento movimientoGuardado = movimientoRepository.save(movimiento);

        return mapToMovimientoResponse(movimientoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos() {
        return movimientoRepository.findAll()
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtenerMovimientoPorId(Long idMovimiento) {
        Movimiento movimiento = buscarMovimientoPorId(idMovimiento);
        return mapToMovimientoResponse(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(String tipoMovimiento) {
        String tipoNormalizado = normalizarTipoMovimiento(tipoMovimiento);

        return movimientoRepository.findByTipoMovimiento(tipoNormalizado)
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorRut(String rutUsuario) {
        String rutNormalizado = normalizarTexto(rutUsuario);

        return movimientoRepository.findByRutUsuario(rutNormalizado)
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorPatente(String patente) {
        String patenteNormalizada = normalizarPatente(patente);

        return movimientoRepository.findByPatente(patenteNormalizada)
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorCodigoPlaza(String codigoPlaza) {
        String codigoNormalizado = normalizarCodigoPlaza(codigoPlaza);

        return movimientoRepository.findByCodigoPlaza(codigoNormalizado)
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorServicioOrigen(String servicioOrigen) {
        String servicioNormalizado = normalizarTexto(servicioOrigen);

        return movimientoRepository.findByServicioOrigen(servicioNormalizado)
                .stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    private Movimiento buscarMovimientoPorId(Long idMovimiento) {
        return movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> new MovimientoNoEncontradoException(
                        "No existe un movimiento con ID " + idMovimiento
                ));
    }

    private MovimientoResponse mapToMovimientoResponse(Movimiento movimiento) {
        return new MovimientoResponse(
                movimiento.getIdMovimiento(),
                movimiento.getTipoMovimiento(),
                movimiento.getIdUsuario(),
                movimiento.getRutUsuario(),
                movimiento.getIdVehiculo(),
                movimiento.getPatente(),
                movimiento.getIdPlaza(),
                movimiento.getCodigoPlaza(),
                movimiento.getIdReferencia(),
                movimiento.getServicioOrigen(),
                movimiento.getFechaMovimiento(),
                movimiento.getDescripcion()
        );
    }

    private String normalizarTipoMovimiento(String tipoMovimiento) {
        return tipoMovimiento.trim().toUpperCase();
    }

    private String normalizarPatente(String patente) {
        return patente.trim().toUpperCase();
    }

    private String normalizarCodigoPlaza(String codigoPlaza) {
        return codigoPlaza.trim().toUpperCase();
    }

    private String normalizarTexto(String texto) {
        return texto.trim();
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }

    private String normalizarPatenteOpcional(String patente) {
        if (patente == null || patente.isBlank()) {
            return null;
        }

        return patente.trim().toUpperCase();
    }

    private String normalizarCodigoPlazaOpcional(String codigoPlaza) {
        if (codigoPlaza == null || codigoPlaza.isBlank()) {
            return null;
        }

        return codigoPlaza.trim().toUpperCase();
    }
}