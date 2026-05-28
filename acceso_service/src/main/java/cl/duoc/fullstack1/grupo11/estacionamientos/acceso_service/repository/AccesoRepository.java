package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.model.Acceso;

public interface AccesoRepository extends JpaRepository<Acceso, Long> {

    List<Acceso> findByIdUsuario(Long idUsuario);

    List<Acceso> findByIdVehiculo(Long idVehiculo);

    List<Acceso> findByIdPlaza(Long idPlaza);

    List<Acceso> findByPatente(String patente);

    List<Acceso> findByRutUsuario(String rutUsuario);

    List<Acceso> findByCodigoPlaza(String codigoPlaza);
}