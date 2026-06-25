package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.request.ReservaRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.dto.response.ReservaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.exception.ReservaNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.model.Reserva;
import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.repository.ReservaRepository;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private PlazaClient plazaClient;

    @Mock
    private MovimientoClient movimientoClient;

    @InjectMocks
    private ReservaService reservaService;

    private static final String TOKEN = "Bearer test-token";

    private PlazaResponse crearPlazaDisponible() {
        return new PlazaResponse(1L, "P01", "Disponible");
    }

    private PlazaResponse crearPlazaOcupada() {
        return new PlazaResponse(1L, "P01", "Ocupada");
    }

    private Reserva crearReserva() {
        return new Reserva(1L, "11111111-1", "ABC123", 1L, LocalDateTime.of(2026, 6, 20, 10, 30));
    }

    @Test
    void listarReservas_debeRetornarListaDeReservas() {
        Reserva reserva = crearReserva();
        when(reservaRepository.findAll()).thenReturn(List.of(reserva));

        List<ReservaResponse> resultado = reservaService.listarReservas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdReserva()).isEqualTo(1L);
        assertThat(resultado.get(0).getRutReserva()).isEqualTo("11111111-1");
        assertThat(resultado.get(0).getPatente()).isEqualTo("ABC123");
        assertThat(resultado.get(0).getIdPlaza()).isEqualTo(1L);
    }

    @Test
    void obtenerReservaPorId_debeRetornarReservaCuandoExiste() {
        Reserva reserva = crearReserva();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        ReservaResponse resultado = reservaService.obtenerReservaPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdReserva()).isEqualTo(1L);
        verify(reservaRepository).findById(1L);
    }

    @Test
    void obtenerReservaPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.obtenerReservaPorId(99L))
                .isInstanceOf(ReservaNoEncontradaException.class)
                .hasMessageContaining("No existe una reserva con ID 99");

        verify(reservaRepository).findById(99L);
    }

    @Test
    void eliminarReserva_debeEliminarCuandoExiste() {
        Reserva reserva = crearReserva();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.eliminarReserva(1L);

        verify(reservaRepository).findById(1L);
        verify(reservaRepository).delete(reserva);
    }

    @Test
    void eliminarReserva_debeLanzarExcepcionCuandoNoExiste() {
        when(reservaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.eliminarReserva(99L))
                .isInstanceOf(ReservaNoEncontradaException.class)
                .hasMessageContaining("No existe una reserva con ID 99");

        verify(reservaRepository).findById(99L);
    }

    @Test
    void crearReserva_debeCrearReservaCorrectamente() {
        ReservaRequest request = new ReservaRequest("11111111-1", "ABC123", 1L);
        PlazaResponse plaza = crearPlazaDisponible();

        when(plazaClient.obtenerPlaza(1L, TOKEN)).thenReturn(plaza);
        when(plazaClient.actualizarEstadoPlaza(1L, "Reservada", TOKEN)).thenReturn(plaza);

        when(reservaRepository.save(any(Reserva.class))).thenAnswer(invocation -> {
            Reserva r = invocation.getArgument(0);
            r.setIdReserva(1L);
            r.setFechaReserva(LocalDateTime.of(2026, 6, 20, 10, 30));
            return r;
        });

        when(movimientoClient.registrarMovimiento(any(MovimientoCreateRequest.class), anyString()))
                .thenReturn(new MovimientoResponse());

        ReservaResponse resultado = reservaService.crearReserva(request, TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdReserva()).isEqualTo(1L);
        assertThat(resultado.getRutReserva()).isEqualTo("11111111-1");
        assertThat(resultado.getPatente()).isEqualTo("ABC123");
        assertThat(resultado.getIdPlaza()).isEqualTo(1L);

        ArgumentCaptor<Reserva> reservaCaptor = ArgumentCaptor.forClass(Reserva.class);
        verify(reservaRepository).save(reservaCaptor.capture());
        assertThat(reservaCaptor.getValue().getRutReserva()).isEqualTo("11111111-1");
    }

    @Test
    void crearReserva_debeLanzarExcepcionCuandoPlazaNoExiste() {
        ReservaRequest request = new ReservaRequest("11111111-1", "ABC123", 99L);
        when(plazaClient.obtenerPlaza(99L, TOKEN)).thenReturn(null);

        assertThatThrownBy(() -> reservaService.crearReserva(request, TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe una plaza con ID 99");
    }

    @Test
    void crearReserva_debeLanzarExcepcionCuandoPlazaNoDisponible() {
        ReservaRequest request = new ReservaRequest("11111111-1", "ABC123", 1L);
        PlazaResponse plaza = crearPlazaOcupada();
        when(plazaClient.obtenerPlaza(1L, TOKEN)).thenReturn(plaza);

        assertThatThrownBy(() -> reservaService.crearReserva(request, TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no está disponible");
    }
}
