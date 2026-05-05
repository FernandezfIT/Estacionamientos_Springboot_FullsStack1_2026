package cl.Fernandez.Proyecto.UsuarioService.dto.response;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioResponse {

    private Integer idUsuario;

    private String rut;

    private String nombre;

    private LocalDate fechaRegistro;

    private boolean necesidades;

}
