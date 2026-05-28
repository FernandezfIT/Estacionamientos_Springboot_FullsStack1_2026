package cl.duoc.fullstack1.grupo11.estacionamientos.liberacion_service.model;

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
@Table(name = "liberacion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Liberacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_liberacion")
    private Long idLiberacion;

    @Column(name = "id_plaza", nullable = false)
    private Long idPlaza;

    @Column(name = "fecha_liberacion", nullable = false)
    private LocalDateTime fechaLiberacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaLiberacion == null) {
            this.fechaLiberacion = LocalDateTime.now();
        }
    }
}
