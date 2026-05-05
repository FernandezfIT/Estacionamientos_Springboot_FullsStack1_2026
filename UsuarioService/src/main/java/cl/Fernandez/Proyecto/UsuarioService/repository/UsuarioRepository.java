package cl.Fernandez.Proyecto.UsuarioService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.Fernandez.Proyecto.UsuarioService.model.Usuario;


@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    // Busquedas personalizadas
    // ── TIPO 1: QUERY METHODS ────────────────────────
    // Spring analiza el nombre del método y genera SQL.
    // Atributo debe coincidir EXACTAMENTE con el campo
    // de la entidad Java (mayúsculas incluidas).

    // → SELECT * FROM Usuario WHERE UPPER(nombre) LIKE UPPER('%?%')
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    // → SELECT * FROM Usuario WHERE UPPER(nombre) LIKE UPPER('%?%')
    List<Usuario> findByRutContainingIgnoreCase(String rut);

    // Ejemplos de otras variantes:
    // findByNombre(String nombre) // exacto
    // findByNombreStartsWith(String nombre) // empieza con
    // findByNombreEndsWith(String nombre) // termina con
    // findByCapacidadGreaterThan(int cap) // mayor que



}
