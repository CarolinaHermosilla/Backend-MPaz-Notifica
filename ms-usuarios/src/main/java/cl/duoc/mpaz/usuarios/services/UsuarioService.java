package cl.duoc.mpaz.usuarios.services;

import cl.duoc.mpaz.usuarios.dto.UsuarioRequest;
import cl.duoc.mpaz.usuarios.dto.UsuarioResponse;
import cl.duoc.mpaz.usuarios.entities.Usuario;
import cl.duoc.mpaz.usuarios.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.RepositorioUsuarios;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private static final String ROL_PROFESOR = "PROFESOR_JEFE";
    private static final String ROL_INSPECTORIA = "INSPECTORIA";

    private final RepositorioUsuarios repositorio;

    public UsuarioService(RepositorioUsuarios repositorio) {
        this.repositorio = repositorio;
    }

    public List<UsuarioResponse> listar() {
        return repositorio.listar().stream().map(UsuarioResponse::desde).toList();
    }

    public UsuarioResponse obtener(Long id) {
        return UsuarioResponse.desde(buscar(id));
    }

    public UsuarioResponse crear(UsuarioRequest solicitud) {
        validarRol(solicitud.rol());
        String correo = solicitud.correo().trim().toLowerCase();
        if (repositorio.existeCorreo(correo)) {
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
        return UsuarioResponse.desde(repositorio.guardar(usuario));
    }

    public UsuarioResponse cambiarRol(Long id, String rol) {
        validarRol(rol);
        Usuario usuario = buscar(id);
        usuario.setRol(rol);
        if (ROL_INSPECTORIA.equals(rol)) {
            usuario.setCurso(null);
        }
        return UsuarioResponse.desde(repositorio.guardar(usuario));
    }

    public UsuarioResponse cambiarEstado(Long id, boolean activo) {
        Usuario usuario = buscar(id);
        usuario.setActivo(activo);
        return UsuarioResponse.desde(repositorio.guardar(usuario));
    }

    private Usuario buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la cuenta con id " + id));
    }

    private void validarRol(String rol) {
        if (!ROL_PROFESOR.equals(rol) && !ROL_INSPECTORIA.equals(rol)) {
            throw new SolicitudInvalidaException("El rol debe ser PROFESOR_JEFE o INSPECTORIA");
        }
    }
}
