package cl.duoc.fullstack1.grupo11.estacionamientos.acceso_service.exception;

public class VehiculoNoPerteneceUsuarioException extends RuntimeException {

    public VehiculoNoPerteneceUsuarioException(String message) {
        super(message);
    }
}