package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehiculoCreateRequest {

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede superar los 10 caracteres")
    private String patente;

    @NotBlank(message = "La marca es obligatoria")
    @Size(max = 80, message = "La marca no puede superar los 80 caracteres")
    private String marca;

    @NotBlank(message = "El color es obligatorio")
    @Size(max = 50, message = "El color no puede superar los 50 caracteres")
    private String color;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    @Positive(message = "El idTipoVehiculo debe ser un número positivo")
    private Long idTipoVehiculo;

    @NotNull(message = "El usuario dueño del vehículo es obligatorio")
    @Positive(message = "El idUsuario debe ser un número positivo")
    private Long idUsuario;
}