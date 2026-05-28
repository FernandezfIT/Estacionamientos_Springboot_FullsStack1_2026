package cl.duoc.fullstack1.grupo11.estacionamientos.cierre_service.exception;

public class CierreEjecucionException extends RuntimeException {

    public CierreEjecucionException(String message) {
        super(message);
    }

    public CierreEjecucionException(String message, Throwable cause) {
        super(message, cause);
    }
}