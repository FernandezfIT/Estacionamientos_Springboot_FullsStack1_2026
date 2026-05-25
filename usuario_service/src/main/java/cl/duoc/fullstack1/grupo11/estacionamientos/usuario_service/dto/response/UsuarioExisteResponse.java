package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioExisteResponse {

    private Long idUsuario;

    private boolean existe;
}