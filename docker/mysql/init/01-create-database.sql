CREATE DATABASE IF NOT EXISTS acceso_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS auth_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS cierre_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS liberacion_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS movimiento_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS plaza_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS reporte_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS reserva_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS usuario_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS vehiculo_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'dev_user'@'%' IDENTIFIED BY 'dev123';

GRANT ALL PRIVILEGES ON acceso_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON auth_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON cierre_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON liberacion_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON movimiento_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON plaza_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON reporte_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON reserva_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON usuario_db.* TO 'dev_user'@'%';
GRANT ALL PRIVILEGES ON vehiculo_db.* TO 'dev_user'@'%';

FLUSH PRIVILEGES;