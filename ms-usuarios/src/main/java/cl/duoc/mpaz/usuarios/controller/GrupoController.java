package cl.duoc.mpaz.usuarios.controller;

import cl.duoc.mpaz.usuarios.dto.GrupoRequest;
import cl.duoc.mpaz.usuarios.dto.GrupoResponse;
import cl.duoc.mpaz.usuarios.services.DirectorioService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final DirectorioService directorioService;

    public GrupoController(DirectorioService directorioService) {
        this.directorioService = directorioService;
    }

    @GetMapping
    public List<GrupoResponse> listar() {
        return directorioService.grupos();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GrupoResponse crear(@Valid @RequestBody GrupoRequest solicitud) {
        return directorioService.crear(solicitud);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        directorioService.eliminar(id);
    }
}
