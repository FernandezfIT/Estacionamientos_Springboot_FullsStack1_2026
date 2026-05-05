package cl.Fernandez.Proyecto.UsuarioService.dto.request;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCreateRequest {

    @NotBlank(message="El Rut es obligatorio")
    @Size(min = 9, max = 10, message="El rut no puede tener menos de 9 y más de 10 carácteres en formato: 10.987.654-3")
    private String rut;

    @NotBlank(message="El nombre es obligatorio")
    @Size(min = 5, max = 100, message="El nombre no puede superar los 100 carácteres")
    private String nombre;

    @NotBlank(message="La Fecha es obligatoria")
    @JsonFormat(pattern="dd-MM-yyyy")
    private LocalDate fechaRegistro;

    @NotBlank(message="Esta Información es obligatoria")
    private boolean necesidades;


}
