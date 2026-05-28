package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteResumenResponse {

    private LocalDateTime fechaReporte;
    private String servicioOrigen;
    private ResumenPlazas resumenPlazas;
    private ResumenMovimientos resumenMovimientos;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenPlazas {
        private long disponibles;
        private long ocupadas;
        private long reservadas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResumenMovimientos {
        private long accesos;
        private long salidas;
        private long reservas;
    }
}
