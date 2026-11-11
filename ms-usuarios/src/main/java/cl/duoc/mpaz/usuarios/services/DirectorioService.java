package cl.duoc.mpaz.usuarios.services;

import cl.duoc.mpaz.usuarios.dto.GrupoRequest;
import cl.duoc.mpaz.usuarios.dto.GrupoResponse;
import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import cl.duoc.mpaz.usuarios.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.usuarios.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.usuarios.repository.ApoderadoRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoIntegranteRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DirectorioService {

    private final ApoderadoRepository apoderadoRepository;
    private final GrupoRepository grupoRepository;
    private final GrupoIntegranteRepository grupoIntegranteRepository;

    public DirectorioService(ApoderadoRepository apoderadoRepository,
                             GrupoRepository grupoRepository,
                             GrupoIntegranteRepository grupoIntegranteRepository) {
        this.apoderadoRepository = apoderadoRepository;
        this.grupoRepository = grupoRepository;
        this.grupoIntegranteRepository = grupoIntegranteRepository;
    }

    @Transactional(readOnly = true)
    public List<Apoderado> apoderados() {
        return apoderadoRepository.findAllByOrderByEstudianteAsc();
    }

    @Transactional(readOnly = true)
    public List<GrupoResponse> grupos() {
        Map<Long, Apoderado> apoderados = apoderadoRepository.findAll().stream()
                .collect(Collectors.toMap(Apoderado::getId, Function.identity()));
        List<GrupoResponse> respuesta = new ArrayList<>();
        for (Grupo grupo : grupoRepository.findAllByOrderByIdDesc()) {
            List<String> integrantes = new ArrayList<>();
            for (GrupoIntegrante integrante : grupoIntegranteRepository.findByGrupoId(grupo.getId())) {
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

    @Transactional
    public GrupoResponse crear(GrupoRequest solicitud) {
        String nombre = solicitud.nombre().trim();
        if (grupoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new SolicitudInvalidaException("Ya existe un grupo llamado " + nombre);
        }
        List<Long> integrantes = solicitud.integrantes() == null ? List.of() : solicitud.integrantes();
        if (integrantes.isEmpty()) {
            throw new SolicitudInvalidaException("El grupo debe tener al menos un apoderado");
        }
        Grupo grupo = grupoRepository.save(Grupo.builder()
                .nombre(nombre)
                .descripcion(solicitud.descripcion())
                .creadoPorTaller(false)
                .build());
        for (Long apoderadoId : integrantes) {
            if (!apoderadoRepository.existsById(apoderadoId)) {
                throw new RecursoNoEncontradoException("No existe el apoderado con id " + apoderadoId);
            }
            grupoIntegranteRepository.save(GrupoIntegrante.builder()
                    .grupoId(grupo.getId())
                    .apoderadoId(apoderadoId)
                    .build());
        }
        return grupos().stream()
                .filter(registro -> registro.id().equals(grupo.getId()))
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException("No se pudo leer el grupo recien creado"));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!grupoRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("No existe el grupo con id " + id);
        }
        grupoIntegranteRepository.deleteByGrupoId(id);
        grupoRepository.deleteById(id);
    }
}
