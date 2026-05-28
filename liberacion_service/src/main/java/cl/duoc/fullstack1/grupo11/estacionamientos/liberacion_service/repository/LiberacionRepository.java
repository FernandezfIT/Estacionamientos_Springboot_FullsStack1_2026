package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.model.Liberacion;

public interface LiberacionRepository extends JpaRepository<Liberacion, Long> {

    List<Liberacion> findByIdPlaza(Long idPlaza);
}
