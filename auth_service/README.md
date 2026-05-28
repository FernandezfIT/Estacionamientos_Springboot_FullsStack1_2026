# auth_service

## Responsabilidad

Autentica usuarios y genera tokens JWT. Consulta a `usuario_service` para validar credenciales.

## Puerto

```txt
8080
```

## Base de datos

```txt
Sin BD funcional propia
```

## Endpoint base

```txt
/api/v1/auth
```

## Funciones principales

```txt
login
validación de password con BCrypt
generación de JWT
```

## Endpoints

```http
POST /api/v1/auth/login
```

## Integraciones

```txt
auth_service → usuario_service
```

## Flujo principal

```txt
Cliente envía email/password
→ auth_service consulta usuario por email
→ valida password con BCrypt
→ genera JWT
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
AuthController
AuthService
JwtService
LoginRequest
AuthResponse
GlobalExceptionHandler
```

## Notas

BCrypt no se usa para desencriptar. Solo compara password ingresada contra el hash almacenado.
