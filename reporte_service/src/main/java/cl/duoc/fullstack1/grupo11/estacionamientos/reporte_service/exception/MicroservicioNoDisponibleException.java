package cl.duoc.fullstack1.grupo11.estacionamientos.reporte_service.exception;

public class MicroservicioNoDisponibleException extends RuntimeException {
    public MicroservicioNoDisponibleException(String message) {
        super(message);
    }
    public MicroservicioNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
