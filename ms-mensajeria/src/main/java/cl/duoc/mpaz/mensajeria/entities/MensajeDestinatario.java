package cl.duoc.mpaz.mensajeria.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "mensaje_destinatarios")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeDestinatario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long mensajeId;

    @Column(nullable = false)
    private Long apoderadoId;

    @Column(length = 120)
    private String apoderadoNombre;
}
