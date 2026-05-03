-- ================================
-- V002 - Índices recomendados
-- ================================

-- Usuarios
CREATE INDEX idx_usuario_login ON usuario(usuario_login);
CREATE INDEX idx_usuario_estado ON usuario(estado_logico, estado_fisico);

-- Roles y permisos
CREATE INDEX idx_permiso_modulo ON permiso(modulo);
CREATE INDEX idx_permiso_accion ON permiso(accion);

-- Clientes
CREATE INDEX idx_cliente_tipo ON cliente(tipo_cliente);
CREATE INDEX idx_cliente_dni ON cliente(dni);
CREATE INDEX idx_cliente_ruc ON cliente(ruc);

-- Productos
CREATE INDEX idx_producto_codigo ON producto(codigo_producto);
CREATE INDEX idx_producto_estado ON producto(estado_logico, estado_fisico);

-- Proveedores
CREATE INDEX idx_proveedor_ruc ON proveedor(ruc);

-- Ventas
CREATE INDEX idx_venta_fecha ON venta(fecha_hora_venta);
CREATE INDEX idx_venta_cliente ON venta(id_cliente);
CREATE INDEX idx_venta_usuario ON venta(id_usuario);

-- Compras
CREATE INDEX idx_compra_proveedor ON compra(id_proveedor);
CREATE INDEX idx_compra_usuario ON compra(id_usuario);

-- Inventario
CREATE INDEX idx_movimiento_producto ON movimiento_inventario(id_producto);
CREATE INDEX idx_movimiento_fecha ON movimiento_inventario(fecha_hora);

-- Caja
CREATE INDEX idx_movimiento_caja_fecha ON movimiento_caja(fecha_hora_movimiento);
CREATE INDEX idx_movimiento_caja_tipo ON movimiento_caja(id_tipo_movimiento_caja);