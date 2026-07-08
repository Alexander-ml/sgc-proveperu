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