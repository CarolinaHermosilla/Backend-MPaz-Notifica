package cl.duoc.mpaz.historial.dto;

import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public record RegistroResponse(
        Long id,
        Long mensajeId,
        String titulo,
        String tipo,
        String canal,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime fecha,
        Long remitenteId,
        String remitenteNombre,
        int cantidadDestinatarios,
        List<String> destinatarios) {

    public static RegistroResponse desde(RegistroHistorico registro) {
        List<String> destinatarios = registro.getDestinatarios() == null || registro.getDestinatarios().isBlank()
                ? List.of()
                : Arrays.asList(registro.getDestinatarios().split("\\|"));
        return new RegistroResponse(
                registro.getId(),
                registro.getMensajeId(),
                registro.getTitulo(),
                registro.getTipo(),
                registro.getCanal(),
                registro.getFecha(),
                registro.getRemitenteId(),
                registro.getRemitenteNombre(),
                registro.getCantidadDestinatarios(),
                destinatarios);
    }
}
