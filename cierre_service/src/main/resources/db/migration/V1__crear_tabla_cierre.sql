-- ================================================
-- V1__crear_tabla_cierre.sql
-- Microservicio: cierre_service
-- Descripción: Crea tabla principal de cierres operativos
-- ================================================

CREATE TABLE IF NOT EXISTS cierre (
    id_cierre BIGINT AUTO_INCREMENT PRIMARY KEY,

    fecha_cierre DATE NOT NULL,
    hora_cierre TIME NOT NULL,
    fecha_hora_cierre DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    id_usuario_ejecutor BIGINT,
    email_usuario_ejecutor VARCHAR(255),
    rol_ejecutor VARCHAR(50),

    total_plazas_ocupadas_liberadas INT NOT NULL DEFAULT 0,
    total_reservas_eliminadas INT NOT NULL DEFAULT 0,
    total_plazas_reservadas_liberadas INT NOT NULL DEFAULT 0,

    plazas_disponibles_final INT NOT NULL DEFAULT 0,
    plazas_ocupadas_final INT NOT NULL DEFAULT 0,
    plazas_reservadas_final INT NOT NULL DEFAULT 0,

    total_movimientos INT NOT NULL DEFAULT 0,
    total_accesos INT NOT NULL DEFAULT 0,
    total_salidas INT NOT NULL DEFAULT 0,
    total_reservas INT NOT NULL DEFAULT 0,

    estado_cierre VARCHAR(30) NOT NULL,
    resumen_reporte TEXT,
    observacion VARCHAR(255)
);