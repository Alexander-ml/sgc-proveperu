-- ============================================================
-- V001__audit_triggers.sql
-- Migration: Añadir triggers automáticos de auditoría
-- Fecha: 2026-05-03
-- ============================================================

-- ============================================================
-- 1. FUNCIÓN genérica utilizada por todos los triggers
--    - Actualiza fecha_hora_actualizacion automáticamente
--    - Se ejecuta en cualquier tabla que tenga esa columna
-- ============================================================

CREATE OR REPLACE FUNCTION fn_set_audit_fields()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_hora_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- ============================================================
-- 2. DO BLOCK dinámico:
--    - Detecta TODAS las tablas que tienen fecha_hora_actualizacion
--    - Crea un trigger BEFORE UPDATE para cada una
--    - Evita tener que escribir triggers tabla por tabla
-- ============================================================

DO $$
DECLARE
    r RECORD;
BEGIN
    FOR r IN 
        SELECT table_name
        FROM information_schema.columns
        WHERE column_name = 'fecha_hora_actualizacion'
          AND table_schema = 'public'
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_audit_%I
             BEFORE UPDATE ON %I
             FOR EACH ROW
             EXECUTE FUNCTION fn_set_audit_fields();',
            r.table_name,
            r.table_name
        );
    END LOOP;
END$$;