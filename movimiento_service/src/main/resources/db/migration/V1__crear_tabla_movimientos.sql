-- ================================================
-- V1__crear_tabla_movimiento.sql
-- Microservicio: movimiento_service
-- Descripción: Crea tabla principal de movimientos del sistema
-- ================================================

CREATE TABLE IF NOT EXISTS movimiento (
    id_movimiento BIGINT AUTO_INCREMENT PRIMARY KEY,

    tipo_movimiento VARCHAR(30) NOT NULL,

    id_usuario BIGINT,
    rut_usuario VARCHAR(10),

    id_vehiculo BIGINT,
    patente VARCHAR(10),

    id_plaza BIGINT,
    codigo_plaza VARCHAR(20),

    id_referencia BIGINT,
    servicio_origen VARCHAR(80) NOT NULL,

    fecha_movimiento DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    descripcion VARCHAR(255)
);