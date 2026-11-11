package cl.duoc.mpaz.mensajeria.entities;

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
@Table(name = "mensajes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Lob
    @Column(nullable = false)
    private String cuerpo;

    @Column(nullable = false, length = 60)
    private String tipo;

    @Column(length = 40)
    private String canal;

    @Column(nullable = false, length = 20)
    private String estado;

    private LocalDateTime fecha;

    @Column(nullable = false)
    private Long remitenteId;

    @Column(nullable = false, length = 120)
    private String remitenteNombre;

    @Column(length = 300)
    private String adjuntos;
}
