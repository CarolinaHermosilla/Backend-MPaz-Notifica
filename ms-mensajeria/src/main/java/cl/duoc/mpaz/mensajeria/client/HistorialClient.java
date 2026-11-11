package cl.duoc.mpaz.mensajeria.client;

import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class HistorialClient implements RegistradorHistorial {

    private static final Logger registro = LoggerFactory.getLogger(HistorialClient.class);
    private static final int INTENTOS = 2;

    private final RestClient restClient;
    private final String urlHistorial;

    public HistorialClient(RestClient.Builder constructor,
                           @Value("${historial.url:https://mpaz-historial.onrender.com}") String urlHistorial) {
        this.urlHistorial = urlHistorial.startsWith("http") ? urlHistorial : "https://" + urlHistorial;

        ClientHttpRequestFactorySettings ajustes = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(15))
                .withReadTimeout(Duration.ofSeconds(60));

        this.restClient = constructor
                .baseUrl(this.urlHistorial)
                .requestFactory(ClientHttpRequestFactories.get(ajustes))
                .build();

        registro.info("Los envios se registraran en el historial en {}", this.urlHistorial);
    }

    @Override
    public void registrar(RegistroHistoricoRequest solicitud) {
        for (int intento = 1; intento <= INTENTOS; intento++) {
            try {
                restClient.post()
                        .uri("/api/historial")
                        .body(solicitud)
                        .retrieve()
                        .toBodilessEntity();
                registro.info("Envio registrado en el historial: {} (mensaje {})",
                        solicitud.titulo(), solicitud.mensajeId());
                return;
            } catch (Exception error) {
                registro.warn("No se pudo registrar en el historial en {} (intento {} de {}): {}",
                        urlHistorial, intento, INTENTOS, error.toString());
                if (intento < INTENTOS) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException interrumpido) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        }
        registro.error("El envio quedo guardado, pero no se pudo registrar en el historial. "
                + "Revisar HISTORIAL_URL={}", urlHistorial);
    }
}
