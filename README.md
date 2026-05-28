# Estacionamientos Spring Boot FullStack 1 — 2026

Sistema backend basado en microservicios para la gestión de estacionamientos. El proyecto fue desarrollado como parte de la asignatura FullStack 1 y busca implementar una arquitectura distribuida usando Java, Spring Boot, bases de datos separadas y comunicación HTTP entre servicios.

## Contexto del proyecto

El sistema permite administrar el flujo básico de un estacionamiento:

1. Usuarios autorizados inician sesión mediante `auth_service`.
2. Se gestionan usuarios, vehículos y plazas.
3. Se registran accesos de vehículos asociados a usuarios existentes.
4. Se registran reservas de visitas.
5. Se liberan plazas al finalizar el uso.
6. Cada acción relevante genera un movimiento centralizado.
7. Reporte resume el estado del sistema y los movimientos.
8. Cierre ejecuta limpieza operativa y guarda un resumen histórico.

El objetivo principal fue construir una solución funcional, modular y entendible, aplicando separación de responsabilidades entre microservicios.

---

## Arquitectura general

El proyecto está organizado como un conjunto de microservicios Spring Boot independientes.

Cada microservicio tiene su propia responsabilidad y, cuando corresponde, su propia base de datos.

Regla principal de arquitectura:

```txt
Un microservicio no consulta directamente la base de datos de otro.
Si necesita información, llama al endpoint del microservicio dueño de esa información.
```

Esto permite mantener una separación clara entre servicios y evita acoplar directamente las bases de datos.

---

## Microservicios

| Microservicio | Carpeta | Puerto | Base de datos | Endpoint base | Responsabilidad |
|---|---:|---:|---|---|---|
| Auth | `auth_service` | `8080` | Sin BD propia funcional | `/api/v1/auth` | Login y generación de JWT |
| Usuario | `usuario_service` | `8081` | `usuario_db` | `/api/v1/usuarios` | Usuarios y roles |
| Vehículo | `vehiculo_service` | `8082` | `vehiculo_db` | `/api/v1/vehiculos` | Vehículos y tipos de vehículo |
| Plaza | `plaza_service` | `8083` | `plaza_db` | `/api/v1/plazas` | Plazas y estados |
| Reserva | `reserva_service` | `8084` | `reserva_db` | `/api/v1/reservas` | Reservas de visitas |
| Acceso | `acceso_service` | `8085` | `acceso_db` | `/api/v1/accesos` | Ingreso de vehículos |
| Movimiento | `movimiento_service` | `8086` | `movimiento_db` | `/api/v1/movimientos` | Bitácora central |
| Liberación | `liberacion_service` | `8087` | `liberacion_db` | `/api/v1/liberaciones` | Salida/liberación de plazas |
| Cierre | `cierre_service` | `8088` | `cierre_db` | `/api/v1/cierres` | Cierre operativo diario |
| Reporte | `reporte_service` | `8089` | Sin BD persistente relevante | `/api/v1/reportes` | Reportes desde plazas y movimientos |

---

## Tecnologías principales

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT con `java-jwt`
- MySQL
- Flyway
- Lombok
- Jakarta Validation
- Maven
- Docker para base de datos local

---

## Seguridad

El sistema usa dos mecanismos principales.

### BCrypt

Se usa para proteger contraseñas.

```txt
usuario_service → hashea passwords al crear/actualizar usuarios
auth_service    → compara password ingresada contra hash BCrypt
```

BCrypt no se desencripta. Solo permite comparar la contraseña ingresada contra el hash guardado.

### JWT

Se usa para proteger endpoints.

```txt
auth_service genera el token
los demás microservicios validan el token
los clientes HTTP internos reenvían el token cuando llaman a otros servicios
```

Roles principales:

```txt
Jefe_Seguridad
Jefe_SSDD
Guardia
Funcionario
```

---

## Flujo operativo principal

### 1. Login

```txt
Cliente → auth_service
auth_service → usuario_service
auth_service → devuelve JWT
```

El token JWT se utiliza después para acceder a los microservicios protegidos.

---

### 2. Acceso

```txt
Cliente → acceso_service
acceso_service → usuario_service
acceso_service → vehiculo_service
acceso_service → plaza_service
acceso_service → movimiento_service
```

Resultado:

```txt
plaza queda Ocupada
acceso queda registrado
movimiento tipo ACCESO queda registrado
```

---

### 3. Reserva

```txt
Cliente → reserva_service
reserva_service → plaza_service
reserva_service → movimiento_service
```

Resultado:

```txt
plaza queda Reservada
reserva queda registrada
movimiento tipo RESERVA queda registrado
```

Nota: en esta versión, las reservas corresponden a visitas. Por eso no requieren que el usuario exista en `usuario_service` ni que el vehículo exista en `vehiculo_service`.

---

### 4. Liberación

```txt
Cliente → liberacion_service
liberacion_service → plaza_service
liberacion_service → movimiento_service
```

Resultado:

```txt
plaza queda Disponible
liberación queda registrada
movimiento tipo SALIDA queda registrado
```

---

### 5. Reporte

```txt
Cliente → reporte_service
reporte_service → plaza_service
reporte_service → movimiento_service
```

Resultado:

```txt
reporte resumen global
reporte resumen por fecha
reporte completo
```

`reporte_service` no ejecuta acciones de negocio. Solo consulta y resume información.

---

### 6. Cierre

```txt
Cliente → cierre_service
cierre_service → plaza_service
cierre_service → reserva_service
cierre_service → reporte_service
cierre_service → cierre_db
```

Resultado:

```txt
libera plazas ocupadas
elimina reservas existentes
libera plazas reservadas
consume resumen diario desde reporte_service
guarda snapshot histórico en cierre_db
```

En esta versión, las reservas se consideran para el mismo día. Por eso, durante el cierre, las reservas existentes se eliminan y sus plazas se liberan.

---

## Endpoints principales

### Auth

```http
POST /api/v1/auth/login
```

---

### Usuario

```http
GET    /api/v1/usuarios
GET    /api/v1/usuarios/{idUsuario}
POST   /api/v1/usuarios
PUT    /api/v1/usuarios/{idUsuario}
DELETE /api/v1/usuarios/{idUsuario}
GET    /api/v1/usuarios/auth/email/{email}
GET    /api/v1/usuarios/internal/existe/{idUsuario}
GET    /api/v1/usuarios/internal/rut/{rut}
```

---

### Vehículo

```http
GET    /api/v1/vehiculos
GET    /api/v1/vehiculos/{idVehiculo}
GET    /api/v1/vehiculos/patente/{patente}
GET    /api/v1/vehiculos/usuario/{idUsuario}
POST   /api/v1/vehiculos
PUT    /api/v1/vehiculos/{idVehiculo}
DELETE /api/v1/vehiculos/{idVehiculo}

GET    /api/v1/tipos-vehiculos
GET    /api/v1/tipos-vehiculos/{idTipoVehiculo}
```

---

### Plaza

```http
GET /api/v1/plazas
GET /api/v1/plazas/{idPlaza}
GET /api/v1/plazas/codigo/{codigoPlaza}
PUT /api/v1/plazas/{idPlaza}
PUT /api/v1/plazas/codigo/{codigoPlaza}/ocupar
```

---

### Reserva

```http
GET    /api/v1/reservas
GET    /api/v1/reservas/{idReserva}
POST   /api/v1/reservas
DELETE /api/v1/reservas/{idReserva}
```

---

### Acceso

```http
POST /api/v1/accesos
GET  /api/v1/accesos
GET  /api/v1/accesos/{idAcceso}
GET  /api/v1/accesos/rut/{rut}
GET  /api/v1/accesos/patente/{patente}
GET  /api/v1/accesos/plaza/{codigoPlaza}
```

---

### Movimiento

```http
POST /api/v1/movimientos
GET  /api/v1/movimientos
GET  /api/v1/movimientos/{idMovimiento}
GET  /api/v1/movimientos/tipo/{tipoMovimiento}
GET  /api/v1/movimientos/rut/{rutUsuario}
GET  /api/v1/movimientos/patente/{patente}
GET  /api/v1/movimientos/plaza/{codigoPlaza}
GET  /api/v1/movimientos/origen/{servicioOrigen}
GET  /api/v1/movimientos/fecha/{fecha}
```

---

### Liberación

```http
GET  /api/v1/liberaciones
GET  /api/v1/liberaciones/{idLiberacion}
POST /api/v1/liberaciones
```

---

### Cierre

```http
POST /api/v1/cierres/ejecutar
GET  /api/v1/cierres
GET  /api/v1/cierres/{idCierre}
GET  /api/v1/cierres/fecha/{fechaCierre}
```

---

### Reporte

```http
GET /api/v1/reportes/resumen
GET /api/v1/reportes/resumen/fecha/{fecha}
GET /api/v1/reportes/completo
```

---

## Bases de datos

Cada microservicio usa una base propia:

```txt
usuario_db
vehiculo_db
plaza_db
reserva_db
acceso_db
movimiento_db
liberacion_db
cierre_db
```

Las tablas se crean mediante Flyway en:

```txt
src/main/resources/db/migration
```

Esto permite versionar la estructura de la base de datos junto con el código.

---

## Ejecución local

### 1. Levantar MySQL local

El proyecto trabaja con MySQL local, generalmente levantado mediante Docker.

### 2. Verificar bases de datos

Antes de ejecutar los servicios, deben existir las bases necesarias:

```txt
usuario_db
vehiculo_db
plaza_db
reserva_db
acceso_db
movimiento_db
liberacion_db
cierre_db
```

### 3. Ejecutar microservicios

Desde la carpeta de cada microservicio:

```bash
./mvnw spring-boot:run
```

### 4. Compilar microservicio

```bash
./mvnw clean compile
```

---

## Orden recomendado para levantar servicios

```txt
1. usuario_service
2. auth_service
3. plaza_service
4. vehiculo_service
5. movimiento_service
6. reserva_service
7. acceso_service
8. liberacion_service
9. reporte_service
10. cierre_service
```

Este orden facilita las pruebas porque algunos microservicios dependen de otros para funcionar correctamente.

---

## Pruebas finales validadas

Se validó el flujo completo del sistema:

```txt
login
usuarios
vehículos
plazas
movimiento manual
reserva con movimiento
acceso con movimiento
liberación con movimiento
reporte global
reporte por fecha
cierre operativo
```

Resultado final esperado:

```txt
las plazas ocupadas se liberan
las reservas se eliminan en cierre
las plazas reservadas se liberan
el cierre guarda snapshot histórico
movimiento registra los hechos relevantes
reporte resume el estado operativo
```

---

## Notas de alcance de la versión actual

Esta versión implementa el flujo principal del sistema, pero mantiene algunos alcances acotados para evitar aumentar demasiado la complejidad del proyecto.

Alcances actuales:

- Las reservas son de visitas.
- Las reservas se consideran para el mismo día.
- No se conservan reservas futuras durante el cierre.
- El cambio de plaza queda fuera de esta etapa.
- `movimiento_service` funciona como bitácora central, no como validador de reglas de negocio.
- `reporte_service` consume información desde `movimiento_service` y `plaza_service`.
- `cierre_service` guarda un snapshot histórico del cierre para consulta posterior.

---

## Mejoras futuras

Estas mejoras quedan propuestas para una siguiente iteración del proyecto.

### 1. Implementar `cambio_service`

Crear un microservicio dedicado al cambio de plaza.

Objetivo:

```txt
permitir mover un vehículo desde una plaza ocupada hacia otra plaza disponible
```

Flujo esperado:

```txt
cambio_service recibe plaza origen y plaza destino
→ valida que la plaza origen esté Ocupada
→ valida que la plaza destino esté Disponible
→ libera plaza origen
→ ocupa plaza destino
→ registra movimiento tipo CAMBIO_PLAZA
```

Esto permitiría manejar casos donde un vehículo necesita cambiarse de lugar sin registrar una salida completa.

---

### 2. Implementar reservas futuras

Actualmente las reservas se consideran para el mismo día.

Una mejora importante sería permitir reservas programadas para fechas y horas futuras.

Cambios esperados:

```txt
agregar fechaHoraReservaProgramada en reserva_service
permitir crear reservas para días posteriores
validar disponibilidad de plaza en esa fecha
diferenciar reservas activas, usadas, vencidas y futuras
```

---

### 3. Conservar reservas futuras durante cierre

Relacionado con el punto anterior.

Cuando existan reservas futuras, `cierre_service` debería distinguir:

```txt
reservas vencidas o del día → se eliminan/liberan
reservas futuras → se mantienen
```

Esto haría que el cierre sea más realista y evite eliminar reservas válidas para días posteriores.

---

### 4. Mejorar reporte por rangos de fecha

Actualmente se agregó resumen por fecha puntual.

Una mejora simple y útil sería permitir reportes por rango:

```http
GET /api/v1/reportes/resumen/rango?desde=2026-05-01&hasta=2026-05-28
```

Esto permitiría obtener balances semanales, mensuales o por período personalizado.

---

### 5. Agregar paginación y ordenamiento

Algunos endpoints pueden crecer mucho con el tiempo, especialmente:

```txt
movimientos
accesos
reservas
cierres
liberaciones
```

Una mejora futura sería agregar paginación con Spring Data:

```txt
page
size
sort
```

Esto haría más livianas las consultas y más fácil su uso desde un frontend.

---

### 6. Mejorar trazabilidad de errores entre microservicios

Actualmente los errores se manejan con respuestas controladas y excepciones por microservicio.

Una mejora sería agregar identificadores de solicitud:

```txt
requestId
correlationId
```

Con eso sería más fácil seguir una operación que pasa por varios servicios, por ejemplo:

```txt
acceso_service → plaza_service → movimiento_service
```

---

### 7. Agregar documentación OpenAPI/Swagger

Para facilitar las pruebas y revisión del proyecto, se podría integrar Swagger/OpenAPI en cada microservicio.

Esto permitiría consultar endpoints, requests y responses desde navegador.

---

### 8. Preparar colección Postman oficial

Como el proyecto requiere probar muchos servicios, sería útil crear una colección Postman con:

```txt
login
usuarios
vehículos
plazas
reservas
accesos
liberaciones
movimientos
reportes
cierres
```

También se podrían dejar variables solo para tokens, manteniendo URLs y bodies explícitos para facilitar la revisión.

---

## Estado final de la versión 1.0

La versión actual permite demostrar un flujo funcional completo:

```txt
usuario autenticado
vehículo registrado
plaza disponible
acceso registrado
reserva registrada
liberación registrada
movimientos centralizados
reportes generados
cierre operativo ejecutado
```

Como versión académica, el proyecto cumple con el objetivo principal: separar responsabilidades por microservicio, usar bases de datos independientes, proteger rutas con JWT y coordinar operaciones mediante comunicación HTTP.
