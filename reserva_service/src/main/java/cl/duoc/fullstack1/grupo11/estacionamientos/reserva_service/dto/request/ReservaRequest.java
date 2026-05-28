package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.request;

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
public class ReservaRequest {

    @NotBlank(message = "El RUT es obligatorio")
    @Size(max = 12, message = "El RUT no puede superar los 12 caracteres")
    private String rutReserva;

    @NotBlank(message = "La patente es obligatoria")
    @Size(max = 20, message = "La patente no puede superar los 20 caracteres")
    private String patente;

    @NotNull(message = "El idPlaza es obligatorio")
    @Positive(message = "El idPlaza debe ser un número positivo")
    private Long idPlaza;
}
