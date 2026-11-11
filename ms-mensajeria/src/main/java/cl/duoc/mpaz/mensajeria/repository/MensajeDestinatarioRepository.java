package cl.duoc.mpaz.mensajeria.repository;

import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MensajeDestinatarioRepository extends JpaRepository<MensajeDestinatario, Long> {

    List<MensajeDestinatario> findByMensajeId(Long mensajeId);

    long countByMensajeId(Long mensajeId);
}
