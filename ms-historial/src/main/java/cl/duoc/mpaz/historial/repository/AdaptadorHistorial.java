package cl.duoc.mpaz.historial.repository;

import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdaptadorHistorial implements RepositorioHistorial {

    private final HistorialRepository historialRepository;

    public AdaptadorHistorial(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Override
    public List<RegistroHistorico> listarOrdenados() {
        return historialRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public List<RegistroHistorico> listarPorRemitente(Long remitenteId) {
        return historialRepository.findByRemitenteIdOrderByFechaDesc(remitenteId);
    }

    @Override
    public Optional<RegistroHistorico> buscarPorId(Long id) {
        return historialRepository.findById(id);
    }

    @Override
    public Optional<RegistroHistorico> buscarPorMensajeId(Long mensajeId) {
        return historialRepository.findFirstByMensajeId(mensajeId);
    }

    @Override
    public RegistroHistorico guardar(RegistroHistorico registro) {
        return historialRepository.save(registro);
    }
}
