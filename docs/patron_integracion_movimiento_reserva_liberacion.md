# Patrón de integración con `movimiento_service`

## Objetivo

Este documento define cómo deben integrarse `reserva_service` y `liberacion_service` con `movimiento_service`.

`movimiento_service` funciona como bitácora central del sistema. Su responsabilidad es registrar hechos ya ocurridos, no validar si esos hechos son correctos.

La dirección del flujo siempre es:

```txt
microservicio que realiza la acción
→ movimiento_service
```

Ejemplos:

```txt
reserva_service
→ movimiento_service

liberacion_service
→ movimiento_service

acceso_service
→ movimiento_service
```

`movimiento_service` no consulta a otros microservicios para preguntar si ocurrió algo. Solo recibe movimientos y los guarda.

---

## Regla general para el equipo

Cada microservicio debe registrar un movimiento después de completar correctamente su operación principal.

Orden correcto:

```txt
1. Validar reglas de negocio dentro del microservicio correspondiente.
2. Modificar los estados necesarios en otros microservicios si aplica.
3. Guardar el registro propio del microservicio.
4. Obtener el ID generado.
5. Enviar un POST a movimiento_service.
```

Ejemplo conceptual:

```txt
reserva_service guarda reserva
→ obtiene idReserva
→ envía movimiento tipo RESERVA

liberacion_service guarda liberación
→ obtiene idLiberacion
→ envía movimiento tipo SALIDA
```

---

## Endpoint de `movimiento_service`

Todos los microservicios deben llamar a:

```http
POST http://localhost:8086/api/v1/movimientos
```

Headers:

```txt
Authorization: Bearer TOKEN
Content-Type: application/json
```

El token debe ser el mismo token recibido por el microservicio en la petición original.

---

## DTO esperado por `movimiento_service`

`movimiento_service` recibe un `MovimientoCreateRequest` con esta estructura:

```json
{
  "tipoMovimiento": "ACCESO",
  "idUsuario": 3,
  "rutUsuario": "33333333-3",
  "idVehiculo": 1,
  "patente": "ABCD12",
  "idPlaza": 1,
  "codigoPlaza": "P01",
  "idReferencia": 15,
  "servicioOrigen": "acceso_service",
  "descripcion": "Ingreso autorizado al estacionamiento"
}
```

Campos:

| Campo | Obligatorio | Descripción |
|---|---:|---|
| `tipoMovimiento` | Sí | Tipo de evento: `ACCESO`, `RESERVA`, `SALIDA`, `CAMBIO_PLAZA`, etc. |
| `idUsuario` | No | ID del usuario registrado, si aplica. |
| `rutUsuario` | No | RUT asociado al movimiento. En reservas puede ser RUT de visita. |
| `idVehiculo` | No | ID del vehículo registrado, si aplica. |
| `patente` | No | Patente asociada al movimiento. En reservas puede ser vehículo de visita. |
| `idPlaza` | No | ID interno de la plaza. |
| `codigoPlaza` | No | Código visible de la plaza, por ejemplo `P01`. |
| `idReferencia` | No, pero recomendado | ID del registro original que generó el movimiento. |
| `servicioOrigen` | Sí | Microservicio que generó el movimiento. |
| `descripcion` | No | Texto breve explicando el movimiento. |

---

## Qué representa `idReferencia`

`idReferencia` es el ID del registro creado en el microservicio que originó el movimiento.

Ejemplos:

| Movimiento | Microservicio origen | `idReferencia` significa |
|---|---|---|
| `ACCESO` | `acceso_service` | `idAcceso` |
| `RESERVA` | `reserva_service` | `idReserva` |
| `SALIDA` | `liberacion_service` | `idLiberacion` |
| `CAMBIO_PLAZA` | servicio que implemente cambio | ID del registro de cambio |

Ejemplo:

```json
{
  "tipoMovimiento": "RESERVA",
  "idReferencia": 12,
  "servicioOrigen": "reserva_service"
}
```

Eso se interpreta así:

```txt
Este movimiento corresponde a una reserva.
El registro original está en reserva_service.
El ID del registro original es idReserva = 12.
```

`idReferencia` no es una foreign key SQL. Es una referencia lógica.

---

# Integración desde `reserva_service`

## Alcance funcional de `reserva_service`

`reserva_service` trabaja con visitas.

Eso significa:

```txt
La persona reservada puede no existir en usuario_service.
El vehículo reservado puede no existir en vehiculo_service.
```

Por lo tanto:

```txt
idUsuario puede ir null.
idVehiculo puede ir null.
rutUsuario debe usar el RUT informado en la reserva.
patente debe usar la patente informada en la reserva.
```

La reserva sí trabaja con plaza y modifica su estado. Por eso debe enviar:

```txt
idPlaza
codigoPlaza
```

cuando registre el movimiento.

---

## Datos mínimos que `reserva_service` debe enviar

Cuando una reserva se cree correctamente, debe enviar:

```json
{
  "tipoMovimiento": "RESERVA",
  "idUsuario": null,
  "rutUsuario": "22222222-2",
  "idVehiculo": null,
  "patente": "VISI99",
  "idPlaza": 5,
  "codigoPlaza": "P05",
  "idReferencia": 12,
  "servicioOrigen": "reserva_service",
  "descripcion": "Reserva de visita registrada correctamente"
}
```

## Mapeo de datos desde `reserva_service`

| `MovimientoCreateRequest` | De dónde sale en `reserva_service` |
|---|---|
| `tipoMovimiento` | Valor fijo: `RESERVA` |
| `idUsuario` | `null`, porque reserva corresponde a visitas |
| `rutUsuario` | `reserva.rutReserva` |
| `idVehiculo` | `null`, porque el vehículo de visita no está en `vehiculo_service` |
| `patente` | `reserva.patente` |
| `idPlaza` | `reserva.idPlaza` |
| `codigoPlaza` | Código de plaza usado en la reserva, por ejemplo `P05` |
| `idReferencia` | `reserva.idReserva` |
| `servicioOrigen` | Valor fijo: `reserva_service` |
| `descripcion` | Texto breve, por ejemplo `Reserva de visita registrada correctamente` |

---

## Lugar exacto donde integrar en `reserva_service`

La integración debe ir dentro de:

```txt
reserva_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/reserva_service/service/ReservaService.java
```

Debe ejecutarse al final del método que crea la reserva.

Orden recomendado dentro del método de creación:

```txt
1. Recibir ReservaRequest.
2. Validar datos de reserva.
3. Consultar plaza_service.
4. Verificar que la plaza esté Disponible.
5. Cambiar plaza a Reservada.
6. Guardar Reserva en reserva_db.
7. Registrar movimiento tipo RESERVA en movimiento_service.
8. Devolver ReservaResponse.
```

La integración con movimiento debe ocurrir después de:

```java
Reserva reservaGuardada = reservaRepository.save(reserva);
```

y antes de devolver el response.

---

## Clases que `reserva_service` debe agregar

### 1. DTO para enviar movimiento

Crear:

```txt
reserva_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/reserva_service/client/dto/MovimientoCreateRequest.java
```

Campos:

```java
private String tipoMovimiento;
private Long idUsuario;
private String rutUsuario;
private Long idVehiculo;
private String patente;
private Long idPlaza;
private String codigoPlaza;
private Long idReferencia;
private String servicioOrigen;
private String descripcion;
```

### 2. DTO para recibir respuesta de movimiento

Crear:

```txt
reserva_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/reserva_service/client/dto/MovimientoResponse.java
```

Campos recomendados:

```java
private Long idMovimiento;
private String tipoMovimiento;
private Long idUsuario;
private String rutUsuario;
private Long idVehiculo;
private String patente;
private Long idPlaza;
private String codigoPlaza;
private Long idReferencia;
private String servicioOrigen;
private LocalDateTime fechaMovimiento;
private String descripcion;
```

### 3. Cliente HTTP hacia `movimiento_service`

Crear:

```txt
reserva_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/reserva_service/client/MovimientoClient.java
```

Responsabilidad:

```txt
Enviar POST /api/v1/movimientos a movimiento_service.
Reenviar el header Authorization recibido por reserva_service.
```

### 4. Propiedad de configuración

Agregar en:

```txt
reserva_service/src/main/resources/application-dev.properties
```

```properties
app.services.movimiento.base-url=http://localhost:8086/api/v1/movimientos
```

---

## Firma recomendada en `ReservaController`

Para poder reenviar el token a `movimiento_service`, el controller debe recibir el header `Authorization`.

Ejemplo:

```java
@PostMapping
public ResponseEntity<ReservaResponse> crearReserva(
        @Valid @RequestBody ReservaRequest request,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
) {
    ReservaResponse reserva = reservaService.crearReserva(request, authorizationHeader);
    return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
}
```

El service debe recibir también el token:

```java
public ReservaResponse crearReserva(
        ReservaRequest request,
        String authorizationHeader
) {
    ...
}
```

---

## Método privado recomendado en `ReservaService`

Dentro de `ReservaService`, agregar un método privado parecido a:

```java
private void registrarMovimientoReserva(
        Reserva reserva,
        String codigoPlaza,
        String authorizationHeader
) {
    try {
        MovimientoCreateRequest movimientoRequest = new MovimientoCreateRequest(
                "RESERVA",
                null,
                reserva.getRutReserva(),
                null,
                reserva.getPatente(),
                reserva.getIdPlaza(),
                codigoPlaza,
                reserva.getIdReserva(),
                "reserva_service",
                "Reserva de visita registrada correctamente"
        );

        movimientoClient.registrarMovimiento(movimientoRequest, authorizationHeader);

    } catch (Exception ex) {
        log.warn(
                "No fue posible registrar movimiento de reserva para idReserva {}. Motivo: {}",
                reserva.getIdReserva(),
                ex.getMessage()
        );
    }
}
```

Nota: `codigoPlaza` debe venir del request o de la respuesta de `plaza_service`, según cómo esté implementado `reserva_service`.

---

## Regla de tolerancia a fallos en `reserva_service`

Si `movimiento_service` falla, la reserva no debe anularse automáticamente.

Motivo:

```txt
La reserva ya fue creada.
La plaza ya fue marcada como Reservada.
El movimiento es bitácora.
```

Por ahora se recomienda:

```txt
capturar excepción
dejar warning en log
devolver reserva creada
```

Más adelante se puede mejorar con reintentos/eventos.

---

# Integración desde `liberacion_service`

## Alcance funcional de `liberacion_service`

Según el manual, `liberacion_service` usa:

```txt
Puerto: 8087
Base de datos: liberacion_db
Tabla principal: liberacion
Endpoint base: /api/v1/liberaciones
```

La tabla `liberacion` debe manejar como mínimo:

```txt
idLiberacion
idPlaza
fechaLiberacion
```

También se recomienda que el microservicio pueda obtener o recibir datos suficientes para enviar un movimiento completo:

```txt
idUsuario
rutUsuario
idVehiculo
patente
idPlaza
codigoPlaza
idLiberacion
```

Si `liberacion_service` solo tiene `idPlaza`, puede registrar movimiento, pero quedará pobre para reportes.

---

## Movimiento que debe enviar `liberacion_service`

Cuando se registre una salida/liberación correctamente, debe enviar:

```json
{
  "tipoMovimiento": "SALIDA",
  "idUsuario": 3,
  "rutUsuario": "33333333-3",
  "idVehiculo": 1,
  "patente": "ABCD12",
  "idPlaza": 5,
  "codigoPlaza": "P05",
  "idReferencia": 8,
  "servicioOrigen": "liberacion_service",
  "descripcion": "Salida registrada y plaza liberada correctamente"
}
```

## Mapeo de datos desde `liberacion_service`

| `MovimientoCreateRequest` | De dónde debería salir en `liberacion_service` |
|---|---|
| `tipoMovimiento` | Valor fijo: `SALIDA` |
| `idUsuario` | Del acceso activo, reserva asociada o request, según diseño |
| `rutUsuario` | Del acceso activo, reserva asociada o request |
| `idVehiculo` | Del acceso activo o request |
| `patente` | Del acceso activo, reserva asociada o request |
| `idPlaza` | `liberacion.idPlaza` |
| `codigoPlaza` | De `plaza_service` o request |
| `idReferencia` | `liberacion.idLiberacion` |
| `servicioOrigen` | Valor fijo: `liberacion_service` |
| `descripcion` | Texto breve: `Salida registrada y plaza liberada correctamente` |

---

## Lugar exacto donde integrar en `liberacion_service`

La integración debe ir dentro de:

```txt
liberacion_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/liberacion_service/service/LiberacionService.java
```

Debe ejecutarse al final del método que crea la liberación.

Orden recomendado:

```txt
1. Recibir request de liberación.
2. Validar plaza o acceso asociado.
3. Liberar plaza en plaza_service.
4. Guardar Liberacion en liberacion_db.
5. Registrar movimiento tipo SALIDA en movimiento_service.
6. Devolver LiberacionResponse.
```

La llamada a movimiento debe ocurrir después de:

```java
Liberacion liberacionGuardada = liberacionRepository.save(liberacion);
```

---

## Clases que `liberacion_service` debe agregar

### 1. DTO para enviar movimiento

Crear:

```txt
liberacion_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/liberacion_service/client/dto/MovimientoCreateRequest.java
```

Campos:

```java
private String tipoMovimiento;
private Long idUsuario;
private String rutUsuario;
private Long idVehiculo;
private String patente;
private Long idPlaza;
private String codigoPlaza;
private Long idReferencia;
private String servicioOrigen;
private String descripcion;
```

### 2. DTO para recibir respuesta de movimiento

Crear:

```txt
liberacion_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/liberacion_service/client/dto/MovimientoResponse.java
```

Campos recomendados:

```java
private Long idMovimiento;
private String tipoMovimiento;
private Long idUsuario;
private String rutUsuario;
private Long idVehiculo;
private String patente;
private Long idPlaza;
private String codigoPlaza;
private Long idReferencia;
private String servicioOrigen;
private LocalDateTime fechaMovimiento;
private String descripcion;
```

### 3. Cliente HTTP hacia `movimiento_service`

Crear:

```txt
liberacion_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/liberacion_service/client/MovimientoClient.java
```

Responsabilidad:

```txt
Enviar POST /api/v1/movimientos a movimiento_service.
Reenviar el header Authorization recibido por liberacion_service.
```

### 4. Propiedad de configuración

Agregar en:

```txt
liberacion_service/src/main/resources/application-dev.properties
```

```properties
app.services.movimiento.base-url=http://localhost:8086/api/v1/movimientos
```

---

## Firma recomendada en `LiberacionController`

Para reenviar el token:

```java
@PostMapping
public ResponseEntity<LiberacionResponse> crearLiberacion(
        @Valid @RequestBody LiberacionRequest request,
        @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
) {
    LiberacionResponse liberacion = liberacionService.crearLiberacion(request, authorizationHeader);
    return ResponseEntity.status(HttpStatus.CREATED).body(liberacion);
}
```

En el service:

```java
public LiberacionResponse crearLiberacion(
        LiberacionRequest request,
        String authorizationHeader
) {
    ...
}
```

---

## Método privado recomendado en `LiberacionService`

Dentro de `LiberacionService`, agregar algo similar a:

```java
private void registrarMovimientoSalida(
        Liberacion liberacion,
        DatosSalida datosSalida,
        String authorizationHeader
) {
    try {
        MovimientoCreateRequest movimientoRequest = new MovimientoCreateRequest(
                "SALIDA",
                datosSalida.getIdUsuario(),
                datosSalida.getRutUsuario(),
                datosSalida.getIdVehiculo(),
                datosSalida.getPatente(),
                liberacion.getIdPlaza(),
                datosSalida.getCodigoPlaza(),
                liberacion.getIdLiberacion(),
                "liberacion_service",
                "Salida registrada y plaza liberada correctamente"
        );

        movimientoClient.registrarMovimiento(movimientoRequest, authorizationHeader);

    } catch (Exception ex) {
        log.warn(
                "No fue posible registrar movimiento de salida para idLiberacion {}. Motivo: {}",
                liberacion.getIdLiberacion(),
                ex.getMessage()
        );
    }
}
```

`DatosSalida` representa la información que `liberacion_service` debe obtener desde su propio flujo: puede venir de acceso activo, de la plaza, del request o de otro cliente HTTP.

---

## Regla de tolerancia a fallos en `liberacion_service`

Si `movimiento_service` falla, la liberación no debe anularse automáticamente.

Motivo:

```txt
La plaza ya fue liberada.
La liberación ya fue registrada.
El movimiento es bitácora.
```

Por ahora:

```txt
capturar excepción
dejar warning en log
devolver liberación creada
```

---

# Cliente `MovimientoClient` recomendado

Este patrón sirve igual para `reserva_service`, `liberacion_service`, `acceso_service` y cualquier otro microservicio.

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.NOMBRE_SERVICIO.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import cl.duoc.fullstack1.grupo11.estacionamientos.NOMBRE_SERVICIO.client.dto.MovimientoCreateRequest;
import cl.duoc.fullstack1.grupo11.estacionamientos.NOMBRE_SERVICIO.client.dto.MovimientoResponse;

@Component
public class MovimientoClient {

    private final RestTemplate restTemplate;
    private final String movimientoServiceBaseUrl;

    public MovimientoClient(
            @Qualifier("microserviciosRestTemplate") RestTemplate restTemplate,
            @Value("${app.services.movimiento.base-url}") String movimientoServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.movimientoServiceBaseUrl = movimientoServiceBaseUrl;
    }

    public MovimientoResponse registrarMovimiento(
            MovimientoCreateRequest request,
            String authorizationHeader
    ) {
        try {
            HttpEntity<MovimientoCreateRequest> entity = crearHttpEntityConToken(
                    request,
                    authorizationHeader
            );

            ResponseEntity<MovimientoResponse> response = restTemplate.exchange(
                    movimientoServiceBaseUrl,
                    HttpMethod.POST,
                    entity,
                    MovimientoResponse.class
            );

            return response.getBody();

        } catch (RestClientException ex) {
            throw ex;
        }
    }

    private HttpEntity<MovimientoCreateRequest> crearHttpEntityConToken(
            MovimientoCreateRequest request,
            String authorizationHeader
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, authorizationHeader);

        return new HttpEntity<>(request, headers);
    }
}
```

---

# `RestTemplateConfig` requerido

Si el microservicio no tiene aún `RestTemplateConfig`, agregar:

```txt
src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/NOMBRE_SERVICIO/config/RestTemplateConfig.java
```

Código base:

```java
package cl.duoc.fullstack1.grupo11.estacionamientos.NOMBRE_SERVICIO.config;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean(name = "microserviciosRestTemplate")
    public RestTemplate microserviciosRestTemplate(
            @Value("${app.services.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${app.services.read-timeout-ms}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        return new RestTemplate(requestFactory);
    }
}
```

Propiedades requeridas:

```properties
app.services.connect-timeout-ms=3000
app.services.read-timeout-ms=5000
```

---

# Patrón final para explicar al equipo

```txt
Movimiento_service es una bitácora central.

Cada microservicio que completa una acción importante debe enviar un POST a movimiento_service.

El movimiento se registra después de guardar el registro propio del microservicio.

idReferencia siempre es el ID del registro creado en el microservicio origen.

servicioOrigen siempre es el nombre del microservicio que generó el movimiento.

Movimiento_service no valida usuarios, vehículos ni plazas. Solo registra lo que el microservicio origen ya validó o procesó.
```

---

## Resumen específico

### Reserva

```txt
tipoMovimiento = RESERVA
idReferencia = idReserva
servicioOrigen = reserva_service
rutUsuario = rutReserva de la visita
patente = patente de la visita
idUsuario = null
idVehiculo = null
idPlaza = plaza reservada
codigoPlaza = código de plaza reservada
```

### Liberación

```txt
tipoMovimiento = SALIDA
idReferencia = idLiberacion
servicioOrigen = liberacion_service
idUsuario/rutUsuario/idVehiculo/patente = datos asociados a la salida, si están disponibles
idPlaza = plaza liberada
codigoPlaza = código de plaza liberada
```
