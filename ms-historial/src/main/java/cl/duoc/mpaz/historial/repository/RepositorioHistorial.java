package cl.duoc.mpaz.historial.repository;

import cl.duoc.mpaz.historial.entities.RegistroHistorico;
import java.util.List;
import java.util.Optional;

public interface RepositorioHistorial {

    List<RegistroHistorico> listarOrdenados();

    Optional<RegistroHistorico> buscarPorId(Long id);

    RegistroHistorico guardar(RegistroHistorico registro);
}
