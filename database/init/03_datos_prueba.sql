-- ============================================
-- 03 - DATOS DE PRUEBA
-- ============================================

SET search_path TO public;

-- ============================================
-- COMPRAS
-- ============================================

INSERT INTO compra (id_proveedor, id_usuario_registro, total, estado_fisico)
VALUES
(1, 4, 500.00, 'RECIBIDO'),
(2, 4, 850.00, 'PARCIAL'),
(3, 4, 1200.00, 'PENDIENTE');

-- ============================================
-- DETALLE COMPRA
-- ============================================

INSERT INTO detalle_compra (
    id_compra,
    id_producto,
    cantidad,
    precio_unitario_compra,
    subtotal
)
VALUES
(1,1,20,10.00,200.00),
(1,2,10,30.00,300.00),

(2,3,50,12.00,600.00),
(2,5,10,25.00,250.00),

(3,7,20,20.00,400.00),
(3,10,40,20.00,800.00);

-- ============================================
-- RECEPCIONES DE COMPRA
-- ============================================

INSERT INTO recepcion_compra (
    id_compra,
    id_usuario_registro
)
VALUES
(1,3),
(2,3);

-- ============================================
-- PAGOS DE COMPRA
-- ============================================

INSERT INTO pago_compra (
    id_compra,
    id_metodo_pago,
    id_usuario_registro,
    monto
)
VALUES
(1,1,5,500.00),
(2,3,5,400.00),
(2,4,5,450.00);

-- ============================================
-- VENTAS
-- ============================================

INSERT INTO venta (
    id_cliente,
    id_usuario,
    total,
    estado_fisico
)
VALUES
(1,2,150.00,'REGISTRADA'),
(2,2,350.00,'REGISTRADA'),
(1,2,90.00,'ANULADA');

-- ============================================
-- DETALLE VENTA
-- ============================================

INSERT INTO detalle_venta (
    id_venta,
    id_producto,
    cantidad,
    precio_unitario,
    subtotal
)
VALUES
(1,1,5,20.00,100.00),
(1,4,2,25.00,50.00),

(2,2,5,50.00,250.00),
(2,5,2,50.00,100.00),

(3,8,3,15.00,45.00),
(3,10,3,15.00,45.00);

-- ============================================
-- PAGOS DE VENTA
-- ============================================

INSERT INTO pago (
    id_venta,
    id_metodo_pago,
    monto
)
VALUES
(1,4,150.00),
(2,2,350.00),
(3,1,90.00);

-- ============================================
-- COMPROBANTES
-- ============================================

INSERT INTO comprobante (
    id_venta,
    tipo_comprobante,
    serie,
    correlativo
)
VALUES
(1,'BOLETA','B001','00000001'),
(2,'FACTURA','F001','00000001'),
(3,'NOTA','N001','00000001');

-- ============================================
-- APERTURA DE CAJA
-- ============================================

INSERT INTO apertura_caja (
    id_caja,
    id_usuario_registro,
    monto_inicial
)
VALUES
(1,5,1000.00),
(1,5,500.00);

-- ============================================
-- MOVIMIENTOS DE CAJA
-- ============================================

INSERT INTO movimiento_caja (
    id_caja,
    id_tipo_movimiento_caja,
    id_usuario_registra,
    id_venta,
    id_metodo_pago,
    monto,
    descripcion
)
VALUES
(1,1,5,1,4,150.00,'Ingreso por venta'),
(1,1,5,2,2,350.00,'Ingreso por venta'),
(1,2,5,NULL,1,200.00,'Pago de servicios'),
(1,2,5,NULL,3,150.00,'Compra de suministros');

-- ============================================
-- MOVIMIENTOS DE INVENTARIO
-- ============================================

INSERT INTO movimiento_inventario (
    id_producto,
    id_tipo_movimiento_inventario,
    id_usuario_registro,
    id_compra,
    cantidad,
    stock_anterior,
    stock_nuevo
)
VALUES
(1,1,3,1,20,50,70),

(2,1,3,1,10,50,60);

INSERT INTO movimiento_inventario (
    id_producto,
    id_tipo_movimiento_inventario,
    id_usuario_registro,
    id_venta,
    cantidad,
    stock_anterior,
    stock_nuevo
)
VALUES
(1,2,3,1,5,70,65),

(4,2,3,1,2,50,48);

INSERT INTO movimiento_inventario (
    id_producto,
    id_tipo_movimiento_inventario,
    id_usuario_registro,
    cantidad,
    stock_anterior,
    stock_nuevo
)
VALUES
(3,3,3,10,50,60),

(5,4,3,5,50,45);