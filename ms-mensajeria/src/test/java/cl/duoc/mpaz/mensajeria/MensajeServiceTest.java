package cl.duoc.mpaz.mensajeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.mpaz.mensajeria.client.RegistradorHistorial;
import cl.duoc.mpaz.mensajeria.dto.DestinatarioRequest;
import cl.duoc.mpaz.mensajeria.dto.EnvioRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeResponse;
import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;
import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.entities.MensajeDestinatario;
import cl.duoc.mpaz.mensajeria.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.mensajeria.repository.RepositorioMensajes;
import cl.duoc.mpaz.mensajeria.services.MensajeService;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MensajeServiceTest {

    private RepositorioMensajesEnMemoria repositorio;
    private RegistradorHistorialEnMemoria registrador;
    private MensajeService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioMensajesEnMemoria();
        registrador = new RegistradorHistorialEnMemoria();
        servicio = new MensajeService(repositorio, registrador);
    }

    private MensajeResponse crearBorrador() {
        return servicio.crearBorrador(new MensajeRequest(
                "Citación para reunión", "Se cita a reunión el viernes.", "Citación", 1L, "Andrea Muñoz", null));
    }

    @Test
    @DisplayName("El borrador se guarda sin fecha ni canal")
    void borradorSinFechaNiCanal() {
        MensajeResponse respuesta = crearBorrador();

        assertEquals(MensajeService.BORRADOR, respuesta.estado());
        assertNull(respuesta.fecha());
        assertNull(respuesta.canal());
        assertEquals(1, repositorio.datos.size());
    }

    @Test
    @DisplayName("No se puede enviar sin destinatarios")
    void enviarSinDestinatariosFalla() {
        MensajeResponse borrador = crearBorrador();

        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.enviar(borrador.id(), new EnvioRequest("SMS", List.of())));
        assertTrue(registrador.registros.isEmpty());
    }

    @Test
    @DisplayName("No se puede enviar por un canal que no existe")
    void canalInvalidoFalla() {
        MensajeResponse borrador = crearBorrador();

        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.enviar(borrador.id(), new EnvioRequest("Paloma mensajera",
                        List.of(new DestinatarioRequest(1L, "Rocío Alarcón Vega")))));
    }

    @Test
    @DisplayName("Enviar cambia el estado, guarda la fecha y avisa al historial")
    void enviarCambiaEstadoYAvisaAlHistorial() {
        MensajeResponse borrador = crearBorrador();

        MensajeResponse enviado = servicio.enviar(borrador.id(), new EnvioRequest("WhatsApp",
                List.of(new DestinatarioRequest(1L, "Rocío Alarcón Vega"),
                        new DestinatarioRequest(2L, "Marcelo Bustos Pinto"))));

        assertEquals(MensajeService.ENVIADA, enviado.estado());
        assertNotNull(enviado.fecha());
        assertEquals(2, enviado.cantidadDestinatarios());
        assertEquals(1, registrador.registros.size());
        assertEquals(2, registrador.registros.get(0).destinatarios().size());
    }

    @Test
    @DisplayName("Una comunicación enviada no se puede enviar dos veces")
    void noSeEnviaDosVeces() {
        MensajeResponse borrador = crearBorrador();
        servicio.enviar(borrador.id(), new EnvioRequest("SMS",
                List.of(new DestinatarioRequest(1L, "Rocío Alarcón Vega"))));

        assertThrows(SolicitudInvalidaException.class,
                () -> servicio.enviar(borrador.id(), new EnvioRequest("SMS",
                        List.of(new DestinatarioRequest(2L, "Marcelo Bustos Pinto")))));
        assertEquals(1, registrador.registros.size());
    }

    private static class RepositorioMensajesEnMemoria implements RepositorioMensajes {

        private final Map<Long, Mensaje> datos = new LinkedHashMap<>();
        private final List<MensajeDestinatario> destinatarios = new ArrayList<>();
        private long siguienteId = 1;

        @Override
        public Optional<Mensaje> buscarPorId(Long id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Mensaje guardar(Mensaje mensaje) {
            if (mensaje.getId() == null) {
                mensaje.setId(siguienteId++);
            }
            datos.put(mensaje.getId(), mensaje);
            return mensaje;
        }

        @Override
        public List<Mensaje> listarPorRemitente(Long remitenteId) {
            return datos.values().stream().filter(mensaje -> mensaje.getRemitenteId().equals(remitenteId)).toList();
        }

        @Override
        public long contarDestinatarios(Long mensajeId) {
            return destinatarios.stream().filter(uno -> uno.getMensajeId().equals(mensajeId)).count();
        }

        @Override
        public MensajeDestinatario guardarDestinatario(MensajeDestinatario destinatario) {
            destinatarios.add(destinatario);
            return destinatario;
        }
    }

    private static class RegistradorHistorialEnMemoria implements RegistradorHistorial {

        private final List<RegistroHistoricoRequest> registros = new ArrayList<>();

        @Override
        public void registrar(RegistroHistoricoRequest solicitud) {
            registros.add(solicitud);
        }
    }
}
