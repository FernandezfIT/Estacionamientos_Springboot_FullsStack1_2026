package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Long idReserva;

    @Column(name = "rut_reserva", nullable = false, length = 12)
    private String rutReserva;

    @Column(name = "patente", nullable = false, length = 20)
    private String patente;

    @Column(name = "id_plaza", nullable = false)
    private Long idPlaza;

    @Column(name = "fecha_reserva", nullable = false)
    private LocalDateTime fechaReserva;

    @PrePersist
    public void prePersist() {
        if (this.fechaReserva == null) {
            this.fechaReserva = LocalDateTime.now();
        }
    }
}
