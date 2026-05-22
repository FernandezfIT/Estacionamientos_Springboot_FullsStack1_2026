-- ================================================
-- V2__insertar_datos_rol_usuario.sql
-- Microservicio: usuario_service
-- Descripción: Inserta roles base y usuarios iniciales
-- ================================================

-- ================================================
-- 1. Población tabla de Roles
-- ================================================

INSERT INTO rol (nombre, descripcion) VALUES
('Jefe_Seguridad', 'Usuario responsable de la gestión de seguridad del sistema'),
('Jefe_SSDD', 'Usuario responsable de los servicios digitales del sistema'),
('Guardia', 'Usuario encargado de registrar entradas, salidas, visitas y reservas operativas'),
('Funcionario', 'Usuario funcionario con acceso limitado al sistema');

-- ================================================
-- 2. Password generica para datos de prueba
-- Password real: 123456
-- Hash generado con BCrypt
-- ================================================

SET @password_default = '$2y$10$2zzo8HtIccaMkQPK8UxHNeiKDJt.wQimIXMlQTXvXS833ZrtEtVQC';

-- ================================================
-- 3. Insertar usuarios iniciales
-- Consideraciones:
-- - 1 Jefe_Seguridad
-- - 1 Jefe_SSDD
-- - 1 Guardia
-- - 12 Funcionarios
-- Total: 15 usuarios
-- ================================================

INSERT INTO usuario (
    rut,
    nombre,
    apellido,
    email,
    password,
    telefono,
    fecha_creacion,
    id_rol
) VALUES
(
    '11111111-1',
    'Carlos',
    'Muñoz',
    'jefe.seguridad@estacionamientos.cl',
    @password_default,
    '912345678',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Jefe_Seguridad')
),
(
    '22222222-2',
    'María',
    'González',
    'jefe.ssdd@estacionamientos.cl',
    @password_default,
    '923456789',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Jefe_SSDD')
),
(
    '33333333-3',
    'Pedro',
    'Rojas',
    'guardia@estacionamientos.cl',
    @password_default,
    '934567890',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Guardia')
),
(
    '44444444-4',
    'Ana',
    'Pérez',
    'funcionario01@estacionamientos.cl',
    @password_default,
    '945678901',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '55555555-5',
    'Luis',
    'Soto',
    'funcionario02@estacionamientos.cl',
    @password_default,
    '956789012',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '66666666-6',
    'Camila',
    'Vargas',
    'funcionario03@estacionamientos.cl',
    @password_default,
    '967890123',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '77777777-7',
    'Jorge',
    'Castro',
    'funcionario04@estacionamientos.cl',
    @password_default,
    '978901234',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '88888888-8',
    'Valentina',
    'Morales',
    'funcionario05@estacionamientos.cl',
    @password_default,
    '989012345',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '99999999-9',
    'Matías',
    'Herrera',
    'funcionario06@estacionamientos.cl',
    @password_default,
    '990123456',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10111111-1',
    'Fernanda',
    'Silva',
    'funcionario07@estacionamientos.cl',
    @password_default,
    '901234567',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10222222-2',
    'Diego',
    'Navarro',
    'funcionario08@estacionamientos.cl',
    @password_default,
    '912340987',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10333333-3',
    'Paula',
    'Reyes',
    'funcionario09@estacionamientos.cl',
    @password_default,
    '923450876',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10444444-4',
    'Ignacio',
    'Fuentes',
    'funcionario10@estacionamientos.cl',
    @password_default,
    '934560765',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10555555-5',
    'Daniela',
    'Araya',
    'funcionario11@estacionamientos.cl',
    @password_default,
    '945670654',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
),
(
    '10666666-6',
    'Tomás',
    'Contreras',
    'funcionario12@estacionamientos.cl',
    @password_default,
    '956780543',
    NOW(),
    (SELECT id_rol FROM rol WHERE nombre = 'Funcionario')
);