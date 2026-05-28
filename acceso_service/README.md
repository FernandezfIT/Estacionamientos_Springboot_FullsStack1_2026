# acceso_service

## Responsabilidad

Registra ingresos al estacionamiento. Valida usuario, vehículo, pertenencia y plaza.

## Puerto

```txt
8085
```

## Base de datos

```txt
acceso_db
```

## Endpoint base

```txt
/api/v1/accesos
```

## Funciones principales

```txt
registrar acceso
listar accesos
buscar por ID
buscar por RUT
buscar por patente
buscar por plaza
registrar movimiento ACCESO
```

## Endpoints

```http
POST /api/v1/accesos
GET  /api/v1/accesos
GET  /api/v1/accesos/{idAcceso}
GET  /api/v1/accesos/rut/{rut}
GET  /api/v1/accesos/patente/{patente}
GET  /api/v1/accesos/plaza/{codigoPlaza}
```

## Integraciones

```txt
acceso_service → usuario_service
acceso_service → vehiculo_service
acceso_service → plaza_service
acceso_service → movimiento_service
```

## Flujo principal

```txt
Recibe rut, patente y codigoPlaza
→ valida usuario
→ valida vehículo
→ valida pertenencia
→ ocupa plaza
→ guarda acceso
→ registra movimiento ACCESO
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
Acceso
AccesoController
AccesoService
AccesoRepository
UsuarioClient
VehiculoClient
PlazaClient
MovimientoClient
AccesoCreateRequest
AccesoResponse
```

## Notas

Si falla movimiento_service, el acceso no se revierte automáticamente; se registra advertencia en log.
