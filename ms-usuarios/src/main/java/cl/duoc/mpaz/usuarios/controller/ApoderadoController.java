package cl.duoc.mpaz.usuarios.controller;

import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.services.DirectorioService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/apoderados")
public class ApoderadoController {

    private final DirectorioService directorioService;

    public ApoderadoController(DirectorioService directorioService) {
        this.directorioService = directorioService;
    }

    @GetMapping
    public List<Apoderado> listar() {
        return directorioService.apoderados();
    }
}
