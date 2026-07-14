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