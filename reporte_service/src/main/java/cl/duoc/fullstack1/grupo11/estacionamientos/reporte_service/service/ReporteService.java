package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteCompletoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse.ResumenMovimientos;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse.ResumenPlazas;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private static final String NOMBRE_SERVICIO = "reporte_service";

    private final PlazaClient plazaClient;
    private final MovimientoClient movimientoClient;

    public ReporteResumenResponse generarReporteResumen(String authorizationHeader) {
        List<PlazaResponse> plazas = plazaClient.listarPlazas(authorizationHeader);
        List<MovimientoResponse> movimientos = movimientoClient.listarMovimientos(authorizationHeader);

        ResumenPlazas resumenPlazas = construirResumenPlazas(plazas);
        ResumenMovimientos resumenMovimientos = construirResumenMovimientos(movimientos);

        return new ReporteResumenResponse(
                LocalDateTime.now(),
                NOMBRE_SERVICIO,
                resumenPlazas,
                resumenMovimientos
        );
    }

    public ReporteCompletoResponse generarReporteCompleto(String authorizationHeader) {
        List<PlazaResponse> plazas = plazaClient.listarPlazas(authorizationHeader);
        List<MovimientoResponse> movimientos = movimientoClient.listarMovimientos(authorizationHeader);

        return new ReporteCompletoResponse(
                LocalDateTime.now(),
                NOMBRE_SERVICIO,
                plazas,
                movimientos
        );
    }

    private ResumenPlazas construirResumenPlazas(List<PlazaResponse> plazas) {
        long disponibles = plazas.stream().filter(p -> "Disponible".equals(p.getEstado())).count();
        long ocupadas = plazas.stream().filter(p -> "Ocupada".equals(p.getEstado())).count();
        long reservadas = plazas.stream().filter(p -> "Reservada".equals(p.getEstado())).count();

        return new ResumenPlazas(disponibles, ocupadas, reservadas);
    }

    private ResumenMovimientos construirResumenMovimientos(List<MovimientoResponse> movimientos) {
        long accesos = movimientos.stream().filter(m -> "ACCESO".equals(m.getTipoMovimiento())).count();
        long salidas = movimientos.stream().filter(m -> "SALIDA".equals(m.getTipoMovimiento())).count();
        long reservas = movimientos.stream().filter(m -> "RESERVA".equals(m.getTipoMovimiento())).count();

        return new ResumenMovimientos(accesos, salidas, reservas);
    }
}
