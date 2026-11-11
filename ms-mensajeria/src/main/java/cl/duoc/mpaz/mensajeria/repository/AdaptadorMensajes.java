package cl.duoc.mpaz.mensajeria.repository;

import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdaptadorMensajes implements RepositorioMensajes {

    private final MensajeRepository mensajeRepository;
    private final MensajeDestinatarioRepository destinatarioRepository;

    public AdaptadorMensajes(MensajeRepository mensajeRepository,
                             MensajeDestinatarioRepository destinatarioRepository) {
        this.mensajeRepository = mensajeRepository;
        this.destinatarioRepository = destinatarioRepository;
    }

    @Override
    public Optional<Mensaje> buscarPorId(Long id) {
        return mensajeRepository.findById(id);
    }

    @Override
    public Mensaje guardar(Mensaje mensaje) {
        return mensajeRepository.save(mensaje);
    }

    @Override
    public List<Mensaje> listarPorRemitente(Long remitenteId) {
        return mensajeRepository.findByRemitenteIdOrderByIdDesc(remitenteId);
    }

    @Override
    public long contarDestinatarios(Long mensajeId) {
        return destinatarioRepository.countByMensajeId(mensajeId);
    }

    @Override
    public MensajeDestinatario guardarDestinatario(MensajeDestinatario destinatario) {
        return destinatarioRepository.save(destinatario);
    }
}
