package cl.duoc.mpaz.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.mpaz.usuarios.dto.GrupoRequest;
import cl.duoc.mpaz.usuarios.dto.GrupoResponse;
import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.ApoderadoRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoIntegranteRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoRepository;
import cl.duoc.mpaz.usuarios.services.DirectorioService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DirectorioServiceTest {

    @Mock
    private ApoderadoRepository apoderadoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @Mock
    private GrupoIntegranteRepository grupoIntegranteRepository;

    @InjectMocks
    private DirectorioService directorioService;

    @Test
    void losGruposDevuelvenSusIntegrantes() {
        Grupo grupo = Grupo.builder().id(5L).nombre("Taller de Ajedrez").creadoPorTaller(true).build();
        Apoderado primero = Apoderado.builder().id(1L).nombre("Marcelo Bustos Pinto").build();
        Apoderado segundo = Apoderado.builder().id(2L).nombre("Héctor Fuentes Navarro").build();
        when(grupoRepository.findAllByOrderByIdDesc()).thenReturn(List.of(grupo));
        when(apoderadoRepository.findAll()).thenReturn(List.of(primero, segundo));
        when(grupoIntegranteRepository.findByGrupoId(5L)).thenReturn(List.of(
                GrupoIntegrante.builder().id(1L).grupoId(5L).apoderadoId(1L).build(),
                GrupoIntegrante.builder().id(2L).grupoId(5L).apoderadoId(2L).build()));

        List<GrupoResponse> grupos = directorioService.grupos();

        assertEquals(1, grupos.size());
        assertEquals(2, grupos.get(0).cantidadIntegrantes());
        assertTrue(grupos.get(0).integrantes().contains("Marcelo Bustos Pinto"));
    }

    @Test
    void noSePuedeCrearUnGrupoConNombreRepetido() {
        when(grupoRepository.existsByNombreIgnoreCase("Directiva del curso")).thenReturn(true);

        assertThrows(SolicitudInvalidaException.class,
                () -> directorioService.crear(new GrupoRequest("Directiva del curso", "descripcion", List.of(1L))));
        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    void noSePuedeCrearUnGrupoSinIntegrantes() {
        when(grupoRepository.existsByNombreIgnoreCase("Nuevo grupo")).thenReturn(false);

        assertThrows(SolicitudInvalidaException.class,
                () -> directorioService.crear(new GrupoRequest("Nuevo grupo", null, List.of())));
        verify(grupoRepository, never()).save(any(Grupo.class));
    }

    @Test
    void eliminarUnGrupoBorraTambienSusIntegrantes() {
        when(grupoRepository.existsById(7L)).thenReturn(true);

        directorioService.eliminar(7L);

        verify(grupoIntegranteRepository).deleteByGrupoId(7L);
        verify(grupoRepository).deleteById(7L);
    }
}
