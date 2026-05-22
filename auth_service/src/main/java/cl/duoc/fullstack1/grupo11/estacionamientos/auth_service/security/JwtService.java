package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.client.dto.UsuarioAuthResponse;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpirationMs;

    public String generarToken(UsuarioAuthResponse usuario) {
        Instant fechaActual = Instant.now();
        Instant fechaExpiracion = fechaActual.plusMillis(jwtExpirationMs);

        return JWT.create()
                .withSubject(usuario.getEmail())
                .withClaim("idUsuario", usuario.getIdUsuario())
                .withClaim("nombre", usuario.getNombre())
                .withClaim("apellido", usuario.getApellido())
                .withClaim("rol", usuario.getRol())
                .withIssuedAt(Date.from(fechaActual))
                .withExpiresAt(Date.from(fechaExpiracion))
                .sign(getAlgorithm());
    }

    public boolean validarToken(String token) {
        try {
            decodificarToken(token);
            return true;
        } catch (JWTVerificationException ex) {
            return false;
        }
    }

    public String obtenerEmailDesdeToken(String token) {
        DecodedJWT decodedJWT = decodificarToken(token);
        return decodedJWT.getSubject();
    }

    private DecodedJWT decodificarToken(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }
}