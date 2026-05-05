-- ================================================================
-- DIAGNOSTICS.SQL
-- Consultas para verificar el estado actual de la base de datos
-- ================================================================

-- =====================================================
-- 1. Validar tablas existentes en schema 'public'
-- =====================================================
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- =====================================================
-- 2. Conteo de registros por tabla principal
-- =====================================================
SELECT 'rol' AS tabla, COUNT(*) FROM rol
UNION ALL SELECT 'usuario', COUNT(*) FROM usuario
UNION ALL SELECT 'proveedor', COUNT(*) FROM proveedor
UNION ALL SELECT 'cliente', COUNT(*) FROM cliente
UNION ALL SELECT 'categoria', COUNT(*) FROM categoria
UNION ALL SELECT 'producto', COUNT(*) FROM producto
UNION ALL SELECT 'venta', COUNT(*) FROM venta
UNION ALL SELECT 'detalle_venta', COUNT(*) FROM detalle_venta;

-- =====================================================
-- 3. Validar integridad referencial (todas las FKs)
-- =====================================================
SELECT
    tc.table_name AS tabla,
    kcu.column_name AS columna,
    ccu.table_name AS tabla_referenciada,
    ccu.column_name AS columna_referenciada
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
     ON tc.constraint_name = kcu.constraint_name
JOIN information_schema.constraint_column_usage AS ccu
     ON ccu.constraint_name = tc.constraint_name
WHERE tc.constraint_type = 'FOREIGN KEY'
ORDER BY tc.table_name;

-- =====================================================
-- 4. Verificar suma de subtotales vs total de venta
-- =====================================================
SELECT 
    v.id_venta,
    v.total,
    SUM(dv.subtotal) AS suma_detalles
FROM venta v
LEFT JOIN detalle_venta dv ON dv.id_venta = v.id_venta
GROUP BY v.id_venta, v.total;

-- =====================================================
-- 5. Detectar productos en estado crítico (stock negativo)
-- =====================================================
SELECT * 
FROM producto 
WHERE stock_actual < 0;