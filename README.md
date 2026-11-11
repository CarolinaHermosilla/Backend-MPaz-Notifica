# Backend MPAZ Notifica

Microservicios en **Spring Boot** para la aplicación móvil MPAZ Notifica (Escuela Escritora Marcela Paz).
Proyecto de **DSY1105 — Desarrollo de Aplicaciones Móviles**, Duoc UC, Experiencia 3.

La aplicación Android consume estos servicios con **Retrofit**. La autenticación se mantiene en SQLite
en el dispositivo, como indica la rúbrica: el teléfono sincroniza las cuentas desde `ms-usuarios` y valida
el inicio de sesión localmente. Las cuentas **solo pueden originarse en el servidor**, por lo que una
persona no puede crear su propia cuenta ni asignarse un rol.

## Servicios

| Servicio | Puerto | Responsabilidad | Datos |
|---|---|---|---|
| `ms-usuarios` | 8081 | Cuentas de profesores e inspectoría, apoderados y grupos | Postgres (esquema `usuarios`) |
| `ms-mensajeria` | 8082 | Borrador y envío de comunicaciones, con canal y destinatarios | Postgres (esquema `mensajeria`) |
| `ms-historial` | 8083 | Registro de cada envío para la trazabilidad del colegio | Postgres (esquema `historial`) |

`ms-mensajeria` avisa a `ms-historial` (POST /api/historial) cuando una comunicación se envía. Si el
historial no responde, el envío no se pierde: se registra el aviso en el log y la comunicación queda
enviada igual.

## Endpoints

### ms-usuarios

| Método | Ruta | Para qué |
|---|---|---|
| GET | `/api/usuarios` | Listar las cuentas (la app las sincroniza) |
| GET | `/api/usuarios/{id}` | Ver una cuenta |
| POST | `/api/usuarios` | Crear una cuenta (solo desde la administración) |
| PUT | `/api/usuarios/{id}/rol?rol=` | Cambiar el rol (`PROFESOR_JEFE` o `INSPECTORIA`) |
| PUT | `/api/usuarios/{id}/estado?activo=` | Activar o desactivar una cuenta |
| GET | `/api/apoderados` | Directorio de apoderados del curso |
| GET | `/api/grupos` | Grupos con sus integrantes |
| POST | `/api/grupos` | Crear un grupo con sus integrantes |
| DELETE | `/api/grupos/{id}` | Eliminar un grupo |

### ms-mensajeria

| Método | Ruta | Para qué |
|---|---|---|
| POST | `/api/mensajes` | Crear el borrador de la comunicación |
| PUT | `/api/mensajes/{id}/enviar` | Enviar: canal + destinatarios |
| GET | `/api/mensajes/{id}` | Ver una comunicación |
| GET | `/api/mensajes?remitenteId=` | Comunicaciones de un usuario |

### ms-historial

| Método | Ruta | Para qué |
|---|---|---|
| GET | `/api/historial` | Listar los envíos, del más reciente al más antiguo |
| GET | `/api/historial/{id}` | Ver un registro |
| POST | `/api/historial` | Registrar un envío (lo llama `ms-mensajeria`) |

## Requisitos

- **JDK 17 o superior** (probado con JDK 24).
- No hace falta instalar Maven: cada servicio trae el **Maven wrapper** (`mvnw` / `mvnw.cmd`).
- No hace falta Docker para desarrollar: el perfil `dev` usa **H2** en memoria.
- Postgres solo se usa en la nube (perfil `prod`).

## Cómo se ejecuta en desarrollo

En Windows, desde la carpeta de cada servicio:

```powershell
cd ms-usuarios
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
cd ms-usuarios
./mvnw spring-boot:run
```

Repetir en `ms-mensajeria` y `ms-historial` (en tres terminales distintas). Quedan escuchando en
`http://localhost:8081`, `8082` y `8083`. El estado de cada uno se revisa en `/actuator/health`.

Los datos iniciales se cargan solos la primera vez: las cuentas de prueba
(`profesor.jefe@ejemplo.cl` y `inspector.general@ejemplo.cl`, ambas con clave `123456`), los 8 apoderados
de 5.º Básico B y los grupos Taller de Ajedrez, Taller de Teatro y Directiva del curso.

## Pruebas unitarias

Cada servicio tiene sus pruebas con **JUnit 5 y Mockito**:

```powershell
cd ms-usuarios
.\mvnw.cmd test
```

| Servicio | Pruebas |
|---|---|
| `ms-usuarios` | 9 |
| `ms-mensajeria` | 5 |
| `ms-historial` | 4 |

## Perfiles

- **dev** (por defecto): H2 en memoria, `ddl-auto=create-drop`, sin configuración extra.
- **prod**: Postgres. Se configura con variables de entorno:

| Variable | Ejemplo |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DB_URL` | `jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres?sslmode=require` |
| `DB_USER` | usuario de la base |
| `DB_PASSWORD` | contraseña de la base |
| `HISTORIAL_URL` | `https://ms-historial.onrender.com` (solo en `ms-mensajeria`) |
| `PORT` | lo asigna la plataforma, el servicio lo respeta |

## Docker

Cada servicio trae su `Dockerfile` (compilación en dos etapas: Maven y luego un Java liviano) y en la
raíz está el `docker-compose.yml` con Postgres y los tres servicios, tal como se vio en la práctica
de la asignatura. Para levantarlo en un equipo que tenga Docker:

```bash
docker compose up -d --build
```

## Despliegue en la nube

1. Crear la base de datos en **Supabase** (Postgres) y copiar la cadena de conexión.
2. En **Render**, crear un *Web Service* por cada servicio apuntando a su carpeta
   (`ms-usuarios`, `ms-mensajeria`, `ms-historial`) con el `Dockerfile` incluido.
3. Cargar las variables de entorno de la tabla anterior. En `ms-mensajeria`, `HISTORIAL_URL` debe ser
   la URL pública de `ms-historial`.
4. Verificar `/actuator/health` de cada servicio antes de probar la aplicación.

## Estructura

```
mpaz-backend/
├── docker-compose.yml          Postgres + los tres servicios
├── sql/01_esquemas.sql         Esquemas de la base
├── ms-usuarios/                Cuentas, apoderados y grupos
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/cl/duoc/mpaz/usuarios/
│       ├── controller/         Endpoints REST
│       ├── dto/                Objetos de entrada y salida
│       ├── entities/           Tablas (JPA)
│       ├── exceptions/         Manejo de errores
│       ├── repository/         Acceso a datos
│       └── services/           Reglas de negocio
├── ms-mensajeria/              Comunicaciones y envío
└── ms-historial/               Historial de envíos
```

## Equipo

- Carolina Hermosilla
- Pedro Toledo

Los datos de la aplicación son ficticios y solo se usan con fines académicos.
