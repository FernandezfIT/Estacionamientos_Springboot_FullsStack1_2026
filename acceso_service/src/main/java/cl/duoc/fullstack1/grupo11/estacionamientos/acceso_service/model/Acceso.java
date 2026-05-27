package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "acceso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_acceso")
    private Long idAcceso;

    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;

    @Column(name = "rut_usuario", nullable = false, length = 10)
    private String rutUsuario;

    @Column(name = "id_vehiculo", nullable = false)
    private Long idVehiculo;

    @Column(name = "patente", nullable = false, length = 10)
    private String patente;

    @Column(name = "id_plaza", nullable = false)
    private Long idPlaza;

    @Column(name = "codigo_plaza", nullable = false, length = 20)
    private String codigoPlaza;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDateTime fechaIngreso;

    @Column(name = "estado_acceso", nullable = false, length = 20)
    private String estadoAcceso;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaIngreso == null) {
            this.fechaIngreso = LocalDateTime.now();
        }

        if (this.estadoAcceso == null || this.estadoAcceso.isBlank()) {
            this.estadoAcceso = "AUTORIZADO";
        }
    }
}