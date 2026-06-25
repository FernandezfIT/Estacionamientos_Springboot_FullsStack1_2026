package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.ReporteClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.ReservaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.client.dto.ReservaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.request.CierreEjecutarRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.response.CierreResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.CierreEjecucionException;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception.CierreNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.model.Cierre;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.repository.CierreRepository;
import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.security.JwtService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CierreServiceTest {

    private static final String AUTH_HEADER = "Bearer token-de-prueba";
    private static final String TOKEN = "token-de-prueba";

    @Mock
    private CierreRepository cierreRepository;

    @Mock
    private PlazaClient plazaClient;

    @Mock
    private ReservaClient reservaClient;

    @Mock
    private ReporteClient reporteClient;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CierreService cierreService;

    private Cierre crearCierre() {
        return new Cierre(
                1L,
                LocalDate.of(2026, 6, 20),
                LocalTime.of(22, 0),
                LocalDateTime.of(2026, 6, 20, 22, 0),
                10L,
                "jefe@test.cl",
                "Jefe_Seguridad",
                2,
                3,
                1,
                10,
                0,
                0,
                5,
                2,
                1,
                2,
                "EJECUTADO",
                null,
                "Cierre ejecutado correctamente"
        );
    }

    private PlazaInternaResponse crearPlaza(Long idPlaza, String codigoPlaza, String estado) {
        PlazaInternaResponse plaza = mock(PlazaInternaResponse.class);

        lenient().when(plaza.getIdPlaza()).thenReturn(idPlaza);
        lenient().when(plaza.getCodigoPlaza()).thenReturn(codigoPlaza);
        lenient().when(plaza.getEstado()).thenReturn(estado);

        return plaza;
    }

    private ReservaInternaResponse crearReserva(Long idReserva, Long idPlaza) {
        ReservaInternaResponse reserva = mock(ReservaInternaResponse.class);

        lenient().when(reserva.getIdReserva()).thenReturn(idReserva);
        lenient().when(reserva.getIdPlaza()).thenReturn(idPlaza);

        return reserva;
    }

    @Test
    void listarCierres_debeRetornarListaDeCierres() {
        Cierre cierre = crearCierre();

        when(cierreRepository.findAll()).thenReturn(List.of(cierre));

        List<CierreResponse> resultado = cierreService.listarCierres();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdCierre()).isEqualTo(1L);
        assertThat(resultado.get(0).getEmailUsuarioEjecutor()).isEqualTo("jefe@test.cl");
        assertThat(resultado.get(0).getEstadoCierre()).isEqualTo("EJECUTADO");

        verify(cierreRepository).findAll();
    }

    @Test
    void obtenerCierrePorId_debeRetornarCierreCuandoExiste() {
        Cierre cierre = crearCierre();

        when(cierreRepository.findById(1L)).thenReturn(Optional.of(cierre));

        CierreResponse resultado = cierreService.obtenerCierrePorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCierre()).isEqualTo(1L);
        assertThat(resultado.getFechaCierre()).isEqualTo(LocalDate.of(2026, 6, 20));
        assertThat(resultado.getTotalPlazasOcupadasLiberadas()).isEqualTo(2);

        verify(cierreRepository).findById(1L);
    }

    @Test
    void obtenerCierrePorId_debeLanzarExcepcionCuandoNoExiste() {
        when(cierreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cierreService.obtenerCierrePorId(99L))
                .isInstanceOf(CierreNoEncontradoException.class)
                .hasMessageContaining("No existe un cierre con ID 99");

        verify(cierreRepository).findById(99L);
    }

    @Test
    void listarCierresPorFecha_debeRetornarCierresDeLaFecha() {
        LocalDate fecha = LocalDate.of(2026, 6, 20);
        Cierre cierre = crearCierre();

        when(cierreRepository.findByFechaCierre(fecha)).thenReturn(List.of(cierre));

        List<CierreResponse> resultado = cierreService.listarCierresPorFecha(fecha);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getFechaCierre()).isEqualTo(fecha);

        verify(cierreRepository).findByFechaCierre(fecha);
    }

    @Test
    void ejecutarCierre_debeGuardarCierreCuandoFlujoEsCorrecto() {
        CierreEjecutarRequest request = new CierreEjecutarRequest(" Cierre manual de prueba ");

        List<PlazaInternaResponse> plazasAntes = List.of(
                crearPlaza(1L, "P1", "Ocupada"),
                crearPlaza(2L, "P2", "Reservada"),
                crearPlaza(3L, "P3", "Disponible")
        );

        List<ReservaInternaResponse> reservas = List.of(
                crearReserva(100L, 2L),
                crearReserva(101L, 2L),
                crearReserva(102L, 4L)
        );

        List<PlazaInternaResponse> plazasDespues = List.of(
                crearPlaza(1L, "P1", "Disponible"),
                crearPlaza(2L, "P2", "Disponible"),
                crearPlaza(3L, "P3", "Disponible")
        );

        when(jwtService.obtenerIdUsuarioDesdeToken(TOKEN)).thenReturn(10L);
        when(jwtService.obtenerEmailDesdeToken(TOKEN)).thenReturn("jefe@test.cl");
        when(jwtService.obtenerRolDesdeToken(TOKEN)).thenReturn("Jefe_Seguridad");

        when(plazaClient.listarPlazas(AUTH_HEADER))
                .thenReturn(plazasAntes)
                .thenReturn(plazasDespues);

        when(reservaClient.listarReservas(AUTH_HEADER)).thenReturn(reservas);

        when(reporteClient.obtenerResumenPorFecha(any(LocalDate.class), eq(AUTH_HEADER)))
                .thenReturn(null);

        when(cierreRepository.save(any(Cierre.class))).thenAnswer(invocation -> {
            Cierre cierre = invocation.getArgument(0);
            cierre.setIdCierre(1L);
            return cierre;
        });

        CierreResponse resultado = cierreService.ejecutarCierre(request, AUTH_HEADER);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdCierre()).isEqualTo(1L);
        assertThat(resultado.getIdUsuarioEjecutor()).isEqualTo(10L);
        assertThat(resultado.getEmailUsuarioEjecutor()).isEqualTo("jefe@test.cl");
        assertThat(resultado.getRolEjecutor()).isEqualTo("Jefe_Seguridad");

        assertThat(resultado.getTotalPlazasOcupadasLiberadas()).isEqualTo(1);
        assertThat(resultado.getTotalReservasEliminadas()).isEqualTo(3);
        assertThat(resultado.getTotalPlazasReservadasLiberadas()).isEqualTo(2);

        assertThat(resultado.getPlazasDisponiblesFinal()).isEqualTo(3);
        assertThat(resultado.getPlazasOcupadasFinal()).isEqualTo(0);
        assertThat(resultado.getPlazasReservadasFinal()).isEqualTo(0);

        assertThat(resultado.getTotalMovimientos()).isEqualTo(0);
        assertThat(resultado.getTotalAccesos()).isEqualTo(0);
        assertThat(resultado.getTotalSalidas()).isEqualTo(0);
        assertThat(resultado.getTotalReservas()).isEqualTo(0);

        assertThat(resultado.getEstadoCierre()).isEqualTo("EJECUTADO");
        assertThat(resultado.getObservacion()).isEqualTo("Cierre manual de prueba");

        verify(plazaClient, times(2)).listarPlazas(AUTH_HEADER);

        verify(plazaClient).actualizarEstadoPlaza(1L, "Disponible", AUTH_HEADER);
        verify(plazaClient).actualizarEstadoPlaza(2L, "Disponible", AUTH_HEADER);
        verify(plazaClient).actualizarEstadoPlaza(4L, "Disponible", AUTH_HEADER);

        verify(reservaClient).eliminarReserva(100L, AUTH_HEADER);
        verify(reservaClient).eliminarReserva(101L, AUTH_HEADER);
        verify(reservaClient).eliminarReserva(102L, AUTH_HEADER);

        verify(reporteClient).obtenerResumenPorFecha(any(LocalDate.class), eq(AUTH_HEADER));
        verify(cierreRepository).save(any(Cierre.class));
    }

    @Test
    void ejecutarCierre_debeConstruirObservacionAutomaticaCuandoRequestEsNull() {
        List<PlazaInternaResponse> plazasAntes = List.of(
                crearPlaza(1L, "P1", "Ocupada")
        );

        List<ReservaInternaResponse> reservas = List.of(
                crearReserva(100L, 2L)
        );

        List<PlazaInternaResponse> plazasDespues = List.of(
                crearPlaza(1L, "P1", "Disponible"),
                crearPlaza(2L, "P2", "Disponible")
        );

        when(jwtService.obtenerIdUsuarioDesdeToken(TOKEN)).thenReturn(10L);
        when(jwtService.obtenerEmailDesdeToken(TOKEN)).thenReturn("jefe@test.cl");
        when(jwtService.obtenerRolDesdeToken(TOKEN)).thenReturn("Jefe_Seguridad");

        when(plazaClient.listarPlazas(AUTH_HEADER))
                .thenReturn(plazasAntes)
                .thenReturn(plazasDespues);

        when(reservaClient.listarReservas(AUTH_HEADER)).thenReturn(reservas);

        when(reporteClient.obtenerResumenPorFecha(any(LocalDate.class), eq(AUTH_HEADER)))
                .thenReturn(null);

        when(cierreRepository.save(any(Cierre.class))).thenAnswer(invocation -> {
            Cierre cierre = invocation.getArgument(0);
            cierre.setIdCierre(1L);
            return cierre;
        });

        CierreResponse resultado = cierreService.ejecutarCierre(null, AUTH_HEADER);

        assertThat(resultado.getObservacion())
                .contains("Cierre ejecutado correctamente")
                .contains("Se liberaron 1 plazas ocupadas")
                .contains("se eliminaron 1 reservas")
                .contains("se liberaron 1 plazas reservadas");
    }

    @Test
    void ejecutarCierre_debeLanzarExcepcionCuandoHeaderAuthorizationEsInvalido() {
        CierreEjecutarRequest request = new CierreEjecutarRequest("Cierre inválido");

        assertThatThrownBy(() -> cierreService.ejecutarCierre(request, "token-sin-bearer"))
                .isInstanceOf(CierreEjecucionException.class)
                .hasMessageContaining("Header Authorization inválido");

        verifyNoInteractions(jwtService, plazaClient, reservaClient, reporteClient, cierreRepository);
    }

    @Test
    void ejecutarCierre_debeGuardarCierreConContadoresEnCeroCuandoNoHayPlazasNiReservas() {
        CierreEjecutarRequest request = new CierreEjecutarRequest("Sin movimientos");

        when(jwtService.obtenerIdUsuarioDesdeToken(TOKEN)).thenReturn(10L);
        when(jwtService.obtenerEmailDesdeToken(TOKEN)).thenReturn("jefe@test.cl");
        when(jwtService.obtenerRolDesdeToken(TOKEN)).thenReturn("Jefe_Seguridad");

        when(plazaClient.listarPlazas(AUTH_HEADER))
                .thenReturn(List.of())
                .thenReturn(List.of());

        when(reservaClient.listarReservas(AUTH_HEADER)).thenReturn(List.of());

        when(reporteClient.obtenerResumenPorFecha(any(LocalDate.class), eq(AUTH_HEADER)))
                .thenReturn(null);

        when(cierreRepository.save(any(Cierre.class))).thenAnswer(invocation -> {
            Cierre cierre = invocation.getArgument(0);
            cierre.setIdCierre(1L);
            return cierre;
        });

        CierreResponse resultado = cierreService.ejecutarCierre(request, AUTH_HEADER);

        assertThat(resultado.getTotalPlazasOcupadasLiberadas()).isEqualTo(0);
        assertThat(resultado.getTotalReservasEliminadas()).isEqualTo(0);
        assertThat(resultado.getTotalPlazasReservadasLiberadas()).isEqualTo(0);
        assertThat(resultado.getPlazasDisponiblesFinal()).isEqualTo(0);
        assertThat(resultado.getPlazasOcupadasFinal()).isEqualTo(0);
        assertThat(resultado.getPlazasReservadasFinal()).isEqualTo(0);

        ArgumentCaptor<Cierre> cierreCaptor = ArgumentCaptor.forClass(Cierre.class);
        verify(cierreRepository).save(cierreCaptor.capture());

        Cierre cierreGuardado = cierreCaptor.getValue();

        assertThat(cierreGuardado.getEstadoCierre()).isEqualTo("EJECUTADO");
        assertThat(cierreGuardado.getObservacion()).isEqualTo("Sin movimientos");

        verify(plazaClient, times(2)).listarPlazas(AUTH_HEADER);
        verify(reservaClient).listarReservas(AUTH_HEADER);
        verify(reporteClient).obtenerResumenPorFecha(any(LocalDate.class), eq(AUTH_HEADER));
    }
}