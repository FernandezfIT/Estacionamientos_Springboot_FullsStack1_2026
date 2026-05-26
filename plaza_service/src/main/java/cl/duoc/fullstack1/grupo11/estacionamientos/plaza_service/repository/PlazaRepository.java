package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.model.Plaza;

public interface PlazaRepository extends JpaRepository<Plaza, Long> {

    boolean existsByCodigoPlaza(String codigoPlaza);
}
