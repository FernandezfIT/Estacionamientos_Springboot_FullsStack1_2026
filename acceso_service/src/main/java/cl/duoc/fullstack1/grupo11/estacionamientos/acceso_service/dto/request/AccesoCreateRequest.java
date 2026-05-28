package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccesoCreateRequest {

    @NotBlank(message = "El RUT del usuario es obligatorio")
    @Size(max = 10, message = "El RUT no puede superar los 10 caracteres")
    private String rut;

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 10, message = "La patente no puede superar los 10 caracteres")
    private String patente;

    @NotBlank(message = "El código de plaza es obligatorio")
    @Size(max = 20, message = "El código de plaza no puede superar los 20 caracteres")
    private String codigoPlaza;
}