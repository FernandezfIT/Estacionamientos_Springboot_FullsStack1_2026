# Manual de estandarización de microservicios

Proyecto: `Estacionamientos_Springboot_FullsStack1_2026`

Este documento define las reglas mínimas que debe seguir cada integrante del equipo al trabajar en los microservicios del proyecto.

El objetivo es evitar que cada servicio termine con nombres, puertos, paquetes, rutas, migraciones y estilos distintos. Eso al principio parece libertad. Después parece incendio.

---

## 1. Microservicios oficiales del proyecto

Los microservicios del proyecto son:

| Microservicio | Carpeta | Puerto | Base de datos | Tabla principal | Endpoint base |
|---|---:|---:|---|---|---|
| Auth | `auth_service` | `8080` | `auth_db` | N/A | `/api/v1/auth` |
| Usuario | `usuario_service` | `8081` | `usuario_db` | `usuario` | `/api/v1/usuarios` |
| Vehículo | `vehiculo_service` | `8082` | `vehiculo_db` | `vehiculo` | `/api/v1/vehiculos` |
| Plaza | `plaza_service` | `8083` | `plaza_db` | `plaza` | `/api/v1/plazas` |
| Reserva | `reserva_service` | `8084` | `reserva_db` | `reserva` | `/api/v1/reservas` |
| Acceso | `acceso_service` | `8085` | `acceso_db` | `acceso` | `/api/v1/accesos` |
| Movimiento | `movimiento_service` | `8086` | `movimiento_db` | `movimiento` | `/api/v1/movimientos` |
| Liberación | `liberacion_service` | `8087` | `liberacion_db` | `liberacion` | `/api/v1/liberaciones` |
| Cierre | `cierre_service` | `8088` | `cierre_db` | `cierre` | `/api/v1/cierres` |
| Reporte | `reporte_service` | `8089` | `reporte_db` | `reporte` | `/api/v1/reportes` |

Regla: cada microservicio usa solo su propia base de datos.

Ejemplo correcto:

```txt
usuario_service -> usuario_db
vehiculo_service -> vehiculo_db
reserva_service -> reserva_db
```

Ejemplo incorrecto:

```txt
reserva_service consultando directamente tablas de usuario_db
```

Si un microservicio necesita información de otro, debe hacerlo por API HTTP, no entrando directamente a su base de datos.

---

## 2. Regla para bases de datos y tablas

El archivo:

```txt
docker/mysql/init/01-create-database.sql
```

solo debe crear:

```txt
bases de datos
usuarios de MySQL
permisos
```

No debe crear tablas del negocio.

Las tablas deben ser creadas por cada microservicio usando Flyway:

```txt
src/main/resources/db/migration/
```

Ejemplo:

```txt
usuario_service
└── src/main/resources/db/migration/
    └── V1__crear_tabla_usuario.sql
```

---

## 3. Configuración estándar de `application-dev.properties`

Cada microservicio debe tener un archivo:

```txt
src/main/resources/application-dev.properties
```

Formato base:

```properties
server.port=PUERTO_DEL_SERVICIO

spring.datasource.url=jdbc:mysql://localhost:3306/NOMBRE_DB?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

Si el equipo cambia el puerto externo de MySQL en `.env`, por ejemplo a `3307`, debe actualizar también las URLs de conexión:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/NOMBRE_DB?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
```

---

## 4. Configuración por microservicio

### `auth_service`

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://localhost:3306/auth_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

El servicio `auth_service` no tiene modelo principal definido por ahora. Puede usar DTOs como `LoginRequest`, `RegisterRequest` y `AuthResponse`.

---

### `usuario_service`

```properties
server.port=8081

spring.datasource.url=jdbc:mysql://localhost:3306/usuario_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `vehiculo_service`

```properties
server.port=8082

spring.datasource.url=jdbc:mysql://localhost:3306/vehiculo_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `plaza_service`

```properties
server.port=8083

spring.datasource.url=jdbc:mysql://localhost:3306/plaza_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `reserva_service`

```properties
server.port=8084

spring.datasource.url=jdbc:mysql://localhost:3306/reserva_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `acceso_service`

```properties
server.port=8085

spring.datasource.url=jdbc:mysql://localhost:3306/acceso_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `movimiento_service`

```properties
server.port=8086

spring.datasource.url=jdbc:mysql://localhost:3306/movimiento_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `liberacion_service`

```properties
server.port=8087

spring.datasource.url=jdbc:mysql://localhost:3306/liberacion_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `cierre_service`

```properties
server.port=8088

spring.datasource.url=jdbc:mysql://localhost:3306/cierre_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

### `reporte_service`

```properties
server.port=8089

spring.datasource.url=jdbc:mysql://localhost:3306/reporte_db?useSSL=false&serverTimezone=America/Santiago&allowPublicKeyRetrieval=true
spring.datasource.username=dev_user
spring.datasource.password=dev123

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
```

---

## 5. Dependencias estándar

Todos los microservicios deben mantener estas dependencias base:

| Dependencia | Uso |
|---|---|
| Spring Web / WebMVC | Crear controladores REST |
| Spring Data JPA | Persistencia con repositorios |
| MySQL Driver | Conexión con MySQL |
| Flyway Migration | Crear y versionar tablas |
| Flyway MySQL | Soporte de Flyway para MySQL |
| Validation | Validaciones con anotaciones |
| Lombok | Reducir código repetitivo |
| Spring Security | Seguridad y configuración de autenticación |
| DevTools | Recarga y apoyo en desarrollo |
| Test | Pruebas unitarias e integración |

Si un microservicio no usa todavía alguna dependencia, se mantiene igual por estandarización del proyecto.

---

## 6. Estructura interna estándar de cada microservicio

Cada microservicio debe tener esta estructura base:

```txt
src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/NOMBRE_SERVICIO/
│
├── controller/
├── dto/
│   ├── request/
│   └── response/
├── exception/
├── model/
├── repository/
└── service/
```

Ejemplo para `usuario_service`:

```txt
src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/usuario_service/
│
├── controller/
│   └── UsuarioController.java
├── dto/
│   ├── request/
│   │   └── UsuarioRequest.java
│   └── response/
│       └── UsuarioResponse.java
├── exception/
├── model/
│   └── Usuario.java
├── repository/
│   └── UsuarioRepository.java
└── service/
    └── UsuarioService.java
```

---

## 7. Convención de nombres

### Carpetas

Usar `snake_case`:

```txt
usuario_service
vehiculo_service
reserva_service
```

---

### Clases Java

Usar `PascalCase`:

```java
Usuario
UsuarioController
UsuarioService
UsuarioRepository
UsuarioRequest
UsuarioResponse
```

---

### Atributos Java

Usar `camelCase`:

```java
private Long idUsuario;
private LocalDateTime fechaCreacion;
private String rutReserva;
```

---

### Columnas SQL

Usar `snake_case`:

```sql
id_usuario
fecha_creacion
rut_reserva
```

---

### Tablas SQL

Usar singular en `snake_case`:

```sql
usuario
vehiculo
plaza
reserva
movimiento
```

---

### Rutas REST

Usar plural en minúsculas:

```txt
/api/v1/usuarios
/api/v1/vehiculos
/api/v1/plazas
/api/v1/reservas
```

---

## 8. Estándar de modelos y atributos

Los nombres originales del documento están en `snake_case`. Para Java se usarán en `camelCase`.

### Usuario

| Campo SQL | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_usuario` | `idUsuario` | `Long` | PK autoincremental |
| `rut` | `rut` | `String` | Único si aplica |
| `nombre` | `nombre` | `String` | Nombre del usuario |
| `apellido` | `apellido` | `String` | Apellido del usuario |
| `email` | `email` | `String` | Único si aplica |
| `password` | `password` | `String` | En entorno real debería ir hasheada |
| `telefono` | `telefono` | `String` | Usar String para conservar `+56`, ceros, etc. |
| `fecha_creacion` | `fechaCreacion` | `LocalDateTime` | Fecha y hora de creación |

Clase:

```java
Usuario
```

Tabla:

```sql
usuario
```

---

### Vehículo

| Campo SQL | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_vehiculo` | `idVehiculo` | `Long` | PK autoincremental |
| `patente` | `patente` | `String` | Única si aplica |
| `marca` | `marca` | `String` | Marca del vehículo |
| `color` | `color` | `String` | Color principal |
| `tipo` | `tipo` | `String` | Auto, moto, camioneta, etc. |

Clase:

```java
Vehiculo
```

Tabla:

```sql
vehiculo
```

Nota: en Java evitar tilde en nombres de clases, variables y paquetes. Usar `Vehiculo`, no `Vehículo`.

---

### Plaza

| Campo SQL sugerido | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_plaza` | `idPlaza` | `Long` | PK autoincremental |
| `codigo_plaza` | `codigoPlaza` | `String` | Reemplaza el nombre ambiguo `plaza` |
| `estado` | `estado` | `String` | Disponible, ocupada, reservada, mantención |

Clase:

```java
Plaza
```

Tabla:

```sql
plaza
```

Nota: el atributo original `plaza` es ambiguo porque se llama igual que la entidad. Se recomienda usar `codigoPlaza`.

---

### Movimiento

| Campo SQL sugerido | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_movimiento` | `idMovimiento` | `Long` | PK autoincremental |
| `tipo_movimiento` | `tipoMovimiento` | `String` | Reemplaza `tipo` para evitar ambigüedad |
| `fecha_movimiento` | `fechaMovimiento` | `LocalDateTime` | Fecha y hora del movimiento |

Clase:

```java
Movimiento
```

Tabla:

```sql
movimiento
```

---

### Acceso

| Campo SQL | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_acceso` | `idAcceso` | `Long` | PK autoincremental |
| `id_vehiculo` | `idVehiculo` | `Long` | Referencia lógica al vehículo |
| `id_usuario` | `idUsuario` | `Long` | Referencia lógica al usuario |
| `id_plaza` | `idPlaza` | `Long` | Referencia lógica a la plaza |
| `fecha_ingreso` | `fechaIngreso` | `LocalDateTime` | Fecha y hora de ingreso |

Clase:

```java
Acceso
```

Tabla:

```sql
acceso
```

Nota: al estar en microservicios separados, estos IDs son referencias lógicas. No crear claves foráneas entre bases de datos distintas.

---

### Liberación

| Campo SQL sugerido | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_liberacion` | `idLiberacion` | `Long` | PK autoincremental |
| `id_plaza` | `idPlaza` | `Long` | Referencia lógica a la plaza |
| `fecha_liberacion` | `fechaLiberacion` | `LocalDateTime` | Campo recomendado para trazabilidad |

Clase:

```java
Liberacion
```

Tabla:

```sql
liberacion
```

Nota: el documento original solo incluye `id_liberacion` e `id_plaza`. Se recomienda agregar `fecha_liberacion` porque una liberación sin fecha dice poco.

---

### Reserva

| Campo SQL | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_reserva` | `idReserva` | `Long` | PK autoincremental |
| `rut_reserva` | `rutReserva` | `String` | RUT asociado a la reserva |
| `patente` | `patente` | `String` | Patente del vehículo |
| `id_plaza` | `idPlaza` | `Long` | Referencia lógica a la plaza |
| `fecha_reserva` | `fechaReserva` | `LocalDateTime` | Fecha y hora de reserva |

Clase:

```java
Reserva
```

Tabla:

```sql
reserva
```

---

### Reporte

| Campo SQL | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_reporte` | `idReporte` | `Long` | PK autoincremental |
| `fecha_reporte` | `fechaReporte` | `LocalDateTime` | Fecha y hora del reporte |

Clase:

```java
Reporte
```

Tabla:

```sql
reporte
```

---

### Cierre

| Campo SQL sugerido | Atributo Java | Tipo Java recomendado | Comentario |
|---|---|---|---|
| `id_cierre` | `idCierre` | `Long` | PK autoincremental |
| `hora_cierre` | `horaCierre` | `LocalTime` | Hora de cierre |
| `fecha_cierre` | `fechaCierre` | `LocalDate` | Campo recomendado para saber el día del cierre |

Clase:

```java
Cierre
```

Tabla:

```sql
cierre
```

Nota: el documento original solo incluye `hora_cierre`. Se recomienda agregar `fecha_cierre`; una hora sin fecha sirve poco para reportes.

---

### Auth

No tiene modelo principal definido.

DTOs recomendados:

```java
LoginRequest
RegisterRequest
AuthResponse
```

Campos sugeridos para `LoginRequest`:

```java
private String email;
private String password;
```

Campos sugeridos para `AuthResponse`:

```java
private String mensaje;
private String token;
```

Si todavía no implementan JWT, `token` puede omitirse.

---

## 9. Plantilla base de entidad JPA

Ejemplo con `Usuario`:

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "rut", nullable = false, length = 12)
    private String rut;

    @Column(name = "nombre", nullable = false, length = 80)
    private String nombre;

    @Column(name = "apellido", nullable = false, length = 80)
    private String apellido;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
```

---

## 10. Plantilla base de Repository

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
```

Regla: el repositorio siempre usa el tipo de ID de la entidad.

Si la entidad tiene:

```java
private Long idUsuario;
```

entonces el repository debe ser:

```java
JpaRepository<Usuario, Long>
```

---

## 11. Plantilla base de Controller

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

}
```

Regla: el controller no debe contener lógica de negocio. Solo recibe solicitudes, llama al service y retorna respuesta.

---

## 12. Plantilla base de Service

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.usuario_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

}
```

Regla: la lógica de negocio va en el service, no en el controller ni en el repository.

---

## 13. Convención para DTOs

Usar DTOs para request y response.

No devolver entidades directamente desde los controllers.

Ejemplo:

```txt
UsuarioRequest
UsuarioResponse
```

Estructura:

```txt
dto/
├── request/
│   └── UsuarioRequest.java
└── response/
    └── UsuarioResponse.java
```

Ejemplo de `UsuarioRequest`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequest {
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String password;
    private String telefono;
}
```

Ejemplo de `UsuarioResponse`:

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Long idUsuario;
    private String rut;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private LocalDateTime fechaCreacion;
}
```

Regla: no devolver `password` en respuestas.

---

## 14. Validaciones estándar

Usar anotaciones de `jakarta.validation`.

Ejemplo:

```java
@NotBlank
private String nombre;

@Email
@NotBlank
private String email;

@NotNull
private Long idPlaza;
```

Validaciones recomendadas:

| Tipo de dato | Validación |
|---|---|
| Textos obligatorios | `@NotBlank` |
| IDs obligatorios | `@NotNull` |
| Email | `@Email` |
| Fechas obligatorias | `@NotNull` |
| Números positivos | `@Positive` |

En controller, usar:

```java
public ResponseEntity<?> crear(@Valid @RequestBody UsuarioRequest request)
```

---

## 15. Convención para migraciones Flyway

Cada microservicio debe tener:

```txt
src/main/resources/db/migration/
```

Los archivos deben nombrarse así:

```txt
V1__crear_tabla_usuario.sql
V2__insert_datos_iniciales_usuario.sql
V3__agregar_campo_x_usuario.sql
```

Reglas:

1. No editar una migración que ya fue ejecutada por otros integrantes.
2. Si hay que cambiar algo, crear una nueva migración.
3. Usar nombres claros.
4. Una migración debe tener una responsabilidad concreta.
5. No crear tablas de otros microservicios.

Ejemplo:

```sql
CREATE TABLE usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(12) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    email VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    fecha_creacion DATETIME NOT NULL
);
```

---

## 16. Convención para respuestas HTTP

Usar códigos HTTP de forma consistente:

| Caso | Código |
|---|---:|
| Crear recurso | `201 Created` |
| Consultar recurso | `200 OK` |
| Actualizar recurso | `200 OK` |
| Eliminar recurso | `204 No Content` |
| Validación incorrecta | `400 Bad Request` |
| No encontrado | `404 Not Found` |
| Error interno | `500 Internal Server Error` |

---

## 17. Convención CRUD
d
Métodos sugeridos:

| Acción | Método Java | HTTP | Ruta |
|---|---|---|---|
| Listar | `listar()` | GET | `/api/v1/usuarios` |
| Buscar por ID | `buscarPorId(Long id)` | GET | `/api/v1/usuarios/{id}` |
| Crear | `crear(UsuarioRequest request)` | POST | `/api/v1/usuarios` |
| Actualizar | `actualizar(Long id, UsuarioRequest request)` | PUT | `/api/v1/usuarios/{id}` |
| Eliminar | `eliminar(Long id)` | DELETE | `/api/v1/usuarios/{id}` |

Mantener esta misma lógica en todos los microservicios.

---

## 18. Seguridad durante desarrollo

Todos los microservicios tienen Spring Security incluido.

Durante desarrollo educativo, si todavía no se implementará autenticación real, se puede crear una configuración temporal para permitir requests.

Ejemplo:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .build();
    }
}
```

Regla: si se usa esta configuración, dejar un comentario claro indicando que es temporal.

---

## 19. Manejo de errores

Crear excepciones simples por microservicio.

Ejemplo:

```java
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
```

Y un handler global:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

}
```

Regla: no responder errores con `printStackTrace()` ni mensajes improvisados desde el controller.

---

## 20. Formato de commits

Usar mensajes claros:

```txt
feat(usuario): crear modelo Usuario
feat(vehiculo): agregar migración inicial
fix(reserva): corregir validación de fecha
docs: actualizar instructivo de entorno local
```

Prefijos sugeridos:

| Prefijo | Uso |
|---|---|
| `feat` | Nueva funcionalidad |
| `fix` | Corrección |
| `docs` | Documentación |
| `refactor` | Mejora interna sin cambiar comportamiento |
| `test` | Pruebas |
| `chore` | Configuración o tareas menores |

---

## 21. Flujo de trabajo Git recomendado

Antes de trabajar:

```bash
git checkout main
git pull origin main
```

Crear rama:

```bash
git checkout -b feature/nombre-servicio-tarea
```

Ejemplo:

```bash
git checkout -b feature/usuario-crud
```

Guardar cambios:

```bash
git status
git add .
git commit -m "feat(usuario): crear estructura base"
git push origin feature/usuario-crud
```

Luego crear Pull Request hacia `main`.

---

## 22. Archivos que sí van al repo

```txt
src/
pom.xml
mvnw
mvnw.cmd
.mvn/
docker-compose.yml
.env.ejemplo
docker/mysql/init/01-create-database.sql
docs/
.gitignore
```

---

## 23. Archivos que no van al repo

```txt
.env
target/
.vscode/
.idea/
*.log
*_backup.sql
*_dump.sql
volúmenes de Docker
```

---

## 24. Comandos útiles del entorno local

Levantar MySQL:

```bash
docker compose up -d
```

Detener MySQL:

```bash
docker compose down
```

Reiniciar MySQL borrando datos locales:

```bash
docker compose down -v
docker compose up -d
```

Ver contenedores:

```bash
docker ps
```

Entrar a MySQL:

```bash
docker exec -it fullstack_mysql_local mysql -u dev_user -pdev123
```

Ver bases de datos:

```sql
SHOW DATABASES;
```

---

## 25. Criterios finales obligatorios

1. Cada microservicio usa su propio puerto.
2. Cada microservicio usa su propia base de datos.
3. Las tablas se crean con Flyway dentro de cada microservicio.
4. El archivo `01-create-database.sql` solo crea bases, usuarios y permisos.
5. Las entidades usan `PascalCase`.
6. Los atributos Java usan `camelCase`.
7. Las columnas SQL usan `snake_case`.
8. Los endpoints REST usan plural.
9. Los controllers no contienen lógica de negocio.
10. Las respuestas no deben devolver contraseñas.
11. No crear dependencias directas entre bases de datos de microservicios.
12. Antes de subir cambios, ejecutar `git status` y revisar que no se suba `.env`.

---

## 26. Orden sugerido para empezar el desarrollo

1. Crear `application-dev.properties` en cada microservicio.
2. Crear carpeta `src/main/resources/db/migration/`.
3. Crear migración `V1__crear_tabla_x.sql`.
4. Crear modelo JPA.
5. Crear repository.
6. Crear DTO request/response.
7. Crear service.
8. Crear controller.
9. Probar con Postman, Insomnia o navegador.
10. Subir cambios en una rama propia.

