-- =============================================================
-- V005 — TRIGGERS AUTOMÁTICOS PARA INTEGRIDAD Y BUENAS PRÁCTICAS
-- =============================================================


-- =============================================================
-- 1. ACTUALIZAR fecha_hora_actualizacion AUTOMÁTICAMENTE
-- =============================================================

CREATE OR REPLACE FUNCTION trg_set_fecha_actualizacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_hora_actualizacion := CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tablas que deben actualizar fecha_hora_actualizacion
CREATE TRIGGER tg_update_usuario
BEFORE UPDATE ON usuario
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_rol
BEFORE UPDATE ON rol
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_cliente
BEFORE UPDATE ON cliente
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_producto
BEFORE UPDATE ON producto
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_proveedor
BEFORE UPDATE ON proveedor
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_metodo_pago
BEFORE UPDATE ON metodo_pago
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_venta
BEFORE UPDATE ON venta
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_detalle_venta
BEFORE UPDATE ON detalle_venta
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_pago
BEFORE UPDATE ON pago
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_comprobante
BEFORE UPDATE ON comprobante
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_compra
BEFORE UPDATE ON compra
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_detalle_compra
BEFORE UPDATE ON detalle_compra
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_recepcion_compra
BEFORE UPDATE ON recepcion_compra
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_pago_compra
BEFORE UPDATE ON pago_compra
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_movimiento_inventario
BEFORE UPDATE ON movimiento_inventario
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_caja
BEFORE UPDATE ON caja
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_apertura_caja
BEFORE UPDATE ON apertura_caja
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_cierre_caja
BEFORE UPDATE ON cierre_caja
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();

CREATE TRIGGER tg_update_movimiento_caja
BEFORE UPDATE ON movimiento_caja
FOR EACH ROW EXECUTE FUNCTION trg_set_fecha_actualizacion();



-- =============================================================
-- 2. EVITAR ELIMINAR REGISTROS CON RELACIONES
-- =============================================================
-- En lugar de eliminar, pasamos estado_fisico = 'RELACIONADO'

CREATE OR REPLACE FUNCTION trg_prevent_delete_relacionado()
RETURNS TRIGGER AS $$
BEGIN
    -- Bloquea DELETE y cambia estado_fisico
    UPDATE ONLY %TABLE_NAME%
    SET estado_fisico = 'RELACIONADO',
        estado_logico = 0
    WHERE %PK% = OLD.%PK%;

    RAISE EXCEPTION 'No se puede eliminar registro relacionado, marcado como RELACIONADO.';
    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Se aplicará a tablas sensibles
-- Para evitar errores y no repetir código generamos triggers directos

CREATE OR REPLACE RULE prevent_delete_usuario AS
ON DELETE TO usuario DO INSTEAD
    UPDATE usuario SET estado_fisico='RELACIONADO', estado_logico=0 WHERE id_usuario=OLD.id_usuario;

CREATE OR REPLACE RULE prevent_delete_producto AS
ON DELETE TO producto DO INSTEAD
    UPDATE producto SET estado_fisico='RELACIONADO', estado_logico=0 WHERE id_producto=OLD.id_producto;

CREATE OR REPLACE RULE prevent_delete_cliente AS
ON DELETE TO cliente DO INSTEAD
    UPDATE cliente SET estado_fisico='RELACIONADO', estado_logico=0 WHERE id_cliente=OLD.id_cliente;

CREATE OR REPLACE RULE prevent_delete_proveedor AS
ON DELETE TO proveedor DO INSTEAD
    UPDATE proveedor SET estado_fisico='RELACIONADO', estado_logico=0 WHERE id_proveedor=OLD.id_proveedor;



-- =============================================================
-- 3. IMPEDIR STOCK NEGATIVO
-- =============================================================

CREATE OR REPLACE FUNCTION trg_prevent_negative_stock()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.cantidad_actual < 0 THEN
        RAISE EXCEPTION 'Stock no puede ser negativo';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tg_stock_no_negativo
BEFORE UPDATE ON stock
FOR EACH ROW EXECUTE FUNCTION trg_prevent_negative_stock();



-- =============================================================
-- 4. ASEGURAR estado_logico SOLO 0 o 1
-- =============================================================

CREATE OR REPLACE FUNCTION trg_check_estado_logico()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.estado_logico NOT IN (0,1) THEN
        RAISE EXCEPTION 'estado_logico solo puede ser 0 o 1';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tablas que usan estado_logico
CREATE TRIGGER tg_estado_usuario
BEFORE INSERT OR UPDATE ON usuario
FOR EACH ROW EXECUTE FUNCTION trg_check_estado_logico();

CREATE TRIGGER tg_estado_producto
BEFORE INSERT OR UPDATE ON producto
FOR EACH ROW EXECUTE FUNCTION trg_check_estado_logico();

CREATE TRIGGER tg_estado_cliente
BEFORE INSERT OR UPDATE ON cliente
FOR EACH ROW EXECUTE FUNCTION trg_check_estado_logico();

CREATE TRIGGER tg_estado_proveedor
BEFORE INSERT OR UPDATE ON proveedor
FOR EACH ROW EXECUTE FUNCTION trg_check_estado_logico();