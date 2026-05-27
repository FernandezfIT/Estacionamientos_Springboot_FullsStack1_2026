package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlazaInternaResponse {

    private Long idPlaza;

    private String codigoPlaza;

    private String estado;
}