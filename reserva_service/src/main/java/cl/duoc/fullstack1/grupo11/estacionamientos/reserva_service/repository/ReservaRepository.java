package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByRutReserva(String rutReserva);

    List<Reserva> findByIdPlaza(Long idPlaza);
}
