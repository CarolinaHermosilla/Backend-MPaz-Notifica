package cl.duoc.mpaz.mensajeria.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MensajeRequest(
        @NotBlank(message = "El título de la comunicación es obligatorio")
        @Size(max = 150, message = "El título no puede superar los 150 caracteres")
        String titulo,

        @NotBlank(message = "El mensaje no puede estar vacío")
        String cuerpo,

        @NotBlank(message = "Debes indicar el tipo de comunicación")
        String tipo,

        @NotNull(message = "Falta el remitente")
        Long remitenteId,

        @NotBlank(message = "Falta el nombre del remitente")
        String remitenteNombre,

        String adjuntos) {
}
