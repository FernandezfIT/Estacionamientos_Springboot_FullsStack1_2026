# Auditoría documental y técnica

## Referencia y método

Hallazgos registrados antes de reescribir la documentación. Referencia publicada: `origin/main`, commit `78ef4d1`. Se inspeccionaron historial, ramas remotas, POM, perfiles, controladores, servicios, clientes HTTP, seguridad, pruebas, Compose, Dockerfile, entrypoint, SQL y documentos existentes. La presencia de código o configuración no acredita su funcionamiento extremo a extremo.

La copia original tenía una modificación local en `PlazaServiceTest.java`. Con autorización del propietario se creó un worktree separado y limpio, basado en `origin/main`; ese cambio no forma parte de esta entrega. `main` local y remoto coincidían después de `git fetch origin`.

## Diagnóstico inicial

- El README mezclaba presentación, catálogo de endpoints, instrucciones antiguas y propuestas futuras. Afirmaba validación integral sin adjuntar resultados reproducibles y proponía agregar Swagger aunque ya está configurado.
- Gateway y Eureka están integrados en `main` mediante los PR #8, #9 y #10; la contenedorización, mediante el PR #11. No se consultó la API de PR: la evidencia son los commits de merge locales.
- `origin/testing/eureka-gateway` (`5ef5682`) es ancestro de `origin/main`: no contiene commits exclusivos pendientes. Su diferencia con `main` afecta 16 archivos: 10 inserciones y 557 eliminaciones al comparar en ese sentido; corresponde a infraestructura Docker posterior presente en `main`.
- Las ramas `feature/*` examinadas no tienen commits exclusivos respecto de `main`. `usuario-service/Logs-Swagger` conserva `f88aab3` no alcanzable desde `main`, con cambios en Auth (errores y logs). No se asume integración semántica ni se recomienda merge sin revisión.
- Los doce módulos declaran Java 21 y Spring Boot 4.0.6. Gateway usa Spring Cloud Gateway Server WebMVC, no el Gateway reactivo. Gateway y Eureka declaran Spring Cloud 2025.1.1.
- Diez servicios de negocio tienen JWT y Spring Security; la autorización no es uniforme: Usuario, Vehículo, Acceso, Movimiento y Cierre restringen por autoridades; Plaza, Reserva, Liberación y Reporte exigen autenticación para su API sin filtro equivalente por rol. Auth publica login.
- Usuario publica consultas internas sin autenticación; la respuesta de autenticación incluye el hash de contraseña. No es una frontera de seguridad adecuada para exposición pública. No se publican ejemplos de credenciales ni valores de respuesta.
- Ocho servicios persisten en esquemas MySQL propios mediante JPA/Flyway. Auth y Reporte no usan persistencia de negocio, aunque el SQL inicial crea también `auth_db` y `reporte_db`.
- Compose define MySQL 8.4 y un único contenedor con doce JVM. El entrypoint usa pausas fijas y `tail -f /dev/null`: que el contenedor permanezca activo no demuestra salud de todos los procesos.
- `.env.ejemplo` no incluye `JWT_SECRET` ni `JWT_EXPIRATION`, requeridas por Compose. El SQL crea un usuario y contraseña literales: no interpola `MYSQL_DEV_USER` ni `MYSQL_DEV_PASSWORD`. Cambiar solo `.env` puede romper la conexión.
- Se encontraron valores sensibles de desarrollo versionados en perfiles, SQL y ejemplos documentales. No se reproducen aquí. Debe revisarse su uso real y rotarlos si se reutilizan; suprimirlos de la versión actual no los elimina del historial. No se rotaron ni reescribieron commits.
- El perfil base del Gateway omite la ruta plural `/api/v1/tipos-vehiculos/**`; el perfil Docker sí la incluye. Swagger está configurado por servicio, no agregado en Gateway.
- `springdoc` 2.8.9 convive con Spring Boot 4.0.6; sus endpoints y compatibilidad efectiva requieren prueba HTTP, no basta con resolver dependencias o cargar contexto.
- Los perfiles `prod` de varios servicios solo contienen el nombre; no constituyen un despliegue productivo completo. Las pruebas tienen configuración adicional en `src/test/resources`, además de `src/main/resources`.
- La eliminación individual de una reserva solo elimina su registro: no libera su plaza. Cierre realiza explícitamente ambas operaciones.
- Acceso, Reserva y Liberación capturan errores de registro en Movimiento y mantienen la operación principal. No hay garantía de bitácora completa ni transacción distribuida; las transacciones JPA son locales.

## Vigencia documental

Se conservan los cuatro documentos previos:

| Documento | Contexto y divergencias |
|---|---|
| `INSTRUCTIVO_MYSQL_LOCAL.md` | Inicio de guía local; bloque de código sin cerrar e instrucciones incompletas. La guía actual de instalación lo complementa. |
| `MANUAL_ESTANDARIZACION_MICROSERVICIOS.md` | Convenciones y diseño inicial, no inventario vigente: propone BD para Auth/Reporte y dependencias uniformes. |
| `patron_integracion_movimiento_reserva_liberacion.md` | Diseño de integración; ya hay clientes implementados. Útil para explicar DTO, referencia y tolerancia a fallos, no como tarea pendiente. |
| `resumen_avance_microservicios_estacionamientos.md` | Hito temprano centrado en Auth/Usuario/Vehículo; no describe el estado completo publicado. |

El README anterior permanece consultable en el historial de `78ef4d1`; endpoints, flujos, alcance y mejoras útiles se trasladan a documentación vigente, sin duplicar sus afirmaciones de validación.

## Validaciones ejecutadas sobre la referencia

- Git: `status`, `remote -v`, `branch -a`, `log -5 --oneline`, `fetch origin`, comparación local/remota, `log main..rama`, `diff --stat` y `diff --name-status`.
- Herramientas: OpenJDK 21.0.12.1, Maven 3.8.7, Docker 29.9.2 y Compose 5.1.3 disponibles. `docker info` falla porque no existe el socket del daemon configurado. No se inició MySQL ni se construyeron imágenes.
- `docker compose config --quiet` con variables ficticias: correcto. Esto valida el modelo Compose, no las credenciales ni el arranque.
- `bash -n docker/microservices/entrypoint.sh`: correcto, solo sintaxis de shell.
- `mvn -B -f <servicio>/pom.xml test`, en los diez módulos de negocio: 84 pruebas, 80 satisfactorias, 0 fallos de aserción, 4 errores de contexto y 0 omitidas. Los cuatro errores son conexión MySQL rechazada en Vehículo, Acceso, Movimiento y Cierre. Detalle en [pruebas](pruebas.md).
- Selección posterior `-Dtest=*ServiceTest` en los diez módulos: 74 unitarias satisfactorias. `mvn test` en Gateway y Eureka: compilación correcta, sin tests implementados.
- Todos los enlaces relativos de README/docs apuntan a rutas existentes; bloques de código cerrados. Las tablas y jerarquías de títulos de las guías vigentes pasaron comprobación estática. Los 54 endpoints del catálogo coinciden con las anotaciones de los controladores; puertos `dev`/`docker` coincidentes en los diez servicios.
- Exclusiones verificadas con `git check-ignore`: `.env`, `.env.local`, `target`, temporales, clases e IDE; `.env.ejemplo` permanece permitido. Escaneo de contenido agregado contra secretos literales encontrados en propiedades: ninguna coincidencia nueva, sin imprimir valores.
- Markdown de los once documentos procesado con markdown-it; los dos diagramas aceptados por el parser Mermaid local. Herramientas instaladas únicamente en un directorio temporal, sin agregar dependencias al proyecto.
- API de Markdown de GitHub (`POST /markdown`, modo GFM, sin autenticación): HTTP 200 para el README, un título H1 y dos tablas en el HTML. Esto valida su conversión remota a HTML, no la visualización final de los diagramas en la página del PR.

No se probó el flujo HTTP completo, login real, registro en Eureka, enrutamiento efectivo ni Swagger en navegador. La inspección visual final en la página del repositorio/PR queda pendiente. No se afirma disponibilidad operativa por contar con archivos de configuración. Los resultados anteriores no son cifras de cobertura.

## Integridad de la entrega

El diff final comprende únicamente README, documentación y archivos de exclusión. No se eliminan documentos ni se modifican Java, POM, perfiles, Dockerfile, Compose, entrypoint o SQL. `git diff --check` no detecta problemas de espacios; el worktree documental queda limpio tras los commits. La modificación de PlazaServiceTest en la copia original conserva exactamente el mismo hash de diff que antes del trabajo; `main` permanece en `78ef4d1`.

## Impedimentos y revisión del propietario

Se ampliaron `.gitignore` y `.dockerignore` para variantes de `.env`, artefactos, temporales y salidas de pruebas; Git conserva los ejemplos públicos. No se retiró ningún archivo del seguimiento. Estas exclusiones no corrigen valores sensibles ya versionados en otros archivos.

La corrección de credenciales, SQL, rutas, compatibilidad de bibliotecas, perfiles y supervisión de procesos es funcional y queda fuera de esta entrega documental. La revisión de seguridad debe preceder a cualquier exposición pública de la aplicación. La ausencia de GitHub CLI impide utilizar `gh pr create` en este entorno; los pasos manuales de publicación se entregan en [contribuciones](contribuciones.md).
