-- ======================================
-- V003 - VISTAS DE REPORTES
-- ======================================

-- ======================================
-- VISTA: VENTAS DETALLADAS
-- Combina venta + cliente + usuario + detalle + producto
-- ======================================
CREATE OR REPLACE VIEW vw_ventas_detalladas AS
SELECT 
    v.id_venta,
    v.fecha_hora_venta,
    c.id_cliente,
    COALESCE(c.nombre_completo, c.razon_social) AS cliente_nombre,
    u.id_usuario,
    u.nombre_completo AS usuario_vendedor,
    dv.id_detalle_venta,
    p.id_producto,
    p.nombre_producto,
    dv.cantidad,
    dv.precio_unitario,
    dv.subtotal,
    v.total AS total_venta
FROM venta v
JOIN cliente c ON c.id_cliente = v.id_cliente
JOIN usuario u ON u.id_usuario = v.id_usuario
JOIN detalle_venta dv ON dv.id_venta = v.id_venta
JOIN producto p ON p.id_producto = dv.id_producto;


-- ======================================
-- VISTA: COMPRAS DETALLADAS
-- Combina compra + proveedor + usuario + detalle + producto
-- ======================================
CREATE OR REPLACE VIEW vw_compras_detalladas AS
SELECT 
    c.id_compra,
    c.fecha_hora_registro,
    prov.id_proveedor,
    prov.razon_social AS proveedor_nombre,
    u.id_usuario,
    u.nombre_completo AS usuario_registro,
    dc.id_detalle_compra,
    p.id_producto,
    p.nombre_producto,
    dc.cantidad,
    dc.precio_unitario_compra,
    dc.subtotal,
    c.total AS total_compra
FROM compra c
JOIN proveedor prov ON prov.id_proveedor = c.id_proveedor
JOIN usuario u ON u.id_usuario = c.id_usuario
JOIN detalle_compra dc ON dc.id_compra = c.id_compra
JOIN producto p ON p.id_producto = dc.id_producto;


-- ======================================
-- VISTA: MOVIMIENTOS DE INVENTARIO
-- Combina movimiento + producto + tipo + usuario
-- ======================================
CREATE OR REPLACE VIEW vw_movimientos_inventario AS
SELECT
    mi.id_movimiento,
    mi.fecha_hora,
    mi.id_producto,
    p.nombre_producto,
    mi.id_tipo_movimiento,
    tmi.nombre AS tipo_movimiento,
    mi.cantidad,
    mi.stock_anterior,
    mi.stock_nuevo,
    mi.id_usuario,
    u.nombre_completo AS usuario_registro,
    mi.motivo,
    mi.referencia
FROM movimiento_inventario mi
JOIN producto p ON p.id_producto = mi.id_producto
JOIN tipo_movimiento_inventario tmi ON tmi.id_tipo_movimiento = mi.id_tipo_movimiento
JOIN usuario u ON u.id_usuario = mi.id_usuario;


-- ======================================
-- VISTA: MOVIMIENTOS DE CAJA
-- ======================================
CREATE OR REPLACE VIEW vw_movimientos_caja AS
SELECT
    mc.id_movimiento_caja,
    mc.fecha_hora_movimiento,
    c.id_caja,
    c.nombre_caja,
    mc.id_tipo_movimiento_caja,
    tmc.nombre_tipo_movimiento,
    mc.monto,
    mc.descripcion,
    mc.id_usuario_registra,
    u.nombre_completo AS usuario_registro,
    mc.id_venta,
    mc.id_compra,
    mc.id_metodo_pago
FROM movimiento_caja mc
JOIN caja c ON c.id_caja = mc.id_caja
JOIN tipo_movimiento_caja tmc ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
JOIN usuario u ON u.id_usuario = mc.id_usuario_registra;


-- ======================================
-- VISTA: STOCK ACTUAL CONSOLIDADO
-- ======================================
CREATE OR REPLACE VIEW vw_stock_consolidado AS
SELECT
    p.id_producto,
    p.codigo_producto,
    p.nombre_producto,
    s.cantidad_actual,
    s.stock_minimo,
    (s.cantidad_actual - s.stock_minimo) AS diferencia_stock
FROM producto p
JOIN stock s ON s.id_producto = p.id_producto;