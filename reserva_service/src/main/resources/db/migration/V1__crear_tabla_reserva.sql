CREATE TABLE reserva (
    id_reserva BIGINT AUTO_INCREMENT PRIMARY KEY,
    rut_reserva VARCHAR(12) NOT NULL,
    patente VARCHAR(20) NOT NULL,
    id_plaza BIGINT NOT NULL,
    fecha_reserva DATETIME NOT NULL
);
