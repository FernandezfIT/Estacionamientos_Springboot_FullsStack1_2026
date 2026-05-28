package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.PlazaResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteCompletoResponse {

    private LocalDateTime fechaReporte;
    private String servicioOrigen;
    private List<PlazaResponse> plazas;
    private List<MovimientoResponse> movimientos;
}
