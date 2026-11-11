package cl.duoc.mpaz.usuarios.repository;

import cl.duoc.mpaz.usuarios.entities.Apoderado;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApoderadoRepository extends JpaRepository<Apoderado, Long> {

    List<Apoderado> findAllByOrderByEstudianteAsc();
}
