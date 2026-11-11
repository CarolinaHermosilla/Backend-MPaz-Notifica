package cl.duoc.mpaz.historial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record RegistroRequest(
        @NotNull(message = "Falta el id de la comunicación")
        Long mensajeId,

        @NotBlank(message = "El título es obligatorio")
        String titulo,

        String tipo,

        String canal,

        LocalDateTime fecha,

        Long remitenteId,

        String remitenteNombre,

        List<String> destinatarios) {
}
