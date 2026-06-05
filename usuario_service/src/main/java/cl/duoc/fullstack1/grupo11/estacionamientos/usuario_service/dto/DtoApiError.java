package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DtoApiError {

    private LocalDate timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String claseException;
}