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