package cl.duoc.mpaz.historial.repository;

import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<RegistroHistorico, Long> {

    List<RegistroHistorico> findAllByOrderByFechaDesc();

    List<RegistroHistorico> findByRemitenteIdOrderByFechaDesc(Long remitenteId);

    Optional<RegistroHistorico> findFirstByMensajeId(Long mensajeId);
}
