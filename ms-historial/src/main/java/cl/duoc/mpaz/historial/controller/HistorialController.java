package cl.duoc.mpaz.historial.controller;

import cl.duoc.mpaz.historial.dto.RegistroRequest;
import cl.duoc.mpaz.historial.dto.RegistroResponse;
import cl.duoc.mpaz.historial.services.HistorialService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final HistorialService historialService;

    public HistorialController(HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public List<RegistroResponse> listar() {
        return historialService.listar();
    }

    @GetMapping("/{id}")
    public RegistroResponse obtener(@PathVariable Long id) {
        return historialService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RegistroResponse registrar(@Valid @RequestBody RegistroRequest solicitud) {
        return historialService.registrar(solicitud);
    }
}
