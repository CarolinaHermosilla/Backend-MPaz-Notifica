package cl.duoc.mpaz.usuarios.dto;

import java.util.List;

public record GrupoResponse(
        Long id,
        String nombre,
        String descripcion,
        boolean creadoPorTaller,
        int cantidadIntegrantes,
        List<String> integrantes) {
}
