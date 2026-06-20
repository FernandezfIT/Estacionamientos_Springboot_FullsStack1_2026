package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.VehiculoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.UsuarioInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.VehiculoInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.request.AccesoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.response.AccesoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.AccesoNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.MicroservicioNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.VehiculoNoPerteneceUsuarioException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.model.Acceso;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.repository.AccesoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccesoService {

    private static final String ESTADO_AUTORIZADO = "AUTORIZADO";
    private static final String OBSERVACION_ACCESO_AUTORIZADO = "Acceso autorizado correctamente";

    private static final String TIPO_MOVIMIENTO_ACCESO = "ACCESO";
    private static final String SERVICIO_ORIGEN = "acceso_service";

    private final AccesoRepository accesoRepository;
    private final UsuarioClient usuarioClient;
    private final VehiculoClient vehiculoClient;
    private final PlazaClient plazaClient;
    private final MovimientoClient movimientoClient;

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesos() {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando listado de accesos");
        log.info("Usuario={} realizó listado de accesos", usuarioLogueado);

        List<Acceso> accesos = accesoRepository.findAll();

        log.info("Cantidad de accesos encontrados={}", accesos.size());

        return accesos.stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccesoResponse obtenerAccesoPorId(Long idAcceso) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
    
        log.info("Iniciando búsqueda de acceso idAcceso={}", idAcceso);
        log.info("Usuario={} realizó búsqueda de acceso por ID", usuarioLogueado);
    
        Acceso acceso = buscarAccesoPorId(idAcceso);
    
        log.info("Acceso encontrado idAcceso={} rut={} patente={}",
                acceso.getIdAcceso(),
                acceso.getRutUsuario(),
                acceso.getPatente()
        );
    
        return mapToAccesoResponse(acceso);
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorRut(String rut) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String rutNormalizado = normalizarTexto(rut);

        log.info("Iniciando listado de accesos por RUT={}", rutNormalizado);
        log.info("Usuario={} realizó búsqueda de accesos por RUT", usuarioLogueado);

        List<Acceso> accesos = accesoRepository.findByRutUsuario(rutNormalizado);

        log.info("Cantidad de accesos encontrados por RUT={} cantidad={}", rutNormalizado, accesos.size());

        return accesos.stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorPatente(String patente) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String patenteNormalizada = normalizarPatente(patente);

        log.info("Iniciando listado de accesos por patente={}", patenteNormalizada);
        log.info("Usuario={} realizó búsqueda de accesos por patente", usuarioLogueado);

        List<Acceso> accesos = accesoRepository.findByPatente(patenteNormalizada);

        log.info("Cantidad de accesos encontrados por patente={} cantidad={}", patenteNormalizada, accesos.size());

        return accesos.stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorCodigoPlaza(String codigoPlaza) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String codigoNormalizado = normalizarCodigoPlaza(codigoPlaza);

        log.info("Iniciando listado de accesos por codigoPlaza={}", codigoNormalizado);
        log.info("Usuario={} realizó búsqueda de accesos por plaza", usuarioLogueado);

        List<Acceso> accesos = accesoRepository.findByCodigoPlaza(codigoNormalizado);

        log.info("Cantidad de accesos encontrados por codigoPlaza={} cantidad={}", codigoNormalizado, accesos.size());

        return accesos.stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional
    public AccesoResponse registrarAcceso(
            AccesoCreateRequest request,
            String authorizationHeader
    ) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String rutNormalizado = normalizarTexto(request.getRut());
        String patenteNormalizada = normalizarPatente(request.getPatente());
        String codigoPlazaNormalizado = normalizarCodigoPlaza(request.getCodigoPlaza());

        log.info(
                "Iniciando registro de acceso rut={} patente={} codigoPlaza={}",
                rutNormalizado,
                patenteNormalizada,
                codigoPlazaNormalizado
        );
        log.info("Usuario={} realizó registro de acceso", usuarioLogueado);

        UsuarioInternoResponse usuario = usuarioClient.buscarUsuarioPorRut(rutNormalizado);
        validarRespuestaUsuario(usuario, rutNormalizado);

        log.info("Usuario validado para acceso idUsuario={} rut={}", usuario.getIdUsuario(), usuario.getRut());

        VehiculoInternoResponse vehiculo = vehiculoClient.buscarVehiculoPorPatente(
                patenteNormalizada,
                authorizationHeader
        );
        validarRespuestaVehiculo(vehiculo, patenteNormalizada);

        log.info("Vehículo validado para acceso idVehiculo={} patente={}",
                vehiculo.getIdVehiculo(),
                vehiculo.getPatente()
        );

        validarVehiculoPerteneceUsuario(usuario, vehiculo);

        PlazaInternaResponse plazaOcupada = plazaClient.ocuparPlazaPorCodigo(
                codigoPlazaNormalizado,
                authorizationHeader
        );
        validarRespuestaPlaza(plazaOcupada, codigoPlazaNormalizado);

        log.info("Plaza ocupada correctamente idPlaza={} codigoPlaza={}",
                plazaOcupada.getIdPlaza(),
                plazaOcupada.getCodigoPlaza()
        );

        Acceso acceso = new Acceso();
        acceso.setIdUsuario(usuario.getIdUsuario());
        acceso.setRutUsuario(usuario.getRut());
        acceso.setIdVehiculo(vehiculo.getIdVehiculo());
        acceso.setPatente(vehiculo.getPatente());
        acceso.setIdPlaza(plazaOcupada.getIdPlaza());
        acceso.setCodigoPlaza(plazaOcupada.getCodigoPlaza());
        acceso.setEstadoAcceso(ESTADO_AUTORIZADO);
        acceso.setObservacion(OBSERVACION_ACCESO_AUTORIZADO);

        Acceso accesoGuardado = accesoRepository.save(acceso);

        log.info(
                "Acceso guardado correctamente idAcceso={} rut={} patente={} codigoPlaza={}",
                accesoGuardado.getIdAcceso(),
                accesoGuardado.getRutUsuario(),
                accesoGuardado.getPatente(),
                accesoGuardado.getCodigoPlaza()
        );

        registrarMovimientoAcceso(accesoGuardado, authorizationHeader);

        return mapToAccesoResponse(accesoGuardado);
    }

    private Acceso buscarAccesoPorId(Long idAcceso) {
        return accesoRepository.findById(idAcceso)
                .orElseThrow(() -> {
                    log.warn("Acceso no encontrado idAcceso={}", idAcceso);

                    return new AccesoNoEncontradoException(
                            "No existe un acceso con ID " + idAcceso
                    );
                });
    }

    private void validarVehiculoPerteneceUsuario(
            UsuarioInternoResponse usuario,
            VehiculoInternoResponse vehiculo
    ) {
        if (!vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
            log.warn(
                    "Vehículo no pertenece al usuario idUsuario={} idVehiculo={} idUsuarioVehiculo={}",
                    usuario.getIdUsuario(),
                    vehiculo.getIdVehiculo(),
                    vehiculo.getIdUsuario()
            );

            throw new VehiculoNoPerteneceUsuarioException(
                    "El vehículo indicado no pertenece al usuario informado"
            );
        }
    }

    private void validarRespuestaUsuario(
            UsuarioInternoResponse usuario,
            String rut
    ) {
        if (usuario == null || usuario.getIdUsuario() == null) {
            log.warn("Respuesta inválida desde usuario_service para rut={}", rut);

            throw new MicroservicioNoDisponibleException(
                    "usuario_service respondió sin datos válidos para el RUT " + rut
            );
        }
    }

    private void validarRespuestaVehiculo(
            VehiculoInternoResponse vehiculo,
            String patente
    ) {
        if (vehiculo == null || vehiculo.getIdVehiculo() == null || vehiculo.getIdUsuario() == null) {
            log.warn("Respuesta inválida desde vehiculo_service para patente={}", patente);

            throw new MicroservicioNoDisponibleException(
                    "vehiculo_service respondió sin datos válidos para la patente " + patente
            );
        }
    }

    private void validarRespuestaPlaza(
            PlazaInternaResponse plaza,
            String codigoPlaza
    ) {
        if (plaza == null || plaza.getIdPlaza() == null) {
            log.warn("Respuesta inválida desde plaza_service para codigoPlaza={}", codigoPlaza);

            throw new MicroservicioNoDisponibleException(
                    "plaza_service respondió sin datos válidos para la plaza " + codigoPlaza
            );
        }
    }

    private AccesoResponse mapToAccesoResponse(Acceso acceso) {
        return new AccesoResponse(
                acceso.getIdAcceso(),
                acceso.getIdUsuario(),
                acceso.getRutUsuario(),
                acceso.getIdVehiculo(),
                acceso.getPatente(),
                acceso.getIdPlaza(),
                acceso.getCodigoPlaza(),
                acceso.getFechaIngreso(),
                acceso.getEstadoAcceso(),
                acceso.getObservacion()
        );
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

    private void registrarMovimientoAcceso(
            Acceso acceso,
            String authorizationHeader
    ) {
        try {
            log.info("Registrando movimiento ACCESO para idAcceso={}", acceso.getIdAcceso());

            MovimientoCreateRequest movimientoRequest = new MovimientoCreateRequest(
                    TIPO_MOVIMIENTO_ACCESO,
                    acceso.getIdUsuario(),
                    acceso.getRutUsuario(),
                    acceso.getIdVehiculo(),
                    acceso.getPatente(),
                    acceso.getIdPlaza(),
                    acceso.getCodigoPlaza(),
                    acceso.getIdAcceso(),
                    SERVICIO_ORIGEN,
                    "Ingreso autorizado al estacionamiento"
            );

            movimientoClient.registrarMovimiento(movimientoRequest, authorizationHeader);

            log.info("Movimiento ACCESO registrado correctamente para idAcceso={}", acceso.getIdAcceso());

        } catch (Exception ex) {
            log.warn(
                    "No fue posible registrar movimiento de acceso para idAcceso={}. Motivo={}",
                    acceso.getIdAcceso(),
                    ex.getMessage()
            );
        }
    }
    private String obtenerUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || auth.getName() == null) {
        return "usuario-no-autenticado";
    }

    return auth.getName();
    }
}