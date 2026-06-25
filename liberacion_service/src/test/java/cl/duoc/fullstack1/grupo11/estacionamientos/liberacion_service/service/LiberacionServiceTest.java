package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.service;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.client.dto.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.request.LiberacionRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.dto.response.LiberacionResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.exception.LiberacionNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.model.Liberacion;
import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.repository.LiberacionRepository;

@ExtendWith(MockitoExtension.class)
class LiberacionServiceTest {

    @Mock
    private LiberacionRepository liberacionRepository;

    @Mock
    private PlazaClient plazaClient;

    @Mock
    private MovimientoClient movimientoClient;

    @InjectMocks
    private LiberacionService liberacionService;

    private static final String TOKEN = "Bearer test-token";

    private PlazaResponse crearPlazaOcupada() {
        return new PlazaResponse(1L, "P01", "Ocupada");
    }

    private PlazaResponse crearPlazaReservada() {
        return new PlazaResponse(1L, "P01", "Reservada");
    }

    private PlazaResponse crearPlazaDisponible() {
        return new PlazaResponse(1L, "P01", "Disponible");
    }

    private Liberacion crearLiberacion() {
        return new Liberacion(1L, 1L, LocalDateTime.of(2026, 6, 20, 11, 0));
    }

    @Test
    void listarLiberaciones_debeRetornarListaDeLiberaciones() {
        Liberacion liberacion = crearLiberacion();
        when(liberacionRepository.findAll()).thenReturn(List.of(liberacion));

        List<LiberacionResponse> resultado = liberacionService.listarLiberaciones();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdLiberacion()).isEqualTo(1L);
        assertThat(resultado.get(0).getIdPlaza()).isEqualTo(1L);
    }

    @Test
    void obtenerLiberacionPorId_debeRetornarLiberacionCuandoExiste() {
        Liberacion liberacion = crearLiberacion();
        when(liberacionRepository.findById(1L)).thenReturn(Optional.of(liberacion));

        LiberacionResponse resultado = liberacionService.obtenerLiberacionPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdLiberacion()).isEqualTo(1L);
        verify(liberacionRepository).findById(1L);
    }

    @Test
    void obtenerLiberacionPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(liberacionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> liberacionService.obtenerLiberacionPorId(99L))
                .isInstanceOf(LiberacionNoEncontradaException.class)
                .hasMessageContaining("No existe una liberación con ID 99");

        verify(liberacionRepository).findById(99L);
    }

    @Test
    void liberarPlaza_debeLiberarPlazaOcupada() {
        LiberacionRequest request = new LiberacionRequest(1L);
        PlazaResponse plaza = crearPlazaOcupada();

        when(plazaClient.obtenerPlaza(1L, TOKEN)).thenReturn(plaza);
        when(plazaClient.actualizarEstadoPlaza(1L, "Disponible", TOKEN)).thenReturn(plaza);

        when(liberacionRepository.save(any(Liberacion.class))).thenAnswer(invocation -> {
            Liberacion l = invocation.getArgument(0);
            l.setIdLiberacion(1L);
            l.setFechaLiberacion(LocalDateTime.of(2026, 6, 20, 11, 0));
            return l;
        });

        when(movimientoClient.registrarMovimiento(any(MovimientoCreateRequest.class), anyString()))
                .thenReturn(new MovimientoResponse());

        LiberacionResponse resultado = liberacionService.liberarPlaza(request, TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdLiberacion()).isEqualTo(1L);
        assertThat(resultado.getIdPlaza()).isEqualTo(1L);

        ArgumentCaptor<Liberacion> liberacionCaptor = ArgumentCaptor.forClass(Liberacion.class);
        verify(liberacionRepository).save(liberacionCaptor.capture());
        assertThat(liberacionCaptor.getValue().getIdPlaza()).isEqualTo(1L);
    }

    @Test
    void liberarPlaza_debeLiberarPlazaReservada() {
        LiberacionRequest request = new LiberacionRequest(1L);
        PlazaResponse plaza = crearPlazaReservada();

        when(plazaClient.obtenerPlaza(1L, TOKEN)).thenReturn(plaza);
        when(plazaClient.actualizarEstadoPlaza(1L, "Disponible", TOKEN)).thenReturn(plaza);

        when(liberacionRepository.save(any(Liberacion.class))).thenAnswer(invocation -> {
            Liberacion l = invocation.getArgument(0);
            l.setIdLiberacion(1L);
            l.setFechaLiberacion(LocalDateTime.of(2026, 6, 20, 11, 0));
            return l;
        });

        when(movimientoClient.registrarMovimiento(any(MovimientoCreateRequest.class), anyString()))
                .thenReturn(new MovimientoResponse());

        LiberacionResponse resultado = liberacionService.liberarPlaza(request, TOKEN);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdLiberacion()).isEqualTo(1L);
    }

    @Test
    void liberarPlaza_debeLanzarExcepcionCuandoPlazaNoExiste() {
        LiberacionRequest request = new LiberacionRequest(99L);
        when(plazaClient.obtenerPlaza(99L, TOKEN)).thenReturn(null);

        assertThatThrownBy(() -> liberacionService.liberarPlaza(request, TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No existe una plaza con ID 99");
    }

    @Test
    void liberarPlaza_debeLanzarExcepcionCuandoPlazaNoEstaOcupadaNiReservada() {
        LiberacionRequest request = new LiberacionRequest(1L);
        PlazaResponse plaza = crearPlazaDisponible();
        when(plazaClient.obtenerPlaza(1L, TOKEN)).thenReturn(plaza);

        assertThatThrownBy(() -> liberacionService.liberarPlaza(request, TOKEN))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no está ocupada ni reservada");
    }
}
