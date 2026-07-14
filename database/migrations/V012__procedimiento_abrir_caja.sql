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