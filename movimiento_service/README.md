# movimiento_service

## Responsabilidad

Bitácora central del sistema. Registra hechos ya ocurridos.

## Puerto

```txt
8086
```

## Base de datos

```txt
movimiento_db
```

## Endpoint base

```txt
/api/v1/movimientos
```

## Funciones principales

```txt
crear movimiento
listar movimientos
filtrar por tipo
filtrar por RUT
filtrar por patente
filtrar por plaza
filtrar por origen
filtrar por fecha
```

## Endpoints

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

## Integraciones

```txt
acceso_service → movimiento_service
reserva_service → movimiento_service
liberacion_service → movimiento_service
reporte_service → movimiento_service
```

## Flujo principal

```txt
Recibe MovimientoCreateRequest
→ normaliza datos
→ guarda movimiento
→ devuelve MovimientoResponse
```

## Estructura interna

```txt
client/        # cuando el servicio consume otros microservicios
controller/    # endpoints REST
dto/request/   # DTOs de entrada
dto/response/  # DTOs de salida
exception/     # excepciones y handler global
model/         # entidades JPA, si aplica
repository/    # repositorios JPA, si aplica
service/       # lógica de negocio
security/      # JWT y filtros, si aplica
config/        # configuración general
```

## Clases relevantes

```txt
Movimiento
MovimientoController
MovimientoService
MovimientoRepository
MovimientoCreateRequest
MovimientoResponse
```

## Notas

`idReferencia` apunta al ID del registro original: idAcceso, idReserva o idLiberacion.
