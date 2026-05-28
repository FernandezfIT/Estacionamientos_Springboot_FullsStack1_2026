# cierre_service

## Responsabilidad

Ejecuta cierre operativo, limpia plazas/reservas y guarda snapshot histórico.

## Puerto

```txt
8088
```

## Base de datos

```txt
cierre_db
```

## Endpoint base

```txt
/api/v1/cierres
```

## Funciones principales

```txt
ejecutar cierre
listar cierres
buscar por ID
buscar por fecha
```

## Endpoints

```http
POST /api/v1/cierres/ejecutar
GET  /api/v1/cierres
GET  /api/v1/cierres/{idCierre}
GET  /api/v1/cierres/fecha/{fechaCierre}
```

## Integraciones

```txt
cierre_service → plaza_service
cierre_service → reserva_service
cierre_service → reporte_service
```

## Flujo principal

```txt
Recibe solicitud de cierre
→ libera plazas Ocupada
→ elimina reservas existentes
→ libera plazas Reservada
→ consume reporte por fecha
→ guarda cierre
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
Cierre
CierreController
CierreService
CierreRepository
PlazaClient
ReservaClient
ReporteClient
CierreEjecutarRequest
CierreResponse
```

## Notas

En esta versión no mantiene reservas futuras. Toda reserva existente se elimina durante el cierre.
