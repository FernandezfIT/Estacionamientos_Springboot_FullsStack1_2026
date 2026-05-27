package cl.duoc.fullstack1.grupo11.estacionamientos.movimiento_service.model;

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
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private Long idMovimiento;

    @Column(name = "tipo_movimiento", nullable = false, length = 30)
    private String tipoMovimiento;

    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "rut_usuario", length = 10)
    private String rutUsuario;

    @Column(name = "id_vehiculo")
    private Long idVehiculo;

    @Column(name = "patente", length = 10)
    private String patente;

    @Column(name = "id_plaza")
    private Long idPlaza;

    @Column(name = "codigo_plaza", length = 20)
    private String codigoPlaza;

    @Column(name = "id_referencia")
    private Long idReferencia;

    @Column(name = "servicio_origen", nullable = false, length = 80)
    private String servicioOrigen;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @PrePersist
    public void prePersist() {
        if (this.fechaMovimiento == null) {
            this.fechaMovimiento = LocalDateTime.now();
        }
    }
}