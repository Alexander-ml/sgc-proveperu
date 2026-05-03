------------------------------------------------------------
-- DATOS MAESTROS DEL SISTEMA - PROVEPERU
-- Se ejecuta solo la primera vez al crear el contenedor
------------------------------------------------------------

---------------------------
-- 1. ROLES DEL SISTEMA
---------------------------
INSERT INTO rol(nombre_rol, descripcion, estado_logico, estado_fisico)
VALUES 
 ('ADMIN', 'Administrador del sistema', 1, 'ACTIVO'),
 ('VENDEDOR', 'Encargado de ventas', 1, 'ACTIVO'),
 ('ALMACEN', 'Encargado de inventario', 1, 'ACTIVO'),
 ('CAJA', 'Encargado de caja', 1, 'ACTIVO')
ON CONFLICT DO NOTHING;

---------------------------
-- 2. PERMISOS POR MÓDULO
---------------------------
INSERT INTO permiso(modulo, accion, descripcion, estado_logico, estado_fisico)
VALUES
-- USUARIOS
('USUARIOS', 'CREAR', 'Crear usuarios', 1, 'ACTIVO'),
('USUARIOS', 'LEER', 'Listar usuarios', 1, 'ACTIVO'),
('USUARIOS', 'ACTUALIZAR', 'Actualizar usuarios', 1, 'ACTIVO'),
('USUARIOS', 'ELIMINAR', 'Eliminar usuarios', 1, 'ACTIVO'),

-- CLIENTES
('CRM', 'CREAR', 'Registrar cliente', 1, 'ACTIVO'),
('CRM', 'LEER', 'Listar clientes', 1, 'ACTIVO'),
('CRM', 'ACTUALIZAR', 'Actualizar cliente', 1, 'ACTIVO'),
('CRM', 'ELIMINAR', 'Eliminar cliente', 1, 'ACTIVO'),

-- PRODUCTOS
('INVENTARIO', 'CREAR', 'Registrar producto', 1, 'ACTIVO'),
('INVENTARIO', 'LEER', 'Listar productos', 1, 'ACTIVO'),
('INVENTARIO', 'ACTUALIZAR', 'Actualizar producto', 1, 'ACTIVO'),
('INVENTARIO', 'ELIMINAR', 'Eliminar producto', 1, 'ACTIVO'),

-- VENTAS
('VENTAS', 'CREAR', 'Registrar venta', 1, 'ACTIVO'),
('VENTAS', 'LEER', 'Listar ventas', 1, 'ACTIVO'),
('VENTAS', 'ACTUALIZAR', 'Actualizar venta', 1, 'ACTIVO'),
('VENTAS', 'ELIMINAR', 'Eliminar venta', 1, 'ACTIVO'),

-- COMPRAS
('COMPRAS', 'CREAR', 'Registrar compra', 1, 'ACTIVO'),
('COMPRAS', 'LEER', 'Listar compra', 1, 'ACTIVO'),
('COMPRAS', 'ACTUALIZAR', 'Actualizar compra', 1, 'ACTIVO'),
('COMPRAS', 'ELIMINAR', 'Eliminar compra', 1, 'ACTIVO'),

-- CAJA
('CAJA', 'CREAR', 'Registrar movimiento', 1, 'ACTIVO'),
('CAJA', 'LEER', 'Listar movimientos', 1, 'ACTIVO'),
('CAJA', 'ACTUALIZAR', 'Actualizar movimiento', 1, 'ACTIVO'),
('CAJA', 'ELIMINAR', 'Eliminar movimiento', 1, 'ACTIVO')
ON CONFLICT DO NOTHING;

------------------------------------------
-- 3. ASIGNACIÓN PERMISOS A ROLES
------------------------------------------
-- ADMIN: todos los permisos
INSERT INTO rol_permiso(id_rol, id_permiso, fecha_hora_creacion)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP
FROM rol r CROSS JOIN permiso p
WHERE r.nombre_rol = 'ADMIN'
ON CONFLICT DO NOTHING;

-- VENDEDOR: solo permisos de ventas + clientes
INSERT INTO rol_permiso(id_rol, id_permiso, fecha_hora_creacion)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP
FROM rol r 
JOIN permiso p ON p.modulo IN ('VENTAS','CRM')
WHERE r.nombre_rol = 'VENDEDOR'
ON CONFLICT DO NOTHING;

-- ALMACEN: inventario + compras
INSERT INTO rol_permiso(id_rol, id_permiso, fecha_hora_creacion)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP
FROM rol r 
JOIN permiso p ON p.modulo IN ('INVENTARIO','COMPRAS')
WHERE r.nombre_rol = 'ALMACEN'
ON CONFLICT DO NOTHING;

-- CAJA: movimientos de caja + ventas
INSERT INTO rol_permiso(id_rol, id_permiso, fecha_hora_creacion)
SELECT r.id_rol, p.id_permiso, CURRENT_TIMESTAMP
FROM rol r 
JOIN permiso p ON p.modulo IN ('CAJA','VENTAS')
WHERE r.nombre_rol = 'CAJA'
ON CONFLICT DO NOTHING;

------------------------------------------
-- 4. USUARIO ADMIN INICIAL
------------------------------------------
INSERT INTO usuario(
    nombre_completo, usuario_login, password_hash, id_rol,
    estado_logico, estado_fisico, fecha_hora_creacion
)
VALUES (
    'Administrador General',
    'admin',
    -- Hash de "admin123" (se puede cambiar luego)
    '$2a$10$9t4oYtGjWx/3UZEM6I72UO8DqRLVQjaxAg/P6MqxsVXni4eWh05Sm',
    (SELECT id_rol FROM rol WHERE nombre_rol = 'ADMIN'),
    1, 'ACTIVO', CURRENT_TIMESTAMP
)
ON CONFLICT DO NOTHING;

------------------------------------------
-- 5. TIPOS DE MOVIMIENTO DE INVENTARIO
------------------------------------------
INSERT INTO tipo_movimiento_inventario(nombre, descripcion, estado_logico, estado_fisico)
VALUES
 ('ENTRADA', 'Ingreso de mercancía al almacén', 1, 'ACTIVO'),
 ('SALIDA', 'Salida de mercancía del almacén', 1, 'ACTIVO')
ON CONFLICT DO NOTHING;

------------------------------------------
-- 6. TIPOS DE MOVIMIENTO DE CAJA
------------------------------------------
INSERT INTO tipo_movimiento_caja(nombre_tipo_movimiento, descripcion, estado_logico, estado_fisico)
VALUES
 ('INGRESO', 'Movimiento de ingreso de dinero', 1, 'ACTIVO'),
 ('EGRESO', 'Movimiento de salida de dinero', 1, 'ACTIVO')
ON CONFLICT DO NOTHING;

------------------------------------------
-- 7. MÉTODOS DE PAGO
------------------------------------------
INSERT INTO metodo_pago(nombre_metodo_pago, descripcion, estado_logico, estado_fisico)
VALUES
 ('EFECTIVO', 'Pago en efectivo', 1, 'ACTIVO'),
 ('YAPE', 'Pago digital Yape', 1, 'ACTIVO'),
 ('PLIN', 'Pago digital Plin', 1, 'ACTIVO')
ON CONFLICT DO NOTHING;

------------------------------------------
-- 8. CAJA PRINCIPAL
------------------------------------------
INSERT INTO caja(nombre_caja, saldo_actual, estado_logico, estado_fisico, fecha_hora_creacion)
VALUES
 ('Caja Principal', 0.00, 1, 'ACTIVO', CURRENT_TIMESTAMP)
ON CONFLICT DO NOTHING;

------------------------------------------------------------
-- FIN DE DATOS MAESTROS
------------------------------------------------------------