-- ============================================================
-- V002__create_indexes.sql
-- Índices recomendados para optimizar consultas
-- Compatibles con el schema final
-- ============================================================

-- ============================
-- USUARIO
-- ============================
-- Búsqueda rápida por email (usuario_login)
CREATE INDEX idx_usuario_login ON usuario(usuario_login);

-- ============================
-- CLIENTE
-- ============================
CREATE INDEX idx_cliente_tipo ON cliente(tipo_cliente);
CREATE INDEX idx_cliente_dni ON cliente(dni);
CREATE INDEX idx_cliente_ruc ON cliente(ruc);

-- ============================
-- PRODUCTO
-- ============================
CREATE INDEX idx_producto_codigo ON producto(codigo_producto);

-- ============================
-- PROVEEDOR
-- ============================
CREATE INDEX idx_proveedor_ruc ON proveedor(ruc);

-- ============================
-- VENTA
-- ============================
CREATE INDEX idx_venta_fecha ON venta(fecha_hora_venta);
CREATE INDEX idx_venta_cliente ON venta(id_cliente);
CREATE INDEX idx_venta_usuario ON venta(id_usuario);

-- ============================
-- DETALLE VENTA
-- ============================
CREATE INDEX idx_det_venta_producto ON detalle_venta(id_producto);

-- ============================
-- COMPRA
-- ============================
CREATE INDEX idx_compra_proveedor ON compra(id_proveedor);
CREATE INDEX idx_compra_usuario ON compra(id_usuario_registro);

-- ============================
-- DETALLE COMPRA
-- ============================
CREATE INDEX idx_det_compra_producto ON detalle_compra(id_producto);

-- ============================
-- MOVIMIENTO INVENTARIO
-- ============================
CREATE INDEX idx_mov_inv_producto ON movimiento_inventario(id_producto);
CREATE INDEX idx_mov_inv_fecha ON movimiento_inventario(fecha_hora_movimiento_inventario);

-- ============================
-- MOVIMIENTO CAJA
-- ============================
CREATE INDEX idx_mov_caja_fecha ON movimiento_caja(fecha_hora_movimiento);
CREATE INDEX idx_mov_caja_usuario ON movimiento_caja(id_usuario_registra);