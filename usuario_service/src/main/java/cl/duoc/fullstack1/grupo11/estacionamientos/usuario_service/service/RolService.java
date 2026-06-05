package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.RolResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.RolNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Rol;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository.RolRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<RolResponse> listarRoles() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogueado = auth.getName();
        log.info("Se listan Roles Existentes");
        log.info("Usuario={} realizó búsqueda", usuarioLogueado);

        return rolRepository.findAll()
                .stream()
                .map(this::mapToRolResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RolResponse obtenerRolPorId(Long idRol) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogueado = auth.getName();

        log.info("Iniciando búsqueda de ROL por Id={}", idRol);
        log.info("Usuario={} realizó búsqueda", usuarioLogueado);
        Rol rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new RolNoEncontradoException("No existe un rol con ID " + idRol));

        return mapToRolResponse(rol);
    }

    private RolResponse mapToRolResponse(Rol rol) {
        return new RolResponse(
                rol.getIdRol(),
                rol.getNombre(),
                rol.getDescripcion());
    }
}