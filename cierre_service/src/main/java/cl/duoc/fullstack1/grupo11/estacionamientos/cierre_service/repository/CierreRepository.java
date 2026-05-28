package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.model.Cierre;

public interface CierreRepository extends JpaRepository<Cierre, Long> {

    List<Cierre> findByFechaCierre(LocalDate fechaCierre);
}