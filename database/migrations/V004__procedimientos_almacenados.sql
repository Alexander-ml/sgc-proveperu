-- ============================================================
-- V004__procedimientos_almacenados.sql
-- Procedimientos almacenados oficiales del sistema
-- Mantiene estructura original del proyecto, pero actualizado
-- al schema final corregido.
-- ============================================================


-- ============================================================
-- 1. PROCEDIMIENTO: registrar_venta
-- Objetivo:
--   • Registra la venta
--   • Inserta sus detalles
--   • Actualiza inventario automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION sp_registrar_venta(
    p_id_cliente INTEGER,
    p_id_usuario INTEGER,
    p_detalles JSON
)
RETURNS INTEGER AS $$
DECLARE
    v_id_venta INTEGER;
    item JSON;
    v_id_producto INTEGER;
    v_cantidad NUMERIC;
    v_precio NUMERIC;
    v_subtotal NUMERIC;
BEGIN
    -- 1. Crear la venta
    INSERT INTO venta(id_cliente, id_usuario, total)
    VALUES (p_id_cliente, p_id_usuario, 0)
    RETURNING id_venta INTO v_id_venta;

    -- 2. Insertar detalle + actualizar stock
    FOR item IN SELECT * FROM json_array_elements(p_detalles)
    LOOP
        v_id_producto := (item->>'id_producto')::INTEGER;
        v_cantidad := (item->>'cantidad')::NUMERIC;
        v_precio := (item->>'precio_unitario')::NUMERIC;
        v_subtotal := v_cantidad * v_precio;

        INSERT INTO detalle_venta(id_venta, id_producto, cantidad, precio_unitario, subtotal)
        VALUES (v_id_venta, v_id_producto, v_cantidad, v_precio, v_subtotal);

        -- Actualiza inventario
        PERFORM sp_registrar_movimiento_inventario(
            v_id_producto,
            'EGRESO',
            v_cantidad,
            p_id_usuario,
            v_id_venta,
            NULL,
            NULL
        );
    END LOOP;

    -- 3. Actualizar total de la venta
    UPDATE venta
    SET total = (
        SELECT SUM(subtotal)
        FROM detalle_venta
        WHERE id_venta = v_id_venta
    )
    WHERE id_venta = v_id_venta;

    RETURN v_id_venta;
END;
$$ LANGUAGE plpgsql;



-- ============================================================
-- 2. PROCEDIMIENTO: registrar_compra
-- Objetivo:
--   • Registra una compra
--   • Inserta sus detalles
--   • Actualiza inventario automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION sp_registrar_compra(
    p_id_proveedor INTEGER,
    p_id_usuario INTEGER,
    p_detalles JSON
)
RETURNS INTEGER AS $$
DECLARE
    v_id_compra INTEGER;
    item JSON;
    v_id_producto INTEGER;
    v_cantidad NUMERIC;
    v_precio NUMERIC;
    v_subtotal NUMERIC;
BEGIN
    -- 1. Crear compra
    INSERT INTO compra(id_proveedor, id_usuario_registro, total)
    VALUES (p_id_proveedor, p_id_usuario, 0)
    RETURNING id_compra INTO v_id_compra;

    -- 2. Insertar detalles y actualizar inventario
    FOR item IN SELECT * FROM json_array_elements(p_detalles)
    LOOP
        v_id_producto := (item->>'id_producto')::INTEGER;
        v_cantidad := (item->>'cantidad')::NUMERIC;
        v_precio := (item->>'precio_unitario_compra')::NUMERIC;
        v_subtotal := v_cantidad * v_precio;

        INSERT INTO detalle_compra(id_compra, id_producto, cantidad, precio_unitario_compra, subtotal)
        VALUES (v_id_compra, v_id_producto, v_cantidad, v_precio, v_subtotal);

        -- Movimiento inventario (INGRESO)
        PERFORM sp_registrar_movimiento_inventario(
            v_id_producto,
            'INGRESO',
            v_cantidad,
            p_id_usuario,
            NULL,
            v_id_compra,
            NULL
        );
    END LOOP;

    -- 3. Actualizar total de compra
    UPDATE compra
    SET total = (
        SELECT SUM(subtotal)
        FROM detalle_compra
        WHERE id_compra = v_id_compra
    )
    WHERE id_compra = v_id_compra;

    RETURN v_id_compra;
END;
$$ LANGUAGE plpgsql;



-- ============================================================
-- 3. PROCEDIMIENTO: registrar_movimiento_inventario
-- Objetivo:
--   • Registrar movimientos → INGRESO, EGRESO, AJUSTE_POS/NEG
--   • Actualiza stock automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION sp_registrar_movimiento_inventario(
    p_id_producto INTEGER,
    p_tipo_movimiento VARCHAR,
    p_cantidad NUMERIC,
    p_id_usuario INTEGER,
    p_id_venta INTEGER,
    p_id_compra INTEGER,
    p_id_recepcion INTEGER
)
RETURNS VOID AS $$
DECLARE
    v_id_tipo INTEGER;
    v_stock_anterior NUMERIC;
    v_stock_nuevo NUMERIC;
BEGIN
    SELECT id_tipo_movimiento_inventario INTO v_id_tipo
    FROM tipo_movimiento_inventario
    WHERE nombre = p_tipo_movimiento;

    IF v_id_tipo IS NULL THEN
        RAISE EXCEPTION 'Tipo de movimiento inválido: %', p_tipo_movimiento;
    END IF;

    -- Obtener stock anterior
    SELECT cantidad_actual INTO v_stock_anterior
    FROM stock
    WHERE id_producto = p_id_producto;

    -- Si no existe stock, crear registro inicial
    IF v_stock_anterior IS NULL THEN
        INSERT INTO stock(id_producto, cantidad_actual, stock_minimo)
        VALUES (p_id_producto, 0, 0);

        v_stock_anterior := 0;
    END IF;

    -- Nuevo stock
    v_stock_nuevo :=
        CASE p_tipo_movimiento
            WHEN 'INGRESO' THEN v_stock_anterior + p_cantidad
            WHEN 'EGRESO' THEN v_stock_anterior - p_cantidad
            WHEN 'AJUSTE_POSITIVO' THEN v_stock_anterior + p_cantidad
            WHEN 'AJUSTE_NEGATIVO' THEN v_stock_anterior - p_cantidad
        END;

    IF v_stock_nuevo < 0 THEN
        RAISE EXCEPTION 'Stock no puede quedar negativo';
    END IF;

    -- Guardar movimiento
    INSERT INTO movimiento_inventario(
        id_producto,
        id_tipo_movimiento_inventario,
        id_usuario_registro,
        id_venta,
        id_compra,
        id_recepcion_compra,
        cantidad,
        stock_anterior,
        stock_nuevo
    ) VALUES (
        p_id_producto,
        v_id_tipo,
        p_id_usuario,
        p_id_venta,
        p_id_compra,
        p_id_recepcion,
        p_cantidad,
        v_stock_anterior,
        v_stock_nuevo
    );

    -- Actualizar stock
    UPDATE stock
    SET cantidad_actual = v_stock_nuevo,
        fecha_hora_actualizacion = CURRENT_TIMESTAMP
    WHERE id_producto = p_id_producto;

END;
$$ LANGUAGE plpgsql;



-- ============================================================
-- 4. PROCEDIMIENTO: registrar_movimiento_caja
-- Objetivo:
--   • Registrar ingresos o egresos de caja
--   • Actualizar saldo automáticamente
-- ============================================================

CREATE OR REPLACE FUNCTION sp_registrar_movimiento_caja(
    p_id_caja INTEGER,
    p_tipo VARCHAR,
    p_monto NUMERIC,
    p_id_usuario INTEGER,
    p_descripcion VARCHAR,
    p_id_venta INTEGER,
    p_id_compra INTEGER,
    p_id_metodo_pago INTEGER
)
RETURNS INTEGER AS $$
DECLARE
    v_id_tipo INTEGER;
    v_id_mov INTEGER;
BEGIN
    SELECT id_tipo_movimiento_caja INTO v_id_tipo
    FROM tipo_movimiento_caja
    WHERE nombre_tipo_movimiento = p_tipo;

    IF v_id_tipo IS NULL THEN
        RAISE EXCEPTION 'Tipo movimiento caja inválido: %', p_tipo;
    END IF;

    INSERT INTO movimiento_caja(
        id_caja,
        id_tipo_movimiento_caja,
        id_usuario_registra,
        id_venta,
        id_compra,
        id_metodo_pago,
        monto,
        descripcion
    )
    VALUES (
        p_id_caja,
        v_id_tipo,
        p_id_usuario,
        p_id_venta,
        p_id_compra,
        p_id_metodo_pago,
        p_monto,
        p_descripcion
    )
    RETURNING id_movimiento_caja INTO v_id_mov;

    -- Actualizar saldo caja
    UPDATE caja
    SET saldo_actual = CASE p_tipo
        WHEN 'INGRESO' THEN saldo_actual + p_monto
        WHEN 'EGRESO' THEN saldo_actual - p_monto
    END
    WHERE id_caja = p_id_caja;

    RETURN v_id_mov;
END;
$$ LANGUAGE plpgsql;