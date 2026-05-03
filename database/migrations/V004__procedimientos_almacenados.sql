-- =============================================================
-- V004 — PROCEDIMIENTOS ALMACENADOS PRINCIPALES
-- =============================================================

-- ======================================
-- 1. PROCEDIMIENTO: registrar venta completa
-- ======================================
CREATE OR REPLACE FUNCTION sp_registrar_venta(
    p_id_cliente INTEGER,
    p_id_usuario INTEGER,
    p_id_metodo_pago INTEGER,
    p_detalles JSON
) RETURNS INTEGER AS $$
DECLARE
    v_id_venta INTEGER;
    v_item JSON;
    v_id_producto INTEGER;
    v_cantidad NUMERIC(10,2);
    v_precio NUMERIC(10,2);
    v_subtotal NUMERIC(10,2);
    v_total NUMERIC(10,2) := 0;
BEGIN
    -- Crear la venta vacía (total se calcula al final)
    INSERT INTO venta(id_cliente,id_usuario,id_metodo_pago,total)
    VALUES (p_id_cliente, p_id_usuario, p_id_metodo_pago, 0)
    RETURNING id_venta INTO v_id_venta;

    -- Insertar detalles desde el JSON
    FOR v_item IN SELECT * FROM json_array_elements(p_detalles)
    LOOP
        v_id_producto := (v_item->>'id_producto')::INTEGER;
        v_cantidad    := (v_item->>'cantidad')::NUMERIC;
        v_precio      := (v_item->>'precio_unitario')::NUMERIC;
        v_subtotal    := v_cantidad * v_precio;

        v_total := v_total + v_subtotal;

        INSERT INTO detalle_venta(id_venta,id_producto,cantidad,precio_unitario,subtotal)
        VALUES (v_id_venta, v_id_producto, v_cantidad, v_precio, v_subtotal);

        -- Registrar movimiento inventario
        PERFORM sp_registrar_movimiento_inventario(
            v_id_producto,
            2, -- tipo: salida
            p_id_usuario,
            v_cantidad,
            'VENTA',
            v_id_venta
        );
    END LOOP;

    -- Actualizar total final
    UPDATE venta SET total = v_total WHERE id_venta = v_id_venta;

    RETURN v_id_venta;
END;
$$ LANGUAGE plpgsql;



-- ======================================
-- 2. PROCEDIMIENTO: registrar compra completa
-- ======================================
CREATE OR REPLACE FUNCTION sp_registrar_compra(
    p_id_proveedor INTEGER,
    p_id_usuario INTEGER,
    p_detalles JSON
) RETURNS INTEGER AS $$
DECLARE
    v_id_compra INTEGER;
    v_item JSON;
    v_id_producto INTEGER;
    v_cantidad NUMERIC(10,2);
    v_precio NUMERIC(10,2);
    v_subtotal NUMERIC(10,2);
    v_total NUMERIC(10,2) := 0;
BEGIN
    INSERT INTO compra(id_proveedor,id_usuario,total)
    VALUES(p_id_proveedor,p_id_usuario,0)
    RETURNING id_compra INTO v_id_compra;

    FOR v_item IN SELECT * FROM json_array_elements(p_detalles)
    LOOP
        v_id_producto := (v_item->>'id_producto')::INTEGER;
        v_cantidad    := (v_item->>'cantidad')::NUMERIC;
        v_precio      := (v_item->>'precio_unitario')::NUMERIC;
        v_subtotal    := v_cantidad * v_precio;
        v_total := v_total + v_subtotal;

        INSERT INTO detalle_compra(id_compra,id_producto,cantidad,precio_unitario_compra,subtotal)
        VALUES(v_id_compra,v_id_producto,v_cantidad,v_precio,v_subtotal);

        -- Movimiento inventario: entrada
        PERFORM sp_registrar_movimiento_inventario(
            v_id_producto,
            1, -- tipo: entrada
            p_id_usuario,
            v_cantidad,
            'COMPRA',
            v_id_compra
        );
    END LOOP;

    UPDATE compra SET total = v_total WHERE id_compra = v_id_compra;

    RETURN v_id_compra;
END;
$$ LANGUAGE plpgsql;



-- ======================================
-- 3. PROCEDIMIENTO: movimiento de inventario general
-- ======================================
CREATE OR REPLACE FUNCTION sp_registrar_movimiento_inventario(
    p_id_producto INTEGER,
    p_id_tipo_movimiento INTEGER,
    p_id_usuario INTEGER,
    p_cantidad NUMERIC(10,2),
    p_motivo VARCHAR,
    p_referencia INTEGER
) RETURNS VOID AS $$
DECLARE
    v_stock_anterior NUMERIC(10,2);
    v_stock_nuevo NUMERIC(10,2);
BEGIN
    SELECT cantidad_actual INTO v_stock_anterior
    FROM stock
    WHERE id_producto = p_id_producto;

    IF v_stock_anterior IS NULL THEN
        v_stock_anterior := 0;
    END IF;

    IF p_id_tipo_movimiento = 1 THEN
        v_stock_nuevo := v_stock_anterior + p_cantidad;
    ELSE
        v_stock_nuevo := v_stock_anterior - p_cantidad;
    END IF;

    UPDATE stock SET cantidad_actual = v_stock_nuevo
    WHERE id_producto = p_id_producto;

    INSERT INTO movimiento_inventario(
        id_producto,
        id_tipo_movimiento,
        id_usuario,
        cantidad,
        stock_anterior,
        stock_nuevo,
        motivo,
        referencia
    )
    VALUES(
        p_id_producto,
        p_id_tipo_movimiento,
        p_id_usuario,
        p_cantidad,
        v_stock_anterior,
        v_stock_nuevo,
        p_motivo,
        p_referencia::TEXT
    );

END;
$$ LANGUAGE plpgsql;



-- ======================================
-- 4. PROCEDIMIENTO: cierre de caja
-- ======================================
CREATE OR REPLACE FUNCTION sp_cerrar_caja(
    p_id_apertura INTEGER,
    p_id_usuario INTEGER,
    p_saldo_real NUMERIC
) RETURNS INTEGER AS $$
DECLARE
    v_saldo_teorico NUMERIC;
    v_diferencia NUMERIC;
    v_id_cierre INTEGER;
BEGIN
    -- Saldo teórico de movimientos
    SELECT SUM(
        CASE WHEN tmc.nombre_tipo_movimiento = 'INGRESO' THEN mc.monto
             WHEN tmc.nombre_tipo_movimiento = 'EGRESO' THEN -mc.monto
        END
    ) INTO v_saldo_teorico
    FROM movimiento_caja mc
    JOIN tipo_movimiento_caja tmc
        ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
    WHERE mc.id_caja = (SELECT id_caja FROM apertura_caja WHERE id_apertura = p_id_apertura);

    v_saldo_teorico := COALESCE(v_saldo_teorico, 0);

    v_diferencia := p_saldo_real - v_saldo_teorico;

    INSERT INTO cierre_caja(
        id_apertura,
        id_usuario,
        saldo_teorico,
        saldo_real,
        diferencia
    )
    VALUES(
        p_id_apertura,
        p_id_usuario,
        v_saldo_teorico,
        p_saldo_real,
        v_diferencia
    )
    RETURNING id_cierre INTO v_id_cierre;

    RETURN v_id_cierre;
END;
$$ LANGUAGE plpgsql;