# plaza_service

## Responsabilidad

Administra plazas y sus estados operativos.

## Puerto

```txt
8083
```

## Base de datos

```txt
plaza_db
```

## Endpoint base

```txt
/api/v1/plazas
```

## Funciones principales

```txt
listar plazas
buscar por ID
buscar por código
actualizar estado
ocupar por código
```

## Endpoints

```http
GET /api/v1/plazas
GET /api/v1/plazas/{idPlaza}
GET /api/v1/plazas/codigo/{codigoPlaza}
PUT /api/v1/plazas/{idPlaza}
PUT /api/v1/plazas/codigo/{codigoPlaza}/ocupar
```

## Integraciones

```txt
acceso_service → plaza_service
reserva_service → plaza_service
liberacion_service → plaza_service
cierre_service → plaza_service
reporte_service → plaza_service
```

## Flujo principal

```txt
Recibe solicitud de cambio de estado
→ valida plaza
→ cambia estado
→ devuelve plaza actualizada
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
Plaza
PlazaController
PlazaService
PlazaRepository
PlazaUpdateRequest
PlazaResponse
```

## Notas

Estados usados: `Disponible`, `Ocupada`, `Reservada`.
