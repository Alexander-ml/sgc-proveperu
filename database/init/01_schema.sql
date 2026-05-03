-- ==============================
--   01 - SCHEMA BASE
--   PROVEPERU S.R.L.
--   PostgreSQL
-- ==============================

CREATE SCHEMA IF NOT EXISTS public;

SET search_path TO public;

-- =============================================
-- 🔵 MÓDULO 1 — USUARIOS Y SEGURIDAD
-- =============================================

-- ========== TABLA: rol ==========
CREATE TABLE rol (
    id_rol INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP
);

-- ========== TABLA: usuario ==========
CREATE TABLE usuario (
    id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_completo VARCHAR(100) NOT NULL,
    usuario_login VARCHAR(50) NOT NULL UNIQUE
        CHECK (usuario_login = LOWER(usuario_login) AND usuario_login NOT LIKE '% %'),
    password_hash VARCHAR(255) NOT NULL,
    id_rol INTEGER NOT NULL REFERENCES rol(id_rol),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP,
    id_usuario_creador INTEGER REFERENCES usuario(id_usuario),
    id_usuario_actualizo INTEGER REFERENCES usuario(id_usuario)
);

-- ========== TABLA: permiso ==========
CREATE TABLE permiso (
    id_permiso INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    modulo VARCHAR(30) NOT NULL CHECK (modulo IN 
        ('VENTAS','INVENTARIO','COMPRAS','CAJA','REPORTES','CRM','USUARIOS')),
    accion VARCHAR(15) NOT NULL CHECK (accion IN ('CREAR','LEER','ACTUALIZAR','ELIMINAR')),
    descripcion VARCHAR(200),
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== TABLA: rol_permiso ==========
CREATE TABLE rol_permiso (
    id_rol INTEGER NOT NULL REFERENCES rol(id_rol),
    id_permiso INTEGER NOT NULL REFERENCES permiso(id_permiso),
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_rol, id_permiso)
);

-- ========== TABLA: usuario_sesion ==========
CREATE TABLE usuario_sesion (
    id_usuario_sesion INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha_hora_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_fin TIMESTAMP CHECK (fecha_hora_fin IS NULL OR fecha_hora_fin > fecha_hora_inicio),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);


-- =============================================
-- 🔵 MÓDULO 2 — CRM
-- =============================================

CREATE TABLE cliente (
    id_cliente INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tipo_cliente VARCHAR(10) NOT NULL CHECK (tipo_cliente IN ('PERSONA','EMPRESA')),
    nombre_completo VARCHAR(100),
    razon_social VARCHAR(150),
    dni VARCHAR(15) CHECK (dni ~ '^[0-9]{8}$'),
    ruc VARCHAR(20) CHECK (ruc ~ '^[0-9]{11}$'),
    telefono VARCHAR(20) CHECK (telefono ~ '^[0-9+- ]+$'),
    direccion VARCHAR(200),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP
);


-- =============================================
-- 🔵 MÓDULO 3 — ENTIDADES COMPARTIDAS (TPS)
-- =============================================

-- ========== PRODUCTO ==========
CREATE TABLE producto (
    id_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo_producto VARCHAR(50) NOT NULL UNIQUE,
    nombre_producto VARCHAR(100) NOT NULL,
    descripcion VARCHAR(300),
    unidad_medida VARCHAR(20) NOT NULL DEFAULT 'UNIDAD'
       CHECK (unidad_medida IN ('UNIDAD','KG','LITRO','METRO','CAJA')),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP
);

-- ========== STOCK (1:1 PRODUCTO) ==========
CREATE TABLE stock (
    id_producto INTEGER PRIMARY KEY REFERENCES producto(id_producto),
    cantidad_actual NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (cantidad_actual >= 0),
    stock_minimo NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (stock_minimo >= 0),
    fecha_hora_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== PROVEEDOR ==========
CREATE TABLE proveedor (
    id_proveedor INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ruc VARCHAR(11) NOT NULL UNIQUE CHECK (ruc ~ '^[0-9]{11}$'),
    razon_social VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) CHECK (telefono ~ '^[0-9+- ]+$'),
    direccion VARCHAR(200),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== METODO DE PAGO ==========
CREATE TABLE metodo_pago (
    id_metodo_pago INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_metodo_pago VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(100),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_hora_actualizacion TIMESTAMP
);


-- =============================================
-- 🔵 MÓDULO 4 — VENTAS (TPS)
-- =============================================

-- ========== VENTA ==========
CREATE TABLE venta (
    id_venta INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_cliente INTEGER NOT NULL REFERENCES cliente(id_cliente),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_metodo_pago INTEGER NOT NULL REFERENCES metodo_pago(id_metodo_pago),
    fecha_hora_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== DETALLE VENTA ==========
CREATE TABLE detalle_venta (
    id_detalle_venta INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta),
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    cantidad NUMERIC(10,2) NOT NULL CHECK (cantidad > 0),
    precio_unitario NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),
    subtotal NUMERIC(10,2) NOT NULL CHECK (subtotal >= 0),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== PAGO ==========
CREATE TABLE pago (
    id_pago INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta),
    monto NUMERIC(10,2) NOT NULL CHECK (monto >= 0),
    fecha_hora_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(150),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== COMPROBANTE ==========
CREATE TABLE comprobante (
    id_comprobante INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_venta INTEGER NOT NULL REFERENCES venta(id_venta),
    tipo_comprobante VARCHAR(20) NOT NULL 
        CHECK (tipo_comprobante IN ('BOLETA','FACTURA','TICKET')),
    numero_comprobante VARCHAR(50) NOT NULL UNIQUE,
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);


-- =============================================
-- 🔵 MÓDULO 5 — COMPRAS (TPS)
-- =============================================

-- ========== COMPRA ==========
CREATE TABLE compra (
    id_compra INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_proveedor INTEGER NOT NULL REFERENCES proveedor(id_proveedor),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha_hora_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total NUMERIC(10,2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    estado_compra VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
         CHECK (estado_compra IN ('PENDIENTE','PARCIAL','RECIBIDO','ANULADO')),
    observacion VARCHAR(300),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== DETALLE COMPRA ==========
CREATE TABLE detalle_compra (
    id_detalle_compra INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_compra INTEGER NOT NULL REFERENCES compra(id_compra),
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    cantidad NUMERIC(10,2) NOT NULL CHECK (cantidad > 0),
    precio_unitario_compra NUMERIC(12,2) NOT NULL CHECK (precio_unitario_compra > 0),
    subtotal NUMERIC(12,2) NOT NULL CHECK (subtotal >= 0),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== RECEPCIÓN COMPRA ==========
CREATE TABLE recepcion_compra (
    id_recepcion INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_compra INTEGER NOT NULL REFERENCES compra(id_compra),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    fecha_hora_recepcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_recepcion VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO'
        CHECK (estado_recepcion IN ('REGISTRADO','ANULADO')),
    observacion VARCHAR(300),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== PAGO COMPRA ==========
CREATE TABLE pago_compra (
    id_pago_compra INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_compra INTEGER NOT NULL REFERENCES compra(id_compra),
    id_metodo_pago INTEGER NOT NULL REFERENCES metodo_pago(id_metodo_pago),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    fecha_hora_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(300),
    estado_pago VARCHAR(20) NOT NULL DEFAULT 'REGISTRADO'
          CHECK (estado_pago IN ('REGISTRADO','ANULADO')),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);


-- =============================================
-- 🔵 MÓDULO 6 — INVENTARIO
-- =============================================

-- ========== TIPO MOVIMIENTO INVENTARIO ==========
CREATE TABLE tipo_movimiento_inventario (
    id_tipo_movimiento INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(200),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);

-- ========== MOVIMIENTO INVENTARIO ==========
CREATE TABLE movimiento_inventario (
    id_movimiento INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_producto INTEGER NOT NULL REFERENCES producto(id_producto),
    id_tipo_movimiento INTEGER NOT NULL REFERENCES tipo_movimiento_inventario(id_tipo_movimiento),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    cantidad NUMERIC(10,2) NOT NULL CHECK (cantidad > 0),
    stock_anterior NUMERIC(10,2) NOT NULL CHECK (stock_anterior >= 0),
    stock_nuevo NUMERIC(10,2) NOT NULL CHECK (stock_nuevo >= 0),
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(200),
    referencia VARCHAR(50),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO'
);


-- =============================================
-- 🔵 MÓDULO 7 — CAJA
-- =============================================

-- ========== CAJA ==========
CREATE TABLE caja (
    id_caja INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_caja VARCHAR(50) NOT NULL UNIQUE,
    saldo_actual NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (saldo_actual >= 0),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== APERTURA CAJA ==========
CREATE TABLE apertura_caja (
    id_apertura INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_caja INTEGER NOT NULL REFERENCES caja(id_caja),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    monto_inicial NUMERIC(12,2) NOT NULL DEFAULT 0 CHECK (monto_inicial >= 0),
    fecha_hora_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== CIERRE CAJA ==========
CREATE TABLE cierre_caja (
    id_cierre INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_apertura INTEGER NOT NULL UNIQUE REFERENCES apertura_caja(id_apertura),
    id_usuario INTEGER NOT NULL REFERENCES usuario(id_usuario),
    saldo_teorico NUMERIC(12,2) NOT NULL CHECK (saldo_teorico >= 0),
    saldo_real NUMERIC(12,2) NOT NULL CHECK (saldo_real >= 0),
    diferencia NUMERIC(12,2) NOT NULL,
    fecha_hora_cierre TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== TIPO MOVIMIENTO CAJA ==========
CREATE TABLE tipo_movimiento_caja (
    id_tipo_movimiento_caja INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_tipo_movimiento VARCHAR(20) NOT NULL UNIQUE
        CHECK (nombre_tipo_movimiento IN ('INGRESO','EGRESO')),
    descripcion VARCHAR(200),
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ========== MOVIMIENTO CAJA ==========
CREATE TABLE movimiento_caja (
    id_movimiento_caja INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_caja INTEGER NOT NULL REFERENCES caja(id_caja),
    id_tipo_movimiento_caja INTEGER NOT NULL REFERENCES tipo_movimiento_caja(id_tipo_movimiento_caja),
    id_usuario_registra INTEGER NOT NULL REFERENCES usuario(id_usuario),
    id_venta INTEGER REFERENCES venta(id_venta),
    id_compra INTEGER REFERENCES compra(id_compra),
    id_metodo_pago INTEGER REFERENCES metodo_pago(id_metodo_pago),
    monto NUMERIC(12,2) NOT NULL CHECK (monto > 0),
    descripcion VARCHAR(300),
    fecha_hora_movimiento TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado_logico INTEGER NOT NULL DEFAULT 1 CHECK (estado_logico IN (0,1)),
    estado_fisico VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_hora_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
