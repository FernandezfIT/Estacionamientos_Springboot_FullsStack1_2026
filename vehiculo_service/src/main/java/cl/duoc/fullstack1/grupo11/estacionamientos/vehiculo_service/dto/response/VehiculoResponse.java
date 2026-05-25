package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoResponse {

    private Long idVehiculo;

    private String patente;

    private String marca;

    private String color;

    private Long idTipoVehiculo;

    private String tipoVehiculo;

    private Long idUsuario;
}