package cl.duoc.mpaz.historial.services;

import cl.duoc.mpaz.historial.dto.RegistroRequest;
import cl.duoc.mpaz.historial.dto.RegistroResponse;
import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import cl.duoc.mpaz.historial.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.historial.repository.RepositorioHistorial;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HistorialService {

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Santiago");

    private final RepositorioHistorial repositorio;

    public HistorialService(RepositorioHistorial repositorio) {
        this.repositorio = repositorio;
    }

    public List<RegistroResponse> listar() {
        return repositorio.listarOrdenados().stream().map(RegistroResponse::desde).toList();
    }

    public RegistroResponse obtener(Long id) {
        return RegistroResponse.desde(buscar(id));
    }

    public RegistroResponse registrar(RegistroRequest solicitud) {
        List<String> destinatarios = solicitud.destinatarios() == null ? List.of() : solicitud.destinatarios();
        RegistroHistorico registro = repositorio.guardar(RegistroHistorico.builder()
                .mensajeId(solicitud.mensajeId())
                .titulo(solicitud.titulo())
                .tipo(solicitud.tipo() == null ? "Sin tipo" : solicitud.tipo())
                .canal(solicitud.canal() == null ? "Sin canal" : solicitud.canal())
                .fecha(solicitud.fecha() == null ? LocalDateTime.now(ZONA_HORARIA) : solicitud.fecha())
                .remitenteId(solicitud.remitenteId() == null ? 0L : solicitud.remitenteId())
                .remitenteNombre(solicitud.remitenteNombre() == null ? "Sin remitente" : solicitud.remitenteNombre())
                .cantidadDestinatarios(destinatarios.size())
                .destinatarios(String.join("|", destinatarios))
                .build());
        return RegistroResponse.desde(registro);
    }

    private RegistroHistorico buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el registro con id " + id));
    }
}
