-- ================================================
-- V1__crear_tablas_tipo_vehiculo_vehiculo.sql
-- Microservicio: vehiculo_service
-- Descripción: Crea catálogo de tipos de vehículo y tabla vehiculo
-- ================================================

CREATE TABLE IF NOT EXISTS tipo_vehiculo (
    id_tipo_vehiculo BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS vehiculo (
    id_vehiculo BIGINT AUTO_INCREMENT PRIMARY KEY,
    patente VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(80) NOT NULL,
    color VARCHAR(50) NOT NULL,
    id_tipo_vehiculo BIGINT NOT NULL,
    id_usuario BIGINT NOT NULL,

    CONSTRAINT fk_vehiculo_tipo_vehiculo
        FOREIGN KEY (id_tipo_vehiculo)
        REFERENCES tipo_vehiculo(id_tipo_vehiculo)
);