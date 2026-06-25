package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.service;

import java.time.LocalDateTime;
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
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.MovimientoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.PlazaClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.VehiculoClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.PlazaInternaResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.UsuarioInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto.VehiculoInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.request.AccesoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.dto.response.AccesoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.AccesoNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.MicroservicioNoDisponibleException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception.VehiculoNoPerteneceUsuarioException;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.model.Acceso;
import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.repository.AccesoRepository;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    private static final String AUTH_HEADER = "Bearer token-de-prueba";

    @Mock
    private AccesoRepository accesoRepository;

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private VehiculoClient vehiculoClient;

    @Mock
    private PlazaClient plazaClient;

    @Mock
    private MovimientoClient movimientoClient;

    @InjectMocks
    private AccesoService accesoService;

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

    private Acceso crearAcceso() {
        return new Acceso(
                1L,
                10L,
                "11111111-1",
                20L,
                "AB1234",
                30L,
                "P30",
                LocalDateTime.of(2026, 6, 20, 10, 30),
                "AUTORIZADO",
                "Acceso autorizado correctamente"
        );
    }

    private UsuarioInternoResponse crearUsuarioInterno() {
        return new UsuarioInternoResponse(
                10L,
                "11111111-1",
                "Felipe",
                "Fernandez",
                "felipe@test.cl",
                "Funcionario"
        );
    }

    private VehiculoInternoResponse crearVehiculoInterno() {
        return new VehiculoInternoResponse(
                20L,
                "AB1234",
                "Toyota",
                "Rojo",
                1L,
                "Automovil",
                10L
        );
    }

    private PlazaInternaResponse crearPlazaInterna() {
        return new PlazaInternaResponse(
                30L,
                "P30",
                "OCUPADA"
        );
    }

    @Test
    void listarAccesos_debeRetornarListaDeAccesos() {
        Acceso acceso = crearAcceso();

        when(accesoRepository.findAll()).thenReturn(List.of(acceso));

        List<AccesoResponse> resultado = accesoService.listarAccesos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdAcceso()).isEqualTo(1L);
        assertThat(resultado.get(0).getRutUsuario()).isEqualTo("11111111-1");
        assertThat(resultado.get(0).getPatente()).isEqualTo("AB1234");
        assertThat(resultado.get(0).getCodigoPlaza()).isEqualTo("P30");

        verify(accesoRepository).findAll();
    }

    @Test
    void obtenerAccesoPorId_debeRetornarAccesoCuandoExiste() {
        Acceso acceso = crearAcceso();

        when(accesoRepository.findById(1L)).thenReturn(Optional.of(acceso));

        AccesoResponse resultado = accesoService.obtenerAccesoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdAcceso()).isEqualTo(1L);
        assertThat(resultado.getIdUsuario()).isEqualTo(10L);
        assertThat(resultado.getIdVehiculo()).isEqualTo(20L);
        assertThat(resultado.getEstadoAcceso()).isEqualTo("AUTORIZADO");

        verify(accesoRepository).findById(1L);
    }

    @Test
    void obtenerAccesoPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(accesoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accesoService.obtenerAccesoPorId(99L))
                .isInstanceOf(AccesoNoEncontradoException.class)
                .hasMessageContaining("No existe un acceso con ID 99");

        verify(accesoRepository).findById(99L);
    }

    @Test
    void listarAccesosPorRut_debeNormalizarRutYRetornarLista() {
        Acceso acceso = crearAcceso();

        when(accesoRepository.findByRutUsuario("11111111-1")).thenReturn(List.of(acceso));

        List<AccesoResponse> resultado = accesoService.listarAccesosPorRut(" 11111111-1 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getRutUsuario()).isEqualTo("11111111-1");

        verify(accesoRepository).findByRutUsuario("11111111-1");
    }

    @Test
    void listarAccesosPorPatente_debeNormalizarPatenteYRetornarLista() {
        Acceso acceso = crearAcceso();

        when(accesoRepository.findByPatente("AB1234")).thenReturn(List.of(acceso));

        List<AccesoResponse> resultado = accesoService.listarAccesosPorPatente(" ab1234 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPatente()).isEqualTo("AB1234");

        verify(accesoRepository).findByPatente("AB1234");
    }

    @Test
    void listarAccesosPorCodigoPlaza_debeNormalizarCodigoYRetornarLista() {
        Acceso acceso = crearAcceso();

        when(accesoRepository.findByCodigoPlaza("P30")).thenReturn(List.of(acceso));

        List<AccesoResponse> resultado = accesoService.listarAccesosPorCodigoPlaza(" p30 ");

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getCodigoPlaza()).isEqualTo("P30");

        verify(accesoRepository).findByCodigoPlaza("P30");
    }

    @Test
    void registrarAcceso_debeGuardarAccesoYRegistrarMovimientoCuandoDatosSonValidos() {
        AccesoCreateRequest request = new AccesoCreateRequest(
                " 11111111-1 ",
                " ab1234 ",
                " p30 "
        );

        when(usuarioClient.buscarUsuarioPorRut("11111111-1"))
                .thenReturn(crearUsuarioInterno());

        when(vehiculoClient.buscarVehiculoPorPatente("AB1234", AUTH_HEADER))
                .thenReturn(crearVehiculoInterno());

        when(plazaClient.ocuparPlazaPorCodigo("P30", AUTH_HEADER))
                .thenReturn(crearPlazaInterna());

        when(accesoRepository.save(any(Acceso.class))).thenAnswer(invocation -> {
            Acceso acceso = invocation.getArgument(0);
            acceso.setIdAcceso(1L);
            acceso.setFechaIngreso(LocalDateTime.of(2026, 6, 20, 10, 30));
            return acceso;
        });

        AccesoResponse resultado = accesoService.registrarAcceso(request, AUTH_HEADER);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdAcceso()).isEqualTo(1L);
        assertThat(resultado.getIdUsuario()).isEqualTo(10L);
        assertThat(resultado.getRutUsuario()).isEqualTo("11111111-1");
        assertThat(resultado.getIdVehiculo()).isEqualTo(20L);
        assertThat(resultado.getPatente()).isEqualTo("AB1234");
        assertThat(resultado.getIdPlaza()).isEqualTo(30L);
        assertThat(resultado.getCodigoPlaza()).isEqualTo("P30");
        assertThat(resultado.getEstadoAcceso()).isEqualTo("AUTORIZADO");
        assertThat(resultado.getObservacion()).isEqualTo("Acceso autorizado correctamente");

        ArgumentCaptor<Acceso> accesoCaptor = ArgumentCaptor.forClass(Acceso.class);
        verify(accesoRepository).save(accesoCaptor.capture());

        Acceso accesoGuardado = accesoCaptor.getValue();

        assertThat(accesoGuardado.getIdUsuario()).isEqualTo(10L);
        assertThat(accesoGuardado.getRutUsuario()).isEqualTo("11111111-1");
        assertThat(accesoGuardado.getIdVehiculo()).isEqualTo(20L);
        assertThat(accesoGuardado.getPatente()).isEqualTo("AB1234");
        assertThat(accesoGuardado.getIdPlaza()).isEqualTo(30L);
        assertThat(accesoGuardado.getCodigoPlaza()).isEqualTo("P30");
        assertThat(accesoGuardado.getEstadoAcceso()).isEqualTo("AUTORIZADO");

        ArgumentCaptor<MovimientoCreateRequest> movimientoCaptor =
                ArgumentCaptor.forClass(MovimientoCreateRequest.class);

        verify(movimientoClient).registrarMovimiento(movimientoCaptor.capture(), eq(AUTH_HEADER));

        MovimientoCreateRequest movimiento = movimientoCaptor.getValue();

        assertThat(movimiento.getTipoMovimiento()).isEqualTo("ACCESO");
        assertThat(movimiento.getIdUsuario()).isEqualTo(10L);
        assertThat(movimiento.getRutUsuario()).isEqualTo("11111111-1");
        assertThat(movimiento.getIdVehiculo()).isEqualTo(20L);
        assertThat(movimiento.getPatente()).isEqualTo("AB1234");
        assertThat(movimiento.getIdPlaza()).isEqualTo(30L);
        assertThat(movimiento.getCodigoPlaza()).isEqualTo("P30");
        assertThat(movimiento.getIdReferencia()).isEqualTo(1L);
        assertThat(movimiento.getServicioOrigen()).isEqualTo("acceso_service");
        assertThat(movimiento.getDescripcion()).isEqualTo("Ingreso autorizado al estacionamiento");
    }

    @Test
    void registrarAcceso_debeLanzarExcepcionCuandoUsuarioServiceRespondeNull() {
        AccesoCreateRequest request = new AccesoCreateRequest(
                "11111111-1",
                "AB1234",
                "P30"
        );

        when(usuarioClient.buscarUsuarioPorRut("11111111-1")).thenReturn(null);

        assertThatThrownBy(() -> accesoService.registrarAcceso(request, AUTH_HEADER))
                .isInstanceOf(MicroservicioNoDisponibleException.class)
                .hasMessageContaining("usuario_service respondió sin datos válidos");

        verify(usuarioClient).buscarUsuarioPorRut("11111111-1");
        verifyNoInteractions(vehiculoClient, plazaClient, movimientoClient);
    }

    @Test
    void registrarAcceso_debeLanzarExcepcionCuandoVehiculoNoPerteneceAlUsuario() {
        AccesoCreateRequest request = new AccesoCreateRequest(
                "11111111-1",
                "AB1234",
                "P30"
        );

        UsuarioInternoResponse usuario = crearUsuarioInterno();

        VehiculoInternoResponse vehiculoDeOtroUsuario = new VehiculoInternoResponse(
                20L,
                "AB1234",
                "Toyota",
                "Rojo",
                1L,
                "Automovil",
                99L
        );

        when(usuarioClient.buscarUsuarioPorRut("11111111-1")).thenReturn(usuario);
        when(vehiculoClient.buscarVehiculoPorPatente("AB1234", AUTH_HEADER))
                .thenReturn(vehiculoDeOtroUsuario);

        assertThatThrownBy(() -> accesoService.registrarAcceso(request, AUTH_HEADER))
                .isInstanceOf(VehiculoNoPerteneceUsuarioException.class)
                .hasMessageContaining("El vehículo indicado no pertenece al usuario informado");

        verify(usuarioClient).buscarUsuarioPorRut("11111111-1");
        verify(vehiculoClient).buscarVehiculoPorPatente("AB1234", AUTH_HEADER);
        verifyNoInteractions(plazaClient, movimientoClient);
    }
}