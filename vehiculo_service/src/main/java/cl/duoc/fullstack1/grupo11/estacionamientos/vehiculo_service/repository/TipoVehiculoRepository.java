package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.TipoVehiculo;

@Repository
public interface TipoVehiculoRepository extends JpaRepository<TipoVehiculo, Long> {

    Optional<TipoVehiculo> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}