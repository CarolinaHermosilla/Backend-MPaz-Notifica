package cl.duoc.mpaz.usuarios.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

public record GrupoRequest(
        @NotBlank(message = "El nombre del grupo es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
        String nombre,

        String descripcion,

        List<Long> integrantes) {
}
