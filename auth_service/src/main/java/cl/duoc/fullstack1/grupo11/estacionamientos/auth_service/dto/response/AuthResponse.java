package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    private String tipoToken;

    private Long idUsuario;

    private String nombre;

    private String apellido;

    private String email;

    private String rol;
}