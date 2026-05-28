package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCreateRequest {

    private String tipoMovimiento;
    private Long idUsuario;
    private String rutUsuario;
    private Long idVehiculo;
    private String patente;
    private Long idPlaza;
    private String codigoPlaza;
    private Long idReferencia;
    private String servicioOrigen;
    private String descripcion;
}
