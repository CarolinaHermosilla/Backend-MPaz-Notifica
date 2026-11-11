package cl.duoc.mpaz.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.mpaz.usuarios.dto.UsuarioRequest;
import cl.duoc.mpaz.usuarios.dto.UsuarioResponse;
import cl.duoc.mpaz.usuarios.entities.Usuario;
import cl.duoc.mpaz.usuarios.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.RepositorioUsuarios;
import cl.duoc.mpaz.usuarios.services.UsuarioService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioServiceTest {

    private RepositorioUsuariosEnMemoria repositorio;
    private UsuarioService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioUsuariosEnMemoria();
        servicio = new UsuarioService(repositorio);
    }

    @Test
    @DisplayName("Una cuenta nueva queda guardada con su rol")
    void crearCuentaNuevaLaGuarda() {
        UsuarioResponse respuesta = servicio.crear(
                new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "PROFESOR_JEFE", "5.º Básico B"));

        assertEquals(1L, respuesta.id());
        assertEquals("ana@ejemplo.cl", respuesta.correo());
        assertEquals("PROFESOR_JEFE", respuesta.rol());
        assertTrue(respuesta.activo());
        assertEquals(1, repositorio.listar().size());
    }

    @Test
    @DisplayName("No se puede crear dos veces la misma cuenta")
    void correoRepetidoFalla() {
        servicio.crear(new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "PROFESOR_JEFE", null));

        SolicitudInvalidaException error = assertThrows(SolicitudInvalidaException.class,
                () -> servicio.crear(new UsuarioRequest("Otra", "ANA@ejemplo.cl", "123456", "PROFESOR_JEFE", null)));

        assertEquals("Ya existe una cuenta con el correo ana@ejemplo.cl", error.getMessage());
        assertEquals(1, repositorio.listar().size());
    }

    @Test
    @DisplayName("Un rol que no existe se rechaza")
    void rolInvalidoFalla() {
        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.crear(new UsuarioRequest("Ana", "ana@ejemplo.cl", "123456", "ALUMNO", null)));
        assertTrue(repositorio.listar().isEmpty());
    }

    @Test
    @DisplayName("Al pasar a Inspectoría la cuenta deja de tener curso")
    void inspectoriaQuedaSinCurso() {
        servicio.crear(new UsuarioRequest("Ana Diaz", "ana@ejemplo.cl", "123456", "PROFESOR_JEFE", "5.º Básico B"));

        UsuarioResponse respuesta = servicio.cambiarRol(1L, "INSPECTORIA");

        assertEquals("INSPECTORIA", respuesta.rol());
        assertNull(respuesta.curso());
    }

    @Test
    @DisplayName("Buscar una cuenta que no existe lanza error")
    void cuentaInexistenteFalla() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99L));
    }

    /** Implementación en memoria del repositorio, escrita a mano para la prueba. */
    private static class RepositorioUsuariosEnMemoria implements RepositorioUsuarios {

        private final Map<Long, Usuario> datos = new LinkedHashMap<>();
        private long siguienteId = 1;

        @Override
        public List<Usuario> listar() {
            return new ArrayList<>(datos.values());
        }

        @Override
        public Optional<Usuario> buscarPorId(Long id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public boolean existeCorreo(String correo) {
            return datos.values().stream()
                    .anyMatch(usuario -> usuario.getCorreo().equalsIgnoreCase(correo));
        }

        @Override
        public Usuario guardar(Usuario usuario) {
            if (usuario.getId() == null) {
                usuario.setId(siguienteId++);
            }
            datos.put(usuario.getId(), usuario);
            return usuario;
        }
    }
}
