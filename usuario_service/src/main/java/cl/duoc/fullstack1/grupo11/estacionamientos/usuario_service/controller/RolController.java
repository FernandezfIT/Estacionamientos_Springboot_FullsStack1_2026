package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.RolResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service.RolService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RolController {

    private final RolService rolService;

    @GetMapping
    public ResponseEntity<List<RolResponse>> listarRoles() {
        List<RolResponse> roles = rolService.listarRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{idRol}")
    public ResponseEntity<RolResponse> obtenerRolPorId(@PathVariable Long idRol) {
        RolResponse rol = rolService.obtenerRolPorId(idRol);
        return ResponseEntity.ok(rol);
    }
}