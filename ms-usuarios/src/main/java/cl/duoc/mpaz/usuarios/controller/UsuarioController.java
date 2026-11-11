package cl.duoc.mpaz.usuarios.controller;

import cl.duoc.mpaz.usuarios.dto.UsuarioRequest;
import cl.duoc.mpaz.usuarios.dto.UsuarioResponse;
import cl.duoc.mpaz.usuarios.services.UsuarioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest solicitud) {
        return usuarioService.crear(solicitud);
    }

    @PutMapping("/{id}/rol")
    public UsuarioResponse cambiarRol(@PathVariable Long id, @RequestParam String rol) {
        return usuarioService.cambiarRol(id, rol);
    }

    @PutMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable Long id, @RequestParam boolean activo) {
        return usuarioService.cambiarEstado(id, activo);
    }
}
