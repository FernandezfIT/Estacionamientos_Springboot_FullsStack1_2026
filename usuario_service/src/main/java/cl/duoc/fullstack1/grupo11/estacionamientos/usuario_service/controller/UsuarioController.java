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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ENPOINT QUE LISTA TODOS LOS USUARIOS
    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listarUsuarios() {
        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    // ENDPOINT QUE DEVUELVE EL USUARIO BUSCADO POR ID si es que existe
    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> obtenerUsuarioPorId(@PathVariable Long idUsuario) {
        UsuarioResponse usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
        return ResponseEntity.ok(usuario);
    }

    // ENDPOINT PARA CREAR USUARIO
    @PostMapping
    public ResponseEntity<UsuarioResponse> crearUsuario(@Valid @RequestBody UsuarioCreateRequest request) {
        UsuarioResponse usuarioCreado = usuarioService.crearUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioCreado);
    }

    // ENDPOINT PARA MODIFICAR USUARIO BUSCADO POR ID si es que existe
    @PutMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> actualizarUsuario(
            @PathVariable Long idUsuario,
            @Valid @RequestBody UsuarioUpdateRequest request
    ) {
        UsuarioResponse usuarioActualizado = usuarioService.actualizarUsuario(idUsuario, request);
        return ResponseEntity.ok(usuarioActualizado);
    }

    // ENDPOINT PARA ELIMINAR USUARIO POR ID si es que existe
    @DeleteMapping("/{idUsuario}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long idUsuario) {
        usuarioService.eliminarUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }

    // ENDPOINT QUE DEVUELVE USUARIO BUSCADO POR EMAIL / se usa en Auth
    @GetMapping("/auth/email/{email}")
    public ResponseEntity<UsuarioAuthResponse> obtenerUsuarioAuthPorEmail(@PathVariable String email) {
        UsuarioAuthResponse usuario = usuarioService.obtenerUsuarioAuthPorEmail(email);
        return ResponseEntity.ok(usuario);
    }

    // ENDPOINT QUE DEVUELVE ID de USUARIO para VEHICULO - USO INTERNO
    @GetMapping("/internal/existe/{idUsuario}")
    public ResponseEntity<UsuarioExisteResponse> existeUsuarioPorId(@PathVariable Long idUsuario) {
        UsuarioExisteResponse response = usuarioService.existeUsuarioPorId(idUsuario);
        return ResponseEntity.ok(response);
    }

    // ENDPOINT QUE DEVUELVE DATOS DE USUARIO POR EL RUT - USO INTERNO -
    @GetMapping("/internal/rut/{rut}")
    public ResponseEntity<UsuarioInternoResponse> obtenerUsuarioInternoPorRut(@PathVariable String rut) {
        UsuarioInternoResponse usuario = usuarioService.obtenerUsuarioInternoPorRut(rut);
        return ResponseEntity.ok(usuario);
    }


}