package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import java.util.List;

public interface RepositorioDirectorio {

    List<Apoderado> apoderadosOrdenados();

    List<Apoderado> apoderados();

    boolean existeApoderado(Long id);

    List<Grupo> gruposOrdenados();

    boolean existeGrupo(Long id);

    boolean existeGrupoPorNombre(String nombre);

    Grupo guardarGrupo(Grupo grupo);

    void borrarGrupo(Long id);

    List<GrupoIntegrante> integrantesDe(Long grupoId);

    GrupoIntegrante guardarIntegrante(GrupoIntegrante integrante);

    void borrarIntegrantesDe(Long grupoId);
}
