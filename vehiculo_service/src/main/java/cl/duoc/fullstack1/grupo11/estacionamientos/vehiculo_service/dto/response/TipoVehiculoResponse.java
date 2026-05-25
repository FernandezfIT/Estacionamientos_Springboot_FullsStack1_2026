package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoVehiculoResponse {

    private Long idTipoVehiculo;

    private String nombre;

    private String descripcion;
}