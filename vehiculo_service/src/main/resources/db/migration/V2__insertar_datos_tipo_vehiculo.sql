-- ================================================
-- V2__insertar_datos_tipo_vehiculo.sql
-- Microservicio: vehiculo_service
-- Descripción: Inserta catálogo base de tipos de vehículo
-- ================================================

INSERT INTO tipo_vehiculo (nombre, descripcion) VALUES
('Sedan', 'Automóvil de carrocería sedán'),
('Camioneta', 'Vehículo utilitario tipo camioneta'),
('Moto', 'Vehículo motorizado de dos ruedas'),
('SUV', 'Vehículo utilitario deportivo'),
('Hatchback', 'Automóvil compacto con portalón trasero'),
('Furgon', 'Vehículo de carga o transporte tipo furgón'),
('Station_Wagon', 'Automóvil familiar con mayor espacio de carga');