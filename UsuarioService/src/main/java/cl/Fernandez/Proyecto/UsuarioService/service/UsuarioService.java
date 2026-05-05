package cl.Fernandez.Proyecto.UsuarioService.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import cl.Fernandez.Proyecto.UsuarioService.dto.request.UsuarioCreateRequest;
import cl.Fernandez.Proyecto.UsuarioService.dto.request.UsuarioUpdateRequest;
import cl.Fernandez.Proyecto.UsuarioService.dto.response.UsuarioResponse;
import cl.Fernandez.Proyecto.UsuarioService.model.Usuario;
import cl.Fernandez.Proyecto.UsuarioService.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    // Esto sería Autowired pero con @RequiredArgsConstructor no es necesario
    private final UsuarioRepository usuarioRepository;

    // Mapeo Privado: Entidad -> ResponseDTO
    // Solo lo usa este Service.
    // Ni controller Ni repository conocen al DTO ni la entidad del otro respectivamente

    // Mapeo para Respuesta
    private UsuarioResponse mapToDto(Usuario usuario){
        return new UsuarioResponse(
            usuario.getIdUsuario(),
            usuario.getRut(),
            usuario.getNombre(),
            usuario.getFechaRegistro(),
            usuario.isNecesidades()
        );
    }

    // Guardar usuario
    public UsuarioResponse guardarusuario(UsuarioCreateRequest request){
        Usuario usuario = new Usuario();

        usuario.setRut(request.getRut());
        usuario.setNombre(request.getNombre());
        usuario.setFechaRegistro(request.getFechaRegistro());
        usuario.setNecesidades(request.isNecesidades());

        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return mapToDto(usuarioGuardado);
    }

    // Obtener todos los usuarios
    public List<UsuarioResponse> obtenerTodoUsuario(){
        List<Usuario> usuarios = usuarioRepository.findAll();
        List<UsuarioResponse> respuesta = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            UsuarioResponse response = new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getFechaRegistro(),
                usuario.isNecesidades()
            );
            respuesta.add(response);
        }
        return respuesta;
    }

    // Obtener solo un usuario por ID
    public UsuarioResponse obtenerUsuarioPorId(Integer idUsuario){
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuario == null) {
            return null;
        }

        return mapToDto(usuario);
    }

    // Actualizar Usuario
    public UsuarioResponse actualizarusuario(Integer idUsuario, UsuarioUpdateRequest request){

        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);

        if (usuario == null) {
            return null;
        }

        usuario.setRut(request.getRut());
        usuario.setNombre(request.getNombre());
        usuario.setFechaRegistro(request.getFechaRegistro());
        usuario.setNecesidades(request.isNecesidades());

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        return mapToDto(usuarioActualizado);

    }

    // Eliminar Usuario
    public boolean eliminarUsuario(Integer idUsuario){
        if (usuarioRepository.existsById(idUsuario)) {
            usuarioRepository.deleteById(idUsuario);
            return true;
        }
        return false;
    }


    //// Busquedas de clase 2 (adaptadas al ResponseDTO)
    /// 
    /// BUSQUEDA POR RUT
    public List<UsuarioResponse> buscarPorRut(String rutUsuario){
        return usuarioRepository.findByRutContainingIgnoreCase(rutUsuario)
            .stream().map(this::mapToDto).collect(Collectors.toList());
    }

    ///BUSQUEDA POR NOMBRE
    public List<UsuarioResponse> buscarPorNombre(String rutUsuario){
    return usuarioRepository.findByNombreContainingIgnoreCase(rutUsuario)
        .stream().map(this::mapToDto).collect(Collectors.toList());
    }

}
