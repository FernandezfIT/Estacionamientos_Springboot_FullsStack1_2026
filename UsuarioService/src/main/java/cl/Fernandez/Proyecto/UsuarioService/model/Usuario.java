package cl.Fernandez.Proyecto.UsuarioService.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    // Genera el metodo por el que la secuencia de Ids se genera
    @GeneratedValue(strategy= GenerationType.SEQUENCE, generator= "usuario_seq")
    @SequenceGenerator(name = "usuario_seq", sequenceName="usuario_seq", allocationSize=1)
    private Integer idUsuario;

    @Column(nullable=false)
    private String rut;

    @Column(nullable=false)
    private String nombre;

    @Column(name="fecha_registro", nullable=false)
    private LocalDate fechaRegistro;

    // Atributo que confirma si usuario tiene necesidad de estacionamiento especial
    @Column(nullable=false)
    private  boolean necesidades;

}
