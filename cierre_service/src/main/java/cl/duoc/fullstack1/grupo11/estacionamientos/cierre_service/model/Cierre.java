package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
@Table(name = "cierre")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cierre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cierre")
    private Long idCierre;

    @Column(name = "fecha_cierre", nullable = false)
    private LocalDate fechaCierre;

    @Column(name = "hora_cierre", nullable = false)
    private LocalTime horaCierre;

    @Column(name = "fecha_hora_cierre", nullable = false)
    private LocalDateTime fechaHoraCierre;

    @Column(name = "id_usuario_ejecutor")
    private Long idUsuarioEjecutor;

    @Column(name = "email_usuario_ejecutor", length = 255)
    private String emailUsuarioEjecutor;

    @Column(name = "rol_ejecutor", length = 50)
    private String rolEjecutor;

    @Column(name = "total_plazas_ocupadas_liberadas", nullable = false)
    private Integer totalPlazasOcupadasLiberadas;

    @Column(name = "total_reservas_eliminadas", nullable = false)
    private Integer totalReservasEliminadas;

    @Column(name = "total_plazas_reservadas_liberadas", nullable = false)
    private Integer totalPlazasReservadasLiberadas;

    @Column(name = "plazas_disponibles_final", nullable = false)
    private Integer plazasDisponiblesFinal;

    @Column(name = "plazas_ocupadas_final", nullable = false)
    private Integer plazasOcupadasFinal;

    @Column(name = "plazas_reservadas_final", nullable = false)
    private Integer plazasReservadasFinal;

    @Column(name = "total_movimientos", nullable = false)
    private Integer totalMovimientos;

    @Column(name = "total_accesos", nullable = false)
    private Integer totalAccesos;

    @Column(name = "total_salidas", nullable = false)
    private Integer totalSalidas;

    @Column(name = "total_reservas", nullable = false)
    private Integer totalReservas;

    @Column(name = "estado_cierre", nullable = false, length = 30)
    private String estadoCierre;

    @Column(name = "resumen_reporte", columnDefinition = "TEXT")
    private String resumenReporte;

    @Column(name = "observacion", length = 255)
    private String observacion;

    @PrePersist
    public void prePersist() {
        if (this.fechaHoraCierre == null) {
            this.fechaHoraCierre = LocalDateTime.now();
        }

        if (this.fechaCierre == null) {
            this.fechaCierre = this.fechaHoraCierre.toLocalDate();
        }

        if (this.horaCierre == null) {
            this.horaCierre = this.fechaHoraCierre.toLocalTime();
        }

        if (this.estadoCierre == null || this.estadoCierre.isBlank()) {
            this.estadoCierre = "EJECUTADO";
        }

        inicializarContadores();
    }

    private void inicializarContadores() {
        if (this.totalPlazasOcupadasLiberadas == null) {
            this.totalPlazasOcupadasLiberadas = 0;
        }

        if (this.totalReservasEliminadas == null) {
            this.totalReservasEliminadas = 0;
        }

        if (this.totalPlazasReservadasLiberadas == null) {
            this.totalPlazasReservadasLiberadas = 0;
        }

        if (this.plazasDisponiblesFinal == null) {
            this.plazasDisponiblesFinal = 0;
        }

        if (this.plazasOcupadasFinal == null) {
            this.plazasOcupadasFinal = 0;
        }

        if (this.plazasReservadasFinal == null) {
            this.plazasReservadasFinal = 0;
        }

        if (this.totalMovimientos == null) {
            this.totalMovimientos = 0;
        }

        if (this.totalAccesos == null) {
            this.totalAccesos = 0;
        }

        if (this.totalSalidas == null) {
            this.totalSalidas = 0;
        }

        if (this.totalReservas == null) {
            this.totalReservas = 0;
        }
    }
}