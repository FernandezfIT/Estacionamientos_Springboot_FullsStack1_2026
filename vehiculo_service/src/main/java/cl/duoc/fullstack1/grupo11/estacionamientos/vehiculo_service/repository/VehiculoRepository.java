package cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.duoc.fullstack1.grupo11.estacionamientos.vehiculo_service.model.Vehiculo;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    @Override
    @EntityGraph(attributePaths = "tipoVehiculo")
    List<Vehiculo> findAll();

    @Override
    @EntityGraph(attributePaths = "tipoVehiculo")
    Optional<Vehiculo> findById(Long idVehiculo);

    @EntityGraph(attributePaths = "tipoVehiculo")
    Optional<Vehiculo> findByPatente(String patente);

    @EntityGraph(attributePaths = "tipoVehiculo")
    List<Vehiculo> findByIdUsuario(Long idUsuario);

    boolean existsByPatente(String patente);
}