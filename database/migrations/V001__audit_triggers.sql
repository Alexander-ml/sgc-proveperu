-- ============================================================
-- V001__audit_triggers.sql
-- Migration: Añadir triggers de auditoría para actualización
-- Fecha: 2026-05-03
-- ============================================================

-- ============================================================
-- 1. Función genérica: actualiza fecha_hora_actualizacion y,
--    si existe la columna id_usuario_actualizo, también la marca.
-- ============================================================

CREATE OR REPLACE FUNCTION fn_set_audit_fields()
RETURNS TRIGGER AS $$
BEGIN
    -- actualizar siempre la fecha
    NEW.fecha_hora_actualizacion = CURRENT_TIMESTAMP;

    -- si la tabla tiene campo id_usuario_actualizo (lo evalúa en runtime)
    IF EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_name = TG_TABLE_NAME
        AND column_name = 'id_usuario_actualizo'
    ) THEN
        -- si se envía un usuario desde backend, úsalo; si no, queda igual
        IF NEW.id_usuario_actualizo IS NULL THEN
            NEW.id_usuario_actualizo = OLD.id_usuario_actualizo;
        END IF;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- 2. CREACIÓN DE TRIGGERS PARA TODAS LAS TABLAS
--    SOLO SE APLICAN SI LA TABLA TIENE fecha_hora_actualizacion
-- ============================================================

-- Helper: genera trigger si existe columna
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