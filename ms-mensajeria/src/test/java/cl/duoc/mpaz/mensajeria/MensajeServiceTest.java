package cl.duoc.mpaz.mensajeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.duoc.mpaz.mensajeria.client.HistorialClient;
import cl.duoc.mpaz.mensajeria.dto.DestinatarioRequest;
import cl.duoc.mpaz.mensajeria.dto.EnvioRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeRequest;
import cl.duoc.mpaz.mensajeria.dto.MensajeResponse;
import cl.duoc.mpaz.mensajeria.entities.Mensaje;
import cl.duoc.mpaz.mensajeria.exceptions.SolicitudInvalidaException;
import cl.duoc.mpaz.mensajeria.repository.MensajeDestinatarioRepository;
import cl.duoc.mpaz.mensajeria.repository.MensajeRepository;
import cl.duoc.mpaz.mensajeria.services.MensajeService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MensajeServiceTest {

    @Mock
    private MensajeRepository mensajeRepository;

    @Mock
    private MensajeDestinatarioRepository destinatarioRepository;

    @Mock
    private HistorialClient historialClient;

    @InjectMocks
    private MensajeService mensajeService;

    private Mensaje borrador() {
        return Mensaje.builder()
                .id(10L)
                .titulo("Citación")
                .cuerpo("Se cita a reunión")
                .tipo("Citación")
                .estado(MensajeService.BORRADOR)
                .remitenteId(1L)
                .remitenteNombre("Andrea Muñoz")
                .build();
    }

    @Test
    void crearUnBorradorLoGuardaSinFechaNiCanal() {
        MensajeRequest solicitud = new MensajeRequest("Citación", "Se cita a reunión", "Citación", 1L, "Andrea Muñoz", null);
        when(mensajeRepository.save(any(Mensaje.class))).thenAnswer(invocacion -> {
            Mensaje mensaje = invocacion.getArgument(0);
            mensaje.setId(10L);
            return mensaje;
        });

        MensajeResponse respuesta = mensajeService.crearBorrador(solicitud);

        assertEquals(MensajeService.BORRADOR, respuesta.estado());
        assertEquals(null, respuesta.fecha());
        assertEquals(0, respuesta.cantidadDestinatarios());
    }

    @Test
    void noSePuedeEnviarSinDestinatarios() {
        when(mensajeRepository.findById(10L)).thenReturn(Optional.of(borrador()));

        assertThrows(SolicitudInvalidaException.class,
                () -> mensajeService.enviar(10L, new EnvioRequest("SMS", List.of())));
        verify(mensajeRepository, never()).save(any(Mensaje.class));
    }

    @Test
    void noSePuedeEnviarConUnCanalQueNoExiste() {
        when(mensajeRepository.findById(10L)).thenReturn(Optional.of(borrador()));

        assertThrows(SolicitudInvalidaException.class,
                () -> mensajeService.enviar(10L, new EnvioRequest("Paloma mensajera",
                        List.of(new DestinatarioRequest(1L, "Rocío Alarcón Vega")))));
    }

    @Test
    void enviarCambiaElEstadoYRegistraEnElHistorial() {
        when(mensajeRepository.findById(10L)).thenReturn(Optional.of(borrador()));
        when(mensajeRepository.save(any(Mensaje.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        MensajeResponse respuesta = mensajeService.enviar(10L, new EnvioRequest("WhatsApp",
                List.of(new DestinatarioRequest(1L, "Rocío Alarcón Vega"),
                        new DestinatarioRequest(2L, "Marcelo Bustos Pinto"))));

        assertEquals(MensajeService.ENVIADA, respuesta.estado());
        assertNotNull(respuesta.fecha());
        assertEquals(2, respuesta.cantidadDestinatarios());
        verify(historialClient).registrar(any());
    }
}
