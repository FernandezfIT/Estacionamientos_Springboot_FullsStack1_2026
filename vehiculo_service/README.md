# vehiculo_service

## Responsabilidad

Administra vehículos y tipos de vehículos. Cada vehículo se asocia lógicamente a un usuario.

## Puerto

```txt
8082
```

## Base de datos

```txt
vehiculo_db
```

## Endpoint base

```txt
/api/v1/vehiculos
```

## Funciones principales

```txt
CRUD de vehículos
búsqueda por patente
listado por usuario
catálogo de tipos de vehículo
validación de usuario dueño
```

## Endpoints

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

## Integraciones

```txt
vehiculo_service → usuario_service
acceso_service → vehiculo_service
```

## Flujo principal

```txt
Recibe datos de vehículo
→ valida tipo de vehículo
→ valida existencia del usuario dueño
→ guarda vehículo
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
Vehiculo
TipoVehiculo
VehiculoController
TipoVehiculoController
VehiculoService
TipoVehiculoService
VehiculoRepository
TipoVehiculoRepository
UsuarioClient
VehiculoCreateRequest
VehiculoUpdateRequest
VehiculoResponse
```

## Notas

La patente se normaliza en mayúsculas para evitar duplicados por formato.
