# Desarrollo y contribuciones

## Trabajo colaborativo

Proyecto académico de FullStack 1, Ingeniería en Informática, Duoc UC. La evidencia de contribución es el historial alcanzable desde `origin/main` (`78ef4d1`), no el número de carpetas ni una asignación exclusiva de módulos. Los identificadores de autor son los registrados por Git; no se reproducen correos personales.

El antecedente declarado por Felipe Fernández se contrastó con los commits de `FernandezfIT`. Esto respalda participación, no autoría exclusiva ni medición del esfuerzo total. La coordinación fuera de Git y la distribución exacta de tareas requieren confirmación del equipo.

## Aportes documentables de Felipe Fernández

| Área | Evidencia representativa del historial |
|---|---|
| Auth y Usuario | `8ee5bf9` configuración Auth; `967e650` servicios/controladores de Usuario; `16f592c` y `b0d6e69` integración y roles |
| Vehículo | `3cf9cba` modelos/BD; `171119b` finalización e integración con Usuario |
| Acceso | `2a3ddf9` desarrollo; `7957f18` preparación de servicios relacionados |
| Movimiento | `c71bfce` modelo/BD; `7cc58da` finalización e integración con Acceso; `574e318` consultas por día y conexión con Cierre |
| Cierre | `8994cc4`, `5a26479`, `a00b301`: configuración, desarrollo e integración con resumen diario |
| Pruebas unitarias | `98fd352` Usuario, `3f9507b` Vehículo, `a8d87bd` Movimiento, `e67f3b7` Acceso, `74da3d4` Cierre y `4d7ba56` Auth |
| Documentación/OpenAPI/logs | `d28971b`, `4e6be43`, `c2803e6`, `823a4e6` y otros commits de configuración |
| Entorno MySQL inicial | `42d419d`, `03ef74f`: estructura y entorno Docker local inicial |

La integración de flujos y las pruebas indicadas sí tienen respaldo. No se atribuye a Felipe la implementación final del Gateway/Eureka ni la contenedorización de todas las JVM por el solo hecho de participar en integración o administrar merges.

## Aportes de otros integrantes y evidencia de integración

El historial de `NovoaCl` documenta, entre otros:

| Área | Commits representativos |
|---|---|
| Plaza | `5873e26` |
| Reserva | `807112a`, `ca07144` (integración con Movimiento) |
| Liberación | `893ccaa`, `7cfa95f` (integración con Movimiento) |
| Reporte | `e859888` |
| Pruebas de Plaza/Reserva/Liberación/Reporte | `48d188b` |
| Swagger | `7a62848`, además de aportes de otros autores |
| Gateway y Eureka | `2cb245f`, `7e322e6`, `7284153`, `5ef5682` |
| Contenedorización final | `59d6fd3` |

Los commits de merge #8, #9 y #10 prueban integración de `testing/eureka-gateway`; #11 integra `feature/containers`. La información de PR procede de mensajes de merge: no se consultaron revisiones, conversaciones o aprobaciones mediante API de GitHub.

## Cómo revisar la evidencia

```bash
git log origin/main --no-merges --format='%h %an %s'
git show --stat b0d6e69
git show --stat 59d6fd3
git log --oneline origin/main..origin/testing/eureka-gateway
git log --oneline origin/main..origin/usuario-service/Logs-Swagger
```

La última rama conserva `f88aab3`, un commit de Auth sobre errores/logs no alcanzable desde `main`; no se declara integrado sin revisión semántica. No debe confundirse una rama antigua con todas las diferencias de archivos que tuvo posteriormente `main`.

## About y Topics sugeridos

Descripción respaldada por la implementación:

> Sistema backend de gestión de estacionamientos basado en microservicios con Java, Spring Boot, MySQL y Docker.

Topics: `java`, `spring-boot`, `microservices`, `rest-api`, `mysql`, `docker`.

No se cambiaron metadatos, visibilidad, propiedad ni seguridad. Para configurarlos manualmente en GitHub: abrir el repositorio → sección **About** (panel derecho) → engranaje → completar **Description** y **Topics** → **Save changes**. Requiere permisos sobre el repositorio.

## Publicación de esta entrega documental

Rama dedicada: `docs/portfolio-readme`, creada en un worktree separado desde `origin/main` para no tocar la modificación local del propietario. No hacer merge automático.

Si GitHub CLI no está disponible, después de publicar la rama abrir:

<https://github.com/FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026/compare/main...docs/portfolio-readme?expand=1>

Seleccionar base `main`, comparación `docs/portfolio-readme` y crear el PR con el título:

**docs: mejorar presentación y documentación para portafolio**

Contenido sugerido para el PR:

- **Objetivo:** presentación sobria y verificable para portafolio, preservando la naturaleza académica y colaborativa.
- **Archivos:** README; guías de arquitectura, instalación, API, pruebas, contribuciones y auditoría; notas de vigencia de los cuatro documentos históricos; `.gitignore` y `.dockerignore` solo para exclusiones editoriales.
- **Cambios:** resumen inicial, separación de detalle técnico, diagrama Mermaid, inventario por módulo y distinción entre implementación y validación.
- **Evidencias:** referencia `78ef4d1`, historial/merges y ramas; POM, perfiles, controladores, clientes, servicios, seguridad, tests y archivos Docker.
- **Validaciones:** revisión estática; modelo Compose con valores ficticios; sintaxis del entrypoint; 84 pruebas completas (80 satisfactorias, 4 errores de contexto por ausencia de MySQL, 0 omitidas); 74 unitarias aprobadas en selección aislada; compilación de Gateway y Eureka sin tests; revisión de enlaces y diff; README convertido a HTML por la API de Markdown de GitHub (HTTP 200); dos diagramas aceptados por el parser Mermaid local.
- **Limitaciones:** daemon Docker no disponible, flujos HTTP e inspección visual final en GitHub pendientes, credenciales versionadas sin reproducir sus valores, inicialización SQL no parametrizada y rutas/perfiles pendientes de revisión funcional.
- **Integridad:** no se modificaron lógica de negocio, código Java, dependencias, configuración de aplicación, SQL ni historial. Cambio local del propietario excluido. Sin merge automático.

Alternativa con GitHub CLI instalado y autenticado, desde el worktree documental:

```bash
gh pr create \
  --repo FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026 \
  --base main --head docs/portfolio-readme \
  --title 'docs: mejorar presentación y documentación para portafolio' \
  --body-file /ruta/local/descripcion-pr.md
```

Crear ese archivo local con el contenido anterior antes de ejecutar. No es un archivo requerido del repositorio ni contiene credenciales. Si falta permiso de push, exportar desde la rama documental un parche sin incluir cambios ajenos:

```bash
git diff origin/main...docs/portfolio-readme > ../portfolio-documentacion.patch
# En una copia limpia basada en la misma referencia:
git apply --check ../portfolio-documentacion.patch
git apply ../portfolio-documentacion.patch
```

El parche no incluye commits ni reescribe historial. Revisarlo antes de aplicar, crear una rama nueva y publicar mediante el flujo de contribución del propietario.
