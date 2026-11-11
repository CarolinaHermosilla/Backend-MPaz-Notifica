package cl.duoc.mpaz.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.mpaz.usuarios.dto.GrupoRequest;
import cl.duoc.mpaz.usuarios.dto.GrupoResponse;
import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.RepositorioDirectorio;
import cl.duoc.mpaz.usuarios.services.DirectorioService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DirectorioServiceTest {

    private RepositorioDirectorioEnMemoria repositorio;
    private DirectorioService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioDirectorioEnMemoria();
        repositorio.agregarApoderado(1L, "Marcelo Bustos Pinto");
        repositorio.agregarApoderado(2L, "Héctor Fuentes Navarro");
        repositorio.agregarGrupo(5L, "Taller de Ajedrez", 1L, 2L);
        servicio = new DirectorioService(repositorio);
    }

    @Test
    @DisplayName("Cada grupo se devuelve con sus integrantes")
    void gruposConSusIntegrantes() {
        List<GrupoResponse> grupos = servicio.grupos();

        assertEquals(1, grupos.size());
        assertEquals("Taller de Ajedrez", grupos.get(0).nombre());
        assertEquals(2, grupos.get(0).cantidadIntegrantes());
        assertTrue(grupos.get(0).integrantes().contains("Marcelo Bustos Pinto"));
    }

    @Test
    @DisplayName("No se puede repetir el nombre de un grupo")
    void nombreRepetidoFalla() {
        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.crear(new GrupoRequest("Taller de Ajedrez", "otra vez", List.of(1L))));
        assertEquals(1, repositorio.gruposOrdenados().size());
    }

    @Test
    @DisplayName("Un grupo necesita al menos un apoderado")
    void grupoSinIntegrantesFalla() {
        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.crear(new GrupoRequest("Nuevo grupo", null, List.of())));
    }

    @Test
    @DisplayName("Se crea el grupo con sus integrantes")
    void crearGrupoConIntegrantes() {
        GrupoResponse creado = servicio.crear(new GrupoRequest("Apoderados porteros", "colaboran", List.of(1L, 2L)));

        assertEquals("Apoderados porteros", creado.nombre());
        assertEquals(2, creado.cantidadIntegrantes());
        assertEquals(2, repositorio.gruposOrdenados().size());
    }

    @Test
    @DisplayName("Al eliminar un grupo se borran sus integrantes")
    void eliminarGrupoBorraIntegrantes() {
        servicio.eliminar(5L);

        assertTrue(repositorio.gruposOrdenados().isEmpty());
        assertTrue(repositorio.integrantesDe(5L).isEmpty());
    }

    private static class RepositorioDirectorioEnMemoria implements RepositorioDirectorio {

        private final Map<Long, Apoderado> apoderados = new LinkedHashMap<>();
        private final Map<Long, Grupo> grupos = new LinkedHashMap<>();
        private final Map<Long, List<GrupoIntegrante>> integrantes = new LinkedHashMap<>();
        private long siguienteGrupo = 100;

        void agregarApoderado(Long id, String nombre) {
            apoderados.put(id, Apoderado.builder().id(id).nombre(nombre).estudiante("Estudiante").curso("5.º Básico B").build());
        }

        void agregarGrupo(Long id, String nombre, Long... idsApoderados) {
            grupos.put(id, Grupo.builder().id(id).nombre(nombre).creadoPorTaller(true).build());
            List<GrupoIntegrante> lista = new ArrayList<>();
            for (Long apoderadoId : idsApoderados) {
                lista.add(GrupoIntegrante.builder().grupoId(id).apoderadoId(apoderadoId).build());
            }
            integrantes.put(id, lista);
        }

        @Override
        public List<Apoderado> apoderadosOrdenados() {
            return new ArrayList<>(apoderados.values());
        }

        @Override
        public List<Apoderado> apoderados() {
            return new ArrayList<>(apoderados.values());
        }

        @Override
        public boolean existeApoderado(Long id) {
            return apoderados.containsKey(id);
        }

        @Override
        public List<Grupo> gruposOrdenados() {
            return new ArrayList<>(grupos.values());
        }

        @Override
        public boolean existeGrupo(Long id) {
            return grupos.containsKey(id);
        }

        @Override
        public boolean existeGrupoPorNombre(String nombre) {
            return grupos.values().stream().anyMatch(grupo -> grupo.getNombre().equalsIgnoreCase(nombre));
        }

        @Override
        public Grupo guardarGrupo(Grupo grupo) {
            if (grupo.getId() == null) {
                grupo.setId(siguienteGrupo++);
            }
            grupos.put(grupo.getId(), grupo);
            integrantes.putIfAbsent(grupo.getId(), new ArrayList<>());
            return grupo;
        }

        @Override
        public void borrarGrupo(Long id) {
            grupos.remove(id);
        }

        @Override
        public List<GrupoIntegrante> integrantesDe(Long grupoId) {
            return integrantes.getOrDefault(grupoId, new ArrayList<>());
        }

        @Override
        public GrupoIntegrante guardarIntegrante(GrupoIntegrante integrante) {
            integrantes.computeIfAbsent(integrante.getGrupoId(), clave -> new ArrayList<>()).add(integrante);
            return integrante;
        }

        @Override
        public void borrarIntegrantesDe(Long grupoId) {
            integrantes.put(grupoId, new ArrayList<>());
        }
    }
}
