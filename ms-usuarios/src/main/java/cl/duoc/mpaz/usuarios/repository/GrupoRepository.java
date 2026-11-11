package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Grupo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findAllByOrderByIdDesc();

    boolean existsByNombreIgnoreCase(String nombre);
}
