package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CredencialesInvalidasEx.class)
    public ResponseEntity<ErrorResponse> manejarCredencialesInvalidas(
            CredencialesInvalidasEx ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = construirErrorResponse(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );
        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(UsuarioServiceEx.class)
    public ResponseEntity<ErrorResponse> manejarUsuarioServiceException(
            UsuarioServiceEx ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = construirErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                "No fue posible conectar con usuario_service",
                request.getRequestURI()
        );
        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarErroresValidacion(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse errorResponse = construirErrorResponse(
                HttpStatus.BAD_REQUEST,
                mensaje,
                request.getRequestURI()
        );
        log.error("se registra error", ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> manejarJsonMalFormado(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = construirErrorResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la solicitud no tiene un formato JSON válido",
                request.getRequestURI()
        );
        log.error("se registra error", ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErrorGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        ErrorResponse errorResponse = construirErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en auth_service",
                request.getRequestURI()
        );
        log.error("se registra error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    private ErrorResponse construirErrorResponse(
            HttpStatus status,
            String message,
            String path
    ) {
        return new ErrorResponse(
                
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                path
                
        );
    }
}