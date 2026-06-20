package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.request.UsuarioUpdateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioAuthResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioExisteResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioInternoResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.dto.response.UsuarioResponse;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.RecursoDuplicadoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.RolNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.exception.UsuarioNoEncontradoException;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Rol;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Usuario;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository.RolRepository;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    // LISTA TODOS LOS USUARIOS ALMACENADOS - No Recibe nada
    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarUsuarios() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogeado = auth.getName();

        log.info("Iniciando búsqueda de usuarios registrados.");
        log.info("Usuario={} realizó búsqueda", usuarioLogeado);
        List<Usuario> usuariosEncontrados = usuarioRepository.findAll();
        log.info("Se encontraron ={} usuarios", usuariosEncontrados.size());
        return usuarioRepository.findAll()
                .stream()
                .map(this::mapToUsuarioResponse)
                .toList();
    }

    // BUSCA USUARIO POR SU ID - Recibe idUsuario
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuarioPorId(Long idUsuario) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogueado = auth.getName();

        log.info("Iniciando búsqueda de Usuario por Id={}", idUsuario);
        log.info("Usuario={} realizó búsqueda", usuarioLogueado);

        Usuario usuario = buscarUsuarioPorId(idUsuario);

        return mapToUsuarioResponse(usuario);
    }

    // CREA USUARIO NUEVO - Recibe desde el Controller UsuarioCreateRequest
    @Transactional
    public UsuarioResponse crearUsuario(UsuarioCreateRequest request) {
        String rutNormalizado = normalizarTexto(request.getRut()); // Normaliza el RUT
        String emailNormalizado = normalizarEmail(request.getEmail()); // Normaliza el correo
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogueado = auth.getName();

        log.info("Iniciando registro de usuario nuevo.");
        log.info("Usuario={} realizó registro.", usuarioLogueado);

        validarRutDisponible(rutNormalizado); // Valida que RUT no se repita
        validarEmailDisponible(emailNormalizado); // Valida que correo no se repita

        Rol rol = buscarRolPorId(request.getIdRol()); // Busca el Rol por ID - Si no lo encuentra, arroja error.

        Usuario usuario = new Usuario(); // Se instancia usuario
        usuario.setRut(rutNormalizado); // Se setea rut validado y normalizado
        usuario.setNombre(normalizarTexto(request.getNombre())); // Setea los datos al usuario desde request
        usuario.setApellido(normalizarTexto(request.getApellido())); // Setea los datos al usuario desde request
        usuario.setEmail(emailNormalizado); // Se setea email validado y normalizado
        usuario.setPassword(passwordEncoder.encode(request.getPassword())); // Encripta pass recibida en request
        usuario.setTelefono(normalizarTexto(request.getTelefono())); // Normaliza y setea telefono
        usuario.setRol(rol); // Setea rol

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        log.info("Usuario creado correctamente idUsuario={}", usuarioGuardado.getIdUsuario());
        return mapToUsuarioResponse(usuarioGuardado); // Devuelve UsuarioRepsonse sin Pass
    }

    @Transactional
    public UsuarioResponse actualizarUsuario(Long idUsuario, UsuarioUpdateRequest request) {
        Usuario usuario = buscarUsuarioPorId(idUsuario);

        String rutNormalizado = normalizarTexto(request.getRut());
        String emailNormalizado = normalizarEmail(request.getEmail());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        String usuarioLogueado = auth.getName();

        log.info("Iniciando Actualización de usuario Id={}.", idUsuario);
        log.info("Usuario={} realizó actualización.", usuarioLogueado);

        validarRutDisponibleParaActualizar(usuario, rutNormalizado);
        validarEmailDisponibleParaActualizar(usuario, emailNormalizado);

        Rol rol = buscarRolPorId(request.getIdRol());

        usuario.setRut(rutNormalizado);
        usuario.setNombre(normalizarTexto(request.getNombre()));
        usuario.setApellido(normalizarTexto(request.getApellido()));
        usuario.setEmail(emailNormalizado);
        usuario.setTelefono(normalizarTexto(request.getTelefono()));
        usuario.setRol(rol);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        }


        Usuario usuarioActualizado = usuarioRepository.save(usuario);
         log.info("Se actualiza Usuario ID={}", usuarioActualizado.getIdUsuario());

        return mapToUsuarioResponse(usuarioActualizado);
    }

    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        String usuarioLogueado = obtenerUsuarioAutenticado();

        log.info("Iniciando eliminación de usuario idUsuario={}", idUsuario);
        log.info("Usuario={} realizó eliminación de usuario", usuarioLogueado);

        Usuario usuario = buscarUsuarioPorId(idUsuario);
        usuarioRepository.delete(usuario);

        log.info("Usuario eliminado correctamente idUsuario={}", idUsuario);
    }

    @Transactional(readOnly = true)
    public UsuarioExisteResponse existeUsuarioPorId(Long idUsuario) {
        log.info("Consulta interna para validar existencia de usuario idUsuario={}", idUsuario);

        boolean existe = usuarioRepository.existsById(idUsuario);

        if (!existe) {
            log.warn("Consulta interna: usuario no existe idUsuario={}", idUsuario);
        }

        return new UsuarioExisteResponse(idUsuario, existe);
    }

    @Transactional(readOnly = true)
    public UsuarioAuthResponse obtenerUsuarioAuthPorEmail(String email) {
        String emailNormalizado = normalizarEmail(email);

        log.info("Consulta interna de autenticación para email={}", emailNormalizado);

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado para autenticación email={}", emailNormalizado);
                    return new UsuarioNoEncontradoException("No existe un usuario con el email indicado");
                });

        log.info("Usuario encontrado para autenticación idUsuario={}", usuario.getIdUsuario());

        return mapToUsuarioAuthResponse(usuario);
    }

    @Transactional(readOnly = true)
    public UsuarioInternoResponse obtenerUsuarioInternoPorRut(String rut) {
        String rutNormalizado = normalizarTexto(rut);

        log.info("Consulta interna de usuario por RUT={}", rutNormalizado);

        Usuario usuario = usuarioRepository.findByRut(rutNormalizado)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado por RUT={}", rutNormalizado);
                    return new UsuarioNoEncontradoException(
                            "No existe un usuario con el RUT " + rutNormalizado
                    );
                });

        log.info("Usuario encontrado por RUT={} idUsuario={}", rutNormalizado, usuario.getIdUsuario());

        return mapToUsuarioInternoResponse(usuario);
    }

    private Usuario buscarUsuarioPorId(Long idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> {
                    log.warn("Usuario no encontrado id={}", idUsuario);
                    return new UsuarioNoEncontradoException("No existe un usuario con ID " + idUsuario);
                });
    }

    private Rol buscarRolPorId(Long idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> {
                    log.warn("Rol no encontrado idRol={}", idRol);
                    return new RolNoEncontradoException("No existe un rol con ID " + idRol);
                });
    }

    private void validarRutDisponible(String rut) {
        if (usuarioRepository.existsByRut(rut)) {
            log.warn("Conflicto con rut={}", rut);
            throw new RecursoDuplicadoException("Ya existe un usuario registrado con el RUT indicado");
        }
    }

    private void validarEmailDisponible(String email) {
        if (usuarioRepository.existsByEmail(email)) {
            log.warn("Conflicto con email={}", email);
            throw new RecursoDuplicadoException("Ya existe un usuario registrado con el email indicado");
        }
    }

    private void validarRutDisponibleParaActualizar(Usuario usuarioActual, String nuevoRut) {
        boolean cambioRut = !usuarioActual.getRut().equalsIgnoreCase(nuevoRut);

        if (cambioRut && usuarioRepository.existsByRut(nuevoRut)) {
            log.warn("Conflicto con rut={}", nuevoRut);
            throw new RecursoDuplicadoException("Ya existe un usuario registrado con el RUT indicado");
        }
    }

    private void validarEmailDisponibleParaActualizar(Usuario usuarioActual, String nuevoEmail) {
        boolean cambioEmail = !usuarioActual.getEmail().equalsIgnoreCase(nuevoEmail);

        if (cambioEmail && usuarioRepository.existsByEmail(nuevoEmail)) {
            log.warn("Conflicto con email={}", nuevoEmail);
            throw new RecursoDuplicadoException("Ya existe un usuario registrado con el email indicado");
        }
    }

    private UsuarioInternoResponse mapToUsuarioInternoResponse(Usuario usuario) {
        return new UsuarioInternoResponse(
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol().getNombre());
    }

    private UsuarioResponse mapToUsuarioResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFechaCreacion(),
                usuario.getRol().getIdRol(),
                usuario.getRol().getNombre());
    }

    private UsuarioAuthResponse mapToUsuarioAuthResponse(Usuario usuario) {
        return new UsuarioAuthResponse(
                usuario.getIdUsuario(),
                usuario.getRut(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getPassword(),
                usuario.getRol().getNombre());
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizarTexto(String texto) {
        return texto.trim();
    }

    private String obtenerUsuarioAutenticado() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    if (auth == null || auth.getName() == null) {
        return "usuario-no-autenticado";
    }

    return auth.getName();
}
}