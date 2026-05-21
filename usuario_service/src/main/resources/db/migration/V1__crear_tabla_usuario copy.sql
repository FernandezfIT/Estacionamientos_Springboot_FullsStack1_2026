CREATE TABLE IF NOT EXISTS usuario (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut VARCHAR(10) NOT NULL,
    nombre VARCHAR(80) NOT NULL,
    apellido VARCHAR(80) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono INT(10) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    rol VARCHAR(20)

);


