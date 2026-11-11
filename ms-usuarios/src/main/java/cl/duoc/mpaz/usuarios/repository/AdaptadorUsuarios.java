package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdaptadorUsuarios implements RepositorioUsuarios {

    private final UsuarioRepository usuarioRepository;

    public AdaptadorUsuarios(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioRepository.existsByCorreo(correo);
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }
}
