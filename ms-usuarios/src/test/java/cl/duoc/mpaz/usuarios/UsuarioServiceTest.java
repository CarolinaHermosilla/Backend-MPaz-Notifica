package cl.duoc.mpaz.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.mpaz.usuarios.dto.UsuarioRequest;
import cl.duoc.mpaz.usuarios.dto.UsuarioResponse;
import cl.duoc.mpaz.usuarios.entities.Usuario;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.UsuarioRepository;
import cl.duoc.mpaz.usuarios.services.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void crearCuentaConCorreoNuevoLaGuarda() {
        UsuarioRequest solicitud = new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "PROFESOR_JEFE", "5.º Básico B");
        when(usuarioRepository.existsByCorreo("ana@ejemplo.cl")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> {
            Usuario usuario = invocacion.getArgument(0);
            usuario.setId(1L);
            return usuario;
        });

        UsuarioResponse respuesta = usuarioService.crear(solicitud);

        assertEquals(1L, respuesta.id());
        assertEquals("ana@ejemplo.cl", respuesta.correo());
        assertEquals("PROFESOR_JEFE", respuesta.rol());
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void crearCuentaConCorreoRepetidoFalla() {
        UsuarioRequest solicitud = new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "PROFESOR_JEFE", null);
        when(usuarioRepository.existsByCorreo("ana@ejemplo.cl")).thenReturn(true);

        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class,
                () -> usuarioService.crear(solicitud));

        assertEquals("Ya existe una cuenta con el correo ana@ejemplo.cl", error.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void crearCuentaConRolInvalidoFalla() {
        UsuarioRequest solicitud = new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "ALUMNO", null);

        assertThrows(SolicitudInvalidaException.class, () -> usuarioService.crear(solicitud));
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void cambiarRolALaInspectoriaQuitaElCurso() {
        Usuario usuario = Usuario.builder()
                .id(2L)
                .nombre("Ana Diaz")
                .correo("ana@ejemplo.cl")
                .contrasena("123456")
                .rol("PROFESOR_JEFE")
                .curso("5.º Básico B")
                .activo(true)
                .build();
        when(usuarioRepository.findById(2L)).thenReturn(java.util.Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        UsuarioResponse respuesta = usuarioService.cambiarRol(2L, "INSPECTORIA");

        assertEquals("INSPECTORIA", respuesta.rol());
        assertEquals(null, respuesta.curso());
    }
}
