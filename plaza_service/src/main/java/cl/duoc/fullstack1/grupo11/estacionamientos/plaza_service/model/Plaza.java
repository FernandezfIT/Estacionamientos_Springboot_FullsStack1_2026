package cl.duoc.fullstack1.grupo11.estacionamientos.plaza_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "plaza")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Plaza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plaza")
    private Long idPlaza;

    @Column(name = "codigo_plaza", nullable = false, unique = true, length = 50)
    private String codigoPlaza;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
}
