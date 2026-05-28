package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponse {

    private Long idReserva;
    private String rutReserva;
    private String patente;
    private Long idPlaza;
    private LocalDateTime fechaReserva;
}
