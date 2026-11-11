package cl.duoc.mpaz.mensajeria.repository;

import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import java.util.List;
import java.util.Optional;

public interface RepositorioMensajes {

    Optional<Mensaje> buscarPorId(Long id);

    Mensaje guardar(Mensaje mensaje);

    List<Mensaje> listarPorRemitente(Long remitenteId);

    long contarDestinatarios(Long mensajeId);

    MensajeDestinatario guardarDestinatario(MensajeDestinatario destinatario);
}
