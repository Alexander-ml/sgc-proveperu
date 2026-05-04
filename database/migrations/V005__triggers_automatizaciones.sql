-- ============================================================
-- V005__triggers_automatizaciones.sql
-- Automatizaciones del sistema mediante triggers
-- Base: schema corregido (venta, compra, inventario, caja)
-- ============================================================



-- ============================================================
-- 1. Trigger: validar fecha de sesión
-- Evita que fecha_hora_fin sea menor que fecha_hora_inicio
-- ============================================================

CREATE OR REPLACE FUNCTION tg_validar_fechas_sesion()
RETURNS TRIGGER AS $$
BEGIN
    -- Si viene fecha_fin y es menor que fecha_inicio → error
    IF NEW.fecha_hora_fin IS NOT NULL
       AND NEW.fecha_hora_fin < NEW.fecha_hora_inicio THEN
        RAISE EXCEPTION 'fecha_hora_fin no puede ser menor a fecha_hora_inicio';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_fechas_sesion
BEFORE INSERT OR UPDATE ON usuario_sesion
FOR EACH ROW
EXECUTE FUNCTION tg_validar_fechas_sesion();



-- ============================================================
-- 2. Trigger: actualizar stock automáticamente
-- Este trigger NO modifica stock directamente.
-- El movimiento real lo hace el SP sp_registrar_movimiento_inventario.
-- Este trigger solo evita inserciones manuales inválidas.
-- ============================================================

CREATE OR REPLACE FUNCTION tg_no_actualizar_stock_directo()
RETURNS TRIGGER AS $$
BEGIN
    RAISE EXCEPTION 'No se puede modificar la tabla stock directamente. Use el procedimiento sp_registrar_movimiento_inventario.';
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_no_update_stock
BEFORE UPDATE ON stock
FOR EACH ROW
EXECUTE FUNCTION tg_no_actualizar_stock_directo();



-- ============================================================
-- 3. Trigger: validación automática de estados de venta
-- Evita estados inválidos fuera de ('REGISTRADA','ANULADA')
-- ============================================================

CREATE OR REPLACE FUNCTION tg_validar_estado_venta()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_fisico NOT IN ('REGISTRADA','ANULADA') THEN
        RAISE EXCEPTION 'Estado físico de venta inválido';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_estado_venta
BEFORE INSERT OR UPDATE ON venta
FOR EACH ROW
EXECUTE FUNCTION tg_validar_estado_venta();



-- ============================================================
-- 4. Trigger: validar estado de comprobante
-- Solo ('EMITIDO','ANULADO')
-- ============================================================

CREATE OR REPLACE FUNCTION tg_validar_estado_comprobante()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_fisico NOT IN ('EMITIDO','ANULADO') THEN
        RAISE EXCEPTION 'Estado físico de comprobante inválido';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_estado_comprobante
BEFORE INSERT OR UPDATE ON comprobante
FOR EACH ROW
EXECUTE FUNCTION tg_validar_estado_comprobante();



-- ============================================================
-- 5. Trigger: validar estado de caja
-- Estados válidos: ('ABIERTA','CERRADA','INACTIVA')
-- ============================================================

CREATE OR REPLACE FUNCTION tg_validar_estado_caja()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_fisico NOT IN ('ABIERTA','CERRADA','INACTIVA') THEN
        RAISE EXCEPTION 'Estado físico de caja inválido';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_estado_caja
BEFORE INSERT OR UPDATE ON caja
FOR EACH ROW
EXECUTE FUNCTION tg_validar_estado_caja();



-- ============================================================
-- 6. Trigger: validar estado de movimiento de caja
-- Estados válidos: ('REGISTRADO','ANULADO')
-- ============================================================

CREATE OR REPLACE FUNCTION tg_validar_estado_mov_caja()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_fisico NOT IN ('REGISTRADO','ANULADO') THEN
        RAISE EXCEPTION 'Estado de movimiento de caja inválido';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_validar_estado_mov_caja
BEFORE INSERT OR UPDATE ON movimiento_caja
FOR EACH ROW
EXECUTE FUNCTION tg_validar_estado_mov_caja();