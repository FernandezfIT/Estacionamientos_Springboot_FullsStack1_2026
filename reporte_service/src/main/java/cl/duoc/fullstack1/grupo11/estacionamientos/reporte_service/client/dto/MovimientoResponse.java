package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoResponse {

    private Long idMovimiento;
    private String tipoMovimiento;
    private Long idUsuario;
    private String rutUsuario;
    private Long idVehiculo;
    private String patente;
    private Long idPlaza;
    private String codigoPlaza;
    private Long idReferencia;
    private String servicioOrigen;
    private LocalDateTime fechaMovimiento;
    private String descripcion;
}
