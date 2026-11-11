package cl.duoc.mpaz.usuarios.services;

import cl.duoc.mpaz.usuarios.dto.GrupoRequest;
import cl.duoc.mpaz.usuarios.dto.GrupoResponse;
import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import cl.duoc.mpaz.usuarios.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.RepositorioDirectorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DirectorioService {

    private final RepositorioDirectorio repositorio;

    public DirectorioService(RepositorioDirectorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Apoderado> apoderados() {
        return repositorio.apoderadosOrdenados();
    }

    public List<GrupoResponse> grupos() {
        Map<Long, Apoderado> apoderados = repositorio.apoderados().stream()
                .collect(Collectors.toMap(Apoderado::getId, Function.identity()));
        List<GrupoResponse> respuesta = new ArrayList<>();
        for (Grupo grupo : repositorio.gruposOrdenados()) {
            List<String> integrantes = new ArrayList<>();
            for (GrupoIntegrante integrante : repositorio.integrantesDe(grupo.getId())) {
                Apoderado apoderado = apoderados.get(integrante.getApoderadoId());
                if (apoderado != null) {
                    integrantes.add(apoderado.getNombre());
                }
            }
            respuesta.add(new GrupoResponse(
                    grupo.getId(),
                    grupo.getNombre(),
                    grupo.getDescripcion(),
                    grupo.isCreadoPorTaller(),
                    integrantes.size(),
                    integrantes));
        }
        return respuesta;
    }

    public GrupoResponse crear(GrupoRequest solicitud) {
        String nombre = solicitud.nombre().trim();
        if (repositorio.existeGrupoPorNombre(nombre)) {
            throw new SolicitudInvalidaException("Ya existe un grupo llamado " + nombre);
        }
        List<Long> integrantes = solicitud.integrantes() == null ? List.of() : solicitud.integrantes();
        if (integrantes.isEmpty()) {
            throw new SolicitudInvalidaException("El grupo debe tener al menos un apoderado");
        }
        Grupo grupo = repositorio.guardarGrupo(Grupo.builder()
                .nombre(nombre)
                .descripcion(solicitud.descripcion())
                .creadoPorTaller(false)
                .build());
        for (Long apoderadoId : integrantes) {
            if (!repositorio.existeApoderado(apoderadoId)) {
                throw new RecursoNoEncontradoException("No existe el apoderado con id " + apoderadoId);
            }
            repositorio.guardarIntegrante(GrupoIntegrante.builder()
                    .grupoId(grupo.getId())
                    .apoderadoId(apoderadoId)
                    .build());
        }
        return grupos().stream()
                .filter(registro -> registro.id().equals(grupo.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se pudo leer el grupo recien creado"));
    }

    public void eliminar(Long id) {
        if (!repositorio.existeGrupo(id)) {
            throw new RecursoNoEncontradoException("No existe el grupo con id " + id);
        }
        repositorio.borrarIntegrantesDe(id);
        repositorio.borrarGrupo(id);
    }
}
