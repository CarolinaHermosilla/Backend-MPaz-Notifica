package cl.duoc.mpaz.historial;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cl.duoc.mpaz.historial.dto.RegistroRequest;
import cl.duoc.mpaz.historial.dto.RegistroResponse;
import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import cl.duoc.mpaz.historial.exceptions.RecursoNoEncontradoException;
import cl.duoc.mpaz.historial.repository.RepositorioHistorial;
import cl.duoc.mpaz.historial.services.HistorialService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HistorialServiceTest {

    private RepositorioHistorialEnMemoria repositorio;
    private HistorialService servicio;

    @BeforeEach
    void preparar() {
        repositorio = new RepositorioHistorialEnMemoria();
        servicio = new HistorialService(repositorio);
    }

    @Test
    @DisplayName("Registrar un envío guarda la cantidad de destinatarios")
    void registrarGuardaCantidad() {
        RegistroResponse respuesta = servicio.registrar(new RegistroRequest(
                10L, "Citación", "Citación", "SMS", LocalDateTime.now(), 1L, "Andrea Muñoz",
                List.of("Rocío Alarcón Vega", "Marcelo Bustos Pinto")));

        assertEquals(2, respuesta.cantidadDestinatarios());
        assertEquals("SMS", respuesta.canal());
        assertEquals(2, respuesta.destinatarios().size());
        assertEquals(1, repositorio.listarOrdenados().size());
    }

    @Test
    @DisplayName("Si no llega el canal queda registrado como sin canal")
    void sinCanalQuedaComoValorPorDefecto() {
        RegistroResponse respuesta = servicio.registrar(new RegistroRequest(
                11L, "Aviso", null, null, null, null, null, List.of()));

        assertEquals("Sin canal", respuesta.canal());
        assertEquals("Sin tipo", respuesta.tipo());
        assertEquals("Sin remitente", respuesta.remitenteNombre());
        assertEquals(0, respuesta.cantidadDestinatarios());
    }

    @Test
    @DisplayName("El listado trae los registros del más nuevo al más antiguo")
    void listarOrdenadoPorFecha() {
        servicio.registrar(new RegistroRequest(1L, "Primera", "Aviso", "SMS",
                LocalDateTime.of(2026, 11, 1, 9, 0), 1L, "Andrea", List.of("Uno")));
        servicio.registrar(new RegistroRequest(2L, "Segunda", "Aviso", "WhatsApp",
                LocalDateTime.of(2026, 11, 5, 9, 0), 1L, "Andrea", List.of("Dos")));

        List<RegistroResponse> registros = servicio.listar(null);

        assertEquals(2, registros.size());
        assertEquals("Segunda", registros.get(0).titulo());
    }

    @Test
    @DisplayName("Si se pide por remitente, solo devuelve sus envíos")
    void listarPorRemitenteFiltra() {
        servicio.registrar(new RegistroRequest(1L, "De Andrea", "Aviso", "SMS",
                LocalDateTime.of(2026, 11, 1, 9, 0), 1L, "Andrea", List.of("Uno")));
        servicio.registrar(new RegistroRequest(2L, "De Mario", "Aviso", "SMS",
                LocalDateTime.of(2026, 11, 2, 9, 0), 2L, "Mario", List.of("Dos")));

        List<RegistroResponse> soloAndrea = servicio.listar(1L);

        assertEquals(1, soloAndrea.size());
        assertEquals("De Andrea", soloAndrea.get(0).titulo());
    }

    @Test
    @DisplayName("Un mismo envío no se registra dos veces")
    void registrarDosVecesElMismoEnvioNoDuplica() {
        RegistroRequest solicitud = new RegistroRequest(10L, "Citación", "Citación", "SMS",
                LocalDateTime.now(), 1L, "Andrea", List.of("Rocío Alarcón Vega"));

        servicio.registrar(solicitud);
        servicio.registrar(solicitud);

        assertEquals(1, repositorio.listarOrdenados().size());
    }

    @Test
    @DisplayName("Buscar un registro que no existe lanza error")
    void registroInexistenteFalla() {
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.obtener(99L));
    }

    private static class RepositorioHistorialEnMemoria implements RepositorioHistorial {

        private final Map<Long, RegistroHistorico> datos = new LinkedHashMap<>();
        private long siguienteId = 1;

        @Override
        public List<RegistroHistorico> listarOrdenados() {
            return datos.values().stream()
                    .sorted(Comparator.comparing(RegistroHistorico::getFecha).reversed())
                    .toList();
        }

        @Override
        public List<RegistroHistorico> listarPorRemitente(Long remitenteId) {
            return listarOrdenados().stream()
                    .filter(registro -> registro.getRemitenteId().equals(remitenteId))
                    .toList();
        }

        @Override
        public Optional<RegistroHistorico> buscarPorId(Long id) {
            return Optional.ofNullable(datos.get(id));
        }

        @Override
        public Optional<RegistroHistorico> buscarPorMensajeId(Long mensajeId) {
            return datos.values().stream()
                    .filter(registro -> registro.getMensajeId().equals(mensajeId))
                    .findFirst();
        }

        @Override
        public RegistroHistorico guardar(RegistroHistorico registro) {
            if (registro.getId() == null) {
                registro.setId(siguienteId++);
            }
            datos.put(registro.getId(), registro);
            return registro;
        }
    }
}
