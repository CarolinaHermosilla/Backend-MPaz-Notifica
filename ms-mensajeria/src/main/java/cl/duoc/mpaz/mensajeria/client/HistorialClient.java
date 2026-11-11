package cl.duoc.mpaz.mensajeria.client;

import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HistorialClient implements RegistradorHistorial {

    private static final Logger registro = LoggerFactory.getLogger(HistorialClient.class);

    private final RestClient restClient;

    public HistorialClient(RestClient.Builder constructor, @Value("${historial.url}") String urlHistorial) {
        String base = urlHistorial.startsWith("http") ? urlHistorial : "https://" + urlHistorial;
        this.restClient = constructor.baseUrl(base).build();
    }

    @Override
    public void registrar(RegistroHistoricoRequest solicitud) {
        try {
            restClient.post()
                    .uri("/api/historial")
                    .body(solicitud)
                    .retrieve()
                    .toBodilessEntity();
            registro.info("Envio registrado en el historial: {}", solicitud.titulo());
        } catch (Exception error) {
            registro.warn("No se pudo registrar en el historial: {}", error.getMessage());
        }
    }
}
