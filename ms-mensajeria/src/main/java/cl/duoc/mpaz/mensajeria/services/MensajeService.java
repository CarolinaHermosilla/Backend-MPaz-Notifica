package cl.duoc.mpaz.mensajeria.services;

import cl.duoc.mpaz.mensajeria.client.HistorialClient;
import cl.duoc.mpaz.mensajeria.dto.DestinatarioRequest;
import cl.duoc.mpaz.mensajeria.dto.EnvioRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeResponse;
import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;
import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import cl.duoc.mpaz.mensajeria.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.mensajeria.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.mensajeria.repository.MensajeDestinatarioRepository;
import cl.duoc.mpaz.mensajeria.repository.MensajeRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MensajeService {

    public static final String BORRADOR = "Borrador";
    public static final String ENVIADA = "Enviada";

    private static final Set<String> CANALES = Set.of("Correo electrónico", "SMS", "WhatsApp");

    private final MensajeRepository mensajeRepository;
    private final MensajeDestinatarioRepository destinatarioRepository;
    private final HistorialClient historialClient;

    public MensajeService(MensajeRepository mensajeRepository,
                          MensajeDestinatarioRepository destinatarioRepository,
                          HistorialClient historialClient) {
        this.mensajeRepository = mensajeRepository;
        this.destinatarioRepository = destinatarioRepository;
        this.historialClient = historialClient;
    }

    @Transactional
    public MensajeResponse crearBorrador(MensajeRequest solicitud) {
        Mensaje mensaje = mensajeRepository.save(Mensaje.builder()
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

    @Transactional
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
            destinatarioRepository.save(MensajeDestinatario.builder()
                    .mensajeId(mensaje.getId())
                    .apoderadoId(destinatario.apoderadoId())
                    .apoderadoNombre(destinatario.nombre())
                    .build());
            nombres.add(destinatario.nombre() == null ? "Apoderado " + destinatario.apoderadoId() : destinatario.nombre());
        }
        mensaje.setCanal(solicitud.canal());
        mensaje.setEstado(ENVIADA);
        mensaje.setFecha(LocalDateTime.now());
        Mensaje guardado = mensajeRepository.save(mensaje);

        historialClient.registrar(new RegistroHistoricoRequest(
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

    @Transactional(readOnly = true)
    public MensajeResponse obtener(Long id) {
        Mensaje mensaje = buscar(id);
        return MensajeResponse.desde(mensaje, destinatarioRepository.countByMensajeId(id));
    }

    @Transactional(readOnly = true)
    public List<MensajeResponse> listarPorRemitente(Long remitenteId) {
        return mensajeRepository.findByRemitenteIdOrderByIdDesc(remitenteId).stream()
                .map(mensaje -> MensajeResponse.desde(mensaje, destinatarioRepository.countByMensajeId(mensaje.getId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public long contarBorradores(Long remitenteId) {
        return listarPorRemitente(remitenteId).stream()
                .filter(mensaje -> BORRADOR.equals(mensaje.estado()))
                .count();
    }

    private Mensaje buscar(Long id) {
        return mensajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la comunicación con id " + id));
    }
}
