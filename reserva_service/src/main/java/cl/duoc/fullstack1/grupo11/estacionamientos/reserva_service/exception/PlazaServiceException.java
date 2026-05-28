package cl.duoc.fullstack1.grupo11.estacionamientos.reserva_service.exception;

public class PlazaServiceException extends RuntimeException {

    public PlazaServiceException(String message) {
        super(message);
    }

    public PlazaServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
