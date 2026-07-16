-- =====================================================
-- MIGRACIONES ADICIONALES DEL PROYECTO
-- Este archivo se ejecuta automaticamente solo cuando
-- PostgreSQL crea la base de datos desde cero.
-- =====================================================

-- =====================================================
-- V006: Procedimiento cambiar estado compra
-- =====================================================
-- =====================================================
-- V006 - PROCEDIMIENTO PARA CAMBIAR ESTADO DE COMPRA
-- =====================================================
-- Este procedimiento cambia el estado de una compra.
-- Si la compra pasa a RECIBIDO, registra la recepciÃ³n
-- y actualiza directamente el stock desde la base de datos.
-- =====================================================

CREATE OR REPLACE PROCEDURE sp_cambiar_estado_compra(
    p_id_compra INTEGER,
    p_nuevo_estado VARCHAR,
    p_id_usuario INTEGER
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estado_actual VARCHAR(20);
BEGIN
    -- Validar estado permitido
    IF p_nuevo_estado NOT IN ('PENDIENTE', 'PARCIAL', 'RECIBIDO', 'ANULADO') THEN
        RAISE EXCEPTION 'Estado de compra no vÃ¡lido: %', p_nuevo_estado;
    END IF;

    -- Obtener y bloquear la compra para evitar doble actualizaciÃ³n al mismo tiempo
    SELECT estado_fisico
    INTO v_estado_actual
    FROM compra
    WHERE id_compra = p_id_compra
      AND estado_logico = 1
    FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'Compra no encontrada o inactiva: %', p_id_compra;
    END IF;

    -- Si ya estÃ¡ en el mismo estado, no se hace nada
    IF v_estado_actual = p_nuevo_estado THEN
        RETURN;
    END IF;

    -- Evitar cambiar una compra ya anulada
    IF v_estado_actual = 'ANULADO' THEN
        RAISE EXCEPTION 'No se puede cambiar el estado de una compra anulada';
    END IF;

    -- Evitar revertir una compra ya recibida porque ya afectÃ³ stock
    IF v_estado_actual = 'RECIBIDO' THEN
        RAISE EXCEPTION 'No se puede cambiar el estado de una compra ya recibida';
    END IF;

    -- Si el nuevo estado es RECIBIDO, se registra recepciÃ³n y se actualiza stock
    IF p_nuevo_estado = 'RECIBIDO' THEN

        -- Registrar recepciÃ³n si todavÃ­a no existe una recepciÃ³n registrada
        IF NOT EXISTS (
            SELECT 1
            FROM recepcion_compra
            WHERE id_compra = p_id_compra
              AND estado_fisico = 'REGISTRADO'
        ) THEN
            INSERT INTO recepcion_compra (
                id_compra,
                id_usuario_registro,
                fecha_hora_recepcion,
                estado_fisico
            )
            VALUES (
                p_id_compra,
                p_id_usuario,
                CURRENT_TIMESTAMP,
                'REGISTRADO'
            );
        END IF;

        -- Aumentar stock usando los productos del detalle de compra
        INSERT INTO stock (
            id_producto,
            cantidad_actual,
            stock_minimo,
            fecha_hora_actualizacion
        )
        SELECT
            dc.id_producto,
            dc.cantidad,
            0,
            CURRENT_TIMESTAMP
        FROM detalle_compra dc
        WHERE dc.id_compra = p_id_compra
        ON CONFLICT (id_producto)
        DO UPDATE SET
            cantidad_actual = stock.cantidad_actual + EXCLUDED.cantidad_actual,
            fecha_hora_actualizacion = CURRENT_TIMESTAMP;

    END IF;

    -- Cambiar estado final de la compra
    UPDATE compra
    SET estado_fisico = p_nuevo_estado
    WHERE id_compra = p_id_compra;

END;
$$;

-- =====================================================
-- V007: Vista resumen ventas clientes
-- =====================================================
/**
 * Vista de resumen de ventas por cliente.
 *
 * Consolida Ãºnicamente las ventas con estado REGISTRADA
 * para obtener las estadÃ­sticas utilizadas por el mÃ³dulo de clientes.
 */
CREATE OR REPLACE VIEW vw_resumen_ventas_cliente AS
SELECT
    c.id_cliente,

    COUNT(v.id_venta) AS numero_compras,

    COALESCE(
        SUM(v.total),
        0
    )::NUMERIC(12, 2) AS monto_total,

    COALESCE(
        AVG(v.total),
        0
    )::NUMERIC(12, 2) AS ticket_promedio,

    MAX(v.fecha_hora_venta) AS ultima_compra

FROM cliente c

LEFT JOIN venta v
    ON v.id_cliente = c.id_cliente
    AND v.estado_fisico = 'REGISTRADA'
WHERE c.estado_logico = 1
GROUP BY c.id_cliente;

-- =====================================================
-- V008: Vistas historial compras clientes
-- =====================================================
-- =====================================================
-- VISTA: CABECERA DEL HISTORIAL DE COMPRAS POR CLIENTE
-- =====================================================

CREATE OR REPLACE VIEW vw_historial_compras_cliente AS
SELECT
    v.id_venta,
    v.id_cliente,

    (
        'V-'
        || TO_CHAR(v.fecha_hora_venta, 'YYYY')
        || '-'
        || LPAD(v.id_venta::TEXT, 6, '0')
    ) AS codigo_venta,

    v.fecha_hora_venta,
    v.estado_fisico AS estado_venta,
    v.total::NUMERIC(10, 2) AS total,

    COALESCE(
        pagos.metodo_pago,
        'SIN REGISTRAR'
    ) AS metodo_pago,

    comprobante.tipo_comprobante,
    comprobante.numero_comprobante,

    u.nombre_completo AS atendido_por

FROM venta v

INNER JOIN usuario u
    ON u.id_usuario = v.id_usuario

LEFT JOIN LATERAL (
    SELECT
        STRING_AGG(
            DISTINCT mp.nombre_metodo_pago,
            ' + '
            ORDER BY mp.nombre_metodo_pago
        ) AS metodo_pago

    FROM pago p

    INNER JOIN metodo_pago mp
        ON mp.id_metodo_pago = p.id_metodo_pago

    WHERE p.id_venta = v.id_venta
      AND p.estado_logico = 1
      AND p.estado_fisico = 'REGISTRADO'
) pagos ON TRUE

LEFT JOIN LATERAL (
    SELECT
        c.tipo_comprobante,

        (
            c.serie
            || '-'
            || c.correlativo
        ) AS numero_comprobante

    FROM comprobante c

    WHERE c.id_venta = v.id_venta

    ORDER BY c.id_comprobante DESC

    LIMIT 1
) comprobante ON TRUE

WHERE v.estado_fisico = 'REGISTRADA';


-- =====================================================
-- VISTA: PRODUCTOS DEL HISTORIAL DE COMPRAS
-- =====================================================

CREATE OR REPLACE VIEW vw_historial_productos_cliente AS
SELECT
    (
        dv.id_venta::TEXT
        || '-'
        || dv.id_producto::TEXT
    ) AS id_detalle,

    dv.id_venta,
    dv.id_producto,

    p.nombre_producto,

    dv.cantidad::NUMERIC(10, 2) AS cantidad,
    dv.subtotal::NUMERIC(10, 2) AS subtotal

FROM detalle_venta dv

INNER JOIN venta v
    ON v.id_venta = dv.id_venta

INNER JOIN producto p
    ON p.id_producto = dv.id_producto

WHERE v.estado_fisico = 'REGISTRADA';


-- =====================================================
-- V009: REGISTRAR EGRESO DE CAJA
-- =====================================================
-- =====================================================
-- PROCEDIMIENTO: REGISTRAR EGRESO DE CAJA
-- =====================================================
-- Objetivo:
-- Registrar un egreso manual dentro de una caja abierta.
--
-- Reglas:
-- 1. La caja debe existir.
-- 2. La caja debe estar ABIERTA.
-- 3. El usuario debe existir.
-- 4. El metodo de pago debe existir y estar ACTIVO.
-- 5. El monto debe ser mayor a cero.
-- 6. La caja debe tener saldo suficiente.
-- 7. Se registra el movimiento como EGRESO.
-- 8. Se actualiza el saldo actual de la caja.
-- =====================================================

CREATE OR REPLACE PROCEDURE sp_registrar_egreso_caja(
    IN p_id_caja INTEGER,
    IN p_id_usuario INTEGER,
    IN p_id_metodo_pago INTEGER,
    IN p_monto NUMERIC,
    IN p_descripcion VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_tipo_egreso INTEGER;
    v_id_apertura_caja INTEGER;
    v_monto_inicial NUMERIC(12, 2);
    v_fecha_apertura TIMESTAMP;
    v_saldo_calculado NUMERIC(12, 2);
BEGIN

    IF p_monto IS NULL OR p_monto <= 0 THEN
        RAISE EXCEPTION 'El monto del egreso debe ser mayor a cero';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM caja
        WHERE id_caja = p_id_caja
    ) THEN
        RAISE EXCEPTION 'La caja no existe';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM caja
        WHERE id_caja = p_id_caja
          AND estado_fisico = 'ABIERTA'
    ) THEN
        RAISE EXCEPTION 'La caja no se encuentra abierta';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM usuario
        WHERE id_usuario = p_id_usuario
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El usuario no existe o no se encuentra activo';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM metodo_pago
        WHERE id_metodo_pago = p_id_metodo_pago
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El metodo de pago no existe o no se encuentra activo';
    END IF;

    SELECT id_tipo_movimiento_caja
    INTO v_id_tipo_egreso
    FROM tipo_movimiento_caja
    WHERE nombre_tipo_movimiento = 'EGRESO';

    IF v_id_tipo_egreso IS NULL THEN
        RAISE EXCEPTION 'No existe el tipo de movimiento EGRESO';
    END IF;

    SELECT
        ac.id_apertura_caja,
        ac.monto_inicial,
        ac.fecha_hora_apertura
    INTO
        v_id_apertura_caja,
        v_monto_inicial,
        v_fecha_apertura
    FROM apertura_caja ac
    WHERE ac.id_caja = p_id_caja
      AND NOT EXISTS (
          SELECT 1
          FROM cierre_caja cc
          WHERE cc.id_apertura_caja = ac.id_apertura_caja
      )
    ORDER BY ac.fecha_hora_apertura DESC
    LIMIT 1;

    IF v_id_apertura_caja IS NULL THEN
        RAISE EXCEPTION 'La caja no tiene una apertura activa';
    END IF;

    SELECT
        v_monto_inicial
        + COALESCE(
            SUM(
                CASE tmc.nombre_tipo_movimiento
                    WHEN 'INGRESO' THEN mc.monto
                    WHEN 'EGRESO' THEN mc.monto * -1
                    ELSE 0
                END
            ),
            0
        )
    INTO v_saldo_calculado
    FROM movimiento_caja mc
    INNER JOIN tipo_movimiento_caja tmc
        ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
    WHERE mc.id_caja = p_id_caja
      AND mc.estado_fisico = 'REGISTRADO'
      AND mc.fecha_hora_movimiento >= v_fecha_apertura;

    IF v_saldo_calculado < p_monto THEN
        RAISE EXCEPTION 'La caja no cuenta con saldo suficiente para registrar el egreso';
    END IF;

    INSERT INTO movimiento_caja (
        id_caja,
        id_tipo_movimiento_caja,
        id_usuario_registra,
        id_venta,
        id_compra,
        id_metodo_pago,
        monto,
        descripcion,
        fecha_hora_movimiento,
        estado_fisico
    )
    VALUES (
        p_id_caja,
        v_id_tipo_egreso,
        p_id_usuario,
        NULL,
        NULL,
        p_id_metodo_pago,
        p_monto,
        NULLIF(TRIM(p_descripcion), ''),
        CURRENT_TIMESTAMP,
        'REGISTRADO'
    );

    UPDATE caja
    SET saldo_actual = v_saldo_calculado - p_monto
    WHERE id_caja = p_id_caja;

END;
$$;

-- =====================================================
-- V010: procedimiento_egreso_compra_caja
-- =====================================================
CREATE OR REPLACE PROCEDURE sp_registrar_egreso_compra_caja(
    IN p_id_compra INTEGER,
    IN p_id_usuario INTEGER,
    IN p_descripcion VARCHAR
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_caja INTEGER;
    v_id_tipo_egreso INTEGER;
    v_id_apertura_caja INTEGER;
    v_id_metodo_pago INTEGER;
    v_monto_inicial NUMERIC(12, 2);
    v_fecha_apertura TIMESTAMP;
    v_saldo_calculado NUMERIC(12, 2);
    v_total_compra NUMERIC(12, 2);
BEGIN

    IF p_id_compra IS NULL THEN
        RAISE EXCEPTION 'Debe indicar la compra';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM usuario
        WHERE id_usuario = p_id_usuario
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El usuario no existe o no se encuentra activo';
    END IF;

    SELECT id_caja
    INTO v_id_caja
    FROM caja
    WHERE estado_fisico = 'ABIERTA'
    ORDER BY id_caja ASC
    LIMIT 1;

    IF v_id_caja IS NULL THEN
        RAISE EXCEPTION 'No existe una caja abierta para registrar el egreso';
    END IF;

    SELECT total
    INTO v_total_compra
    FROM compra
    WHERE id_compra = p_id_compra
      AND estado_fisico = 'RECIBIDO'
      AND estado_logico = 1;

    IF v_total_compra IS NULL THEN
        RAISE EXCEPTION 'La compra no existe, no está activa o aún no fue recibida';
    END IF;

    IF v_total_compra <= 0 THEN
        RAISE EXCEPTION 'El total de la compra debe ser mayor a cero';
    END IF;

    SELECT id_metodo_pago
    INTO v_id_metodo_pago
    FROM pago_compra
    WHERE id_compra = p_id_compra
      AND estado_logico = 1
      AND estado_fisico = 'REGISTRADO'
    ORDER BY fecha_hora_pago DESC
    LIMIT 1;

    IF v_id_metodo_pago IS NULL THEN
        RAISE EXCEPTION 'La compra no tiene un método de pago registrado';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM metodo_pago
        WHERE id_metodo_pago = v_id_metodo_pago
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El método de pago de la compra no se encuentra activo';
    END IF;

    SELECT id_tipo_movimiento_caja
    INTO v_id_tipo_egreso
    FROM tipo_movimiento_caja
    WHERE nombre_tipo_movimiento = 'EGRESO';

    IF v_id_tipo_egreso IS NULL THEN
        RAISE EXCEPTION 'No existe el tipo de movimiento EGRESO';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM movimiento_caja
        WHERE id_compra = p_id_compra
          AND id_tipo_movimiento_caja = v_id_tipo_egreso
          AND estado_fisico = 'REGISTRADO'
    ) THEN
        RAISE EXCEPTION 'La compra ya tiene un egreso registrado en caja';
    END IF;

    SELECT
        ac.id_apertura_caja,
        ac.monto_inicial,
        ac.fecha_hora_apertura
    INTO
        v_id_apertura_caja,
        v_monto_inicial,
        v_fecha_apertura
    FROM apertura_caja ac
    WHERE ac.id_caja = v_id_caja
      AND NOT EXISTS (
          SELECT 1
          FROM cierre_caja cc
          WHERE cc.id_apertura_caja = ac.id_apertura_caja
      )
    ORDER BY ac.fecha_hora_apertura DESC
    LIMIT 1;

    IF v_id_apertura_caja IS NULL THEN
        RAISE EXCEPTION 'La caja no tiene una apertura activa';
    END IF;

    SELECT
        v_monto_inicial
        + COALESCE(
            SUM(
                CASE tmc.nombre_tipo_movimiento
                    WHEN 'INGRESO' THEN mc.monto
                    WHEN 'EGRESO' THEN mc.monto * -1
                    ELSE 0
                END
            ),
            0
        )
    INTO v_saldo_calculado
    FROM movimiento_caja mc
    INNER JOIN tipo_movimiento_caja tmc
        ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
    WHERE mc.id_caja = v_id_caja
      AND mc.estado_fisico = 'REGISTRADO'
      AND mc.fecha_hora_movimiento >= v_fecha_apertura;

    IF v_saldo_calculado < v_total_compra THEN
        RAISE EXCEPTION 'La caja no cuenta con saldo suficiente para pagar la compra';
    END IF;

    INSERT INTO movimiento_caja (
        id_caja,
        id_tipo_movimiento_caja,
        id_usuario_registra,
        id_venta,
        id_compra,
        id_metodo_pago,
        monto,
        descripcion,
        fecha_hora_movimiento,
        estado_fisico
    )
    VALUES (
        v_id_caja,
        v_id_tipo_egreso,
        p_id_usuario,
        NULL,
        p_id_compra,
        v_id_metodo_pago,
        v_total_compra,
        COALESCE(NULLIF(TRIM(p_descripcion), ''), 'Pago de compra recibida'),
        CURRENT_TIMESTAMP,
        'REGISTRADO'
    );

    UPDATE caja
    SET saldo_actual = v_saldo_calculado - v_total_compra
    WHERE id_caja = v_id_caja;

END;
$$;

-- =====================================================
-- V011: procedimiento_cerrar_caja
-- =====================================================
CREATE OR REPLACE PROCEDURE sp_cerrar_caja(
    IN p_id_caja INTEGER,
    IN p_id_usuario INTEGER,
    IN p_saldo_real NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_id_apertura_caja INTEGER;
    v_monto_inicial NUMERIC(12, 2);
    v_fecha_apertura TIMESTAMP;
    v_saldo_teorico NUMERIC(12, 2);
    v_diferencia NUMERIC(12, 2);
BEGIN

    IF p_id_caja IS NULL THEN
        RAISE EXCEPTION 'Debe indicar la caja';
    END IF;

    IF p_id_usuario IS NULL THEN
        RAISE EXCEPTION 'Debe indicar el usuario';
    END IF;

    IF p_saldo_real IS NULL OR p_saldo_real < 0 THEN
        RAISE EXCEPTION 'El saldo real no puede ser nulo ni negativo';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM caja
        WHERE id_caja = p_id_caja
          AND estado_fisico = 'ABIERTA'
    ) THEN
        RAISE EXCEPTION 'La caja no existe o no se encuentra abierta';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM usuario
        WHERE id_usuario = p_id_usuario
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El usuario no existe o no se encuentra activo';
    END IF;

    SELECT
        ac.id_apertura_caja,
        ac.monto_inicial,
        ac.fecha_hora_apertura
    INTO
        v_id_apertura_caja,
        v_monto_inicial,
        v_fecha_apertura
    FROM apertura_caja ac
    WHERE ac.id_caja = p_id_caja
      AND NOT EXISTS (
          SELECT 1
          FROM cierre_caja cc
          WHERE cc.id_apertura_caja = ac.id_apertura_caja
      )
    ORDER BY ac.fecha_hora_apertura DESC
    LIMIT 1;

    IF v_id_apertura_caja IS NULL THEN
        RAISE EXCEPTION 'La caja no tiene una apertura activa';
    END IF;

    SELECT
        v_monto_inicial
        + COALESCE(
            SUM(
                CASE tmc.nombre_tipo_movimiento
                    WHEN 'INGRESO' THEN mc.monto
                    WHEN 'EGRESO' THEN mc.monto * -1
                    ELSE 0
                END
            ),
            0
        )
    INTO v_saldo_teorico
    FROM movimiento_caja mc
    INNER JOIN tipo_movimiento_caja tmc
        ON tmc.id_tipo_movimiento_caja = mc.id_tipo_movimiento_caja
    WHERE mc.id_caja = p_id_caja
      AND mc.estado_fisico = 'REGISTRADO'
      AND mc.fecha_hora_movimiento >= v_fecha_apertura;

    v_diferencia = p_saldo_real - v_saldo_teorico;

    INSERT INTO cierre_caja (
        id_apertura_caja,
        id_usuario_registro,
        saldo_teorico,
        saldo_real,
        diferencia,
        fecha_hora_cierre
    )
    VALUES (
        v_id_apertura_caja,
        p_id_usuario,
        v_saldo_teorico,
        p_saldo_real,
        v_diferencia,
        CURRENT_TIMESTAMP
    );

    UPDATE caja
    SET estado_fisico = 'CERRADA',
        saldo_actual = p_saldo_real
    WHERE id_caja = p_id_caja;

END;
$$;

-- =====================================================
-- V012: procedimiento_abrir_caja
-- =====================================================
CREATE OR REPLACE PROCEDURE sp_abrir_caja(
    IN p_id_caja INTEGER,
    IN p_id_usuario INTEGER,
    IN p_monto_inicial NUMERIC
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_estado_caja VARCHAR(20);
BEGIN

    IF p_id_caja IS NULL THEN
        RAISE EXCEPTION 'Debe indicar la caja';
    END IF;

    IF p_id_usuario IS NULL THEN
        RAISE EXCEPTION 'Debe indicar el usuario';
    END IF;

    IF p_monto_inicial IS NULL OR p_monto_inicial < 0 THEN
        RAISE EXCEPTION 'El monto inicial no puede ser nulo ni negativo';
    END IF;

    SELECT estado_fisico
    INTO v_estado_caja
    FROM caja
    WHERE id_caja = p_id_caja
    FOR UPDATE;

    IF v_estado_caja IS NULL THEN
        RAISE EXCEPTION 'La caja no existe';
    END IF;

    IF v_estado_caja = 'INACTIVA' THEN
        RAISE EXCEPTION 'La caja se encuentra inactiva y no puede abrirse';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM usuario
        WHERE id_usuario = p_id_usuario
          AND estado_logico = 1
          AND estado_fisico = 'ACTIVO'
    ) THEN
        RAISE EXCEPTION 'El usuario no existe o no se encuentra activo';
    END IF;

    IF EXISTS (
        SELECT 1
        FROM apertura_caja ac
        WHERE ac.id_caja = p_id_caja
          AND NOT EXISTS (
              SELECT 1
              FROM cierre_caja cc
              WHERE cc.id_apertura_caja = ac.id_apertura_caja
          )
    ) THEN
        RAISE EXCEPTION 'La caja ya tiene una apertura activa';
    END IF;

    INSERT INTO apertura_caja (
        id_caja,
        id_usuario_registro,
        monto_inicial,
        fecha_hora_apertura
    )
    VALUES (
        p_id_caja,
        p_id_usuario,
        p_monto_inicial,
        CURRENT_TIMESTAMP
    );

    UPDATE caja
    SET estado_fisico = 'ABIERTA',
        saldo_actual = p_monto_inicial
    WHERE id_caja = p_id_caja;

END;
$$;