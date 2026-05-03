-- ===============================================
-- CLEANUP: Limpieza de datos de prueba
-- NO modifica estructura
-- ===============================================

-- Orden correcto de borrado por dependencias
DELETE FROM detalle_venta;
DELETE FROM venta;
DELETE FROM producto;
DELETE FROM categoria;
DELETE FROM cliente;
DELETE FROM proveedor;

-- Mantener roles y usuarios base
DELETE FROM usuario WHERE id_usuario > 3;

-- Opcional: resetear secuencias
ALTER SEQUENCE detalle_venta_id_detalle_venta_seq RESTART WITH 1;
ALTER SEQUENCE venta_id_venta_seq RESTART WITH 1;
ALTER SEQUENCE producto_id_producto_seq RESTART WITH 1;
ALTER SEQUENCE categoria_id_categoria_seq RESTART WITH 1;
ALTER SEQUENCE cliente_id_cliente_seq RESTART WITH 1;
ALTER SEQUENCE proveedor_id_proveedor_seq RESTART WITH 1;