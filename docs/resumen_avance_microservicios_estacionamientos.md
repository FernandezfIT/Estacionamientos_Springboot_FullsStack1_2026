# Resumen de avance del proyecto Estacionamientos

Este documento resume lo trabajado hasta ahora en los microservicios `auth_service`, `usuario_service` y `vehiculo_service`.

El foco del resumen es indicar las clases creadas o modificadas, su ubicación dentro del microservicio y la función que cumplen dentro de la arquitectura.

---

## 1. Decisiones generales de arquitectura

### Microservicios trabajados

| Microservicio | Puerto | Responsabilidad principal | Estado |
|---|---:|---|---|
| `auth_service` | `8080` | Login, validación de credenciales y generación de JWT | Funcional y probado |
| `usuario_service` | `8081` | Gestión de usuarios y roles | Funcional, probado y protegido por roles |
| `vehiculo_service` | `8082` | Gestión de vehículos y tipos de vehículo | Compila, pendiente de pruebas completas |

### Reglas arquitectónicas aplicadas

- Cada microservicio administra su propia base de datos.
- Ningún microservicio accede directamente a la base de datos de otro.
- La comunicación entre microservicios se realiza por HTTP.
- `auth_service` es una excepción: no tiene base de datos propia.
- Los roles se manejan desde `usuario_service`.
- Los permisos se validan usando JWT y `hasAuthority(...)`.
- Se usa `Jefe_Seguridad`, `Jefe_SSDD`, `Guardia` y `Funcionario` como nombres oficiales de roles.
- Se evita usar `@Data` en entidades JPA con relaciones.
- Se usa `@Getter`, `@Setter`, `@NoArgsConstructor` y `@AllArgsConstructor` en modelos JPA.
- Las contraseñas se guardan con BCrypt.
- Los endpoints públicos o internos quedan definidos explícitamente.

---

# 2. `auth_service`

## 2.1. Objetivo

`auth_service` se encarga de:

- recibir credenciales;
- consultar a `usuario_service`;
- validar password;
- generar JWT;
- devolver datos básicos del usuario autenticado.

No administra usuarios directamente y no tiene base de datos propia.

---

## 2.2. Configuración

### `src/main/resources/application.properties`

Función:

- Define el nombre del microservicio.
- Activa el perfil `dev`.

### `src/main/resources/application-dev.properties`

Función:

- Define el puerto `8080`.
- Desactiva autoconfiguración de base de datos, JPA/Hibernate y Flyway.
- Define URL de `usuario_service`.
- Define timeouts para comunicación HTTP.
- Define `jwt.secret` y `jwt.expiration`.
- Configura logs y errores para desarrollo.

Decisión importante:

`auth_service` excluye:

- `DataSourceAutoConfiguration`;
- `HibernateJpaAutoConfiguration`;
- `FlywayAutoConfiguration`.

Esto evita que Spring intente levantar una base de datos inexistente para Auth.

---

## 2.3. DTOs

### `dto/request/LoginRequest.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/dto/request/LoginRequest.java`

Función:

- Recibe las credenciales desde el cliente.
- Campos principales:
  - `email`
  - `password`
- Usa validaciones como `@NotBlank` y `@Email`.

---

### `dto/response/AuthResponse.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/dto/response/AuthResponse.java`

Función:

- Devuelve la respuesta del login.
- Incluye:
  - token JWT;
  - tipo de token;
  - id de usuario;
  - nombre;
  - apellido;
  - email;
  - rol.

No devuelve password.

---

### `dto/response/ErrorResponse.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/dto/response/ErrorResponse.java`

Función:

- Estandariza respuestas de error.
- Incluye:
  - timestamp;
  - status;
  - error;
  - message;
  - path.

---

## 2.4. Cliente HTTP

### `client/dto/UsuarioAuthResponse.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/client/dto/UsuarioAuthResponse.java`

Función:

- Representa la respuesta recibida desde `usuario_service`.
- Incluye datos básicos del usuario y password encriptada.
- Se usa solo internamente entre microservicios.

---

### `client/UsuarioClient.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/client/UsuarioClient.java`

Función:

- Consulta a `usuario_service` usando HTTP.
- Busca usuarios por email mediante el endpoint interno:

`GET /api/v1/usuarios/auth/email/{email}`

- Devuelve `Optional<UsuarioAuthResponse>`.

---

## 2.5. Configuración auxiliar

### `config/RestTemplateConfig.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/config/RestTemplateConfig.java`

Función:

- Configura un `RestTemplate` para llamadas hacia `usuario_service`.
- Define timeouts de conexión y lectura.

---

### `config/SecurityBeansConfig.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/config/SecurityBeansConfig.java`

Función:

- Declara el bean `PasswordEncoder`.
- Usa BCrypt para validar contraseñas.

---

### `config/SecurityConfig.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/config/SecurityConfig.java`

Función:

- Permite acceso público a:

`POST /api/v1/auth/login`

- Bloquea todo lo demás con `denyAll()`.
- Desactiva CSRF, form login y HTTP Basic.
- Configura sesión stateless.

---

## 2.6. Seguridad

### `security/JwtService.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/security/JwtService.java`

Función:

- Genera JWT.
- Incluye claims:
  - `idUsuario`;
  - `nombre`;
  - `apellido`;
  - `rol`.
- Usa el email como subject del token.
- Valida token y permite extraer email.

---

## 2.7. Service

### `service/AuthService.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/service/AuthService.java`

Función:

- Recibe `LoginRequest`.
- Normaliza email.
- Consulta usuario en `usuario_service`.
- Valida password con `PasswordEncoder`.
- Genera JWT con `JwtService`.
- Devuelve `AuthResponse`.

---

## 2.8. Controller

### `controller/AuthController.java`

Ubicación:

`auth_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/auth_service/controller/AuthController.java`

Función:

- Expone:

`POST /api/v1/auth/login`

- Recibe `LoginRequest`.
- Devuelve `AuthResponse`.

---

## 2.9. Excepciones

### `exception/CredencialesInvalidasException.java`

Función:

- Representa error de login por credenciales incorrectas.

### `exception/UsuarioServiceException.java`

Función:

- Representa error al comunicarse con `usuario_service`.

### `exception/GlobalExceptionHandler.java`

Función:

- Centraliza manejo de errores del microservicio.
- Devuelve errores claros para:
  - credenciales inválidas;
  - usuario_service no disponible;
  - errores de validación;
  - JSON mal formado;
  - errores internos.

---

## 2.10. Estado de `auth_service`

Estado actual:

- Compila.
- Levanta correctamente en puerto `8080`.
- Login probado con éxito.
- Generación de JWT probada.
- Errores esperados probados:
  - credenciales incorrectas;
  - email inexistente;
  - validaciones;
  - JSON mal formado;
  - usuario_service apagado.

---

# 3. `usuario_service`

## 3.1. Objetivo

`usuario_service` administra:

- usuarios;
- roles;
- relación usuario-rol;
- endpoint interno para Auth;
- endpoint interno para validar existencia de usuario desde otros microservicios.

---

## 3.2. Base de datos y migraciones

### `db/migration/V1__crear_tablas_rol_usuario.sql`

Función:

- Crea tabla `rol`.
- Crea tabla `usuario`.
- Relaciona `usuario.id_rol` con `rol.id_rol`.

### `db/migration/V2__insertar_datos_rol_usuario.sql`

Función:

- Inserta roles base:
  - `Jefe_Seguridad`
  - `Jefe_SSDD`
  - `Guardia`
  - `Funcionario`
- Inserta usuarios iniciales.
- Guarda passwords usando BCrypt.

---

## 3.3. Modelos

### `model/Rol.java`

Ubicación:

`usuario_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/usuario_service/model/Rol.java`

Función:

- Representa la tabla `rol`.
- Campos:
  - `idRol`;
  - `nombre`;
  - `descripcion`.

---

### `model/Usuario.java`

Ubicación:

`usuario_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/usuario_service/model/Usuario.java`

Función:

- Representa la tabla `usuario`.
- Campos principales:
  - `idUsuario`;
  - `rut`;
  - `nombre`;
  - `apellido`;
  - `email`;
  - `password`;
  - `telefono`;
  - `fechaCreacion`;
  - `rol`.

Relación:

- Muchos usuarios pueden tener un rol.
- Usa `@ManyToOne` hacia `Rol`.
- Usa `@JoinColumn(name = "id_rol")`.

---

## 3.4. Repositorios

### `repository/RolRepository.java`

Función:

- Administra acceso a datos de `Rol`.
- Métodos:
  - `findByNombre`;
  - `existsByNombre`.

---

### `repository/UsuarioRepository.java`

Función:

- Administra acceso a datos de `Usuario`.
- Métodos:
  - `findByEmail`;
  - `existsByEmail`;
  - `existsByRut`.

Usa `@EntityGraph(attributePaths = "rol")` en `findByEmail` para cargar el rol junto con el usuario.

---

## 3.5. DTOs

### `dto/request/UsuarioCreateRequest.java`

Función:

- Recibe datos para crear usuario.
- Usa `idRol`, no texto libre de rol.

---

### `dto/request/UsuarioUpdateRequest.java`

Función:

- Recibe datos para actualizar usuario.
- Permite dejar password vacía para conservar la actual.

---

### `dto/response/UsuarioResponse.java`

Función:

- Devuelve usuario sin password.
- Incluye:
  - datos básicos;
  - `idRol`;
  - nombre del rol.

---

### `dto/response/UsuarioAuthResponse.java`

Función:

- DTO interno usado por `auth_service`.
- Devuelve datos del usuario incluyendo password encriptada.

---

### `dto/response/RolResponse.java`

Función:

- Devuelve datos del catálogo de roles.

---

### `dto/response/UsuarioExisteResponse.java`

Función:

- Responde si un usuario existe.
- Usado por otros microservicios, como `vehiculo_service`.

---

### `dto/response/ErrorResponse.java`

Función:

- Estandariza respuestas de error.

---

## 3.6. Services

### `service/RolService.java`

Función:

- Lista roles.
- Busca rol por ID.
- Mapea `Rol` a `RolResponse`.

---

### `service/UsuarioService.java`

Función:

- Lista usuarios.
- Busca usuario por ID.
- Crea usuarios.
- Actualiza usuarios.
- Elimina usuarios.
- Busca usuario por email para Auth.
- Valida existencia de usuario por ID.
- Encripta passwords con BCrypt.
- Valida duplicados por RUT y email.
- Valida existencia de rol.

Métodos principales:

- `listarUsuarios()`
- `obtenerUsuarioPorId(Long idUsuario)`
- `crearUsuario(UsuarioCreateRequest request)`
- `actualizarUsuario(Long idUsuario, UsuarioUpdateRequest request)`
- `eliminarUsuario(Long idUsuario)`
- `obtenerUsuarioAuthPorEmail(String email)`
- `existeUsuarioPorId(Long idUsuario)`

---

## 3.7. Controllers

### `controller/RolController.java`

Función:

- Expone endpoints del catálogo de roles.

Endpoints:

- `GET /api/v1/roles`
- `GET /api/v1/roles/{idRol}`

---

### `controller/UsuarioController.java`

Función:

- Expone CRUD de usuarios.
- Expone endpoints internos para Auth y validación entre microservicios.

Endpoints principales:

- `GET /api/v1/usuarios`
- `GET /api/v1/usuarios/{idUsuario}`
- `POST /api/v1/usuarios`
- `PUT /api/v1/usuarios/{idUsuario}`
- `DELETE /api/v1/usuarios/{idUsuario}`

Endpoints internos:

- `GET /api/v1/usuarios/auth/email/{email}`
- `GET /api/v1/usuarios/internal/existe/{idUsuario}`

---

## 3.8. Seguridad

### `security/JwtService.java`

Función:

- Valida JWT generado por `auth_service`.
- Extrae email, idUsuario y rol.

---

### `security/JwtAuthenticationFilter.java`

Función:

- Lee header `Authorization`.
- Extrae token Bearer.
- Valida JWT.
- Crea autenticación de Spring Security.
- Inserta el rol como authority.

---

### `config/SecurityConfig.java`

Función:

- Configura permisos por rol.
- Permite endpoints internos necesarios.
- Protege gestión de usuarios y roles.

Reglas aplicadas:

- `GET /api/v1/usuarios/auth/email/**`: público interno.
- `GET /api/v1/usuarios/internal/existe/**`: público interno.
- `/api/v1/usuarios/**`: solo `Jefe_Seguridad` y `Jefe_SSDD`.
- `/api/v1/roles/**`: solo `Jefe_Seguridad` y `Jefe_SSDD`.
- Todo lo demás: bloqueado.

---

## 3.9. Excepciones

### `exception/UsuarioNoEncontradoException.java`

Función:

- Usuario no existe.

### `exception/RolNoEncontradoException.java`

Función:

- Rol no existe.

### `exception/RecursoDuplicadoException.java`

Función:

- RUT o email duplicado.

### `exception/GlobalExceptionHandler.java`

Función:

- Maneja errores de negocio, validación, JSON mal formado, integridad y errores generales.

---

## 3.10. Estado de `usuario_service`

Estado actual:

- Compila.
- Levanta en puerto `8081`.
- Flyway crea tablas correctamente.
- Roles y usuarios se insertan correctamente.
- CRUD de usuarios probado.
- Catálogo de roles probado.
- Endpoint interno para Auth probado.
- Endpoint interno de existencia de usuario agregado.
- Seguridad por JWT y roles probada.
- Permisos probados con:
  - `Jefe_Seguridad`;
  - `Jefe_SSDD`;
  - `Guardia`;
  - `Funcionario`.

---

# 4. `vehiculo_service`

## 4.1. Objetivo

`vehiculo_service` administra:

- vehículos;
- tipos de vehículo;
- asociación lógica vehículo-usuario;
- validación de dueño consultando `usuario_service`.

---

## 4.2. Base de datos y migraciones

### `db/migration/V1__crear_tablas_tipo_vehiculo_vehiculo.sql`

Función:

- Crea tabla `tipo_vehiculo`.
- Crea tabla `vehiculo`.
- Relaciona `vehiculo.id_tipo_vehiculo` con `tipo_vehiculo.id_tipo_vehiculo`.

Importante:

- `id_usuario` no tiene FK SQL.
- Es una referencia lógica hacia `usuario_service`.

---

### `db/migration/V2__insertar_datos_tipo_vehiculo.sql`

Función:

- Inserta catálogo base de tipos de vehículo:
  - `Sedan`;
  - `Camioneta`;
  - `Moto`;
  - `SUV`;
  - `Hatchback`;
  - `Furgon`;
  - `Station_Wagon`.

---

## 4.3. Modelos

### `model/TipoVehiculo.java`

Ubicación:

`vehiculo_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/vehiculo_service/model/TipoVehiculo.java`

Función:

- Representa la tabla `tipo_vehiculo`.
- Campos:
  - `idTipoVehiculo`;
  - `nombre`;
  - `descripcion`.

---

### `model/Vehiculo.java`

Ubicación:

`vehiculo_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/vehiculo_service/model/Vehiculo.java`

Función:

- Representa la tabla `vehiculo`.
- Campos:
  - `idVehiculo`;
  - `patente`;
  - `marca`;
  - `color`;
  - `tipoVehiculo`;
  - `idUsuario`.

Relaciones:

- `tipoVehiculo` usa `@ManyToOne` hacia `TipoVehiculo`.
- `idUsuario` es referencia lógica a `usuario_service`.

---

## 4.4. Repositorios

### `repository/TipoVehiculoRepository.java`

Función:

- Administra acceso a datos de `TipoVehiculo`.
- Métodos:
  - `findByNombre`;
  - `existsByNombre`.

---

### `repository/VehiculoRepository.java`

Función:

- Administra acceso a datos de `Vehiculo`.
- Métodos:
  - `findAll`;
  - `findById`;
  - `findByPatente`;
  - `findByIdUsuario`;
  - `existsByPatente`.

Usa `@EntityGraph(attributePaths = "tipoVehiculo")` para cargar tipo de vehículo junto con el vehículo.

---

## 4.5. DTOs

### `dto/request/VehiculoCreateRequest.java`

Función:

- Recibe datos para crear vehículo.
- Campos:
  - `patente`;
  - `marca`;
  - `color`;
  - `idTipoVehiculo`;
  - `idUsuario`.

---

### `dto/request/VehiculoUpdateRequest.java`

Función:

- Recibe datos para actualizar vehículo.
- Permite actualizar patente, marca, color, tipo de vehículo y dueño.

---

### `dto/response/VehiculoResponse.java`

Función:

- Devuelve información pública del vehículo.
- Incluye:
  - `idVehiculo`;
  - `patente`;
  - `marca`;
  - `color`;
  - `idTipoVehiculo`;
  - `tipoVehiculo`;
  - `idUsuario`.

---

### `dto/response/TipoVehiculoResponse.java`

Función:

- Devuelve información del catálogo de tipos de vehículo.

---

### `dto/response/ErrorResponse.java`

Función:

- Estandariza errores del microservicio.

---

## 4.6. Cliente HTTP

### `client/dto/UsuarioExisteResponse.java`

Ubicación:

`vehiculo_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/vehiculo_service/client/dto/UsuarioExisteResponse.java`

Función:

- Representa la respuesta desde `usuario_service`.
- Campos:
  - `idUsuario`;
  - `existe`.

---

### `client/UsuarioClient.java`

Ubicación:

`vehiculo_service/src/main/java/cl/duoc/fullstack1/grupo11/estacionamientos/vehiculo_service/client/UsuarioClient.java`

Función:

- Consulta a `usuario_service`.
- Valida si existe un usuario por ID.
- Consume:

`GET /api/v1/usuarios/internal/existe/{idUsuario}`

---

## 4.7. Configuración auxiliar

### `config/RestTemplateConfig.java`

Función:

- Configura `RestTemplate` para consultar `usuario_service`.
- Usa timeouts de conexión y lectura.
- En la versión actual se usa `SimpleClientHttpRequestFactory`.

---

## 4.8. Services

### `service/TipoVehiculoService.java`

Función:

- Lista tipos de vehículo.
- Busca tipo de vehículo por ID.
- Mapea `TipoVehiculo` a `TipoVehiculoResponse`.

Métodos principales:

- `listarTiposVehiculo()`
- `obtenerTipoVehiculoPorId(Long idTipoVehiculo)`
- `buscarTipoVehiculoPorId(Long idTipoVehiculo)`

---

### `service/VehiculoService.java`

Función:

- Lista vehículos.
- Busca vehículo por ID.
- Busca vehículo por patente.
- Lista vehículos por usuario.
- Crea vehículos.
- Actualiza vehículos.
- Elimina vehículos.
- Valida patente duplicada.
- Valida existencia de tipo de vehículo.
- Valida existencia de usuario dueño consultando `usuario_service`.

Métodos principales:

- `listarVehiculos()`
- `obtenerVehiculoPorId(Long idVehiculo)`
- `obtenerVehiculoPorPatente(String patente)`
- `listarVehiculosPorUsuario(Long idUsuario)`
- `crearVehiculo(VehiculoCreateRequest request)`
- `actualizarVehiculo(Long idVehiculo, VehiculoUpdateRequest request)`
- `eliminarVehiculo(Long idVehiculo)`

---

## 4.9. Controllers

### `controller/TipoVehiculoController.java`

Función:

- Expone endpoints del catálogo de tipos de vehículo.

Endpoints:

- `GET /api/v1/tipos-vehiculos`
- `GET /api/v1/tipos-vehiculos/{idTipoVehiculo}`

---

### `controller/VehiculoController.java`

Función:

- Expone CRUD y consultas de vehículos.

Endpoints:

- `GET /api/v1/vehiculos`
- `GET /api/v1/vehiculos/{idVehiculo}`
- `GET /api/v1/vehiculos/patente/{patente}`
- `GET /api/v1/vehiculos/usuario/{idUsuario}`
- `POST /api/v1/vehiculos`
- `PUT /api/v1/vehiculos/{idVehiculo}`
- `DELETE /api/v1/vehiculos/{idVehiculo}`

---

## 4.10. Seguridad

### `security/JwtService.java`

Función:

- Valida JWT generado por `auth_service`.
- Extrae email, idUsuario y rol.

---

### `security/JwtAuthenticationFilter.java`

Función:

- Lee el header `Authorization`.
- Valida token Bearer.
- Crea autenticación de Spring Security.
- Agrega el rol como authority.

---

### `config/SecurityConfig.java`

Función:

- Configura permisos por rol en `vehiculo_service`.

Reglas iniciales:

- `Jefe_Seguridad`: puede consultar, crear, actualizar y eliminar vehículos.
- `Jefe_SSDD`: puede consultar, crear, actualizar y eliminar vehículos.
- `Guardia`: puede consultar vehículos y tipos de vehículo.
- `Funcionario`: sin acceso por ahora.

Endpoints protegidos:

- `GET /api/v1/tipos-vehiculos/**`: `Jefe_Seguridad`, `Jefe_SSDD`, `Guardia`.
- `GET /api/v1/vehiculos/**`: `Jefe_Seguridad`, `Jefe_SSDD`, `Guardia`.
- `POST /api/v1/vehiculos/**`: `Jefe_Seguridad`, `Jefe_SSDD`.
- `PUT /api/v1/vehiculos/**`: `Jefe_Seguridad`, `Jefe_SSDD`.
- `DELETE /api/v1/vehiculos/**`: `Jefe_Seguridad`, `Jefe_SSDD`.
- Todo lo demás: bloqueado.

---

## 4.11. Excepciones

### `exception/VehiculoNoEncontradoException.java`

Función:

- Vehículo no existe.

### `exception/TipoVehiculoNoEncontradoException.java`

Función:

- Tipo de vehículo no existe.

### `exception/UsuarioNoEncontradoException.java`

Función:

- Usuario dueño no existe en `usuario_service`.

### `exception/UsuarioServiceException.java`

Función:

- Error al comunicarse con `usuario_service`.

### `exception/RecursoDuplicadoException.java`

Función:

- Patente duplicada.

### `exception/GlobalExceptionHandler.java`

Función:

- Centraliza errores de negocio, validación, JSON mal formado, integridad y errores generales.

---

## 4.12. Estado de `vehiculo_service`

Estado actual:

- Configuración base creada.
- Migraciones definidas.
- Modelos creados.
- Repositorios creados.
- DTOs creados.
- Excepciones creadas.
- Cliente HTTP hacia `usuario_service` creado.
- Services creados.
- Controllers creados.
- Seguridad JWT por rol creada.
- Compilación exitosa.
- Pruebas funcionales pendientes.

---

# 5. Estado general actual del sistema

## Completado

### `auth_service`

- Login funcional.
- JWT funcional.
- Integración con `usuario_service` probada.
- Manejo de errores probado.

### `usuario_service`

- Usuarios y roles funcionales.
- CRUD probado.
- Seguridad por rol probada.
- Endpoints internos disponibles para Auth y otros microservicios.

### `vehiculo_service`

- Implementación principal terminada.
- Compila correctamente.
- Falta probar endpoints en Postman.

---

# 6. Próximo paso recomendado

Probar `vehiculo_service` en Postman con tokens reales emitidos por `auth_service`.

Orden recomendado:

1. Levantar `usuario_service`.
2. Levantar `auth_service`.
3. Levantar `vehiculo_service`.
4. Obtener token de `Jefe_Seguridad`.
5. Obtener token de `Guardia`.
6. Probar tipos de vehículo.
7. Probar creación de vehículo.
8. Probar consultas por patente y usuario.
9. Probar errores:
   - sin token;
   - token inválido;
   - rol sin permiso;
   - patente duplicada;
   - tipo inexistente;
   - usuario inexistente.
