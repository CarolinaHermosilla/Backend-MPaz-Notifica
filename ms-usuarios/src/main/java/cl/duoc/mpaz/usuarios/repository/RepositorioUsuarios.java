package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Usuario;
import java.util.List;
import java.util.Optional;

public interface RepositorioUsuarios {

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(Long id);

    boolean existeCorreo(String correo);

    Usuario guardar(Usuario usuario);
}
