# Instalación y ejecución local

Referencia: `main` en `78ef4d1`. Se revisó la configuración y se ejecutaron pruebas Maven, pero **no se levantó el sistema completo**: el daemon Docker no estaba disponible. Los comandos de arranque siguientes son una guía basada en los archivos publicados, condicionada a resolver la preparación de credenciales.

## Requisitos

| Modalidad | Obligatorio | Opcional |
|---|---|---|
| Docker | Git, Docker con daemon activo, Compose con subcomando `docker compose`, acceso a imágenes y Maven Central | curl, navegador, cliente MySQL |
| JVM locales | Git, JDK 21, Maven o Wrapper por módulo, MySQL preparado con esquemas y permisos | Docker para MySQL, IDE |

Compose especifica MySQL 8.4. El Dockerfile usa `maven:3.9-eclipse-temurin-21` y `eclipse-temurin:21-jre`. Las pruebas se ejecutaron con JDK 21.0.12.1 y Maven 3.8.7; no se establece ese Maven como mínimo oficial. No se midió el consumo total de memoria de las doce JVM.

## Clon y variables

```bash
git clone https://github.com/FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
cd Estacionamientos_Springboot_FullsStack1_2026
cp .env.ejemplo .env
```

En PowerShell, el último paso es `Copy-Item .env.ejemplo .env`. Editar `.env` localmente sin adjuntarlo a incidencias, PR ni logs públicos. El archivo de ejemplo **no está completo**.

| Variable | Uso en Compose | Preparación |
|---|---|---|
| `MYSQL_ROOT_PASSWORD` | Contraseña inicial de root MySQL | Definir un valor local nuevo |
| `MYSQL_PORT` | Puerto externo MySQL; por defecto 3306 | Evitar conflictos; no cambia el puerto interno |
| `MYSQL_DEV_USER` | Usuario enviado a las JVM con perfil Docker | Debe existir con permisos sobre los esquemas |
| `MYSQL_DEV_PASSWORD` | Contraseña enviada a las JVM | Debe coincidir con la del usuario real |
| `JWT_SECRET` | Secreto común enviado a servicios de negocio | Falta en el ejemplo; generar localmente un secreto aleatorio, no reutilizar el versionado |
| `JWT_EXPIRATION` | Duración del token en milisegundos | Falta en el ejemplo; por ejemplo 3600000 para una hora |
| `TZ` | Zona horaria de contenedores | Opcional; por defecto America/Santiago |

### Bloqueo de inicialización que requiere atención

[El SQL inicial](../docker/mysql/init/01-create-database.sql) contiene usuario y contraseña literales; **no lee ni interpola las variables de `.env`**. El montaje del directorio SQL en la imagen oficial de MySQL no realiza sustitución de variables automáticamente.

Por ello, no se ofrece una secuencia garantizada de «copiar `.env` y levantar todo». Antes de ejecutar los servicios, el propietario debe aprovisionar localmente un usuario con credenciales nuevas y permisos sobre los ocho esquemas de negocio, y hacer coincidir ese usuario con las variables enviadas a las JVM. Puede revisar privadamente el SQL existente como referencia de esquemas/permisos, sin copiar sus credenciales a documentación. Automatizar de forma segura este paso requeriría un cambio funcional separado, no realizado aquí.

Los scripts iniciales solo se ejecutan al inicializar un directorio de datos vacío. Cambiar `.env` o el SQL después no actualiza usuarios de una base ya creada. No utilizar `docker compose down -v` para intentar corregirlo: eliminaría datos. No se ejecutó ninguna eliminación de volúmenes ni rotación durante esta revisión.

## Modalidad Docker

Comprobar primero el motor:

```bash
docker info
docker compose version
docker compose config --quiet
```

`config --quiet` comprueba Compose sin imprimir la configuración interpolada, que podría contener secretos. Se validó aquí utilizando valores ficticios. No acredita conectividad ni permisos MySQL.

Para preparar y revisar MySQL por separado:

```bash
docker compose up -d mysql-local
docker compose ps
```

Con MySQL preparado y las credenciales coherentes según el apartado anterior:

```bash
docker compose up --build -d
docker compose logs -f microservicios
```

No se ejecutaron estos comandos de arranque en esta auditoría. El Dockerfile compila cada módulo con `mvn -B clean package -DskipTests`; una imagen construida no acredita pruebas aprobadas. Compose lanza **dos contenedores**, no doce: MySQL y un contenedor con Eureka, Gateway y los diez servicios de negocio.

El entrypoint espera intervalos fijos entre lanzamientos. Esperar los mensajes de arranque de cada aplicación; un proceso puede fallar mientras el contenedor sigue activo debido a `tail -f /dev/null`.

### Comprobaciones propuestas después del arranque

- Abrir `http://localhost:8761/` y revisar el registro de los diez servicios y Gateway.
- Consultar un endpoint de negocio con token y autoridad apropiados, tanto directo como por `http://localhost:8090`.
- Abrir Swagger de cada módulo y revisar errores HTTP; no asumir compatibilidad por ver una dependencia en el POM.
- No usar únicamente `/actuator/health`: varios SecurityConfig no permiten esa ruta, aunque esté expuesta por propiedades.

Ejemplos de conectividad, pendientes de ejecución y sin credenciales en los comandos:

```bash
curl -i http://localhost:8761/
curl -i http://localhost:8080/v3/api-docs
curl -i http://localhost:8090/api/v1/usuarios
```

El último comando, sin token, debería ser rechazado por seguridad si el enrutamiento funciona. Un rechazo no valida el flujo completo; un 5xx puede indicar registro, enrutamiento o dependencias fallidos. Para login y operaciones autorizadas, utilizar datos locales propios y el [catálogo de API](api-endpoints.md), sin publicar tokens ni respuestas sensibles.

Para detener conservando los datos:

```bash
docker compose down
```

## Modalidad JVM individual

No mantener a la vez el contenedor `microservicios` y las JVM locales: competirían por los puertos 8080–8089, 8090 y 8761. Se puede iniciar solo `mysql-local` con Compose o usar una instancia MySQL local.

Los ocho esquemas requeridos son los indicados en [arquitectura](arquitectura.md). Flyway crea sus tablas al iniciar los módulos persistentes; Auth y Reporte no requieren BD de negocio. No ejecutar migraciones de prueba sobre datos valiosos.

Los servicios de negocio activan `dev` por defecto y contienen valores locales versionados. Establecer credenciales propias mediante variables Spring (`SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`) y un secreto común mediante `JWT_SECRET`. Configurarlas en el entorno privado de cada proceso. Para Auth, establecer también `JWT_EXPIRATION` si se necesita reemplazar la duración predeterminada.

Las URLs `dev` esperan MySQL en `localhost:3306`. Si `MYSQL_PORT` cambia, sobrescribir `SPRING_DATASOURCE_URL` para **cada servicio persistente**, conservando su esquema. `.env` no se carga automáticamente por `mvn spring-boot:run`: las variables deben estar disponibles en el entorno real del proceso. No recomendar `source .env` sin revisar su sintaxis.

Orden sugerido, derivado del entrypoint: Eureka → Usuario → Vehículo → Plaza → Movimiento → Auth → Reserva → Liberación → Acceso → Reporte → Cierre → Gateway. No es una validación de salud; comprobar cada aplicación antes de consumirla.

En terminales separadas, desde la raíz:

```bash
mvn -f eureka_server/pom.xml spring-boot:run
mvn -f usuario_service/pom.xml spring-boot:run
# Repetir para los módulos anteriores, en el orden indicado
mvn -f api_gateway/pom.xml spring-boot:run
```

Los servicios de negocio también tienen Wrapper:

```bash
cd usuario_service
./mvnw spring-boot:run
# Solo compilación:
./mvnw clean compile
```

En Windows usar `mvnw.cmd`. Gateway y Eureka no incluyen Wrapper. Los arranques individuales y la descarga del Wrapper no se probaron; la ejecución de pruebas utilizó Maven instalado.

## Incidencias conocidas

- Credenciales de desarrollo publicadas: revisar y rotar si fueron reutilizadas; no exponer MySQL ni la aplicación en una red pública.
- El Gateway base omite la ruta plural de tipos de vehículos; usar el puerto 8082 para esa consulta hasta una corrección funcional separada. Docker sí configura esa ruta.
- springdoc 2.8.9 y Boot 4.0.6 requieren comprobar Swagger por HTTP; los tests de contexto no bastan.
- No hay configuración productiva integral ni supervisión por JVM en el contenedor común.

El [instructivo original de MySQL](INSTRUCTIVO_MYSQL_LOCAL.md) se conserva como antecedente; esta guía documenta el estado publicado y sus impedimentos.
