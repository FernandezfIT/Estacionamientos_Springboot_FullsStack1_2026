package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoInternoResponse {

    private Long idVehiculo;

    private String patente;

    private String marca;

    private String color;

    private Long idTipoVehiculo;

    private String tipoVehiculo;

    private Long idUsuario;
}