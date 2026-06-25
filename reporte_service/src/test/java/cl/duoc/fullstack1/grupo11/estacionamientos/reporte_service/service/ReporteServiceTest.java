package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteCompletoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.dto.response.ReporteResumenResponse;

@ExtendWith(MockitoExtension.class)
class ReporteServiceTest {

    @Mock
    private PlazaClient plazaClient;

    @Mock
    private MovimientoClient movimientoClient;

    @InjectMocks
    private ReporteService reporteService;

    private static final String TOKEN = "Bearer test-token";

    private List<PlazaResponse> crearPlazas() {
        return List.of(
                new PlazaResponse(1L, "P01", "Disponible"),
                new PlazaResponse(2L, "P02", "Ocupada"),
                new PlazaResponse(3L, "P03", "Ocupada"),
                new PlazaResponse(4L, "P04", "Reservada")
        );
    }

    private List<MovimientoResponse> crearMovimientos() {
        return List.of(
                new MovimientoResponse(1L, "ACCESO", null, null, null, null, null, null, null, null, null, null),
                new MovimientoResponse(2L, "SALIDA", null, null, null, null, null, null, null, null, null, null),
                new MovimientoResponse(3L, "RESERVA", null, null, null, null, null, null, null, null, null, null),
                new MovimientoResponse(4L, "ACCESO", null, null, null, null, null, null, null, null, null, null)
        );
    }

    @Test
    void generarReporteResumen_debeRetornarResumenCorrecto() {
        when(plazaClient.listarPlazas(TOKEN)).thenReturn(crearPlazas());
        when(movimientoClient.listarMovimientos(TOKEN)).thenReturn(crearMovimientos());

        ReporteResumenResponse resultado = reporteService.generarReporteResumen(TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getServicioOrigen()).isEqualTo("reporte_service");

        assertThat(resultado.getResumenPlazas().getDisponibles()).isEqualTo(1);
        assertThat(resultado.getResumenPlazas().getOcupadas()).isEqualTo(2);
        assertThat(resultado.getResumenPlazas().getReservadas()).isEqualTo(1);

        assertThat(resultado.getResumenMovimientos().getAccesos()).isEqualTo(2);
        assertThat(resultado.getResumenMovimientos().getSalidas()).isEqualTo(1);
        assertThat(resultado.getResumenMovimientos().getReservas()).isEqualTo(1);

        verify(plazaClient).listarPlazas(TOKEN);
        verify(movimientoClient).listarMovimientos(TOKEN);
    }

    @Test
    void generarReporteResumenPorFecha_debeRetornarResumenFiltrado() {
        LocalDate fecha = LocalDate.of(2026, 6, 23);
        when(plazaClient.listarPlazas(TOKEN)).thenReturn(crearPlazas());
        when(movimientoClient.listarMovimientosPorFecha(fecha, TOKEN)).thenReturn(crearMovimientos());

        ReporteResumenResponse resultado = reporteService.generarReporteResumenPorFecha(fecha, TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getServicioOrigen()).isEqualTo("reporte_service");

        assertThat(resultado.getResumenPlazas().getDisponibles()).isEqualTo(1);
        assertThat(resultado.getResumenPlazas().getOcupadas()).isEqualTo(2);
        assertThat(resultado.getResumenPlazas().getReservadas()).isEqualTo(1);

        assertThat(resultado.getResumenMovimientos().getAccesos()).isEqualTo(2);
        assertThat(resultado.getResumenMovimientos().getSalidas()).isEqualTo(1);
        assertThat(resultado.getResumenMovimientos().getReservas()).isEqualTo(1);

        verify(plazaClient).listarPlazas(TOKEN);
        verify(movimientoClient).listarMovimientosPorFecha(fecha, TOKEN);
    }

    @Test
    void generarReporteCompleto_debeRetornarReporteCompleto() {
        List<PlazaResponse> plazas = crearPlazas();
        List<MovimientoResponse> movimientos = crearMovimientos();

        when(plazaClient.listarPlazas(TOKEN)).thenReturn(plazas);
        when(movimientoClient.listarMovimientos(TOKEN)).thenReturn(movimientos);

        ReporteCompletoResponse resultado = reporteService.generarReporteCompleto(TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getServicioOrigen()).isEqualTo("reporte_service");
        assertThat(resultado.getPlazas()).hasSize(4);
        assertThat(resultado.getMovimientos()).hasSize(4);

        verify(plazaClient).listarPlazas(TOKEN);
        verify(movimientoClient).listarMovimientos(TOKEN);
    }
}
