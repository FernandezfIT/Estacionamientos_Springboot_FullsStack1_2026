package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.model.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findByTipoMovimiento(String tipoMovimiento);

    List<Movimiento> findByIdUsuario(Long idUsuario);

    List<Movimiento> findByRutUsuario(String rutUsuario);

    List<Movimiento> findByIdVehiculo(Long idVehiculo);

    List<Movimiento> findByPatente(String patente);

    List<Movimiento> findByIdPlaza(Long idPlaza);

    List<Movimiento> findByCodigoPlaza(String codigoPlaza);

    List<Movimiento> findByServicioOrigen(String servicioOrigen);

    List<Movimiento> findByFechaMovimientoBetween(
        LocalDateTime inicioDia,
        LocalDateTime finDia
    );
}