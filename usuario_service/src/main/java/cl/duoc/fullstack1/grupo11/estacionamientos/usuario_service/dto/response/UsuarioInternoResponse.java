package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioInternoResponse {

    private Long idUsuario;

    private String rut;

    private String nombre;

    private String apellido;

    private String email;

    private String rol;
}