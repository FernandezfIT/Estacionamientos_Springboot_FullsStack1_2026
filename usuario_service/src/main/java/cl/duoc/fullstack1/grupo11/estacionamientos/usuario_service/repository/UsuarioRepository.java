package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByEmail(String email);

    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByRut(String rut);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);
}