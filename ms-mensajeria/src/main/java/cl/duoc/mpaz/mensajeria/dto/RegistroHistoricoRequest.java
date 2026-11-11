package cl.duoc.mpaz.mensajeria.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RegistroHistoricoRequest(
        Long mensajeId,
        String titulo,
        String tipo,
        String canal,
        LocalDateTime fecha,
        Long remitenteId,
        String remitenteNombre,
        List<String> destinatarios) {
}
