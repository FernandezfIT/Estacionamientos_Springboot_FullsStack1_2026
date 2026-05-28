package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@RequiredArgsConstructor
public class AccesoService {

    private static final String ESTADO_AUTORIZADO = "AUTORIZADO";
    private static final String OBSERVACION_ACCESO_AUTORIZADO = "Acceso autorizado correctamente";

    private static final Logger log = LoggerFactory.getLogger(AccesoService.class);

    private static final String TIPO_MOVIMIENTO_ACCESO = "ACCESO";
    private static final String SERVICIO_ORIGEN = "acceso_service";

    private final AccesoRepository accesoRepository;
    private final UsuarioClient usuarioClient;
    private final VehiculoClient vehiculoClient;
    private final PlazaClient plazaClient;
    private final MovimientoClient movimientoClient;

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesos() {
        return accesoRepository.findAll()
                .stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccesoResponse obtenerAccesoPorId(Long idAcceso) {
        Acceso acceso = buscarAccesoPorId(idAcceso);
        return mapToAccesoResponse(acceso);
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorRut(String rut) {
        String rutNormalizado = normalizarTexto(rut);

        return accesoRepository.findByRutUsuario(rutNormalizado)
                .stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorPatente(String patente) {
        String patenteNormalizada = normalizarPatente(patente);

        return accesoRepository.findByPatente(patenteNormalizada)
                .stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AccesoResponse> listarAccesosPorCodigoPlaza(String codigoPlaza) {
        String codigoNormalizado = normalizarCodigoPlaza(codigoPlaza);

        return accesoRepository.findByCodigoPlaza(codigoNormalizado)
                .stream()
                .map(this::mapToAccesoResponse)
                .toList();
    }

    @Transactional
    public AccesoResponse registrarAcceso(
            AccesoCreateRequest request,
            String authorizationHeader
    ) {
        String rutNormalizado = normalizarTexto(request.getRut());
        String patenteNormalizada = normalizarPatente(request.getPatente());
        String codigoPlazaNormalizado = normalizarCodigoPlaza(request.getCodigoPlaza());

        UsuarioInternoResponse usuario = usuarioClient.buscarUsuarioPorRut(rutNormalizado);
        validarRespuestaUsuario(usuario, rutNormalizado);

        VehiculoInternoResponse vehiculo = vehiculoClient.buscarVehiculoPorPatente(
                patenteNormalizada,
                authorizationHeader
        );
        validarRespuestaVehiculo(vehiculo, patenteNormalizada);

        validarVehiculoPerteneceUsuario(usuario, vehiculo);

        PlazaInternaResponse plazaOcupada = plazaClient.ocuparPlazaPorCodigo(
                codigoPlazaNormalizado,
                authorizationHeader
        );
        validarRespuestaPlaza(plazaOcupada, codigoPlazaNormalizado);

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

        registrarMovimientoAcceso(accesoGuardado, authorizationHeader);

        return mapToAccesoResponse(accesoGuardado);
    }

    private Acceso buscarAccesoPorId(Long idAcceso) {
        return accesoRepository.findById(idAcceso)
                .orElseThrow(() -> new AccesoNoEncontradoException(
                        "No existe un acceso con ID " + idAcceso
                ));
    }

    private void validarVehiculoPerteneceUsuario(
            UsuarioInternoResponse usuario,
            VehiculoInternoResponse vehiculo
    ) {
        if (!vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
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
        
        } catch (Exception ex) {
            log.warn(
                    "No fue posible registrar movimiento de acceso para idAcceso {}. Motivo: {}",
                    acceso.getIdAcceso(),
                    ex.getMessage()
            );
        }
    }
}