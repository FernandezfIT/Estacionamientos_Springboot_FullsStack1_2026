package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RolResponse {

    private Long idRol;

    private String nombre;

    private String descripcion;
}