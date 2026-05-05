-- ============================================
--   02 - DATOS MAESTROS
-- ============================================

SET search_path TO public;

-- ============================================
-- 🔹 ROLES
-- ============================================

INSERT INTO rol (nombre_rol, descripcion)
VALUES 
('ADMIN', 'Acceso total al sistema'),
('VENDEDOR', 'Gestión de ventas'),
('ALMACEN', 'Gestión de inventario'),
('COMPRAS', 'Gestión de proveedores y compras'),
('CAJERO', 'Operación de caja');

-- ============================================
-- 🔹 USUARIOS
-- ============================================

INSERT INTO usuario (nombre_completo, usuario_login, password_hash, id_rol)
VALUES
('Administrador General', 'admin@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 1),
('Carlos Pérez', 'carlos.perez@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 2),
('Lucía Ramos', 'lucia.ramos@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 3),
('Jorge Medina', 'jorge.medina@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 4),
('María Torres', 'maria.torres@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 5);

-- ============================================
-- 🔹 PERMISOS (actualizados según tu líder)
-- ============================================

INSERT INTO permiso (modulo, accion, descripcion)
VALUES
('USUARIOS','CREAR','Crear usuarios'),
('USUARIOS','LEER','Leer usuarios'),
('USUARIOS','ACTUALIZAR','Actualizar usuarios'),
('USUARIOS','ELIMINAR','Eliminar usuarios'),

('CLIENTES','LEER','Ver clientes'),
('CLIENTES','CREAR','Registrar cliente'),
('CLIENTES','ACTUALIZAR','Modificar cliente'),

('VENTAS','CREAR','Registrar venta'),
('VENTAS','LEER','Ver ventas'),
('VENTAS','ACTUALIZAR','Modificar venta'),

('INVENTARIO','LEER','Ver inventario'),
('INVENTARIO','ACTUALIZAR','Actualizar inventario'),

('COMPRAS','CREAR','Registrar compras'),
('COMPRAS','LEER','Leer compras'),

('CAJA','CREAR','Registrar movimientos'),
('CAJA','LEER','Ver movimientos'),

('PROCESOS_COMPARTIDOS','LEER','Acceso a procesos del sistema');

-- Asignar TODOS los permisos al rol ADMIN
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso FROM permiso;

-- ============================================
-- 🔹 MÉTODOS DE PAGO
-- ============================================

INSERT INTO metodo_pago (nombre_metodo_pago, descripcion)
VALUES
('EFECTIVO', 'Pago en efectivo'),
('TARJETA', 'Pago con tarjeta Visa/Mastercard'),
('TRANSFERENCIA', 'Transferencia bancaria'),
('YAPE', 'Pago digital Yape'),
('PLIN', 'Pago digital Plin');

-- ============================================
-- 🔹 PRODUCTOS (10 productos reales)
-- ============================================

INSERT INTO producto (codigo_producto, nombre_producto, descripcion, unidad_medida)
VALUES
('PRD-0001', 'Pintura Látex Blanco 1GL', 'Pintura acrílica lavable', 'GALON'),
('PRD-0002', 'Cemento Sol 42.5KG', 'Bolsa de cemento Sol', 'KG'),
('PRD-0003', 'Malla Raschel 2m', 'Malla de sombreo 80%', 'METRO'),
('PRD-0004', 'Tornillo Punta Broca 1”', 'Caja x 100 unidades', 'CAJA'),
('PRD-0005', 'Thinner Industrial 1GL', 'Thinner para pinturas', 'GALON'),
('PRD-0006', 'Pintura Óleo Rojo 1QT', 'Óleo sintético brillante', 'CUARTO'),
('PRD-0007', 'Espuma Expansiva 750ml', 'Espuma multiusos', 'UNIDAD'),
('PRD-0008', 'Lija de Agua #220', 'Lija de grano fino', 'UNIDAD'),
('PRD-0009', 'Pintura Latex Azul 1GL', 'Pintura látex azul', 'GALON'),
('PRD-0010', 'Masilla Profesional 1KG', 'Masilla para pared', 'KG');

-- ============================================
-- 🔹 STOCK (inicial)
-- ============================================

INSERT INTO stock (id_producto, cantidad_actual, stock_minimo)
SELECT id_producto, 50, 10 FROM producto;

-- ============================================
-- 🔹 PROVEEDORES
-- ============================================

INSERT INTO proveedor (ruc, razon_social, telefono, direccion)
VALUES
('20123456789','Aceros Arequipa S.A.','016123456','Av. Argentina 345'),
('20654321876','Pinturas Anypsa S.A.','014567890','Av. Separadora Industrial 500'),
('20345698712','Corporación Paraíso','017894561','Av. Evitamiento 900');

-- ============================================
-- 🔹 CLIENTES
-- ============================================

INSERT INTO cliente (tipo_cliente, nombre_completo, dni, telefono, direccion)
VALUES
('PERSONA','Luis García','74231569','987654321','Av. Los Pinos 345');

INSERT INTO cliente (tipo_cliente, razon_social, ruc, telefono, direccion)
VALUES
('EMPRESA','Construcciones San Miguel SAC','20654298761','016785432','Av. La Marina 123');

-- ============================================
-- 🔹 CAJA
-- ============================================

INSERT INTO caja (nombre_caja, saldo_actual, estado_fisico)
VALUES
('CAJA PRINCIPAL', 0, 'ABIERTA');