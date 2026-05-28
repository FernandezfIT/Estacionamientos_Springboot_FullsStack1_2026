package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CierreEjecutarRequest {

    @Size(max = 255, message = "La observación no puede superar los 255 caracteres")
    private String observacion;
}