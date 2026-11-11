package cl.duoc.mpaz.mensajeria.controller;

import cl.duoc.mpaz.mensajeria.dto.EnvioRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeResponse;
import cl.duoc.mpaz.mensajeria.services.MensajeService;
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
@RequestMapping("/api/mensajes")
public class MensajeController {

    private final MensajeService mensajeService;

    public MensajeController(MensajeService mensajeService) {
        this.mensajeService = mensajeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MensajeResponse crearBorrador(@Valid @RequestBody MensajeRequest solicitud) {
        return mensajeService.crearBorrador(solicitud);
    }

    @PutMapping("/{id}/enviar")
    public MensajeResponse enviar(@PathVariable Long id, @Valid @RequestBody EnvioRequest solicitud) {
        return mensajeService.enviar(id, solicitud);
    }

    @GetMapping("/{id}")
    public MensajeResponse obtener(@PathVariable Long id) {
        return mensajeService.obtener(id);
    }

    @GetMapping
    public List<MensajeResponse> listar(@RequestParam Long remitenteId) {
        return mensajeService.listarPorRemitente(remitenteId);
    }
}
