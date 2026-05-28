package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovimientoCreateRequest {

    @NotBlank(message = "El tipo de movimiento es obligatorio")
    @Size(max = 30, message = "El tipo de movimiento no puede superar los 30 caracteres")
    private String tipoMovimiento;

    private Long idUsuario;

    @Size(max = 10, message = "El RUT del usuario no puede superar los 10 caracteres")
    private String rutUsuario;

    private Long idVehiculo;

    @Size(max = 10, message = "La patente no puede superar los 10 caracteres")
    private String patente;

    private Long idPlaza;

    @Size(max = 20, message = "El código de plaza no puede superar los 20 caracteres")
    private String codigoPlaza;

    private Long idReferencia;

    @NotBlank(message = "El servicio de origen es obligatorio")
    @Size(max = 80, message = "El servicio de origen no puede superar los 80 caracteres")
    private String servicioOrigen;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    private String descripcion;
}