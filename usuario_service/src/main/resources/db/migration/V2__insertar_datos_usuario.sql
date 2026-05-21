
INSERT INTO usuario (
    rut,
    nombre,
    apellido,
    email,
    password,
    telefono,
    fecha_creacion,
    rol
) VALUES
-- Jefe de Seguridad
('11111111-1', 'Carlos', 'Muñoz', 'jefe.seguridad@duoc.cl', '123456', 987654321, NOW(), 'Jefe de Seguridad'),

-- Jefe de Servicios Digitales
('22222222-2', 'María', 'González', 'jefe.servicios.digitales@duoc.cl', '123456', 987654321, NOW(), 'Jefe de Servicios Digitales'),

-- Guardia
('33333333-3', 'Pedro', 'Rojas', 'guardia@duoc.cl', '123456', 987654321, NOW(), 'Guardia'),

-- Funcionarios
('44444444-4', 'Ana', 'Soto', 'soto@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('55555555-5', 'Luis', 'Pérez', 'perez@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('66666666-6', 'Camila', 'Torres', 'torres@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('77777777-7', 'Javier', 'Castillo', 'castillo@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('88888888-8', 'Fernanda', 'Reyes', 'reyes@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('99999999-9', 'Diego', 'Morales', 'morales@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('12121212-1', 'Valentina', 'Herrera', 'herrera@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('13131313-1', 'Matías', 'Navarro', 'navarro@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('14141414-1', 'Paula', 'Vargas', 'vargas@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('15151515-1', 'Sebastián', 'Fuentes', 'fuentes@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('16161616-1', 'Daniela', 'Silva', 'silva@duoc.cl', '123456', 987654321, NOW(), 'Funcionario'),
('17171717-1', 'Ignacio', 'Contreras', 'contreras@duoc.cl', '123456', 987654321, NOW(), 'Funcionario');