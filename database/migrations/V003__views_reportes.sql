-- ============================================================
-- V003__views_reportes.sql
-- Vistas para reportes del sistema
-- Compatibles con el schema final
-- ============================================================


-- ================================
-- VISTA DE VENTAS DETALLADAS
-- ================================
-- Devuelve información de:
-- venta + cliente + usuario + detalle_venta + producto
CREATE OR REPLACE VIEW vw_ventas_detalladas AS
SELECT
    v.id_venta,
    v.fecha_hora_venta,
    c.id_cliente,
    COALESCE(c.nombre_completo, c.razon_social) AS cliente,
    u.nombre_completo AS usuario_vendedor,
    dv.id_producto,
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


-- ================================
-- VISTA DE COMPRAS DETALLADAS
-- ================================
CREATE OR REPLACE VIEW vw_compras_detalladas AS
SELECT
    c.id_compra,
    c.fecha_hora_registro,
    prov.razon_social AS proveedor,
    u.nombre_completo AS usuario_registro,
    dc.id_producto,
    p.nombre_producto,
    dc.cantidad,
    dc.precio_unitario_compra,
    dc.subtotal,
    c.total AS total_compra
FROM compra c
JOIN proveedor prov ON prov.id_proveedor = c.id_proveedor
JOIN usuario u ON u.id_usuario = c.id_usuario_registro
JOIN detalle_compra dc ON dc.id_compra = c.id_compra
JOIN producto p ON p.id_producto = dc.id_producto;


-- ================================
-- VISTA DE MOVIMIENTOS DE INVENTARIO
-- ================================
CREATE OR REPLACE VIEW vw_movimientos_inventario AS
SELECT
    mi.id_movimiento_inventario,
    mi.fecha_hora_movimiento_inventario,
    p.nombre_producto,
    tmi.nombre AS tipo_movimiento,
    mi.cantidad,
    mi.stock_anterior,
    mi.stock_nuevo,
    u.nombre_completo AS usuario_registro,
    mi.id_venta,
    mi.id_compra,
    mi.id_recepcion_compra
FROM movimiento_inventario mi
JOIN producto p ON p.id_producto = mi.id_producto
JOIN tipo_movimiento_inventario tmi ON tmi.id_tipo_movimiento_inventario = mi.id_tipo_movimiento_inventario
JOIN usuario u ON u.id_usuario = mi.id_usuario_registro;


-- ================================
-- VISTA DE MOVIMIENTOS DE CAJA
-- ================================
CREATE OR REPLACE VIEW vw_movimientos_caja AS
SELECT
    mc.id_movimiento_caja,
    mc.fecha_hora_movimiento,
    c.nombre_caja,
    tmc.nombre_tipo_movimiento,
    mc.monto,
    mc.descripcion,
    u.nombre_completo AS usuario_registro,
    mc.id_venta,
    mc.id_compra,
    mc.id_metodo_pago
FROM movimiento_caja mc
JOIN caja c ON c.id_caja = mc.id_caja
JOIN tipo_movimiento_caja tmc ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
JOIN usuario u ON u.id_usuario = mc.id_usuario_registra;