-- ================================================================
-- CLEANUP.SQL
-- Limpieza completa de datos de prueba
-- Respeta estructura, FK y secuencias del schema actual
-- ================================================================

-- =====================================================
-- 1. Borrar tablas dependientes en orden correcto
-- =====================================================
DELETE FROM detalle_venta;
DELETE FROM venta;
DELETE FROM producto;
DELETE FROM categoria;
DELETE FROM cliente;
DELETE FROM proveedor;

-- =====================================================
-- 2. Mantener usuarios base
-- Solo elimina usuarios extras creados para pruebas
-- =====================================================
DELETE FROM usuario WHERE id_usuario > 3;

-- =====================================================
-- 3. Reset de secuencias reales del schema actual
-- =====================================================
ALTER SEQUENCE usuario_id_usuario_seq RESTART WITH 1;
ALTER SEQUENCE proveedor_id_proveedor_seq RESTART WITH 1;
ALTER SEQUENCE cliente_id_cliente_seq RESTART WITH 1;
ALTER SEQUENCE categoria_id_categoria_seq RESTART WITH 1;
ALTER SEQUENCE producto_id_producto_seq RESTART WITH 1;
ALTER SEQUENCE venta_id_venta_seq RESTART WITH 1;

-- detalle_venta NO usa secuencia, es PK compuesta