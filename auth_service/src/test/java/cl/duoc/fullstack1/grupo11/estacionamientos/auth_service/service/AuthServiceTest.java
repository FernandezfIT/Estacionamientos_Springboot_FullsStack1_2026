package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.service;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.dto.UsuarioAuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.request.LoginRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.response.AuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.exception.CredencialesInvalidasEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioClient usuarioClient;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private UsuarioAuthResponse crearUsuarioAuth() {
        return new UsuarioAuthResponse(
                1L,
                "11111111-1",
                "Felipe",
                "Fernandez",
                "felipe@test.cl",
                "password-hasheada",
                "Funcionario"
        );
    }

    @Test
    void login_debeRetornarAuthResponseCuandoCredencialesSonValidas() {
        LoginRequest request = new LoginRequest(
                "felipe@test.cl",
                "123456"
        );

        UsuarioAuthResponse usuario = crearUsuarioAuth();

        when(usuarioClient.buscarPorEmail("felipe@test.cl"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123456", "password-hasheada"))
                .thenReturn(true);

        when(jwtService.generarToken(usuario))
                .thenReturn("jwt-token-de-prueba");

        AuthResponse resultado = authService.login(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getToken()).isEqualTo("jwt-token-de-prueba");
        assertThat(resultado.getTipoToken()).isEqualTo("Bearer");
        assertThat(resultado.getIdUsuario()).isEqualTo(1L);
        assertThat(resultado.getNombre()).isEqualTo("Felipe");
        assertThat(resultado.getApellido()).isEqualTo("Fernandez");
        assertThat(resultado.getEmail()).isEqualTo("felipe@test.cl");
        assertThat(resultado.getRol()).isEqualTo("Funcionario");

        verify(usuarioClient).buscarPorEmail("felipe@test.cl");
        verify(passwordEncoder).matches("123456", "password-hasheada");
        verify(jwtService).generarToken(usuario);
    }

    @Test
    void login_debeNormalizarEmailAntesDeBuscarUsuario() {
        LoginRequest request = new LoginRequest(
                "  FELIPE@TEST.CL  ",
                "123456"
        );

        UsuarioAuthResponse usuario = crearUsuarioAuth();

        when(usuarioClient.buscarPorEmail("felipe@test.cl"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("123456", "password-hasheada"))
                .thenReturn(true);

        when(jwtService.generarToken(usuario))
                .thenReturn("jwt-token-de-prueba");

        AuthResponse resultado = authService.login(request);

        assertThat(resultado.getToken()).isEqualTo("jwt-token-de-prueba");
        assertThat(resultado.getEmail()).isEqualTo("felipe@test.cl");

        verify(usuarioClient).buscarPorEmail("felipe@test.cl");
    }

    @Test
    void login_debeLanzarExcepcionCuandoUsuarioNoExiste() {
        LoginRequest request = new LoginRequest(
                "noexiste@test.cl",
                "123456"
        );

        when(usuarioClient.buscarPorEmail("noexiste@test.cl"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredencialesInvalidasEx.class)
                .hasMessageContaining("Email o password incorrectos");

        verify(usuarioClient).buscarPorEmail("noexiste@test.cl");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void login_debeLanzarExcepcionCuandoPasswordGuardadaEsNull() {
        LoginRequest request = new LoginRequest(
                "felipe@test.cl",
                "123456"
        );

        UsuarioAuthResponse usuario = crearUsuarioAuth();
        usuario.setPassword(null);

        when(usuarioClient.buscarPorEmail("felipe@test.cl"))
                .thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredencialesInvalidasEx.class)
                .hasMessageContaining("Email o password incorrectos");

        verify(usuarioClient).buscarPorEmail("felipe@test.cl");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void login_debeLanzarExcepcionCuandoPasswordGuardadaEstaVacia() {
        LoginRequest request = new LoginRequest(
                "felipe@test.cl",
                "123456"
        );

        UsuarioAuthResponse usuario = crearUsuarioAuth();
        usuario.setPassword("   ");

        when(usuarioClient.buscarPorEmail("felipe@test.cl"))
                .thenReturn(Optional.of(usuario));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredencialesInvalidasEx.class)
                .hasMessageContaining("Email o password incorrectos");

        verify(usuarioClient).buscarPorEmail("felipe@test.cl");
        verifyNoInteractions(passwordEncoder, jwtService);
    }

    @Test
    void login_debeLanzarExcepcionCuandoPasswordEsIncorrecta() {
        LoginRequest request = new LoginRequest(
                "felipe@test.cl",
                "password-mala"
        );

        UsuarioAuthResponse usuario = crearUsuarioAuth();

        when(usuarioClient.buscarPorEmail("felipe@test.cl"))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches("password-mala", "password-hasheada"))
                .thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(CredencialesInvalidasEx.class)
                .hasMessageContaining("Email o password incorrectos");

        verify(usuarioClient).buscarPorEmail("felipe@test.cl");
        verify(passwordEncoder).matches("password-mala", "password-hasheada");
        verifyNoInteractions(jwtService);
    }
}