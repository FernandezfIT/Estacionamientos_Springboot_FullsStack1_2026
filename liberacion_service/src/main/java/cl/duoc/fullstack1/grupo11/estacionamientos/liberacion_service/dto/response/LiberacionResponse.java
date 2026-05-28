package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LiberacionResponse {

    private Long idLiberacion;
    private Long idPlaza;
    private LocalDateTime fechaLiberacion;
}
