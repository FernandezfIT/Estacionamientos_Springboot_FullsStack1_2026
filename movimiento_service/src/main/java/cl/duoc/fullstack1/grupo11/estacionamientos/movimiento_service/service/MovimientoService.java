package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.request.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.response.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.exception.MovimientoNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.model.Movimiento;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.repository.MovimientoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;

    @Transactional
    public MovimientoResponse crearMovimiento(MovimientoCreateRequest request) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info(
                "Iniciando creación de movimiento tipo={} servicioOrigen={} idReferencia={}",
                request.getTipoMovimiento(),
                request.getServicioOrigen(),
                request.getIdReferencia()
        );
        log.info("Usuario={} realizó creación de movimiento", usuarioLogueado);

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

        log.info(
                "Movimiento creado correctamente idMovimiento={} tipo={} servicioOrigen={} idReferencia={}",
                movimientoGuardado.getIdMovimiento(),
                movimientoGuardado.getTipoMovimiento(),
                movimientoGuardado.getServicioOrigen(),
                movimientoGuardado.getIdReferencia()
        );

        return mapToMovimientoResponse(movimientoGuardado);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos() {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando listado de movimientos");
        log.info("Usuario={} realizó listado de movimientos", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findAll();

        log.info("Cantidad de movimientos encontrados={}", movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovimientoResponse obtenerMovimientoPorId(Long idMovimiento) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando búsqueda de movimiento idMovimiento={}", idMovimiento);
        log.info("Usuario={} realizó búsqueda de movimiento por ID", usuarioLogueado);

        Movimiento movimiento = buscarMovimientoPorId(idMovimiento);

        log.info("Movimiento encontrado idMovimiento={} tipo={}",
                movimiento.getIdMovimiento(),
                movimiento.getTipoMovimiento()
        );

        return mapToMovimientoResponse(movimiento);
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorTipo(String tipoMovimiento) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String tipoNormalizado = normalizarTipoMovimiento(tipoMovimiento);

        log.info("Iniciando listado de movimientos por tipo={}", tipoNormalizado);
        log.info("Usuario={} realizó búsqueda de movimientos por tipo", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findByTipoMovimiento(tipoNormalizado);

        log.info("Cantidad de movimientos encontrados por tipo={} cantidad={}", tipoNormalizado, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorRut(String rutUsuario) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String rutNormalizado = normalizarTexto(rutUsuario);

        log.info("Iniciando listado de movimientos por rutUsuario={}", rutNormalizado);
        log.info("Usuario={} realizó búsqueda de movimientos por RUT", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findByRutUsuario(rutNormalizado);

        log.info("Cantidad de movimientos encontrados por RUT={} cantidad={}", rutNormalizado, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorPatente(String patente) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String patenteNormalizada = normalizarPatente(patente);

        log.info("Iniciando listado de movimientos por patente={}", patenteNormalizada);
        log.info("Usuario={} realizó búsqueda de movimientos por patente", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findByPatente(patenteNormalizada);

        log.info("Cantidad de movimientos encontrados por patente={} cantidad={}", patenteNormalizada, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorCodigoPlaza(String codigoPlaza) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String codigoNormalizado = normalizarCodigoPlaza(codigoPlaza);

        log.info("Iniciando listado de movimientos por codigoPlaza={}", codigoNormalizado);
        log.info("Usuario={} realizó búsqueda de movimientos por plaza", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findByCodigoPlaza(codigoNormalizado);

        log.info("Cantidad de movimientos encontrados por codigoPlaza={} cantidad={}", codigoNormalizado, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorServicioOrigen(String servicioOrigen) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String servicioNormalizado = normalizarTexto(servicioOrigen);

        log.info("Iniciando listado de movimientos por servicioOrigen={}", servicioNormalizado);
        log.info("Usuario={} realizó búsqueda de movimientos por servicio de origen", usuarioLogueado);

        List<Movimiento> movimientos = movimientoRepository.findByServicioOrigen(servicioNormalizado);

        log.info("Cantidad de movimientos encontrados por servicioOrigen={} cantidad={}", servicioNormalizado, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientosPorFecha(LocalDate fechaMovimiento) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando listado de movimientos por fecha={}", fechaMovimiento);
        log.info("Usuario={} realizó búsqueda de movimientos por fecha", usuarioLogueado);

        LocalDateTime inicioDia = fechaMovimiento.atStartOfDay();
        LocalDateTime finDia = fechaMovimiento.atTime(LocalTime.MAX);

        List<Movimiento> movimientos = movimientoRepository.findByFechaMovimientoBetween(inicioDia, finDia);

        log.info("Cantidad de movimientos encontrados por fecha={} cantidad={}", fechaMovimiento, movimientos.size());

        return movimientos.stream()
                .map(this::mapToMovimientoResponse)
                .toList();
    }

    private Movimiento buscarMovimientoPorId(Long idMovimiento) {
        return movimientoRepository.findById(idMovimiento)
                .orElseThrow(() -> {
                    log.warn("Movimiento no encontrado idMovimiento={}", idMovimiento);

                    return new MovimientoNoEncontradoException(
                            "No existe un movimiento con ID " + idMovimiento
                    );
                });
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

    private String obtenerUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || auth.getName() == null) {
        return "usuario-no-autenticado";
    }

    return auth.getName();
    }
}