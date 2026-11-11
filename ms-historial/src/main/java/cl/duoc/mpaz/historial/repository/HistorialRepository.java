package cl.duoc.mpaz.historial.repository;

import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialRepository extends JpaRepository<RegistroHistorico, Long> {

    List<RegistroHistorico> findAllByOrderByFechaDesc();
}
