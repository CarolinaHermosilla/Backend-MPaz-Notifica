package cl.duoc.mpaz.usuarios.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no tiene un formato valido")
        String correo,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres")
        String contrasena,

        @NotBlank(message = "El rol es obligatorio")
        @Pattern(regexp = "PROFESOR_JEFE|INSPECTORIA", message = "El rol debe ser PROFESOR_JEFE o INSPECTORIA")
        String rol,

        String curso) {
}
