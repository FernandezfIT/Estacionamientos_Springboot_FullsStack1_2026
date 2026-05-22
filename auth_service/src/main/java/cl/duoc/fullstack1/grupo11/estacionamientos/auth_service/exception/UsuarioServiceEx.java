package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.exception;

public class UsuarioServiceEx extends RuntimeException {

    public UsuarioServiceEx(String message) {
        super(message);
    }

    public UsuarioServiceEx(String message, Throwable cause) {
        super(message, cause);
    }
}
