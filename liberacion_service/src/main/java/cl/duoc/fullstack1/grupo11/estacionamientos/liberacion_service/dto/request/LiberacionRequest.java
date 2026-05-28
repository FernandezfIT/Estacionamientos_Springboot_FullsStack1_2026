package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiberacionRequest {

    @NotNull(message = "El idPlaza es obligatorio")
    @Positive(message = "El idPlaza debe ser un número positivo")
    private Long idPlaza;
}
