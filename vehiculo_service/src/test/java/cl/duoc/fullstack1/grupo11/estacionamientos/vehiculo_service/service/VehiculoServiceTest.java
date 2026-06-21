package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.service;

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

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.request.VehiculoUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.dto.response.VehiculoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.RecursoDuplicadoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.UsuarioNoEncontradoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.exception.VehiculoNoEncontradoEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.TipoVehiculo;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.Vehiculo;
import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.repository.VehiculoRepository;

@ExtendWith(MockitoExtension.class)
class VehiculoServiceTest {

    @Mock
    private VehiculoRepository vehiculoRepository;

    @Mock
    private TipoVehiculoService tipoVehiculoService;

    @Mock
    private UsuarioClient usuarioClient;

    @InjectMocks
    private VehiculoService vehiculoService;

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

    private TipoVehiculo crearTipoVehiculo() {
        return new TipoVehiculo(
                1L,
                "Automovil",
                "Vehículo liviano"
        );
    }

    private Vehiculo crearVehiculo() {
        return new Vehiculo(
                1L,
                "AB1234",
                "Toyota",
                "Rojo",
                crearTipoVehiculo(),
                10L
        );
    }

    @Test
    void listarVehiculos_debeRetornarListaDeVehiculos() {
        Vehiculo vehiculo = crearVehiculo();

        when(vehiculoRepository.findAll()).thenReturn(List.of(vehiculo));

        List<VehiculoResponse> resultado = vehiculoService.listarVehiculos();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdVehiculo()).isEqualTo(1L);
        assertThat(resultado.get(0).getPatente()).isEqualTo("AB1234");
        assertThat(resultado.get(0).getTipoVehiculo()).isEqualTo("Automovil");
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(10L);

        verify(vehiculoRepository).findAll();
    }

    @Test
    void obtenerVehiculoPorId_debeRetornarVehiculoCuandoExiste() {
        Vehiculo vehiculo = crearVehiculo();

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculo));

        VehiculoResponse resultado = vehiculoService.obtenerVehiculoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVehiculo()).isEqualTo(1L);
        assertThat(resultado.getPatente()).isEqualTo("AB1234");
        assertThat(resultado.getMarca()).isEqualTo("Toyota");
        assertThat(resultado.getColor()).isEqualTo("Rojo");

        verify(vehiculoRepository).findById(1L);
    }

    @Test
    void obtenerVehiculoPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(vehiculoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> vehiculoService.obtenerVehiculoPorId(99L))
                .isInstanceOf(VehiculoNoEncontradoEx.class)
                .hasMessageContaining("No existe un vehículo con ID 99");

        verify(vehiculoRepository).findById(99L);
    }

    @Test
    void obtenerVehiculoPorPatente_debeNormalizarPatenteYRetornarVehiculo() {
        Vehiculo vehiculo = crearVehiculo();

        when(vehiculoRepository.findByPatente("AB1234")).thenReturn(Optional.of(vehiculo));

        VehiculoResponse resultado = vehiculoService.obtenerVehiculoPorPatente(" ab1234 ");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPatente()).isEqualTo("AB1234");
        assertThat(resultado.getTipoVehiculo()).isEqualTo("Automovil");

        verify(vehiculoRepository).findByPatente("AB1234");
    }

    @Test
    void crearVehiculo_debeGuardarVehiculoNormalizadoCuandoDatosSonValidos() {
        VehiculoCreateRequest request = new VehiculoCreateRequest(
                " ab1234 ",
                " Toyota ",
                " Rojo ",
                1L,
                10L
        );

        TipoVehiculo tipoVehiculo = crearTipoVehiculo();

        when(vehiculoRepository.existsByPatente("AB1234")).thenReturn(false);
        when(usuarioClient.existeUsuarioPorId(10L)).thenReturn(true);
        when(tipoVehiculoService.buscarTipoVehiculoPorId(1L)).thenReturn(tipoVehiculo);

        when(vehiculoRepository.save(any(Vehiculo.class))).thenAnswer(invocation -> {
            Vehiculo vehiculo = invocation.getArgument(0);
            vehiculo.setIdVehiculo(1L);
            return vehiculo;
        });

        VehiculoResponse resultado = vehiculoService.crearVehiculo(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdVehiculo()).isEqualTo(1L);
        assertThat(resultado.getPatente()).isEqualTo("AB1234");
        assertThat(resultado.getMarca()).isEqualTo("Toyota");
        assertThat(resultado.getColor()).isEqualTo("Rojo");
        assertThat(resultado.getTipoVehiculo()).isEqualTo("Automovil");
        assertThat(resultado.getIdUsuario()).isEqualTo(10L);

        ArgumentCaptor<Vehiculo> vehiculoCaptor = ArgumentCaptor.forClass(Vehiculo.class);
        verify(vehiculoRepository).save(vehiculoCaptor.capture());

        Vehiculo vehiculoGuardado = vehiculoCaptor.getValue();

        assertThat(vehiculoGuardado.getPatente()).isEqualTo("AB1234");
        assertThat(vehiculoGuardado.getMarca()).isEqualTo("Toyota");
        assertThat(vehiculoGuardado.getColor()).isEqualTo("Rojo");
        assertThat(vehiculoGuardado.getTipoVehiculo().getNombre()).isEqualTo("Automovil");
        assertThat(vehiculoGuardado.getIdUsuario()).isEqualTo(10L);
    }

    @Test
    void crearVehiculo_debeLanzarExcepcionCuandoPatenteYaExiste() {
        VehiculoCreateRequest request = new VehiculoCreateRequest(
                "ab1234",
                "Toyota",
                "Rojo",
                1L,
                10L
        );

        when(vehiculoRepository.existsByPatente("AB1234")).thenReturn(true);

        assertThatThrownBy(() -> vehiculoService.crearVehiculo(request))
                .isInstanceOf(RecursoDuplicadoEx.class)
                .hasMessageContaining("Ya existe un vehículo registrado con la patente indicada");

        verify(vehiculoRepository).existsByPatente("AB1234");
    }

    @Test
    void crearVehiculo_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        VehiculoCreateRequest request = new VehiculoCreateRequest(
                "ab1234",
                "Toyota",
                "Rojo",
                1L,
                99L
        );

        when(vehiculoRepository.existsByPatente("AB1234")).thenReturn(false);
        when(usuarioClient.existeUsuarioPorId(99L)).thenReturn(false);

        assertThatThrownBy(() -> vehiculoService.crearVehiculo(request))
                .isInstanceOf(UsuarioNoEncontradoEx.class)
                .hasMessageContaining("No existe un usuario con ID 99");

        verify(usuarioClient).existeUsuarioPorId(99L);
    }

    @Test
    void actualizarVehiculo_debeActualizarVehiculoCuandoExiste() {
        Vehiculo vehiculoExistente = crearVehiculo();

        VehiculoUpdateRequest request = new VehiculoUpdateRequest(
                " cd5678 ",
                " Nissan ",
                " Azul ",
                1L,
                10L
        );

        TipoVehiculo tipoVehiculo = crearTipoVehiculo();

        when(vehiculoRepository.findById(1L)).thenReturn(Optional.of(vehiculoExistente));
        when(vehiculoRepository.existsByPatente("CD5678")).thenReturn(false);
        when(usuarioClient.existeUsuarioPorId(10L)).thenReturn(true);
        when(tipoVehiculoService.buscarTipoVehiculoPorId(1L)).thenReturn(tipoVehiculo);
        when(vehiculoRepository.save(any(Vehiculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VehiculoResponse resultado = vehiculoService.actualizarVehiculo(1L, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getPatente()).isEqualTo("CD5678");
        assertThat(resultado.getMarca()).isEqualTo("Nissan");
        assertThat(resultado.getColor()).isEqualTo("Azul");

        ArgumentCaptor<Vehiculo> vehiculoCaptor = ArgumentCaptor.forClass(Vehiculo.class);
        verify(vehiculoRepository).save(vehiculoCaptor.capture());

        Vehiculo vehiculoActualizado = vehiculoCaptor.getValue();

        assertThat(vehiculoActualizado.getPatente()).isEqualTo("CD5678");
        assertThat(vehiculoActualizado.getMarca()).isEqualTo("Nissan");
        assertThat(vehiculoActualizado.getColor()).isEqualTo("Azul");
        assertThat(vehiculoActualizado.getIdUsuario()).isEqualTo(10L);
    }
}