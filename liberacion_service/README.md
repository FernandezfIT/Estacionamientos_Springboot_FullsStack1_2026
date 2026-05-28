# liberacion_service

## Responsabilidad

Registra salidas/liberaciones de plazas y movimientos tipo SALIDA.

## Puerto

```txt
8087
```

## Base de datos

```txt
liberacion_db
```

## Endpoint base

```txt
/api/v1/liberaciones
```

## Funciones principales

```txt
listar liberaciones
buscar por ID
liberar plaza
registrar movimiento SALIDA
```

## Endpoints

```http
GET  /api/v1/liberaciones
GET  /api/v1/liberaciones/{idLiberacion}
POST /api/v1/liberaciones
```

## Integraciones

```txt
liberacion_service → plaza_service
liberacion_service → movimiento_service
```

## Flujo principal

```txt
Recibe idPlaza
→ consulta plaza_service
→ cambia plaza a Disponible
→ guarda liberación
→ registra movimiento SALIDA
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
Liberacion
LiberacionController
LiberacionService
LiberacionRepository
PlazaClient
MovimientoClient
LiberacionRequest
LiberacionResponse
```

## Notas

Debe reenviar el header Authorization a plaza_service y movimiento_service.
