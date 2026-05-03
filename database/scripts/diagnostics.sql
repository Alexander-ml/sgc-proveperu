-- ===============================================
-- DIAGNOSTICS: Consultas para validar el estado
-- ===============================================

-- 1. Tablas existentes
SELECT table_name
FROM information_schema.tables
WHERE table_schema = 'public'
ORDER BY table_name;

-- 2. Conteo de registros por tabla
SELECT 'rol' AS tabla, COUNT(*) FROM rol
UNION ALL
SELECT 'usuario', COUNT(*) FROM usuario
UNION ALL
SELECT 'proveedor', COUNT(*) FROM proveedor
UNION ALL
SELECT 'cliente', COUNT(*) FROM cliente
UNION ALL
SELECT 'categoria', COUNT(*) FROM categoria
UNION ALL
SELECT 'producto', COUNT(*) FROM producto
UNION ALL
SELECT 'venta', COUNT(*) FROM venta
UNION ALL
SELECT 'detalle_venta', COUNT(*) FROM detalle_venta;

-- 3. Verificar integridad referencial
SELECT
    tc.table_name AS tabla,
    kcu.column_name AS columna,
    ccu.table_name AS tabla_referenciada,
    ccu.column_name AS columna_referenciada
FROM 
    information_schema.table_constraints AS tc
JOIN 
    information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
JOIN 
    information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
WHERE constraint_type = 'FOREIGN KEY';

-- 4. Ventas con total calculado
SELECT v.id_venta, v.total,
       SUM(dv.subtotal) AS suma_detalles
FROM venta v
LEFT JOIN detalle_venta dv ON dv.id_venta = v.id_venta
GROUP BY v.id_venta, v.total;

-- 5. Validar stock negativo
SELECT * FROM producto WHERE stock < 0;