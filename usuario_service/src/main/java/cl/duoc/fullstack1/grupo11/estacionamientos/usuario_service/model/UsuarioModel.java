package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Indica que esta clase representa una tabla en la BD
@Entity

// Indica el nombre exato de la tabla
@Table(name = "usuario_db")

// Genera Getters, setters, toString, equals y hashCode
@Data

// Constructores
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioModel {

    // Indica que este atributo es la clave primaria
    @Id

    // Indica que el ID se generará automáticamente
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // Mapea este atributo con la columna id_usuario
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable=false)
    private String rut;

    @Column(nullable=false)
    private String nombre;

    @Column(nullable=false)
    private String apellido;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String password;

    @Column(nullable=false)
    private Integer telefono;

    @Column(nullable=false)
    private LocalDate fechaCreacion;

    @Column(nullable=false)
    private String rol;

    


}
