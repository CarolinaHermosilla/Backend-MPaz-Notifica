package cl.duoc.mpaz.mensajeria.client;

import cl.duoc.mpaz.mensajeria.dto.RegistroHistoricoRequest;

public interface RegistradorHistorial {

    void registrar(RegistroHistoricoRequest solicitud);
}
