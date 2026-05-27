package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlazaUpdateRequest {

    @NotBlank(message = "El estado es obligatorio")
    @Pattern(regexp = "^(Disponible|Ocupada|Reservada)$",
             message = "El estado debe ser Disponible, Ocupada o Reservada")
    private String estado;
}
