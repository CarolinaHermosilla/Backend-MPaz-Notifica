package cl.duoc.mpaz.mensajeria.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record EnvioRequest(
        @NotBlank(message = "Debes elegir un canal de envío")
        String canal,

        @NotEmpty(message = "Debes elegir al menos un apoderado")
        List<@Valid DestinatarioRequest> destinatarios) {
}
