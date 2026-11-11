package cl.duoc.mpaz.usuarios.entities;

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
@Table(name = "grupo_integrantes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrupoIntegrante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long grupoId;

    @Column(nullable = false)
    private Long apoderadoId;
}
