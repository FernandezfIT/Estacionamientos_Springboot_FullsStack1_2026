package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public DecodedJWT validarYDecodificarToken(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token);
    }

    public String obtenerEmailDesdeToken(String token) {
        DecodedJWT decodedJWT = validarYDecodificarToken(token);
        return decodedJWT.getSubject();
    }

    public Long obtenerIdUsuarioDesdeToken(String token) {
        DecodedJWT decodedJWT = validarYDecodificarToken(token);
        return decodedJWT.getClaim("idUsuario").asLong();
    }

    public String obtenerRolDesdeToken(String token) {
        DecodedJWT decodedJWT = validarYDecodificarToken(token);
        return decodedJWT.getClaim("rol").asString();
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtSecret);
    }
}
