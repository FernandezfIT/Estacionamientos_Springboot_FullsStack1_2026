# Estacionamientos Spring Boot FullStack 1 - 2026

Repositorio del proyecto:

```bash
git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
```

Este instructivo explica cómo preparar el equipo, clonar el repositorio y trabajar correctamente en el proyecto.

---

# 1. Consideraciones importantes

Cada integrante del equipo debe trabajar con su propia cuenta de GitHub.

No se debe compartir:

```txt
contraseñas
tokens
claves SSH privadas
cuentas de GitHub
```

Cada integrante debe tener:

```txt
1. Una cuenta personal de GitHub.
2. Acceso como colaborador al repositorio.
3. Git instalado en su equipo.
4. Una clave SSH propia configurada en GitHub.
5. Una copia local del repositorio.
```

---

# 2. Antes de clonar el repositorio

Antes de ejecutar `git clone`, cada integrante debe completar estos pasos.

---

## 2.1. Tener cuenta de GitHub

Cada integrante debe tener una cuenta personal en GitHub.

Si aún no tiene una, debe crearla en:

```txt
https://github.com
```

---

## 2.2. Enviar usuario de GitHub al dueño del repositorio

Cada integrante debe enviar su usuario de GitHub al dueño del repositorio.

Ejemplo:

```txt
Usuario GitHub: nombreUsuario
```

El dueño del repositorio debe agregar a cada integrante como colaborador desde GitHub:

```txt
Repositorio → Settings → Collaborators → Add people
```

Después de ser invitado, cada integrante debe aceptar la invitación desde GitHub o desde el correo recibido.

Si no aceptan la invitación, no podrán clonar ni subir cambios correctamente.

---

# 3. Instalar Git

## 3.1. En Windows

Descargar e instalar **Git for Windows** desde:

```txt
https://git-scm.com/download/win
```

Durante la instalación se pueden dejar las opciones por defecto.

Después de instalar, abrir:

```txt
Git Bash
```

Importante:

```txt
Para este instructivo se recomienda usar Git Bash.
No usar CMD ni PowerShell al principio para evitar diferencias en los comandos.
```

---

## 3.2. En Linux

Ejecutar:

```bash
sudo apt update
sudo apt install git
```

Verificar instalación:

```bash
git --version
```

---

# 4. Configurar nombre y correo en Git

Cada integrante debe configurar su nombre y correo.

El correo debe ser el mismo que usa en GitHub.

```bash
git config --global user.name "Nombre Apellido"
git config --global user.email "correo_usado_en_github"
```

Ejemplo:

```bash
git config --global user.name "Juan Perez"
git config --global user.email "juanperez@gmail.com"
```

Verificar configuración:

```bash
git config --global --list
```

Debe aparecer algo similar a:

```txt
user.name=Juan Perez
user.email=juanperez@gmail.com
```

---

# 5. Configurar SSH para GitHub

Este paso permite conectarse a GitHub sin ingresar usuario y contraseña cada vez.

Cada integrante debe configurar su propia clave SSH.

---

## 5.1. Revisar si ya existe una clave SSH

En Git Bash o terminal, ejecutar:

```bash
ls -al ~/.ssh
```

Si aparecen archivos como estos:

```txt
id_ed25519
id_ed25519.pub
```

ya existe una clave SSH.

Si no aparecen, continuar con el siguiente paso.

---

## 5.2. Crear una nueva clave SSH

Ejecutar:

```bash
ssh-keygen -t ed25519 -C "correo_usado_en_github"
```

Ejemplo:

```bash
ssh-keygen -t ed25519 -C "juanperez@gmail.com"
```

Cuando pregunte dónde guardar la clave, presionar Enter:

```txt
Enter file in which to save the key:
```

Cuando pregunte por una contraseña para la clave, pueden:

```txt
Presionar Enter para dejarla vacía
```

o

```txt
Ingresar una contraseña personal
```

---

## 5.3. Activar el agente SSH

Ejecutar:

```bash
eval "$(ssh-agent -s)"
```

Luego agregar la clave SSH:

```bash
ssh-add ~/.ssh/id_ed25519
```

---

## 5.4. Copiar la clave pública

Ejecutar:

```bash
cat ~/.ssh/id_ed25519.pub
```

Se mostrará una línea larga parecida a esta:

```txt
ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAI... correo@ejemplo.com
```

Copiar la línea completa.

Importante:

```txt
Se copia el archivo id_ed25519.pub
NO se copia el archivo id_ed25519
```

El archivo:

```txt
id_ed25519
```

es la clave privada y nunca debe compartirse.

---

## 5.5. Agregar la clave SSH en GitHub

Entrar a GitHub y seguir esta ruta:

```txt
GitHub → Settings → SSH and GPG keys → New SSH key
```

Completar:

```txt
Title: PC personal - Nombre
Key type: Authentication Key
Key: pegar aquí la clave pública copiada
```

Luego presionar:

```txt
Add SSH key
```

---

## 5.6. Probar conexión SSH con GitHub

Ejecutar:

```bash
ssh -T git@github.com
```

La primera vez puede aparecer:

```txt
Are you sure you want to continue connecting?
```

Responder:

```bash
yes
```

Si todo está correcto, debería aparecer un mensaje parecido a:

```txt
Hi nombreUsuario! You've successfully authenticated, but GitHub does not provide shell access.
```

Eso significa que la conexión SSH quedó funcionando correctamente.

---

# 6. Clonar el repositorio

Una vez configurado Git y SSH, recién ahora se debe clonar el repositorio.

Elegir una carpeta donde se guardarán los proyectos.

## En Windows

Desde Git Bash:

```bash
cd ~/Documents
```

## En Linux

```bash
cd ~/Documentos
```

Clonar el repositorio:

```bash
git clone git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
```

Entrar a la carpeta del proyecto:

```bash
cd Estacionamientos_Springboot_FullsStack1_2026
```

Verificar que el repositorio remoto quedó configurado por SSH:

```bash
git remote -v
```

Debe aparecer algo como:

```txt
origin  git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git (fetch)
origin  git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git (push)
```

Si aparece una URL con `https://`, está mal configurado para este instructivo.

La URL correcta debe comenzar con:

```txt
git@github.com:
```

---

# 7. Después de clonar el repositorio

Después de clonar, cada integrante debe hacer estas verificaciones.

---

## 7.1. Abrir el proyecto en VS Code

Desde la carpeta raíz del proyecto:

```bash
code .
```

Si el comando `code .` no funciona, abrir Visual Studio Code manualmente y seleccionar:

```txt
File → Open Folder → Estacionamientos_Springboot_FullsStack1_2026
```

---

## 7.2. Verificar el estado del repositorio

Ejecutar:

```bash
git status
```

Si todo está bien, debería aparecer algo similar a:

```txt
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```

---

## 7.3. Descargar los últimos cambios antes de trabajar

Antes de modificar cualquier archivo, siempre ejecutar:

```bash
git pull origin main
```

Esto descarga la versión más reciente del proyecto desde GitHub.

---

# 8. Estructura esperada del repositorio

El repositorio puede contener varios microservicios en la raíz.

Ejemplo:

```txt
Estacionamientos_Springboot_FullsStack1_2026/
│
├── README.md
├── .gitignore
│
├── usuarioCrud/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── ...
│
├── otroMicroservicio/
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── ...
```

Cada microservicio Spring Boot debe mantenerse dentro de su propia carpeta.

---

# 9. Forma correcta de trabajar

Para evitar conflictos, no se recomienda trabajar directamente sobre `main`.

Cada integrante debe crear una rama propia para la tarea que va a desarrollar.

---

## 9.1. Actualizar main antes de crear una rama

Primero cambiar a `main`:

```bash
git checkout main
```

Luego actualizar:

```bash
git pull origin main
```

---

## 9.2. Crear una rama nueva

Crear una rama con un nombre claro:

```bash
git checkout -b feature/nombre-tarea
```

Ejemplos:

```bash
git checkout -b feature/crud-usuarios
```

```bash
git checkout -b feature/configuracion-bbdd
```

```bash
git checkout -b feature/validaciones-usuario
```

```bash
git checkout -b feature/crud-estacionamientos
```

---

## 9.3. Verificar en qué rama estás trabajando

Ejecutar:

```bash
git branch
```

La rama actual aparecerá marcada con `*`.

Ejemplo:

```txt
* feature/crud-usuarios
  main
```

Si aparece `* main`, significa que estás trabajando directamente sobre `main`.

En ese caso, crear una rama antes de modificar archivos:

```bash
git checkout -b feature/nombre-tarea
```

---

# 10. Subir cambios al repositorio

Después de modificar archivos, seguir este flujo.

---

## 10.1. Revisar cambios

```bash
git status
```

Esto muestra los archivos modificados, agregados o eliminados.

---

## 10.2. Agregar cambios

Para agregar todos los cambios:

```bash
git add .
```

---

## 10.3. Crear commit

Crear un commit con un mensaje claro:

```bash
git commit -m "Agrega funcionalidad de usuarios"
```

Ejemplos de buenos mensajes:

```bash
git commit -m "Agrega entidad Usuario"
git commit -m "Crea controlador de usuarios"
git commit -m "Configura conexion a base de datos"
git commit -m "Corrige validacion de rut"
git commit -m "Agrega endpoint para listar usuarios"
```

Evitar mensajes poco claros como:

```bash
git commit -m "cambios"
git commit -m "avance"
git commit -m "arreglo"
git commit -m "cosas"
```

---

## 10.4. Subir la rama a GitHub

Si es la primera vez que se sube la rama:

```bash
git push -u origin feature/nombre-tarea
```

Ejemplo:

```bash
git push -u origin feature/crud-usuarios
```

Si la rama ya fue subida antes, después se puede usar simplemente:

```bash
git push
```

---

# 11. Crear Pull Request

Después de subir la rama, entrar al repositorio en GitHub.

GitHub normalmente mostrará un botón para crear el Pull Request.

Configurar el Pull Request así:

```txt
Base: main
Compare: feature/nombre-tarea
```

Ejemplo:

```txt
Base: main
Compare: feature/crud-usuarios
```

Luego crear el Pull Request.

El Pull Request permite revisar los cambios antes de unirlos a `main`.

---

# 12. Actualizar una rama con cambios recientes de main

Si otro compañero ya subió cambios a `main`, actualizar primero `main`:

```bash
git checkout main
git pull origin main
```

Luego volver a tu rama:

```bash
git checkout feature/nombre-tarea
```

Ejemplo:

```bash
git checkout feature/crud-usuarios
```

Actualizar tu rama con los cambios de `main`:

```bash
git merge main
```

Si no hay conflictos, Git hará el merge automáticamente.

Si aparecen conflictos, deben resolverse manualmente en VS Code.

Después de resolver conflictos:

```bash
git add .
git commit -m "Resuelve conflictos con main"
git push
```

---

# 13. Ejecutar un microservicio Spring Boot

Para ejecutar un microservicio, primero entrar a su carpeta.

Ejemplo:

```bash
cd usuarioCrud
```

---

## 13.1. Ejecutar en Windows

Desde Git Bash o terminal:

```bash
mvnw.cmd spring-boot:run
```

---

## 13.2. Ejecutar en Linux

```bash
./mvnw spring-boot:run
```

Si aparece error de permisos con `mvnw`, ejecutar:

```bash
chmod +x mvnw
```

Luego volver a ejecutar:

```bash
./mvnw spring-boot:run
```

---

# 14. Comandos útiles de Git

## Ver estado del repositorio

```bash
git status
```

---

## Ver ramas existentes

```bash
git branch
```

---

## Cambiar de rama

```bash
git checkout nombre-rama
```

Ejemplo:

```bash
git checkout main
```

---

## Crear una rama nueva

```bash
git checkout -b feature/nombre-tarea
```

---

## Descargar últimos cambios de main

```bash
git pull origin main
```

---

## Agregar cambios

```bash
git add .
```

---

## Crear commit

```bash
git commit -m "mensaje claro"
```

---

## Subir cambios

```bash
git push
```

---

## Subir una rama por primera vez

```bash
git push -u origin nombre-rama
```

---

## Ver repositorio remoto configurado

```bash
git remote -v
```

---

# 15. Archivos que no se deben subir

No se deben subir archivos generados automáticamente o configuraciones personales.

Ejemplos:

```txt
target/
*.class
.vscode/
.idea/
*.iml
.env
*.log
```

El archivo `.gitignore` debe encargarse de excluir estos archivos.

Si accidentalmente aparecen en `git status`, avisar al equipo antes de hacer commit.

---

# 16. Reglas de trabajo del equipo

Para evitar problemas:

```txt
1. No trabajar directamente en main.
2. Antes de empezar a trabajar, ejecutar git pull origin main.
3. Crear una rama por cada tarea.
4. Usar nombres claros para las ramas.
5. Usar mensajes de commit claros.
6. No subir carpetas target ni archivos .class.
7. No subir archivos con contraseñas, tokens o credenciales.
8. No compartir claves SSH privadas.
9. Antes de hacer Pull Request, verificar que el proyecto compile.
10. Avisar al equipo antes de modificar archivos compartidos importantes.
```

---

# 17. Flujo diario recomendado

Cada vez que vayas a trabajar:

```bash
git checkout main
git pull origin main
git checkout -b feature/nombre-tarea
```

Después de modificar archivos:

```bash
git status
git add .
git commit -m "mensaje claro"
git push -u origin feature/nombre-tarea
```

Luego crear un Pull Request en GitHub hacia `main`.

---

# 18. Problemas comunes

---

## 18.1. Error: Permission denied publickey

Ejemplo:

```txt
Permission denied (publickey).
fatal: Could not read from remote repository.
```

Significa que la clave SSH no está bien configurada o no fue agregada a GitHub.

Probar conexión:

```bash
ssh -T git@github.com
```

Si falla, revisar que la clave pública esté agregada en:

```txt
GitHub → Settings → SSH and GPG keys
```

También revisar que la clave esté cargada:

```bash
ssh-add -l
```

Si no aparece ninguna clave, ejecutar:

```bash
eval "$(ssh-agent -s)"
ssh-add ~/.ssh/id_ed25519
```

---

## 18.2. Error: Repository not found

Puede significar:

```txt
1. No aceptaste la invitación al repositorio.
2. No fuiste agregado como colaborador.
3. Estás usando otra cuenta de GitHub.
4. La URL del repositorio está mal escrita.
5. El repositorio es privado y no tienes acceso.
```

Verificar que puedes entrar al repositorio desde el navegador.

---

## 18.3. Error al hacer push

Primero revisar la rama actual:

```bash
git branch
```

Luego revisar el estado:

```bash
git status
```

Y revisar el remoto:

```bash
git remote -v
```

Si estás en una rama nueva y es la primera vez que subes cambios:

```bash
git push -u origin nombre-rama
```

---

## 18.4. Error porque el remoto está en HTTPS

Si al ejecutar:

```bash
git remote -v
```

aparece algo como:

```txt
https://github.com/FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
```

significa que el remoto está configurado por HTTPS.

Para cambiarlo a SSH:

```bash
git remote set-url origin git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
```

Verificar nuevamente:

```bash
git remote -v
```

Debe aparecer:

```txt
git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
```

---

# 19. Resumen rápido para clonar y trabajar

## Antes de clonar

```txt
1. Crear cuenta GitHub.
2. Enviar usuario GitHub al dueño del repo.
3. Aceptar invitación al repositorio.
4. Instalar Git.
5. Configurar user.name y user.email.
6. Crear clave SSH.
7. Agregar clave SSH pública a GitHub.
8. Probar conexión con ssh -T git@github.com.
```

---

## Clonar

```bash
git clone git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
cd Estacionamientos_Springboot_FullsStack1_2026
```

---

## Antes de trabajar

```bash
git checkout main
git pull origin main
git checkout -b feature/nombre-tarea
```

---

## Subir cambios

```bash
git status
git add .
git commit -m "mensaje claro"
git push -u origin feature/nombre-tarea
```

---

## Luego

Crear Pull Request en GitHub desde la rama creada hacia `main`.

---

# 20. Resumen ultra corto

```bash
git clone git@github.com:FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
cd Estacionamientos_Springboot_FullsStack1_2026
git checkout main
git pull origin main
git checkout -b feature/nombre-tarea
git add .
git commit -m "mensaje claro"
git push -u origin feature/nombre-tarea
```

Después de eso, crear Pull Request en GitHub.
