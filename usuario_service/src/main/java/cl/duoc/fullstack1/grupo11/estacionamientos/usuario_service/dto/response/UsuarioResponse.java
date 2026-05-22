package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {

    private Long idUsuario;

    private String rut;

    private String nombre;

    private String apellido;

    private String email;

    private String telefono;

    private LocalDateTime fechaCreacion;

    private Long idRol;

    private String rol;
}