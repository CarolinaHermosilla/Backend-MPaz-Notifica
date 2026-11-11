package cl.duoc.mpaz.mensajeria.repository;

import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByRemitenteIdOrderByIdDesc(Long remitenteId);

    List<Mensaje> findByEstadoOrderByIdDesc(String estado);
}
