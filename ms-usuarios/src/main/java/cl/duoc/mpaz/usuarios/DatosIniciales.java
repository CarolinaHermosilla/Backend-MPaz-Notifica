package cl.duoc.mpaz.usuarios;

import cl.duoc.mpaz.usuarios.entities.Apoderado;
import cl.duoc.mpaz.usuarios.entities.Grupo;
import cl.duoc.mpaz.usuarios.entities.GrupoIntegrante;
import cl.duoc.mpaz.usuarios.entities.Usuario;
import cl.duoc.mpaz.usuarios.repository.ApoderadoRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoIntegranteRepository;
import cl.duoc.mpaz.usuarios.repository.GrupoRepository;
import cl.duoc.mpaz.usuarios.repository.UsuarioRepository;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatosIniciales implements CommandLineRunner {

    private static final String CURSO = "5.º Básico B";

    private final UsuarioRepository usuarioRepository;
    private final ApoderadoRepository apoderadoRepository;
    private final GrupoRepository grupoRepository;
    private final GrupoIntegranteRepository grupoIntegranteRepository;

    public DatosIniciales(UsuarioRepository usuarioRepository,
                          ApoderadoRepository apoderadoRepository,
                          GrupoRepository grupoRepository,
                          GrupoIntegranteRepository grupoIntegranteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.apoderadoRepository = apoderadoRepository;
        this.grupoRepository = grupoRepository;
        this.grupoIntegranteRepository = grupoIntegranteRepository;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            usuarioRepository.saveAll(List.of(
                    Usuario.builder()
                            .nombre("Andrea Muñoz")
                            .correo("profesor.jefe@ejemplo.cl")
                            .contrasena("123456")
                            .rol("PROFESOR_JEFE")
                            .curso(CURSO)
                            .activo(true)
                            .build(),
                    Usuario.builder()
                            .nombre("Mario Gutierrez")
                            .correo("inspector.general@ejemplo.cl")
                            .contrasena("123456")
                            .rol("INSPECTORIA")
                            .curso(null)
                            .activo(true)
                            .build()));
        }

        if (apoderadoRepository.count() == 0) {
            apoderadoRepository.saveAll(List.of(
                    apoderado("Rocío Alarcón Vega", "Estudiante A. Alarcón", null),
                    apoderado("Marcelo Bustos Pinto", "Estudiante B. Bustos", "Taller de Ajedrez"),
                    apoderado("Paula Cárdenas Muñoz", "Estudiante C. Cárdenas", null),
                    apoderado("Iván Donoso Rivas", "Estudiante D. Donoso", "Taller de Teatro"),
                    apoderado("Carolina Espinoza Lagos", "Estudiante E. Espinoza", null),
                    apoderado("Héctor Fuentes Navarro", "Estudiante F. Fuentes", "Taller de Ajedrez"),
                    apoderado("Daniela Guzmán Órdenes", "Estudiante G. Guzmán", null),
                    apoderado("Rodrigo Herrera Salas", "Estudiante H. Herrera", "Taller de Teatro")));
        }

        if (grupoRepository.count() == 0) {
            Grupo ajedrez = grupoRepository.save(Grupo.builder()
                    .nombre("Taller de Ajedrez")
                    .descripcion("Apoderados de los estudiantes que participan del Taller de Ajedrez.")
                    .creadoPorTaller(true)
                    .build());
            Grupo teatro = grupoRepository.save(Grupo.builder()
                    .nombre("Taller de Teatro")
                    .descripcion("Apoderados de los estudiantes que participan del Taller de Teatro.")
                    .creadoPorTaller(true)
                    .build());
            Grupo directiva = grupoRepository.save(Grupo.builder()
                    .nombre("Directiva del curso")
                    .descripcion("Apoderados que integran la directiva del curso.")
                    .creadoPorTaller(false)
                    .build());

            unir(ajedrez, "Marcelo Bustos Pinto", "Héctor Fuentes Navarro");
            unir(teatro, "Iván Donoso Rivas", "Rodrigo Herrera Salas");
            unir(directiva, "Carolina Espinoza Lagos", "Paula Cárdenas Muñoz", "Rocío Alarcón Vega");
        }
    }

    private Apoderado apoderado(String nombre, String estudiante, String taller) {
        return Apoderado.builder()
                .nombre(nombre)
                .estudiante(estudiante)
                .curso(CURSO)
                .taller(taller)
                .build();
    }

    private void unir(Grupo grupo, String... nombres) {
        for (String nombre : nombres) {
            apoderadoRepository.findAll().stream()
                    .filter(apoderado -> apoderado.getNombre().equals(nombre))
                    .findFirst()
                    .ifPresent(apoderado -> grupoIntegranteRepository.save(GrupoIntegrante.builder()
                            .grupoId(grupo.getId())
                            .apoderadoId(apoderado.getId())
                            .build()));
        }
    }
}
