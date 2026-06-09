package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.UsuarioClient;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.dto.UsuarioAuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.request.LoginRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.response.AuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.exception.CredencialesInvalidasEx;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsuarioClient usuarioClient;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        String emailNormalizado = request.getEmail().trim().toLowerCase(); // "Empareja el correo a min"

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogeado = auth.getName();

        log.info("Usuario={} Intentando iniciar sesion.", usuarioLogeado);
        UsuarioAuthResponse usuario = usuarioClient.buscarPorEmail(emailNormalizado)
                .orElseThrow(() -> new CredencialesInvalidasEx("Email o password incorrectos"));

        validarPassword(request.getPassword(), usuario.getPassword());
        log.info("Sesión iniciada.");
        log.info("Generando token.");
        
        String token = jwtService.generarToken(usuario);
        log.info("Token generado.");

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
    }

    private void validarPassword(String passwordIngresada, String passwordGuardada) {
        if (passwordGuardada == null || passwordGuardada.isBlank()) {
            throw new CredencialesInvalidasEx("Email o password incorrectos");
        }

        boolean passwordValida = passwordEncoder.matches(passwordIngresada, passwordGuardada);

        if (!passwordValida) {
            throw new CredencialesInvalidasEx("Email o password incorrectos");
        }
    }
}