# reporte_service

## Responsabilidad

Genera reportes operativos usando plazas y movimientos.

## Puerto

```txt
8089
```

## Base de datos

```txt
Sin persistencia relevante en esta versión
```

## Endpoint base

```txt
/api/v1/reportes
```

## Funciones principales

```txt
resumen global
resumen por fecha
reporte completo
```

## Endpoints

```http
GET /api/v1/reportes/resumen
GET /api/v1/reportes/resumen/fecha/{fecha}
GET /api/v1/reportes/completo
```

## Integraciones

```txt
reporte_service → plaza_service
reporte_service → movimiento_service
cierre_service → reporte_service
```

## Flujo principal

```txt
Consulta plazas
→ consulta movimientos
→ calcula resumen
→ responde reporte
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
ReporteController
ReporteService
PlazaClient
MovimientoClient
ReporteResumenResponse
ReporteCompletoResponse
PlazaResponse
MovimientoResponse
```

## Notas

El resumen por fecha usa movimientos filtrados por fecha. El estado de plazas corresponde al estado actual.
