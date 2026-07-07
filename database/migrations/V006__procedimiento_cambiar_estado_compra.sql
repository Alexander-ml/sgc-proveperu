-- =====================================================
-- V006 - PROCEDIMIENTO PARA CAMBIAR ESTADO DE COMPRA
-- =====================================================
-- Este procedimiento cambia el estado de una compra.
-- Si la compra pasa a RECIBIDO, registra la recepción
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
        RAISE EXCEPTION 'Estado de compra no válido: %', p_nuevo_estado;
    END IF;

    -- Obtener y bloquear la compra para evitar doble actualización al mismo tiempo
    SELECT estado_fisico
    INTO v_estado_actual
    FROM compra
    WHERE id_compra = p_id_compra
      AND estado_logico = 1
    FOR UPDATE;

    IF v_estado_actual IS NULL THEN
        RAISE EXCEPTION 'Compra no encontrada o inactiva: %', p_id_compra;
    END IF;

    -- Si ya está en el mismo estado, no se hace nada
    IF v_estado_actual = p_nuevo_estado THEN
        RETURN;
    END IF;

    -- Evitar cambiar una compra ya anulada
    IF v_estado_actual = 'ANULADO' THEN
        RAISE EXCEPTION 'No se puede cambiar el estado de una compra anulada';
    END IF;

    -- Evitar revertir una compra ya recibida porque ya afectó stock
    IF v_estado_actual = 'RECIBIDO' THEN
        RAISE EXCEPTION 'No se puede cambiar el estado de una compra ya recibida';
    END IF;

    -- Si el nuevo estado es RECIBIDO, se registra recepción y se actualiza stock
    IF p_nuevo_estado = 'RECIBIDO' THEN

        -- Registrar recepción si todavía no existe una recepción registrada
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