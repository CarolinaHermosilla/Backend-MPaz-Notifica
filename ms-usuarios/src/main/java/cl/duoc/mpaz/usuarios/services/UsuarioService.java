package cl.duoc.mpaz.usuarios.services;

import cl.duoc.mpaz.usuarios.dto.UsuarioRequest;
import cl.duoc.mpaz.usuarios.dto.UsuarioResponse;
import cl.duoc.mpaz.usuarios.entities.Usuario;
import cl.duoc.mpaz.usuarios.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    private static final String ROL_PROFESOR = "PROFESOR_JEFE";
    private static final String ROL_INSPECTORIA = "INSPECTORIA";

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponse::desde).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    @Transactional
    public UsuarioResponse crear(UsuarioRequest solicitud) {
        validarRol(solicitud.rol());
        String correo = solicitud.correo().trim().toLowerCase();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new SolicitudInvalidaException("Ya existe una cuenta con el correo " + correo);
        }
        Usuario usuario = Usuario.builder()
                .nombre(solicitud.nombre().trim())
                .correo(correo)
                .contrasena(solicitud.contrasena())
                .rol(solicitud.rol())
                .curso(solicitud.curso())
                .activo(true)
                .build();
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarRol(Long id, String rol) {
        validarRol(rol);
        Usuario usuario = buscar(id);
        usuario.setRol(rol);
        if (ROL_INSPECTORIA.equals(rol)) {
            usuario.setCurso(null);
        }
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscar(id);
        usuario.setActivo(activo);
        return UsuarioResponse.desde(usuarioRepository.save(usuario));
    }

    private Usuario buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta con id " + id));
    }

    private void validarRol(String rol) {
        if (!ROL_PROFESOR.equals(rol) && !ROL_INSPECTORIA.equals(rol)) {
            throw new SolicitudInvalidaException("El rol debe ser PROFESOR_JEFE o INSPECTORIA");
        }
    }
}
