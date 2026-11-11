package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class AdaptadorDirectorio implements RepositorioDirectorio {

    private final ApoderadoRepository apoderadoRepository;
    private final GrupoRepository grupoRepository;
    private final GrupoIntegranteRepository grupoIntegranteRepository;

    public AdaptadorDirectorio(ApoderadoRepository apoderadoRepository,
                               GrupoRepository grupoRepository,
                               GrupoIntegranteRepository grupoIntegranteRepository) {
        this.apoderadoRepository = apoderadoRepository;
        this.grupoRepository = grupoRepository;
        this.grupoIntegranteRepository = grupoIntegranteRepository;
    }

    @Override
    public List<Apoderado> apoderadosOrdenados() {
        return apoderadoRepository.findAllByOrderByEstudianteAsc();
    }

    @Override
    public List<Apoderado> apoderados() {
        return apoderadoRepository.findAll();
    }

    @Override
    public boolean existeApoderado(Long id) {
        return apoderadoRepository.existsById(id);
    }

    @Override
    public List<Grupo> gruposOrdenados() {
        return grupoRepository.findAllByOrderByIdDesc();
    }

    @Override
    public boolean existeGrupo(Long id) {
        return grupoRepository.existsById(id);
    }

    @Override
    public boolean existeGrupoPorNombre(String nombre) {
        return grupoRepository.existsByNombreIgnoreCase(nombre);
    }

    @Override
    public Grupo guardarGrupo(Grupo grupo) {
        return grupoRepository.save(grupo);
    }

    @Override
    public void borrarGrupo(Long id) {
        grupoRepository.deleteById(id);
    }

    @Override
    public List<GrupoIntegrante> integrantesDe(Long grupoId) {
        return grupoIntegranteRepository.findByGrupoId(grupoId);
    }

    @Override
    public GrupoIntegrante guardarIntegrante(GrupoIntegrante integrante) {
        return grupoIntegranteRepository.save(integrante);
    }

    @Override
    public void borrarIntegrantesDe(Long grupoId) {
        grupoIntegranteRepository.deleteByGrupoId(grupoId);
    }
}
