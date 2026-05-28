-- ================================================
-- V1__crear_tabla_acceso.sql
-- Microservicio: acceso_service
-- Descripción: Crea tabla principal de accesos
-- ================================================

CREATE TABLE IF NOT EXISTS acceso (
    id_acceso BIGINT AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT NOT NULL,
    rut_usuario VARCHAR(10) NOT NULL,

    id_vehiculo BIGINT NOT NULL,
    patente VARCHAR(10) NOT NULL,

    id_plaza BIGINT NOT NULL,
    codigo_plaza VARCHAR(20) NOT NULL,

    fecha_ingreso DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    estado_acceso VARCHAR(20) NOT NULL,
    observacion VARCHAR(255)
);