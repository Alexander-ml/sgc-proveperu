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