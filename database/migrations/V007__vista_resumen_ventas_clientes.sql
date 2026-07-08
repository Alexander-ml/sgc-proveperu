/**
 * Vista de resumen de ventas por cliente.
 *
 * Consolida únicamente las ventas con estado REGISTRADA
 * para obtener las estadísticas utilizadas por el módulo de clientes.
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