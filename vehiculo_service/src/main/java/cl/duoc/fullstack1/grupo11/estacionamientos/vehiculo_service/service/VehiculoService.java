package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response.VehiculoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.RecursoDuplicadoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.UsuarioNoEncontradoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.VehiculoNoEncontradoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.TipoVehiculo;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.Vehiculo;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VehiculoService {

        private final VehiculoRepository vehiculoRepository;
        private final TipoVehiculoService tipoVehiculoService;
        private final UsuarioClient usuarioClient;

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculos() {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando listado de vehículos");
        log.info("Usuario={} realizó listado de vehículos", usuarioLogueado);

        List<Vehiculo> vehiculos = vehiculoRepository.findAll();

        log.info("Cantidad de vehículos encontrados={}", vehiculos.size());

        return vehiculos.stream()
                .map(this::mapToVehiculoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorId(Long idVehiculo) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando búsqueda de vehículo por idVehiculo={}", idVehiculo);
        log.info("Usuario={} realizó búsqueda de vehículo por ID", usuarioLogueado);

        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);

        log.info("Vehículo encontrado idVehiculo={} patente={}", vehiculo.getIdVehiculo(), vehiculo.getPatente());

        return mapToVehiculoResponse(vehiculo);
    }

    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorPatente(String patente) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String patenteNormalizada = normalizarPatente(patente);

        log.info("Iniciando búsqueda de vehículo por patente={}", patenteNormalizada);
        log.info("Usuario={} realizó búsqueda de vehículo por patente", usuarioLogueado);

        Vehiculo vehiculo = vehiculoRepository.findByPatente(patenteNormalizada)
                .orElseThrow(() -> {
                    log.warn("Vehículo no encontrado patente={}", patenteNormalizada);
                    return new VehiculoNoEncontradoEx(
                            "No existe un vehículo con la patente " + patenteNormalizada
                    );
                });

        log.info("Vehículo encontrado idVehiculo={} patente={}", vehiculo.getIdVehiculo(), vehiculo.getPatente());

        return mapToVehiculoResponse(vehiculo);
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculosPorUsuario(Long idUsuario) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando listado de vehículos por idUsuario={}", idUsuario);
        log.info("Usuario={} realizó listado de vehículos por usuario", usuarioLogueado);

        validarUsuarioExiste(idUsuario);

        List<Vehiculo> vehiculos = vehiculoRepository.findByIdUsuario(idUsuario);

        log.info("Cantidad de vehículos encontrados para idUsuario={} cantidad={}", idUsuario, vehiculos.size());

        return vehiculos.stream()
                .map(this::mapToVehiculoResponse)
                .toList();
    }

    @Transactional
    public VehiculoResponse crearVehiculo(VehiculoCreateRequest request) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String patenteNormalizada = normalizarPatente(request.getPatente());

        log.info("Iniciando creación de vehículo patente={} idUsuario={}", patenteNormalizada, request.getIdUsuario());
        log.info("Usuario={} realizó creación de vehículo", usuarioLogueado);

        validarPatenteDisponible(patenteNormalizada);
        validarUsuarioExiste(request.getIdUsuario());

        TipoVehiculo tipoVehiculo = tipoVehiculoService.buscarTipoVehiculoPorId(
                request.getIdTipoVehiculo()
        );

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPatente(patenteNormalizada);
        vehiculo.setMarca(normalizarTexto(request.getMarca()));
        vehiculo.setColor(normalizarTexto(request.getColor()));
        vehiculo.setTipoVehiculo(tipoVehiculo);
        vehiculo.setIdUsuario(request.getIdUsuario());

        Vehiculo vehiculoGuardado = vehiculoRepository.save(vehiculo);

        log.info(
                "Vehículo creado correctamente idVehiculo={} patente={} idUsuario={}",
                vehiculoGuardado.getIdVehiculo(),
                vehiculoGuardado.getPatente(),
                vehiculoGuardado.getIdUsuario()
        );

        return mapToVehiculoResponse(vehiculoGuardado);
    }

    @Transactional
    public VehiculoResponse actualizarVehiculo(Long idVehiculo, VehiculoUpdateRequest request) {
        String usuarioLogueado = obtenerUsuarioAutenticado();
        String patenteNormalizada = normalizarPatente(request.getPatente());

        log.info("Iniciando actualización de vehículo idVehiculo={}", idVehiculo);
        log.info("Usuario={} realizó actualización de vehículo", usuarioLogueado);

        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);

        validarPatenteDisponibleParaActualizar(vehiculo, patenteNormalizada);
        validarUsuarioExiste(request.getIdUsuario());

        TipoVehiculo tipoVehiculo = tipoVehiculoService.buscarTipoVehiculoPorId(
                request.getIdTipoVehiculo()
        );

        vehiculo.setPatente(patenteNormalizada);
        vehiculo.setMarca(normalizarTexto(request.getMarca()));
        vehiculo.setColor(normalizarTexto(request.getColor()));
        vehiculo.setTipoVehiculo(tipoVehiculo);
        vehiculo.setIdUsuario(request.getIdUsuario());

        Vehiculo vehiculoActualizado = vehiculoRepository.save(vehiculo);

        log.info(
                "Vehículo actualizado correctamente idVehiculo={} patente={} idUsuario={}",
                vehiculoActualizado.getIdVehiculo(),
                vehiculoActualizado.getPatente(),
                vehiculoActualizado.getIdUsuario()
        );

        return mapToVehiculoResponse(vehiculoActualizado);
    }

    @Transactional
    public void eliminarVehiculo(Long idVehiculo) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando eliminación de vehículo idVehiculo={}", idVehiculo);
        log.info("Usuario={} realizó eliminación de vehículo", usuarioLogueado);

        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);

        vehiculoRepository.delete(vehiculo);

        log.info("Vehículo eliminado correctamente idVehiculo={} patente={}", idVehiculo, vehiculo.getPatente());
    }

    private Vehiculo buscarVehiculoPorId(Long idVehiculo) {
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> {
                    log.warn("Vehículo no encontrado idVehiculo={}", idVehiculo);
                    return new VehiculoNoEncontradoEx(
                            "No existe un vehículo con ID " + idVehiculo
                    );
                });
    }

    private void validarPatenteDisponible(String patente) {
        if (vehiculoRepository.existsByPatente(patente)) {
            log.warn("Conflicto: patente ya registrada patente={}", patente);

            throw new RecursoDuplicadoEx(
                    "Ya existe un vehículo registrado con la patente indicada"
            );
        }
    }

    private void validarPatenteDisponibleParaActualizar(Vehiculo vehiculoActual, String nuevaPatente) {
        boolean cambioPatente = !vehiculoActual.getPatente().equalsIgnoreCase(nuevaPatente);

        if (cambioPatente && vehiculoRepository.existsByPatente(nuevaPatente)) {
            log.warn(
                    "Conflicto al actualizar vehículo idVehiculo={} nuevaPatente={}",
                    vehiculoActual.getIdVehiculo(),
                    nuevaPatente
            );

            throw new RecursoDuplicadoEx(
                    "Ya existe un vehículo registrado con la patente indicada"
            );
        }
    }

    private void validarUsuarioExiste(Long idUsuario) {
        boolean existeUsuario = usuarioClient.existeUsuarioPorId(idUsuario);

        if (!existeUsuario) {
            log.warn("Usuario no encontrado al validar vehículo idUsuario={}", idUsuario);

            throw new UsuarioNoEncontradoEx(
                    "No existe un usuario con ID " + idUsuario
            );
        }
    }

        private VehiculoResponse mapToVehiculoResponse(Vehiculo vehiculo) {
            return new VehiculoResponse(
                    vehiculo.getIdVehiculo(),
                    vehiculo.getPatente(),
                    vehiculo.getMarca(),
                    vehiculo.getColor(),
                    vehiculo.getTipoVehiculo().getIdTipoVehiculo(),
                    vehiculo.getTipoVehiculo().getNombre(),
                    vehiculo.getIdUsuario()
            );
        }

        private String normalizarPatente(String patente) {
            return patente.trim().toUpperCase();
        }

        private String normalizarTexto(String texto) {
            return texto.trim();
        }

        private String obtenerUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null) {
            return "usuario-no-autenticado";
        }

        return auth.getName();
    }
}