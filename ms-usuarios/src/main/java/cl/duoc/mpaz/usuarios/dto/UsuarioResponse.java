package cl.duoc.mpaz.usuarios.dto;

import cl.duoc.mpaz.usuarios.entities.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String correo,
        String contrasena,
        String rol,
        String curso,
        boolean activo) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getContrasena(),
                usuario.getRol(),
                usuario.getCurso(),
                usuario.isActivo());
    }
}
