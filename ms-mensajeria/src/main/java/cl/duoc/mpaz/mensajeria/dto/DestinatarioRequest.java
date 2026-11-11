package cl.duoc.mpaz.mensajeria.dto;

import jakarta.validation.constraints.NotNull;

public record DestinatarioRequest(
        @NotNull(message = "Cada destinatario necesita su id")
        Long apoderadoId,
        String nombre) {
}
