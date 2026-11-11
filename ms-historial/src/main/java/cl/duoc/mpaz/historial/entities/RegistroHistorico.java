package cl.duoc.mpaz.historial.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "historial")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistroHistorico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mensajeId;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(nullable = false, length = 40)
    private String canal;

    @Column(nullable = false)
    private LocalDateTime fecha;

    @Column(nullable = false)
    private Long remitenteId;

    @Column(nullable = false, length = 120)
    private String remitenteNombre;

    @Column(nullable = false)
    private int cantidadDestinatarios;

    @Column(length = 8000)
    private String destinatarios;
}
