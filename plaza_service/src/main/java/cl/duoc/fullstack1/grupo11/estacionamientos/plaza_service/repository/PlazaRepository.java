package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.model.Plaza;


@Repository
public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    boolean existsByCodigoPlaza(String codigoPlaza);

    Optional<Plaza> findByCodigoPlaza(String codigoPlaza);
}
