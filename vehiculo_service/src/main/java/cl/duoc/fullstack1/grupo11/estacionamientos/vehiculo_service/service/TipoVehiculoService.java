package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response.TipoVehiculoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.TipoVehiculoNoEncontradoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.TipoVehiculo;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.repository.TipoVehiculoRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TipoVehiculoService {

    private final TipoVehiculoRepository tipoVehiculoRepository;

    @Transactional(readOnly = true)
    public List<TipoVehiculoResponse> listarTiposVehiculo() {
        return tipoVehiculoRepository.findAll()
                .stream()
                .map(this::mapToTipoVehiculoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TipoVehiculoResponse obtenerTipoVehiculoPorId(Long idTipoVehiculo) {
        TipoVehiculo tipoVehiculo = buscarTipoVehiculoPorId(idTipoVehiculo);
        return mapToTipoVehiculoResponse(tipoVehiculo);
    }

    public TipoVehiculo buscarTipoVehiculoPorId(Long idTipoVehiculo) {
        return tipoVehiculoRepository.findById(idTipoVehiculo)
                .orElseThrow(() -> new TipoVehiculoNoEncontradoEx(
                        "No existe un tipo de vehículo con ID " + idTipoVehiculo
                ));
    }

    private TipoVehiculoResponse mapToTipoVehiculoResponse(TipoVehiculo tipoVehiculo) {
        return new TipoVehiculoResponse(
                tipoVehiculo.getIdTipoVehiculo(),
                tipoVehiculo.getNombre(),
                tipoVehiculo.getDescripcion()
        );
    }
}