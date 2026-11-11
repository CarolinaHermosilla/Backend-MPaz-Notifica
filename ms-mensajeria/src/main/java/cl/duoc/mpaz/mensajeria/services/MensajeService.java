package cl.duoc.mpaz.mensajeria.services;

import cl.duoc.mpaz.mensajeria.client.RegistradorHistorial;
import cl.duoc.mpaz.mensajeria.dto.DestinatarioRequest;
import cl.duoc.mpaz.mensajeria.dto.EnvioRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeResponse;
import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;
import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import cl.duoc.mpaz.mensajeria.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.mensajeria.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.mensajeria.repository.RepositorioMensajes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MensajeService {

    public static final String BORRADOR = "Borrador";
    public static final String ENVIADA = "Enviada";

    private static final ZoneId ZONA_HORARIA = ZoneId.of("America/Santiago");

    private static final Set<String> CANALES = Set.of("Correo electrónico", "SMS", "WhatsApp");

    private final RepositorioMensajes repositorio;
    private final RegistradorHistorial registradorHistorial;

    public MensajeService(RepositorioMensajes repositorio, RegistradorHistorial registradorHistorial) {
        this.repositorio = repositorio;
        this.registradorHistorial = registradorHistorial;
    }

    public MensajeResponse crearBorrador(MensajeRequest solicitud) {
        Mensaje mensaje = repositorio.guardar(Mensaje.builder()
                .titulo(solicitud.titulo().trim())
                .cuerpo(solicitud.cuerpo())
                .tipo(solicitud.tipo())
                .canal(null)
                .estado(BORRADOR)
                .fecha(null)
                .remitenteId(solicitud.remitenteId())
                .remitenteNombre(solicitud.remitenteNombre())
                .adjuntos(solicitud.adjuntos())
                .build());
        return MensajeResponse.desde(mensaje, 0);
    }

    public MensajeResponse enviar(Long id, EnvioRequest solicitud) {
        Mensaje mensaje = buscar(id);
        if (ENVIADA.equals(mensaje.getEstado())) {
            throw new SolicitudInvalidaException("Esta comunicación ya fue enviada");
        }
        if (solicitud.destinatarios() == null || solicitud.destinatarios().isEmpty()) {
            throw new SolicitudInvalidaException("Debes elegir al menos un apoderado");
        }
        if (!CANALES.contains(solicitud.canal())) {
            throw new SolicitudInvalidaException("El canal debe ser Correo electrónico, SMS o WhatsApp");
        }
        List<String> nombres = new ArrayList<>();
        for (DestinatarioRequest destinatario : solicitud.destinatarios()) {
            repositorio.guardarDestinatario(MensajeDestinatario.builder()
                    .mensajeId(mensaje.getId())
                    .apoderadoId(destinatario.apoderadoId())
                    .apoderadoNombre(destinatario.nombre())
                    .build());
            nombres.add(destinatario.nombre() == null ? "Apoderado " + destinatario.apoderadoId() : destinatario.nombre());
        }
        mensaje.setCanal(solicitud.canal());
        mensaje.setEstado(ENVIADA);
        mensaje.setFecha(LocalDateTime.now(ZONA_HORARIA));
        Mensaje guardado = repositorio.guardar(mensaje);

        registradorHistorial.registrar(new RegistroHistoricoRequest(
                guardado.getId(),
                guardado.getTitulo(),
                guardado.getTipo(),
                guardado.getCanal(),
                guardado.getFecha(),
                guardado.getRemitenteId(),
                guardado.getRemitenteNombre(),
                nombres));

        return MensajeResponse.desde(guardado, nombres.size());
    }

    public MensajeResponse obtener(Long id) {
        Mensaje mensaje = buscar(id);
        return MensajeResponse.desde(mensaje, repositorio.contarDestinatarios(id));
    }

    public List<MensajeResponse> listarPorRemitente(Long remitenteId) {
        return repositorio.listarPorRemitente(remitenteId).stream()
                .map(mensaje -> MensajeResponse.desde(mensaje, repositorio.contarDestinatarios(mensaje.getId())))
                .toList();
    }

    private Mensaje buscar(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la comunicación con id " + id));
    }
}
