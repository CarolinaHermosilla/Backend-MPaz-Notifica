package cl.duoc.mpaz.historial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import cl.duoc.mpaz.historial.dto.RegistroRequest;
import cl.duoc.mpaz.historial.dto.RegistroResponse;
import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import cl.duoc.mpaz.historial.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.historial.repository.HistorialRepository;
import cl.duoc.mpaz.historial.services.HistorialService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HistorialServiceTest {

    @Mock
    private HistorialRepository historialRepository;

    @InjectMocks
    private HistorialService historialService;

    @Test
    void registrarUnEnvioGuardaCantidadYDestinatarios() {
        when(historialRepository.save(any(RegistroHistorico.class))).thenAnswer(invocacion -> {
            RegistroHistorico registro = invocacion.getArgument(0);
            registro.setId(1L);
            return registro;
        });

        RegistroResponse respuesta = historialService.registrar(new RegistroRequest(
                10L, "Citación", "Citación", "SMS", LocalDateTime.now(), 1L, "Andrea Muñoz",
                List.of("Rocío Alarcón Vega", "Marcelo Bustos Pinto")));

        assertEquals(2, respuesta.cantidadDestinatarios());
        assertEquals("SMS", respuesta.canal());
        assertFalse(respuesta.destinatarios().isEmpty());
    }

    @Test
    void listarDevuelveLosRegistros() {
        RegistroHistorico registro = RegistroHistorico.builder()
                .id(2L)
                .mensajeId(11L)
                .titulo("Reunión de apoderados")
                .tipo("Citación")
                .canal("WhatsApp")
                .fecha(LocalDateTime.now())
                .remitenteId(1L)
                .remitenteNombre("Andrea Muñoz")
                .cantidadDestinatarios(3)
                .destinatarios("Rocío Alarcón Vega|Marcelo Bustos Pinto|Paula Cárdenas Muñoz")
                .build();
        when(historialRepository.findAllByOrderByFechaDesc()).thenReturn(List.of(registro));

        List<RegistroResponse> registros = historialService.listar();

        assertEquals(1, registros.size());
        assertEquals(3, registros.get(0).destinatarios().size());
    }

    @Test
    void buscarUnRegistroQueNoExisteFalla() {
        when(historialRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RecursoNoEncontradoException.class, () -> historialService.obtener(99L));
    }
}
