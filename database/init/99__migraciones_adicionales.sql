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
