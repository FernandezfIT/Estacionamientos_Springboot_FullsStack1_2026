package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlazaResponse {

    private Long idPlaza;
    private String codigoPlaza;
    private String estado;
}
