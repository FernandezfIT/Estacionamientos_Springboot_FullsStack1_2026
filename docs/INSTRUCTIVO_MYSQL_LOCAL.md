# Instructivo: MySQL local con Docker

Este proyecto usa MySQL local mediante Docker Compose.

Cada integrante del equipo tendrá su propia base de datos local.
El archivo `docker-compose.yml` y los scripts SQL iniciales están versionados en Git.  
El archivo `.env` es local y no debe subirse al repositorio.

---

## 1. Requisitos previos

Cada integrante debe tener instalado:

- Git
- Docker
- Docker Compose
- JDK compatible con el proyecto
- VSCode, IntelliJ o NetBeans
- MySQL Workbench opcional

---

## 2. Clonar el repositorio

```bash
git clone https://github.com/FernandezfIT/Estacionamientos_Springboot_FullsStack1_2026.git
cd Estacionamientos_Springboot_FullsStack1_2026

Copiar archivo .env.ejemplo a .env para configuracion inicial de Docker de MySql

Win PowerShell -> Copy-Item .env.ejemplo .env