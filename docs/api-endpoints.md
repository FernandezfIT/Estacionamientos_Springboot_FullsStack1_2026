# Catálogo de endpoints

Rutas obtenidas de `@RequestMapping` y anotaciones de métodos de los controladores en `main` (`78ef4d1`). No representan pruebas HTTP ejecutadas. El prefijo de cada sección se combina con la ruta relativa; `/` representa el propio prefijo, sin requerir barra final.

Bases de acceso configuradas:

- Directa: `http://localhost:<puerto>` según [inventario](arquitectura.md).
- Gateway: `http://localhost:8090`, con los mismos prefijos de negocio; requiere registro y enrutamiento operativos.
- Swagger por servicio: `http://localhost:<puerto>/doc/swagger-ui.html`.
- Especificación por servicio: `http://localhost:<puerto>/v3/api-docs`.

Swagger/OpenAPI está configurado en los diez servicios de negocio y pendiente de verificación HTTP. No está agregado en Gateway. Consultar los DTO `dto/request` y `dto/response` de cada módulo para contratos; no se incluyen tokens, cuentas ni contraseñas de ejemplo.

## Acceso y autorización

Enviar `Authorization: Bearer <token-local>` en las rutas protegidas. Auth publica login. Usuario permite consultas internas sin token; es una limitación de seguridad, no un mecanismo seguro de autenticación entre servicios. Varias APIs restringen por autoridades; otras solo comprueban autenticación. Véase la [matriz de seguridad](arquitectura.md#seguridad).

La configuración base de Gateway no enruta el prefijo plural `/api/v1/tipos-vehiculos`; el perfil Docker sí lo incluye. No hay que cambiar el nombre real del endpoint para hacerlo coincidir con la ruta singular incorrecta.

## Auth — puerto 8080

Prefijo `/api/v1/auth`.

```text
POST /login
```

Valida credenciales mediante Usuario y devuelve un JWT.

## Usuario — puerto 8081

Prefijo `/api/v1/usuarios`.

```text
GET    /
GET    /{idUsuario}
POST   /
PUT    /{idUsuario}
DELETE /{idUsuario}
GET    /auth/email/{email}
GET    /internal/existe/{idUsuario}
GET    /internal/rut/{rut}
```

Las tres últimas consultas están permitidas sin token en SecurityConfig. La respuesta de autenticación contiene el hash BCrypt y no debe exponerse públicamente ni copiarse a informes.

Catálogo de roles, prefijo `/api/v1/roles`:

```text
GET /
GET /{idRol}
```

## Vehículo — puerto 8082

Prefijo `/api/v1/vehiculos`.

```text
GET    /
GET    /{idVehiculo}
GET    /patente/{patente}
GET    /usuario/{idUsuario}
POST   /
PUT    /{idVehiculo}
DELETE /{idVehiculo}
```

Tipos, prefijo `/api/v1/tipos-vehiculos`:

```text
GET /
GET /{idTipoVehiculo}
```

## Plaza — puerto 8083

Prefijo `/api/v1/plazas`.

```text
GET /
GET /{idPlaza}
GET /codigo/{codigoPlaza}
PUT /{idPlaza}
PUT /codigo/{codigoPlaza}/ocupar
```

El `PUT` por ID actualiza estado; no se anuncia un CRUD de creación/eliminación de plazas que el controlador no ofrece.

## Reserva — puerto 8084

Prefijo `/api/v1/reservas`.

```text
GET    /
GET    /{idReserva}
POST   /
DELETE /{idReserva}
```

Reservas de visita, sin programación futura implementada. Crear cambia la plaza a reservada; eliminar solo borra el registro, sin liberar la plaza. El cierre realiza explícitamente la liberación.

## Acceso — puerto 8085

Prefijo `/api/v1/accesos`.

```text
POST /
GET  /
GET  /{idAcceso}
GET  /rut/{rut}
GET  /patente/{patente}
GET  /plaza/{codigoPlaza}
```

## Movimiento — puerto 8086

Prefijo `/api/v1/movimientos`.

```text
POST /
GET  /
GET  /{idMovimiento}
GET  /tipo/{tipoMovimiento}
GET  /rut/{rutUsuario}
GET  /patente/{patente}
GET  /plaza/{codigoPlaza}
GET  /origen/{servicioOrigen}
GET  /fecha/{fechaMovimiento}
```

Las fechas se reciben como fecha ISO (`AAAA-MM-DD`), no como rango. La bitácora registra hechos; no valida por sí misma toda la lógica del estacionamiento.

## Liberación — puerto 8087

Prefijo `/api/v1/liberaciones`.

```text
GET  /
GET  /{idLiberacion}
POST /
```

## Cierre — puerto 8088

Prefijo `/api/v1/cierres`.

```text
POST /ejecutar
GET  /
GET  /{idCierre}
GET  /fecha/{fechaCierre}
```

La ejecución modifica plazas y elimina reservas: no es una consulta ni una prueba inocua de disponibilidad. Utilizar solo datos locales desechables al validar el flujo. La fecha de consulta usa formato ISO.

## Reporte — puerto 8089

Prefijo `/api/v1/reportes`.

```text
GET /resumen
GET /resumen/fecha/{fecha}
GET /completo
```

La fecha utiliza formato ISO. No hay endpoint implementado de resumen por rango ni exportación PDF/Excel documentada como funcionalidad.

## Fuentes y límites

Controladores: `*/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/<modulo>/controller/`. Permisos: `configuration/SecurityConfig.java` de cada módulo. Las rutas declaradas `/api/v1/public/**` en seguridad no acreditan por sí solas que exista un controlador público adicional. No se infieren endpoints desde reglas de seguridad.

El enrutamiento, contratos en ejecución, códigos de respuesta y permisos completos deben comprobarse con el sistema levantado. [Preparación del entorno](instalacion.md) y [validaciones pendientes](pruebas.md#validaciones-pendientes).
