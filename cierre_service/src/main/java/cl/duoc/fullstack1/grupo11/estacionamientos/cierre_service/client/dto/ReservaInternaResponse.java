package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaInternaResponse {

    private Long idReserva;

    private String rutReserva;

    private String patente;

    private Long idPlaza;

    private LocalDateTime fechaReserva;
}