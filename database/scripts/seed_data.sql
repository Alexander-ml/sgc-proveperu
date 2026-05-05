-- ================================================================
-- SEED_DATA.SQL
-- Datos de prueba REALISTAS para SGC-PROVEPERU
-- No modifica estructura, solo inserta información base
-- ================================================================

-- =====================================================
-- 1. ROLES (Tabla no modificada en el nuevo schema)
-- =====================================================
INSERT INTO rol (nombre, descripcion) VALUES
('ADMIN', 'Acceso total al sistema'),
('VENDEDOR', 'Acceso a ventas y clientes'),
('ALMACEN', 'Gestión de inventarios');

-- =====================================================
-- 2. USUARIOS (Ajustado al schema actual: id_rol FK)
-- =====================================================
INSERT INTO usuario (nombres, apellidos, correo, contrasena, id_rol) VALUES
('Carlos', 'García López', 'cgarcia@proveperu.com', 'admin123', 1),
('María', 'Rojas Pérez', 'mrojas@proveperu.com', 'vendedor123', 2),
('Jorge', 'Santos Díaz', 'jsantos@proveperu.com', 'almacen123', 3);

-- =====================================================
-- 3. PROVEEDORES
-- =====================================================
INSERT INTO proveedor (razon_social, ruc, telefono, correo) VALUES
('Paraíso', '20123456789', '987654321', 'contacto@paraiso.com'),
('Anypsa', '20456789123', '912345678', 'ventas@anypsa.com'),
('Aceros Arequipa', '20111222333', '901222333', 'contacto@aceros.com');

-- =====================================================
-- 4. CLIENTES
-- =====================================================
INSERT INTO cliente (nombres, apellidos, tipo_documento, nro_documento, telefono, correo) VALUES
('Luis', 'Medina Torres', 'DNI', '71234567', '945112233', 'lmedina@gmail.com'),
('Andrea', 'Vásquez Paredes', 'DNI', '70123456', '912334455', 'avasquez@gmail.com'),
('Constructora Solari SAC', 'RUC', '20451233456', '987222111', 'contacto@solari.com');

-- =====================================================
-- 5. CATEGORÍAS
-- =====================================================
INSERT INTO categoria (nombre, descripcion) VALUES
('Pinturas', 'Productos de pintura y recubrimiento'),
('Maderas y Melamina', 'Planchas, tableros y derivados'),
('Ferretería', 'Tornillos, herramientas, adhesivos'),
('Colchones y Espumas', 'Productos Paraíso');

-- =====================================================
-- 6. PRODUCTOS (Ajustado al schema final)
-- =====================================================
INSERT INTO producto (nombre, descripcion, precio_unitario, stock_actual, id_categoria, id_proveedor) VALUES
('Pintura Anypsa Latex 5L', 'Latex premium para interiores y exteriores', 89.90, 40, 1, 2),
('Pintura Vencedor 4L', 'Pintura resistente de alta cobertura', 79.50, 25, 1, 2),
('Plancha Melamina Blanca 18mm', 'Tablero melamínico resistente de alta calidad', 189.00, 10, 2, 3),
('Plancha MDF 15mm', 'Tablero de fibra de densidad media', 109.00, 15, 2, 3),
('Espuma Paraíso 2 Plazas', 'Colchón espuma de alta densidad', 330.00, 8, 4, 1),
('Tornillo Drywall 1”', 'Tornillo fosfatado para drywall', 0.15, 1000, 3, 3),
('Pega Todo 250g', 'Adhesivo multiusos industrial', 12.50, 60, 3, 3);

-- =====================================================
-- 7. VENTAS (ajustado al nuevo schema)
-- =====================================================
INSERT INTO venta (id_cliente, id_usuario, fecha_venta, total, estado_fisico) VALUES
(1, 2, NOW(), 169.80, 'COMPLETADO'),
(2, 2, NOW(), 330.00, 'COMPLETADO'),
(3, 1, NOW(), 568.00, 'COMPLETADO');

-- =====================================================
-- 8. DETALLE VENTA (PK compuesta: id_venta + id_producto)
-- =====================================================
INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 2, 89.90, 179.80),
(2, 5, 1, 330.00, 330.00),
(3, 3, 2, 189.00, 378.00),
(3, 6, 800, 0.15, 120.00),
(3, 7, 5, 12.50, 62.50);