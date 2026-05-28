# usuario_service

## Responsabilidad

Administra usuarios y roles del sistema. Entrega datos internos a otros microservicios.

## Puerto

```txt
8081
```

## Base de datos

```txt
usuario_db
```

## Endpoint base

```txt
/api/v1/usuarios
```

## Funciones principales

```txt
CRUD de usuarios
gestión de roles
búsqueda por email para auth
búsqueda por RUT para integraciones
verificación de existencia por ID
```

## Endpoints

```http
GET    /api/v1/usuarios
GET    /api/v1/usuarios/{idUsuario}
POST   /api/v1/usuarios
PUT    /api/v1/usuarios/{idUsuario}
DELETE /api/v1/usuarios/{idUsuario}
GET    /api/v1/usuarios/auth/email/{email}
GET    /api/v1/usuarios/internal/existe/{idUsuario}
GET    /api/v1/usuarios/internal/rut/{rut}
```

## Integraciones

```txt
auth_service → usuario_service
vehiculo_service → usuario_service
acceso_service → usuario_service
```

## Flujo principal

```txt
Crea/actualiza usuarios
→ almacena password hasheada con BCrypt
→ expone datos internos para autenticación e integraciones
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
Usuario
Rol
UsuarioController
UsuarioService
RolService
UsuarioRepository
RolRepository
UsuarioCreateRequest
UsuarioUpdateRequest
UsuarioResponse
UsuarioAuthResponse
UsuarioInternoResponse
UsuarioExisteResponse
```

## Notas

No devolver passwords en respuestas públicas. Solo el endpoint de auth entrega datos necesarios para validar login.
