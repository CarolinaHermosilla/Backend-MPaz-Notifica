package cl.duoc.mpaz.historial.services;

import cl.duoc.mpaz.historial.dto.RegistroRequest;
import cl.duoc.mpaz.historial.dto.RegistroResponse;
import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import cl.duoc.mpaz.historial.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.historial.repository.HistorialRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistorialService {

    private final HistorialRepository historialRepository;

    public HistorialService(HistorialRepository historialRepository) {
        this.historialRepository = historialRepository;
    }

    @Transactional(readOnly = true)
    public List<RegistroResponse> listar() {
        return historialRepository.findAllByOrderByFechaDesc().stream()
                .map(RegistroResponse::desde)
                .toList();
    }

    @Transactional(readOnly = true)
    public RegistroResponse obtener(Long id) {
        return RegistroResponse.desde(buscar(id));
    }

    @Transactional
    public RegistroResponse registrar(RegistroRequest solicitud) {
        List<String> destinatarios = solicitud.destinatarios() == null ? List.of() : solicitud.destinatarios();
        RegistroHistorico registro = historialRepository.save(RegistroHistorico.builder()
                .mensajeId(solicitud.mensajeId())
                .titulo(solicitud.titulo())
                .tipo(solicitud.tipo() == null ? "Sin tipo" : solicitud.tipo())
                .canal(solicitud.canal() == null ? "Sin canal" : solicitud.canal())
                .fecha(solicitud.fecha() == null ? LocalDateTime.now() : solicitud.fecha())
                .remitenteId(solicitud.remitenteId() == null ? 0L : solicitud.remitenteId())
                .remitenteNombre(solicitud.remitenteNombre() == null ? "Sin remitente" : solicitud.remitenteNombre())
                .cantidadDestinatarios(destinatarios.size())
                .destinatarios(String.join("|", destinatarios))
                .build());
        return RegistroResponse.desde(registro);
    }

    private RegistroHistorico buscar(Long id) {
        return historialRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el registro con id " + id));
    }
}
