package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioAuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioExisteResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service.UsuarioService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Operaciones para gestionar usuarios y roles del sistema")
@SecurityRequirement(name = "bearer-jwt")
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ENDPOINT QUE LISTA TODOS LOS USUARIOS REGISTRADOS
    @Operation(
            summary = "Listar usuarios",
            description = "Obtiene la lista completa de usuarios registrados en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios listados correctamente"),
            @ApiResponse(responseCode = "403", description = "No autorizado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // ENDPOINT QUE BUSCA UN USUARIO POR SU ID
    @Operation(
            summary = "Buscar usuario por ID",
            description = "Obtiene un usuario específico usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Long idUsuario
    ) {
        UsuarioResponse usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        return ResponseEntity.ok(usuario);
    }

    // ENDPOINT QUE CREA UN NUEVO USUARIO
    @Operation(
            summary = "Crear usuario",
            description = "Registra un nuevo usuario en el sistema."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Usuario duplicado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(
            @Valid @RequestBody UsuarioCreateRequest request
    ) {
        UsuarioResponse usuarioCreado = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    // ENDPOINT QUE ACTUALIZA UN USUARIO EXISTENTE
    @Operation(
            summary = "Actualizar usuario",
            description = "Actualiza los datos de un usuario existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Long idUsuario,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        UsuarioResponse usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, request);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // ENDPOINT QUE ELIMINA UN USUARIO EXISTENTE
    @Operation(
            summary = "Eliminar usuario",
            description = "Elimina un usuario existente usando su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "403", description = "No autorizado")
    })
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario", example = "3")
            @PathVariable Long idUsuario
    ) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT INTERNO USADO POR auth_service PARA AUTENTICAR USUARIOS. NO SE MUESTRA EN SWAGGER.
    @Hidden
    @GetMapping("/auth/email/{email}")
    public ResponseEntity<UsuarioAuthResponse> obtenerUsuarioAuthPorEmail(
            @PathVariable String email
    ) {
        UsuarioAuthResponse usuario = usuarioService.obtenerUsuarioAuthPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    // ENDPOINT INTERNO USADO POR vehiculo_service PARA VALIDAR SI EXISTE UN USUARIO. NO SE MUESTRA EN SWAGGER.
    @Hidden
    @GetMapping("/internal/existe/{idUsuario}")
    public ResponseEntity<UsuarioExisteResponse> existeUsuarioPorId(
            @PathVariable Long idUsuario
    ) {
        UsuarioExisteResponse response = usuarioService.existeUsuarioPorId(idUsuario);
        return ResponseEntity.ok(response);
    }

    // ENDPOINT INTERNO USADO POR acceso_service PARA BUSCAR USUARIO POR RUT. NO SE MUESTRA EN SWAGGER.
    @Hidden
    @GetMapping("/internal/rut/{rut}")
    public ResponseEntity<UsuarioInternoResponse> obtenerUsuarioInternoPorRut(
            @PathVariable String rut
    ) {
        UsuarioInternoResponse usuario = usuarioService.obtenerUsuarioInternoPorRut(rut);
        return ResponseEntity.ok(usuario);
    }
}