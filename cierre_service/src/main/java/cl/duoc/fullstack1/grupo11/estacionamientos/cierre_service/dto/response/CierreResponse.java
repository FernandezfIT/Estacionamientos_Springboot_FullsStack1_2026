package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CierreResponse {

    private Long idCierre;

    private LocalDate fechaCierre;

    private LocalTime horaCierre;

    private LocalDateTime fechaHoraCierre;

    private Long idUsuarioEjecutor;

    private String emailUsuarioEjecutor;

    private String rolEjecutor;

    private Integer totalPlazasOcupadasLiberadas;

    private Integer totalReservasEliminadas;

    private Integer totalPlazasReservadasLiberadas;

    private Integer plazasDisponiblesFinal;

    private Integer plazasOcupadasFinal;

    private Integer plazasReservadasFinal;

    private Integer totalMovimientos;

    private Integer totalAccesos;

    private Integer totalSalidas;

    private Integer totalReservas;

    private String estadoCierre;

    private String resumenReporte;

    private String observacion;
}