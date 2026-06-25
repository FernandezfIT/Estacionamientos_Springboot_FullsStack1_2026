package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.request.PlazaUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.dto.response.PlazaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.exception.PlazaNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.exception.PlazaNoEncontradaException;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.model.Plaza;
import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.repository.PlazaRepository;

@ExtendWith(MockitoExtension.class)
class PlazaServiceTest {

    @Mock
    private PlazaRepository plazaRepository;

    @InjectMocks
    private PlazaService plazaService;

    private Plaza crearPlazaDisponible() {
        return new Plaza(1L, "P01", "Disponible");
    }

    private Plaza crearPlazaOcupada() {
        return new Plaza(2L, "P02", "Ocupada");
    }

    @Test
    void listarPlazas_debeRetornarListaDePlazas() {
        when(plazaRepository.findAll()).thenReturn(List.of(crearPlazaDisponible(), crearPlazaOcupada()));

        List<PlazaResponse> resultado = plazaService.listarPlazas();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getCodigoPlaza()).isEqualTo("P01");
        assertThat(resultado.get(0).getEstado()).isEqualTo("Disponible");
        assertThat(resultado.get(1).getCodigoPlaza()).isEqualTo("P02");
        assertThat(resultado.get(1).getEstado()).isEqualTo("Ocupada");
    }

    @Test
    void obtenerPlazaPorId_debeRetornarPlazaCuandoExiste() {
        Plaza plaza = crearPlazaDisponible();
        when(plazaRepository.findById(1L)).thenReturn(Optional.of(plaza));

        PlazaResponse resultado = plazaService.obtenerPlazaPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdPlaza()).isEqualTo(1L);
        assertThat(resultado.getCodigoPlaza()).isEqualTo("P01");
        assertThat(resultado.getEstado()).isEqualTo("Disponible");
        verify(plazaRepository).findById(1L);
    }

    @Test
    void obtenerPlazaPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(plazaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plazaService.obtenerPlazaPorId(99L))
                .isInstanceOf(PlazaNoEncontradaException.class)
                .hasMessageContaining("No existe una plaza con ID 99");

        verify(plazaRepository).findById(99L);
    }

    @Test
    void obtenerPlazaPorCodigo_debeRetornarPlazaCuandoExiste() {
        Plaza plaza = crearPlazaDisponible();
        when(plazaRepository.findByCodigoPlaza("P01")).thenReturn(Optional.of(plaza));

        PlazaResponse resultado = plazaService.obtenerPlazaPorCodigo("P01");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getCodigoPlaza()).isEqualTo("P01");
        assertThat(resultado.getEstado()).isEqualTo("Disponible");
        verify(plazaRepository).findByCodigoPlaza("P01");
    }

    @Test
    void obtenerPlazaPorCodigo_debeLanzarExcepcionCuandoNoExiste() {
        when(plazaRepository.findByCodigoPlaza("P99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plazaService.obtenerPlazaPorCodigo("P99"))
                .isInstanceOf(PlazaNoEncontradaException.class)
                .hasMessageContaining("No existe una plaza con código P99");

        verify(plazaRepository).findByCodigoPlaza("P99");
    }

    @Test
    void actualizarEstadoPlaza_debeActualizarEstado() {
        Plaza plaza = crearPlazaDisponible();
        PlazaUpdateRequest request = new PlazaUpdateRequest("Ocupada");

        when(plazaRepository.findById(1L)).thenReturn(Optional.of(plaza));
        when(plazaRepository.save(plaza)).thenReturn(plaza);

        PlazaResponse resultado = plazaService.actualizarEstadoPlaza(1L, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("Ocupada");
        verify(plazaRepository).findById(1L);
        verify(plazaRepository).save(plaza);
    }

    @Test
    void actualizarEstadoPlaza_debeLanzarExcepcionCuandoNoExiste() {
        PlazaUpdateRequest request = new PlazaUpdateRequest("Ocupada");
        when(plazaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> plazaService.actualizarEstadoPlaza(99L, request))
                .isInstanceOf(PlazaNoEncontradaException.class)
                .hasMessageContaining("No existe una plaza con ID 99");

        verify(plazaRepository).findById(99L);
    }

    @Test
    void ocuparPlazaPorCodigo_debeOcuparPlazaDisponible() {
        Plaza plaza = crearPlazaDisponible();
        when(plazaRepository.findByCodigoPlaza("P01")).thenReturn(Optional.of(plaza));
        when(plazaRepository.save(plaza)).thenReturn(plaza);

        PlazaResponse resultado = plazaService.ocuparPlazaPorCodigo("P01");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getEstado()).isEqualTo("Ocupada");
        verify(plazaRepository).findByCodigoPlaza("P01");
        verify(plazaRepository).save(plaza);
    }

    @Test
    void ocuparPlazaPorCodigo_debeLanzarExcepcionCuandoPlazaNoDisponible() {
        Plaza plaza = crearPlazaOcupada();
        when(plazaRepository.findByCodigoPlaza("P02")).thenReturn(Optional.of(plaza));

        assertThatThrownBy(() -> plazaService.ocuparPlazaPorCodigo("P02"))
                .isInstanceOf(PlazaNoDisponibleException.class)
                .hasMessageContaining("no está disponible");

        verify(plazaRepository).findByCodigoPlaza("P02");
    }
}
