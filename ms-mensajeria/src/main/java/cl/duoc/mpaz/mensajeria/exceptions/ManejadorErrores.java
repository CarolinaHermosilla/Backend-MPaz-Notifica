package cl.duoc.mpaz.mensajeria.exceptions;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(SolicitudInvalidaException.class)
    public ResponseEntity<Map<String, Object>> invalida(SolicitudInvalidaException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Los datos enviados no son validos");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo(HttpStatus.BAD_REQUEST, mensaje));
    }

    private Map<String, Object> cuerpo(HttpStatus estado, String mensaje) {
        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("fecha", LocalDateTime.now().toString());
        respuesta.put("estado", estado.value());
        respuesta.put("mensaje", mensaje);
        return respuesta;
    }
}
