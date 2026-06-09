package cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.request.LoginRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.dto.response.AuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autentificacion", description = "Operaciones de autentificacion y manejo de usuario")
public class AuthController {

    private final AuthService authService;

    // Define el ENDPOINT POST /api/v1/auth/login
    @PostMapping("/login")
    //Documentación Swagger
    @Operation(summary = "Obtener el token JWT", description = "Entrega el token JWT validando user y pass")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operacion exitosa"),
        @ApiResponse(responseCode = "403",description = "Usuario o pass son invalidos"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}