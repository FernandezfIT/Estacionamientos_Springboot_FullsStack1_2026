# Pruebas automatizadas

Referencia probada: `78ef4d1`, sin cambios en código, POM ni perfiles. Entorno: OpenJDK 21.0.12.1, Maven 3.8.7, sin MySQL disponible ni daemon Docker operativo.

## Qué se prueba

Los diez servicios de negocio incluyen clases `*ServiceTest` con JUnit Jupiter y Mockito. Simulan repositorios y clientes HTTP; prueban lógica de servicio, no la red ni las reglas completas de Spring Security. Cada módulo incluye además una prueba `contextLoads` con `@SpringBootTest`.

| Servicio | Tests unitarios | Componentes/casos observados |
|---|---|---|
| Auth | 6 | Login, normalización de email y rechazo de credenciales |
| Usuario | 6 | Listado, consulta, creación con BCrypt simulado, duplicados y actualización |
| Vehículo | 8 | Consultas, normalización, creación/actualización, duplicados y existencia de usuario |
| Plaza | 9 | Consultas, cambio de estado, ocupación y conflictos |
| Reserva | 8 | Consultas, creación/eliminación y disponibilidad de plaza |
| Acceso | 9 | Consultas, normalización, registro y pertenencia de vehículo |
| Movimiento | 10 | Registro, normalización y filtros, incluida fecha |
| Liberación | 7 | Consultas, plazas ocupadas/reservadas y rechazo de estados |
| Cierre | 8 | Consultas, limpieza operativa simulada, snapshot y cabecera de autorización |
| Reporte | 3 | Resumen global, por fecha y completo |

Gateway y Eureka tienen dependencias de prueba, pero no clases de test en la referencia examinada. No se identificó una suite extremo a extremo ni un informe de cobertura publicado que permita dar porcentajes.

## Ejecutar solo pruebas unitarias

Desde la raíz:

```bash
mvn -f auth_service/pom.xml '-Dtest=*ServiceTest' test
```

Para los diez módulos, en Bash:

```bash
for servicio in auth usuario vehiculo plaza reserva acceso movimiento liberacion cierre reporte; do
  mvn -B -f "${servicio}_service/pom.xml" '-Dtest=*ServiceTest' test || exit 1
done
```

Esta selección excluye las clases `*ApplicationTests`. Se ejecutó y terminó correctamente en los diez módulos: **74 pruebas, 0 fallos, 0 errores, 0 omitidas**. Requiere JDK/Maven y acceso a dependencias; no requiere servicios HTTP ni MySQL para los casos unitarios existentes.

## Ejecutar suites completas

Cada módulo es independiente; no hay `./mvnw` ni POM agregador en la raíz.

```bash
mvn -B -f vehiculo_service/pom.xml test
# Alternativa dentro de un módulo con Wrapper:
(cd vehiculo_service && ./mvnw test)
```

El primer comando se ejecutó en los diez módulos. La variante Wrapper se proporciona por la estructura versionada, pero no se ejecutó. El Wrapper puede descargar Maven la primera vez.

### Perfiles y dependencias de contexto

- Usuario, Plaza, Reserva y Liberación anotan `contextLoads` con `@ActiveProfiles("test")`; usan H2 y desactivan Flyway/Eureka. Plaza, Reserva y Liberación añaden configuración en `src/test/resources/application-test.properties` que no debe confundirse con el archivo homónimo de `src/main/resources`.
- Vehículo, Acceso, Movimiento y Cierre no seleccionan `test` en su clase de contexto: activan `dev` y requieren MySQL con credenciales/esquemas preparados. Sus fallos de conexión no son fallos de aserción unitaria.
- Auth y Reporte cargaron contexto sin BD; el perfil de desarrollo puede intentar conectarse a Eureka. Su éxito no demuestra login remoto ni consulta de reportes real.
- No se recomienda activar genéricamente `test` en todos los módulos sin revisar las propiedades HTTP requeridas. La configuración no es uniforme.
- H2 con Flyway desactivado no valida las migraciones MySQL.

### Resultados observados de suites completas

| Módulo | Total | Satisfactorias | Fallos de aserción | Errores | Omitidas | Resultado Maven |
|---|---|---|---|---|---|---|
| Auth | 7 | 7 | 0 | 0 | 0 | Correcto |
| Usuario | 7 | 7 | 0 | 0 | 0 | Correcto |
| Vehículo | 9 | 8 | 0 | 1 | 0 | Error |
| Plaza | 10 | 10 | 0 | 0 | 0 | Correcto |
| Reserva | 9 | 9 | 0 | 0 | 0 | Correcto |
| Acceso | 10 | 9 | 0 | 1 | 0 | Error |
| Movimiento | 11 | 10 | 0 | 1 | 0 | Error |
| Liberación | 8 | 8 | 0 | 0 | 0 | Correcto |
| Cierre | 9 | 8 | 0 | 1 | 0 | Error |
| Reporte | 4 | 4 | 0 | 0 | 0 | Correcto |
| **Total** | **84** | **80** | **0** | **4** | **0** | **6 suites correctas, 4 con error** |

Los cuatro errores corresponden a `contextLoads`: `CommunicationsException` y conexión MySQL rechazada. Los resultados se obtuvieron de XML Surefire y salidas Maven, no se infirieron del número de anotaciones. Una ejecución posterior de solo unitarias también aprobó las 74; no se suman como 158 pruebas diferentes.

También se ejecutaron:

```bash
mvn -B -f eureka_server/pom.xml test
mvn -B -f api_gateway/pom.xml test
```

Ambos finalizaron correctamente y compilaron sus módulos; Maven informó ausencia de tests. No es una prueba de registro ni enrutamiento.

Los reportes se generan localmente en `target/surefire-reports/` por módulo, excluidos de Git. Las salidas completas pueden incluir datos sensibles de configuración; no se incorporaron al repositorio.

## Validaciones pendientes

1. Arranque completo con MySQL 8.4 y migraciones Flyway, en un entorno aislado.
2. Registro efectivo en Eureka y rutas por Gateway, incluida diferencia de tipos de vehículos entre perfiles.
3. Login, permisos por rol y exposición de consultas internas de Usuario.
4. Flujos de reserva, acceso, liberación, reportes y cierre con HTTP/BD reales.
5. Fallos parciales: verificar consistencia de plazas y bitácora cuando falla un servicio remoto.
6. Compatibilidad y disponibilidad de Swagger/OpenAPI por HTTP.

No se ejecutaron estas validaciones, ni se comprobó despliegue productivo. Para preparar el entorno, consultar [instalación](instalacion.md).
