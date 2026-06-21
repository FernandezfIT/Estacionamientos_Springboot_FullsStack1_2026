package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.RecursoDuplicadoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.UsuarioNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Rol;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Usuario;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository.RolRepository;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

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

    private Rol crearRolFuncionario() {
        return new Rol(
                1L,
                "Funcionario",
                "Usuario funcionario del sistema"
        );
    }

    private Usuario crearUsuario() {
        return new Usuario(
                1L,
                "11111111-1",
                "Felipe",
                "Fernandez",
                "felipe@test.cl",
                "password-hasheada",
                "912345678",
                LocalDateTime.of(2026, 6, 20, 10, 30),
                crearRolFuncionario()
        );
    }

    @Test
    void listarUsuarios_debeRetornarListaDeUsuarios() {
        Usuario usuario = crearUsuario();

        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponse> resultado = usuarioService.listarUsuarios();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdUsuario()).isEqualTo(1L);
        assertThat(resultado.get(0).getRut()).isEqualTo("11111111-1");
        assertThat(resultado.get(0).getEmail()).isEqualTo("felipe@test.cl");
        assertThat(resultado.get(0).getRol()).isEqualTo("Funcionario");
    }

    @Test
    void obtenerUsuarioPorId_debeRetornarUsuarioCuandoExiste() {
        Usuario usuario = crearUsuario();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        UsuarioResponse resultado = usuarioService.obtenerUsuarioPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdUsuario()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Felipe");
        assertThat(resultado.getApellido()).isEqualTo("Fernandez");
        assertThat(resultado.getIdRol()).isEqualTo(1L);

        verify(usuarioRepository).findById(1L);
    }

    @Test
    void obtenerUsuarioPorId_debeLanzarExcepcionCuandoNoExiste() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.obtenerUsuarioPorId(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("No existe un usuario con ID 99");

        verify(usuarioRepository).findById(99L);
    }

    @Test
    void crearUsuario_debeGuardarUsuarioNormalizadoYConPasswordEncriptada() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                " 11111111-1 ",
                " Felipe ",
                " Fernandez ",
                " FELIPE@TEST.CL ",
                "123456",
                " 912345678 ",
                1L
        );

        Rol rol = crearRolFuncionario();

        when(usuarioRepository.existsByRut("11111111-1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("felipe@test.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("123456")).thenReturn("password-hasheada");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setIdUsuario(1L);
            usuario.setFechaCreacion(LocalDateTime.of(2026, 6, 20, 10, 30));
            return usuario;
        });

        UsuarioResponse resultado = usuarioService.crearUsuario(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getIdUsuario()).isEqualTo(1L);
        assertThat(resultado.getRut()).isEqualTo("11111111-1");
        assertThat(resultado.getNombre()).isEqualTo("Felipe");
        assertThat(resultado.getEmail()).isEqualTo("felipe@test.cl");
        assertThat(resultado.getTelefono()).isEqualTo("912345678");
        assertThat(resultado.getRol()).isEqualTo("Funcionario");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioGuardado = usuarioCaptor.getValue();

        assertThat(usuarioGuardado.getRut()).isEqualTo("11111111-1");
        assertThat(usuarioGuardado.getEmail()).isEqualTo("felipe@test.cl");
        assertThat(usuarioGuardado.getPassword()).isEqualTo("password-hasheada");
        assertThat(usuarioGuardado.getRol().getNombre()).isEqualTo("Funcionario");
    }

    @Test
    void crearUsuario_debeLanzarExcepcionCuandoRutYaExiste() {
        UsuarioCreateRequest request = new UsuarioCreateRequest(
                "11111111-1",
                "Felipe",
                "Fernandez",
                "felipe@test.cl",
                "123456",
                "912345678",
                1L
        );

        when(usuarioRepository.existsByRut("11111111-1")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(request))
                .isInstanceOf(RecursoDuplicadoException.class)
                .hasMessageContaining("Ya existe un usuario registrado con el RUT indicado");

        verify(usuarioRepository).existsByRut("11111111-1");
    }

    @Test
    void actualizarUsuario_debeActualizarCuandoExiste() {
        Usuario usuarioExistente = crearUsuario();

        UsuarioUpdateRequest request = new UsuarioUpdateRequest(
                "11111111-1",
                "Felipe Actualizado",
                "Fernandez",
                "felipe.actualizado@test.cl",
                "987654",
                "987654321",
                1L
        );

        Rol rol = crearRolFuncionario();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.existsByEmail("felipe.actualizado@test.cl")).thenReturn(false);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(passwordEncoder.encode("987654")).thenReturn("password-nueva-hasheada");

        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponse resultado = usuarioService.actualizarUsuario(1L, request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Felipe Actualizado");
        assertThat(resultado.getEmail()).isEqualTo("felipe.actualizado@test.cl");
        assertThat(resultado.getTelefono()).isEqualTo("987654321");

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());

        Usuario usuarioActualizado = usuarioCaptor.getValue();

        assertThat(usuarioActualizado.getNombre()).isEqualTo("Felipe Actualizado");
        assertThat(usuarioActualizado.getEmail()).isEqualTo("felipe.actualizado@test.cl");
        assertThat(usuarioActualizado.getPassword()).isEqualTo("password-nueva-hasheada");
    }
}