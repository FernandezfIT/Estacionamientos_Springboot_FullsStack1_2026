# reserva_service

## Responsabilidad

Registra reservas de visitas. No exige que el visitante exista en usuario_service ni que el vehículo exista en vehiculo_service.

## Puerto

```txt
8084
```

## Base de datos

```txt
reserva_db
```

## Endpoint base

```txt
/api/v1/reservas
```

## Funciones principales

```txt
listar reservas
buscar por ID
crear reserva
eliminar reserva
marcar plaza como Reservada
registrar movimiento RESERVA
```

## Endpoints

```http
GET    /api/v1/reservas
GET    /api/v1/reservas/{idReserva}
POST   /api/v1/reservas
DELETE /api/v1/reservas/{idReserva}
```

## Integraciones

```txt
reserva_service → plaza_service
reserva_service → movimiento_service
cierre_service → reserva_service
```

## Flujo principal

```txt
Recibe rutReserva, patente e idPlaza
→ consulta plaza_service
→ cambia plaza a Reservada
→ guarda reserva
→ registra movimiento RESERVA
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
Reserva
ReservaController
ReservaService
ReservaRepository
PlazaClient
MovimientoClient
ReservaRequest
ReservaResponse
MovimientoCreateRequest
MovimientoResponse
```

## Notas

En esta versión las reservas son del mismo día. El cierre elimina reservas existentes.
