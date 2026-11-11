package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoIntegranteRepository extends JpaRepository<GrupoIntegrante, Long> {

    List<GrupoIntegrante> findByGrupoId(Long grupoId);

    void deleteByGrupoId(Long grupoId);
}
