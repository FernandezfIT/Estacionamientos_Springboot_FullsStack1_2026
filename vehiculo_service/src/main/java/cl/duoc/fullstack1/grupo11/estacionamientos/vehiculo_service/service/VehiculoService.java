package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service;

import java.util.List;

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

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final TipoVehiculoService tipoVehiculoService;
    private final UsuarioClient usuarioClient;

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculos() {
        return vehiculoRepository.findAll()
                .stream()
                .map(this::mapToVehiculoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorId(Long idVehiculo) {
        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);
        return mapToVehiculoResponse(vehiculo);
    }

    @Transactional(readOnly = true)
    public VehiculoResponse obtenerVehiculoPorPatente(String patente) {
        String patenteNormalizada = normalizarPatente(patente);

        Vehiculo vehiculo = vehiculoRepository.findByPatente(patenteNormalizada)
                .orElseThrow(() -> new VehiculoNoEncontradoEx(
                        "No existe un vehículo con la patente " + patenteNormalizada
                ));

        return mapToVehiculoResponse(vehiculo);
    }

    @Transactional(readOnly = true)
    public List<VehiculoResponse> listarVehiculosPorUsuario(Long idUsuario) {
        validarUsuarioExiste(idUsuario);

        return vehiculoRepository.findByIdUsuario(idUsuario)
                .stream()
                .map(this::mapToVehiculoResponse)
                .toList();
    }

    @Transactional
    public VehiculoResponse crearVehiculo(VehiculoCreateRequest request) {
        String patenteNormalizada = normalizarPatente(request.getPatente());

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

        return mapToVehiculoResponse(vehiculoGuardado);
    }

    @Transactional
    public VehiculoResponse actualizarVehiculo(Long idVehiculo, VehiculoUpdateRequest request) {
        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);

        String patenteNormalizada = normalizarPatente(request.getPatente());

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

        return mapToVehiculoResponse(vehiculoActualizado);
    }

    @Transactional
    public void eliminarVehiculo(Long idVehiculo) {
        Vehiculo vehiculo = buscarVehiculoPorId(idVehiculo);
        vehiculoRepository.delete(vehiculo);
    }

    private Vehiculo buscarVehiculoPorId(Long idVehiculo) {
        return vehiculoRepository.findById(idVehiculo)
                .orElseThrow(() -> new VehiculoNoEncontradoEx(
                        "No existe un vehículo con ID " + idVehiculo
                ));
    }

    private void validarPatenteDisponible(String patente) {
        if (vehiculoRepository.existsByPatente(patente)) {
            throw new RecursoDuplicadoEx(
                    "Ya existe un vehículo registrado con la patente indicada"
            );
        }
    }

    private void validarPatenteDisponibleParaActualizar(Vehiculo vehiculoActual, String nuevaPatente) {
        boolean cambioPatente = !vehiculoActual.getPatente().equalsIgnoreCase(nuevaPatente);

        if (cambioPatente && vehiculoRepository.existsByPatente(nuevaPatente)) {
            throw new RecursoDuplicadoEx(
                    "Ya existe un vehículo registrado con la patente indicada"
            );
        }
    }

    private void validarUsuarioExiste(Long idUsuario) {
        boolean existeUsuario = usuarioClient.existeUsuarioPorId(idUsuario);

        if (!existeUsuario) {
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
}