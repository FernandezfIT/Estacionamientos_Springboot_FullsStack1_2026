package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.request.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.dto.response.MovimientoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.exception.MovimientoNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.model.Movimiento;
import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.repository.MovimientoRepository;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken("tester@test.cl", null, List.of());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Movimiento crearMovimiento() {
        return new Movimiento(
                1L,
                "ACCESO",
                10L,
                "11111111-1",
                20L,
                "AB1234",
                30L,
                "P30",
                40L,
                "acceso_service",
                LocalDateTime.of(2026, 6, 20, 10, 30),
                "Movimiento de acceso registrado"
        );
    }

    @Test
    void crearMovimiento_debeGuardarMovimientoNormalizadoCuandoRequestEsValido() {
        MovimientoCreateRequest request = new MovimientoCreateRequest(
                " acceso ",
                10L,
                " 11111111-1 ",
                20L,
                " ab1234 ",
                30L,
                " p30 ",
                40L,
                " acceso_service ",
                " Movimiento de acceso registrado "
        );

        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(invocation -> {
            Movimiento movimiento = invocation.getArgument(0);
            movimiento.setIdMovimiento(1L);
            movimiento.setFechaMovimiento(LocalDateTime.of(2026, 6, 20, 10, 30));
            return movimiento;
        });

        MovimientoResponse resultado = movimientoService.crearMovimiento(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdMovimiento()).isEqualTo(1L);
        assertThat(resultado.getTipoMovimiento()).isEqualTo("ACCESO");
        assertThat(resultado.getRutUsuario()).isEqualTo("11111111-1");
        assertThat(resultado.getPatente()).isEqualTo("AB1234");
        assertThat(resultado.getCodigoPlaza()).isEqualTo("P30");
        assertThat(resultado.getServicioOrigen()).isEqualTo("acceso_service");
        assertThat(resultado.getDescripcion()).isEqualTo("Movimiento de acceso registrado");

        ArgumentCaptor<Movimiento> movimientoCaptor = ArgumentCaptor.forClass(Movimiento.class);
        verify(movimientoRepository).save(movimientoCaptor.capture());

        Movimiento movimientoGuardado = movimientoCaptor.getValue();

        assertThat(movimientoGuardado.getTipoMovimiento()).isEqualTo("ACCESO");
        assertThat(movimientoGuardado.getRutUsuario()).isEqualTo("11111111-1");
        assertThat(movimientoGuardado.getPatente()).isEqualTo("AB1234");
        assertThat(movimientoGuardado.getCodigoPlaza()).isEqualTo("P30");
        assertThat(movimientoGuardado.getServicioOrigen()).isEqualTo("acceso_service");
        assertThat(movimientoGuardado.getDescripcion()).isEqualTo("Movimiento de acceso registrado");
    }

    @Test
    void listarMovimientos_debeRetornarListaDeMovimientos() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findAll()).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdMovimiento()).isEqualTo(1L);
        assertThat(resultado.get(0).getTipoMovimiento()).isEqualTo("ACCESO");
        assertThat(resultado.get(0).getPatente()).isEqualTo("AB1234");

        verify(movimientoRepository).findAll();
    }

    @Test
    void obtenerMovimientoPorId_debeRetornarMovimientoCuandoExiste() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findById(1L)).thenReturn(Optional.of(movimiento));

        MovimientoResponse resultado = movimientoService.obtenerMovimientoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdMovimiento()).isEqualTo(1L);
        assertThat(resultado.getTipoMovimiento()).isEqualTo("ACCESO");
        assertThat(resultado.getCodigoPlaza()).isEqualTo("P30");

        verify(movimientoRepository).findById(1L);
    }

    @Test
    void obtenerMovimientoPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(movimientoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.obtenerMovimientoPorId(99L))
                .isInstanceOf(MovimientoNoEncontradoException.class)
                .hasMessageContaining("No existe un movimiento con ID 99");

        verify(movimientoRepository).findById(99L);
    }

    @Test
    void listarMovimientosPorTipo_debeNormalizarTipoYRetornarLista() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findByTipoMovimiento("ACCESO")).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorTipo(" acceso ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getTipoMovimiento()).isEqualTo("ACCESO");

        verify(movimientoRepository).findByTipoMovimiento("ACCESO");
    }

    @Test
    void listarMovimientosPorRut_debeNormalizarRutYRetornarLista() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findByRutUsuario("11111111-1")).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorRut(" 11111111-1 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRutUsuario()).isEqualTo("11111111-1");

        verify(movimientoRepository).findByRutUsuario("11111111-1");
    }

    @Test
    void listarMovimientosPorPatente_debeNormalizarPatenteYRetornarLista() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findByPatente("AB1234")).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorPatente(" ab1234 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPatente()).isEqualTo("AB1234");

        verify(movimientoRepository).findByPatente("AB1234");
    }

    @Test
    void listarMovimientosPorCodigoPlaza_debeNormalizarCodigoYRetornarLista() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findByCodigoPlaza("P30")).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorCodigoPlaza(" p30 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigoPlaza()).isEqualTo("P30");

        verify(movimientoRepository).findByCodigoPlaza("P30");
    }

    @Test
    void listarMovimientosPorServicioOrigen_debeNormalizarTextoYRetornarLista() {
        Movimiento movimiento = crearMovimiento();

        when(movimientoRepository.findByServicioOrigen("acceso_service")).thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorServicioOrigen(" acceso_service ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getServicioOrigen()).isEqualTo("acceso_service");

        verify(movimientoRepository).findByServicioOrigen("acceso_service");
    }

    @Test
    void listarMovimientosPorFecha_debeBuscarEntreInicioYFinDelDia() {
        Movimiento movimiento = crearMovimiento();

        LocalDate fecha = LocalDate.of(2026, 6, 20);
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);

        when(movimientoRepository.findByFechaMovimientoBetween(inicioDia, finDia))
                .thenReturn(List.of(movimiento));

        List<MovimientoResponse> resultado = movimientoService.listarMovimientosPorFecha(fecha);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getFechaMovimiento())
                .isEqualTo(LocalDateTime.of(2026, 6, 20, 10, 30));

        verify(movimientoRepository).findByFechaMovimientoBetween(inicioDia, finDia);
    }
}