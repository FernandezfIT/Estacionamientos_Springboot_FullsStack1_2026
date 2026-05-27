package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccesoResponse {

    private Long idAcceso;

    private Long idUsuario;

    private String rutUsuario;

    private Long idVehiculo;

    private String patente;

    private Long idPlaza;

    private String codigoPlaza;

    private LocalDateTime fechaIngreso;

    private String estadoAcceso;

    private String observacion;
}