package cl.duoc.mpaz.mensajeria.dto;

import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import java.time.LocalDateTime;

public record MensajeResponse(
        Long id,
        String titulo,
        String cuerpo,
        String tipo,
        String canal,
        String estado,
        LocalDateTime fecha,
        Long remitenteId,
        String remitenteNombre,
        String adjuntos,
        long cantidadDestinatarios) {

    public static MensajeResponse desde(Mensaje mensaje, long cantidadDestinatarios) {
        return new MensajeResponse(
                mensaje.getId(),
                mensaje.getTitulo(),
                mensaje.getCuerpo(),
                mensaje.getTipo(),
                mensaje.getCanal(),
                mensaje.getEstado(),
                mensaje.getFecha(),
                mensaje.getRemitenteId(),
                mensaje.getRemitenteNombre(),
                mensaje.getAdjuntos(),
                cantidadDestinatarios);
    }
}
