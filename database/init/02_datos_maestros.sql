-- ============================================================
--   PROVEPERU ERP — DATOS DE PRUEBA COMPLETO
-- ============================================================
SET search_path TO public;

-- ============================================================
-- ROLES
-- ============================================================
INSERT INTO rol (nombre_rol, descripcion) VALUES
('ADMIN',    'Acceso total al sistema'),
('VENDEDOR', 'Gestión de ventas y atención al cliente'),
('ALMACEN',  'Gestión de inventario y stock'),
('COMPRAS',  'Gestión de proveedores y órdenes de compra'),
('CAJERO',   'Operación de caja y movimientos de efectivo');

-- ============================================================
-- PERMISOS
-- ============================================================
INSERT INTO permiso (modulo, accion, descripcion) VALUES
('USUARIOS','CREAR','Crear usuarios'),
('USUARIOS','LEER','Consultar usuarios'),
('USUARIOS','ACTUALIZAR','Modificar usuarios'),
('USUARIOS','ELIMINAR','Eliminar usuarios'),
('CLIENTES','CREAR','Registrar clientes'),
('CLIENTES','LEER','Consultar clientes'),
('CLIENTES','ACTUALIZAR','Modificar clientes'),
('CLIENTES','ELIMINAR','Eliminar clientes'),
('VENTAS','CREAR','Registrar ventas'),
('VENTAS','LEER','Consultar ventas'),
('VENTAS','ACTUALIZAR','Modificar ventas'),
('VENTAS','ELIMINAR','Anular ventas'),
('INVENTARIO','CREAR','Ingresar movimientos de inventario'),
('INVENTARIO','LEER','Consultar inventario'),
('INVENTARIO','ACTUALIZAR','Modificar inventario'),
('INVENTARIO','ELIMINAR','Anular movimientos de inventario'),
('COMPRAS','CREAR','Registrar órdenes de compra'),
('COMPRAS','LEER','Consultar compras'),
('COMPRAS','ACTUALIZAR','Modificar compras'),
('COMPRAS','ELIMINAR','Anular compras'),
('CAJA','CREAR','Registrar movimientos de caja'),
('CAJA','LEER','Consultar caja'),
('CAJA','ACTUALIZAR','Modificar movimientos de caja'),
('CAJA','ELIMINAR','Anular movimientos de caja'),
('PROCESOS_COMPARTIDOS','CREAR','Crear en módulos compartidos'),
('PROCESOS_COMPARTIDOS','LEER','Leer módulos compartidos'),
('PROCESOS_COMPARTIDOS','ACTUALIZAR','Actualizar módulos compartidos'),
('PROCESOS_COMPARTIDOS','ELIMINAR','Eliminar en módulos compartidos');

-- ADMIN: todos los permisos
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 1, id_permiso FROM permiso;

-- VENDEDOR: ventas + clientes + caja leer + inventario leer
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 2, id_permiso FROM permiso
WHERE (modulo = 'VENTAS' AND accion IN ('CREAR','LEER','ACTUALIZAR'))
   OR (modulo = 'CLIENTES' AND accion IN ('CREAR','LEER','ACTUALIZAR'))
   OR (modulo = 'CAJA' AND accion = 'LEER')
   OR (modulo = 'INVENTARIO' AND accion = 'LEER')
   OR (modulo = 'PROCESOS_COMPARTIDOS' AND accion = 'LEER');

-- ALMACEN: inventario completo + compras leer
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 3, id_permiso FROM permiso
WHERE (modulo = 'INVENTARIO')
   OR (modulo = 'COMPRAS' AND accion = 'LEER')
   OR (modulo = 'PROCESOS_COMPARTIDOS' AND accion = 'LEER');

-- COMPRAS: compras completo + inventario leer + proveedores
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 4, id_permiso FROM permiso
WHERE (modulo = 'COMPRAS')
   OR (modulo = 'INVENTARIO' AND accion IN ('LEER','ACTUALIZAR'))
   OR (modulo = 'PROCESOS_COMPARTIDOS' AND accion = 'LEER');

-- CAJERO: caja completo + ventas leer
INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT 5, id_permiso FROM permiso
WHERE (modulo = 'CAJA')
   OR (modulo = 'VENTAS' AND accion = 'LEER')
   OR (modulo = 'PROCESOS_COMPARTIDOS' AND accion = 'LEER');

-- ============================================================
-- USUARIOS
-- ============================================================
INSERT INTO usuario (nombre_completo, usuario_login, password_hash, id_rol, estado_fisico) VALUES
-- Admins
('Administrador General',     'admin@proveperu.com',            '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 1, 'ACTIVO'),
('Rosa Elena Díaz Vásquez',   'rosa@proveperu.com',        '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 1, 'ACTIVO'),
-- Vendedores
('Carlos Pérez Llontop',      'carlos@proveperu.com',     '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 2, 'ACTIVO'),
('Ana Cecilia Quispe Rojas',  'ana@proveperu.com',       '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 2, 'ACTIVO'),
('Pedro Iván Sánchez Cruz',   'pedro@proveperu.com',    '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 2, 'ACTIVO'),
('Fiorella Mendoza Zuñiga',   'fiorella@proveperu.com', '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 2, 'SUSPENDIDO'),
-- Almacén
('Lucía Ramos Herrera',       'lucia@proveperu.com',      '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 3, 'ACTIVO'),
('Marco Aurelio Flores Paz',  'marco@proveperu.com',     '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 3, 'ACTIVO'),
('Silvia Patricia Neyra Deza','silvia@proveperu.com',     '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 3, 'SUSPENDIDO'),
-- Compras
('Jorge Medina Llajaruna',    'jorge@proveperu.com',     '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 4, 'ACTIVO'),
('Carmen Elsa Vega Cortez',   'carmen@proveperu.com',      '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 4, 'ACTIVO'),
-- Cajeros
('María Torres Iparraguirre', 'maria@proveperu.com',     '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 5, 'ACTIVO'),
('Raúl Enrique Chávez Montes','raul@proveperu.com',      '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 5, 'ACTIVO'),
('Yolanda Purisima Cerna',    'yolanda@proveperu.com',    '$2a$12$B2zfshox0A5K42Gs3yvOVuKs/.wdz8fv4hKoEPIX/.Y2i0oMd3A/e', 5, 'SUSPENDIDO');

-- ============================================================
-- MÉTODOS DE PAGO
-- ============================================================
INSERT INTO metodo_pago (nombre_metodo_pago, descripcion) VALUES
('EFECTIVO',      'Pago en efectivo'),
('TARJETA',       'Tarjeta débito/crédito Visa o Mastercard'),
('TRANSFERENCIA', 'Transferencia bancaria BCP / Interbank / BBVA'),
('YAPE',          'Pago digital Yape'),
('PLIN',          'Pago digital Plin'),
('CHEQUE',        'Pago con cheque bancario');

-- ============================================================
-- PRODUCTOS (60 productos de ferretería / construcción / pinturas)
-- ============================================================
INSERT INTO producto (codigo_producto, nombre_producto, descripcion, unidad_medida) VALUES
('PRD-0001','Pintura Látex Blanco Humo 1GL','Pintura acrílica lavable interior/exterior','GALON'),
('PRD-0002','Cemento Sol Portland 42.5 KG','Bolsa de cemento tipo I','BOLSA'),
('PRD-0003','Malla Raschel 80% 2m','Malla de sombreo reforzada','METRO'),
('PRD-0004','Tornillo Punta Broca 1" Caja x100','Tornillo auto perforante galvanizado','CAJA'),
('PRD-0005','Thinner Estándar 1GL','Diluyente para esmalte y barniz','GALON'),
('PRD-0006','Pintura Esmalte Rojo Tráfico 1QT','Esmalte sintético brillante','CUARTO'),
('PRD-0007','Espuma Expansiva 750ml','Espuma de poliuretano multiusos','UNIDAD'),
('PRD-0008','Lija al Agua Grano 220','Lija impermeable grano fino','UNIDAD'),
('PRD-0009','Pintura Látex Azul Colonial 1GL','Látex color azul colonial','GALON'),
('PRD-0010','Masilla Acrílica Profesional 1 KG','Masilla para paredes interiores','KG'),
('PRD-0011','Pintura Látex Verde Menta 1GL','Látex color verde menta','GALON'),
('PRD-0012','Pintura Esmalte Negro Mate 1QT','Esmalte negro mate','CUARTO'),
('PRD-0013','Barniz Marino 1/4 GL','Barniz resistente humedad','CUARTO'),
('PRD-0014','Sellador Multipropósito 1GL','Sellador de poros madera/concreto','GALON'),
('PRD-0015','Impermeabilizante Chema Tekno 1GL','Impermeabilizante para azoteas','GALON'),
('PRD-0016','Cinta de Embalaje 2" Transparente','Rollo 50m','UNIDAD'),
('PRD-0017','Disco de Corte Metal 4.5"','Disco abrasivo para amoladora','UNIDAD'),
('PRD-0018','Disco de Corte Concreto 9"','Disco diamantado segmentado','UNIDAD'),
('PRD-0019','Broca para Concreto 1/4"','Broca SDS para taladro','UNIDAD'),
('PRD-0020','Broca HSS 8mm Juego x10','Juego de brocas acero rápido','JUEGO'),
('PRD-0021','Cable Eléctrico NYM 2.5mm² x100m','Cable bipolar flexible','ROLLO'),
('PRD-0022','Tubería PVC SAP 1/2" x5m','Tubo agua fría','UNIDAD'),
('PRD-0023','Tubería PVC SAL 4" x3m','Tubo desagüe','UNIDAD'),
('PRD-0024','Codo PVC 1/2" 90°','Accesorio para tuberías','UNIDAD'),
('PRD-0025','Tee PVC 4" Desagüe','Tee sanitaria','UNIDAD'),
('PRD-0026','Cemento Blanco 1 KG','Cemento blanco para juntas','KG'),
('PRD-0027','Arena Fina x Bolsa 25 KG','Arena lavada tamizada','BOLSA'),
('PRD-0028','Yeso Construcción 5 KG','Yeso blanco para cielos rasos','BOLSA'),
('PRD-0029','Alambre Negro N°16 x Rollo 5 KG','Alambre recocido','ROLLO'),
('PRD-0030','Clavo de Acero 2.5" x KG','Clavo para madera','KG'),
('PRD-0031','Clavos para Concreto 2" x100u','Clavos endurecidos','CAJA'),
('PRD-0032','Llave de Paso 1/2" PVC','Válvula de paso agua','UNIDAD'),
('PRD-0033','Trampa PVC 2"','Trampa P para desagüe','UNIDAD'),
('PRD-0034','Registro de Piso 4"','Registro de limpieza para piso','UNIDAD'),
('PRD-0035','Pintura Anticorrosiva Rojo Óxido 1GL','Base anticorrosiva','GALON'),
('PRD-0036','Pintura Tráfico Amarillo 1GL','Pintura para señalización','GALON'),
('PRD-0037','Diluyente Nitro 1GL','Diluyente para lacas','GALON'),
('PRD-0038','Lija Madera Grano 80 Pliego','Lija para madera grueso','UNIDAD'),
('PRD-0039','Lija Madera Grano 150 Pliego','Lija para madera fino','UNIDAD'),
('PRD-0040','Rodillo Felpa 9" + Extensión','Kit rodillo para pintar','UNIDAD'),
('PRD-0041','Brocha Cerda Natural 4"','Brocha profesional','UNIDAD'),
('PRD-0042','Espátula Flexible 4"','Espátula para masilla','UNIDAD'),
('PRD-0043','Cinta Masking Tape 1" x18m','Cinta de papel para pintura','UNIDAD'),
('PRD-0044','Niple PVC 1/2" x1/2"','Niple roscado','UNIDAD'),
('PRD-0045','Unión PVC 1/2"','Unión simple para agua fría','UNIDAD'),
('PRD-0046','Sikaflex-11 FC 300ml','Sellante poliuretano','UNIDAD'),
('PRD-0047','Chema Grout 25 KG','Mortero de reparación','BOLSA'),
('PRD-0048','Aditivo Chema-1 1GL','Impermeabilizante concreto','GALON'),
('PRD-0049','Cerámica Pared 30x30 Blanca m²','Cerámica para pared','M2'),
('PRD-0050','Porcelanato 60x60 Gris Oscuro m²','Porcelanato rectificado','M2'),
('PRD-0051','Pegamento Cerámica Novacel 25 KG','Adhesivo para cerámica','BOLSA'),
('PRD-0052','Fragua Blanca 1 KG','Mortero para juntas','KG'),
('PRD-0053','Ángulo de Acero 1"x1/8" x6m','Ángulo estructural','BARRA'),
('PRD-0054','Platina de Acero 2"x1/4" x6m','Platina metálica','BARRA'),
('PRD-0055','Soldadura Cellocord 6011 3/32" x KG','Electrodo de soldadura','KG'),
('PRD-0056','Soldadura Supercito 7018 1/8" x KG','Electrodo bajo hidrógeno','KG'),
('PRD-0057','Calamina Galvanizada 1.8m x 0.8m','Calamina para techos','UNIDAD'),
('PRD-0058','Eternit Onda 3 1.83m x 1.10m','Plancha de fibrocemento','UNIDAD'),
('PRD-0059','Madera Tornillo 2"x4"x10','Madera de construcción','PIE'),
('PRD-0060','Triplay Lupuna 4mm 1.22m x 2.44m','Triplay para encofrado','PLANCHA');

-- ============================================================
-- STOCK INICIAL
-- ============================================================
INSERT INTO stock (id_producto, cantidad_actual, stock_minimo) VALUES
(1, 120, 20),(2, 500, 50),(3, 200, 30),(4, 80, 15),(5, 90, 10),
(6, 60, 10),(7, 45, 10),(8, 200, 30),(9, 100, 20),(10, 150, 25),
(11, 80, 15),(12, 55, 10),(13, 40, 8),(14, 70, 10),(15, 65, 10),
(16, 120, 20),(17, 300, 50),(18, 150, 30),(19, 200, 30),(20, 60, 10),
(21, 40, 5),(22, 180, 30),(23, 100, 20),(24, 250, 40),(25, 120, 20),
(26, 200, 30),(27, 300, 50),(28, 150, 20),(29, 80, 15),(30, 120, 20),
(31, 90, 15),(32, 100, 15),(33, 80, 15),(34, 60, 10),(35, 70, 10),
(36, 45, 8),(37, 55, 10),(38, 150, 25),(39, 180, 30),(40, 60, 10),
(41, 100, 20),(42, 80, 15),(43, 150, 25),(44, 200, 30),(45, 180, 30),
(46, 50, 10),(47, 80, 15),(48, 60, 10),(49, 90, 15),(50, 75, 12),
(51, 120, 20),(52, 200, 30),(53, 50, 8),(54, 40, 6),(55, 80, 15),
(56, 75, 12),(57, 200, 30),(58, 150, 25),(59, 500, 80),(60, 80, 10);

-- ============================================================
-- PROVEEDORES (25 proveedores)
-- ============================================================
INSERT INTO proveedor (ruc, razon_social, telefono, direccion, estado_fisico) VALUES
('20123456789','Aceros Arequipa S.A.','016123456','Av. Argentina 345, Lima', 'ACTIVO'),
('20654321876','Pinturas Anypsa Corporation S.A.','014567890','Av. Separadora Industrial 500, Lima', 'ACTIVO'),
('20345698712','Corporación Paraíso S.A.C.','017894561','Av. Evitamiento 900, Lima', 'ACTIVO'),
('20456789123','Cementos Pacasmayo S.A.A.','044612000','Av. Pacasmayo 123, Pacasmayo', 'ACTIVO'),
('20567891234','Distribuidora Ferretera Del Norte S.A.C.','074234567','Av. Bolognesi 456, Chiclayo', 'ACTIVO'),
('20678912345','Tubos y Accesorios PVC Perú S.A.C.','012345678','Av. Industrial 234, Lima', 'ACTIVO'),
('20789123456','Importaciones Eléctricas Nor-Perú S.R.L.','074345678','Calle Las Flores 789, Chiclayo', 'ACTIVO'),
('20891234567','Maderas y Triplay del Norte S.A.C.','044789456','Av. Agricultura 567, Trujillo', 'ACTIVO'),
('20912345678','Cerámicas y Porcelanatos Lima S.A.','017123456','Av. Colonial 890, Lima', 'ACTIVO'),
('20123987654','Sika Perú S.A.','012678901','Calle Amador Merino 234, Lima', 'ACTIVO'),
('20234198765','Chema Tecnología y Construcción S.A.','016789012','Av. Naciones Unidas 456, Lima', 'ACTIVO'),
('20345209876','Ferretería Industrial Norte S.R.L.','074456789','Av. Venezuela 123, Chiclayo', 'ACTIVO'),
('20456310987','Distribuciones Metales Chiclayo S.A.C.','074567890','Calle Elías Aguirre 456, Chiclayo', 'ACTIVO'),
('20567421098','Herramientas y Abrasivos del Norte S.A.C.','044678901','Av. España 789, Trujillo', 'ACTIVO'),
('20678532109','Pinturas CPP S.A.','016890123','Av. Naranjal 234, Lima', 'ACTIVO'),
('20789643210','Ferremundo Distribuciones S.A.C.','012901234','Av. República de Panamá 678, Lima', 'ACTIVO'),
('20890754321','Grupo Industrial Ferramas S.R.L.','044901234','Calle Libertad 345, Trujillo', 'ACTIVO'),
('20901865432','Materiales de Construcción Lambayeque E.I.R.L.','074678901','Av. Salaverry 789, Chiclayo', 'ACTIVO'),
('20112976543','Soldaduras y Electrodos Perú S.A.C.','017012345','Av. Faucett 567, Lima', 'ACTIVO'),
('20223087654','Eternit Perú S.A.','012123456','Av. Separadora Industrial 890, Lima', 'ACTIVO'),
('20334198765','Calaminas del Norte S.R.L.','044234567','Av. América Norte 123, Trujillo', 'ACTIVO'),
('20445209876','Distribuidora Global Ferretería S.A.C.','076123456','Av. Aviación 456, Cajamarca', 'ACTIVO'),
('20556310987','Plásticos y PVC Mega S.A.C.','016234567','Av. Túpac Amaru 678, Lima', 'ACTIVO'),
('20667421098','Insumos Industriales Norcerú S.A.C.','074789012','Av. Progreso 234, Chiclayo', 'ACTIVO'),
('20778532109','Ferroproyectos del Norte E.I.R.L.','074890123','Calle Juan Tomis 567, Chiclayo', 'ACTIVO');

-- ============================================================
-- CLIENTES — PERSONAS (35)
-- ============================================================
INSERT INTO cliente (tipo_cliente, nombre_completo, dni, telefono, direccion, estado_fisico) VALUES
('PERSONA','Luis Alberto García Peralta','74231569','987654321','Av. Los Pinos 345, Chiclayo','ACTIVO'),
('PERSONA','María Elena Huanca Quispe','62345678','976543219','Jr. Tarapacá 123, Chiclayo','ACTIVO'),
('PERSONA','José Miguel Castillo Llanos','53456789','965432198','Calle San José 456, Lambayeque','ACTIVO'),
('PERSONA','Carmen Rosa Delgado Vásquez','44567890','954321987','Av. Elvira García 789, Chiclayo','ACTIVO'),
('PERSONA','Pedro Pablo Fernández Cruz','35678901','943219876','Jr. Unión 234, Ferreñafe','ACTIVO'),
('PERSONA','Ana Luisa Mendoza Salazar','76789012','932198765','Av. Leguía 567, Chiclayo','ACTIVO'),
('PERSONA','Roberto Carlos Soto Díaz','67890123','921987654','Calle Las Rosas 890, Pimentel','ACTIVO'),
('PERSONA','Gloria Patricia Reyes Herrera','58901234','919876543','Av. Bolognesi 345, Chiclayo','ACTIVO'),
('PERSONA','Víctor Manuel Torres Ramos','49012345','908765432','Jr. Loreto 678, Chiclayo','ACTIVO'),
('PERSONA','Silvia Augustina Llanos Paz','30123456','897654321','Av. Balta 234, Chiclayo','ACTIVO'),
('PERSONA','Eduardo Enrique Vásquez Neyra','71234567','886543210','Calle Colón 567, Moshoqueque','ACTIVO'),
('PERSONA','Patricia Giovanna Núñez Deza','62345678','875432109','Av. Salaverry 890, Chiclayo','ACTIVO'),
('PERSONA','Miguel Ángel Romero Zapata','53456780','864321098','Jr. Arica 123, Chiclayo','ACTIVO'),
('PERSONA','Luciana Beatriz Chávez Puris','44567891','853219987','Av. Progreso 456, La Victoria','ACTIVO'),
('PERSONA','Fernando José Guzmán León','35678902','842198876','Calle Santa Rosa 789, Chiclayo','ACTIVO'),
('PERSONA','Isabel Cristina Vargas Benites','76789013','831987765','Av. Los Incas 234, Chiclayo','ACTIVO'),
('PERSONA','Andrés Alberto Cerna Espinoza','67890124','820876654','Jr. Tacna 567, Lambayeque','ACTIVO'),
('PERSONA','Norma Esther Ruiz Muro','58901235','819765543','Av. Universitaria 890, Chiclayo','ACTIVO'),
('PERSONA','Gonzalo Martín Pizarro Quispe','49012346','808654432','Calle Los Álamos 345, Chiclayo','ACTIVO'),
('PERSONA','Alejandra Sofía Burga Monsalve','30123457','797543321','Av. Fitzcarrald 678, Chiclayo','ACTIVO'),
('PERSONA','Raúl Alfredo Heredia Checa','71234568','786432210','Jr. San Martín 234, Ferreñafe','ACTIVO'),
('PERSONA','Cecilia Margarita Valera Flores','62345679','775321109','Av. Chinchaysuyo 567, Chiclayo','ACTIVO'),
('PERSONA','Humberto Augusto Gamarra Soto','53456781','764219998','Calle Manco Cápac 890, Chiclayo','ACTIVO'),
('PERSONA','Diana Lucía Meza Iparraguirre','44567892','753198887','Av. Mariscal Nieto 123, Chiclayo','ACTIVO'),
('PERSONA','Claudio Emilio Peralta Rojas','35678903','742187776','Jr. Leoncio Prado 456, Chiclayo','ACTIVO'),
('PERSONA','Yolanda Pilar Chávarry Albújar','76789014','731076665','Av. Pedro Ruiz 789, Chiclayo','ACTIVO'),
('PERSONA','Alonso Rodrigo Villareal Tantarico','67890125','719965554','Calle Juan XXIII 234, Chiclayo','ACTIVO'),
('PERSONA','Mónica Susana Cabrejos Farro','58901236','708854443','Av. Los Libertadores 567, Chiclayo','ACTIVO'),
('PERSONA','César Augusto Cubas Quiroz','49012347','697743332','Jr. Bolívar 890, Ferreñafe','ACTIVO'),
('PERSONA','Lidia Isabel Llontop Altamirano','30123458','686632221','Av. Colectora 345, Chiclayo','ACTIVO'),
('PERSONA','Oswaldo René Tafur Briones','71234569','675521110','Calle Zarumilla 678, Chiclayo','ACTIVO'),
('PERSONA','Marlene Paola Sánchez Benavides','62345670','664419999','Av. Sesquicentenario 234, Chiclayo','ACTIVO'),
('PERSONA','Ítalo Renato Coronado Chambergo','53456782','653308888','Jr. Amazonas 567, Lambayeque','ACTIVO'),
('PERSONA','Flor de María Zuloeta Melly','44567893','642207777','Av. Víctor Raúl 890, Chiclayo','INACTIVO'),
('PERSONA','Gastón Ernesto Espinoza Muñoz','35678904','631196666','Calle Los Fresnos 123, Chiclayo','INACTIVO');

-- ============================================================
-- CLIENTES — EMPRESAS (22)
-- ============================================================
INSERT INTO cliente (tipo_cliente, razon_social, ruc, telefono, direccion, estado_fisico) VALUES
('EMPRESA','Construcciones San Miguel S.A.C.','20654298761','016785432','Av. La Marina 123, Lima','ACTIVO'),
('EMPRESA','Inmobiliaria Del Norte S.A.C.','20765309872','074345678','Av. Balta 456, Chiclayo','ACTIVO'),
('EMPRESA','Grupo Constructor Lambayeque S.R.L.','20876410983','074456789','Calle Elías Aguirre 789, Chiclayo','ACTIVO'),
('EMPRESA','Edificaciones Pérez Hermanos E.I.R.L.','20987521094','044567890','Av. España 345, Trujillo','ACTIVO'),
('EMPRESA','Multiservicios JMC S.A.C.','20198632105','074678901','Av. Salaverry 678, Chiclayo','ACTIVO'),
('EMPRESA','Constructora Norcerú S.A.C.','20209743216','076789012','Av. Atahualpa 123, Cajamarca','ACTIVO'),
('EMPRESA','Proyectos y Obras Civiles S.A.C.','20310854327','073890123','Av. Grau 456, Piura','ACTIVO'),
('EMPRESA','Ferretería y Pinturas El Constructor S.R.L.','20421965438','074901234','Av. Chinchaysuyo 789, Chiclayo','ACTIVO'),
('EMPRESA','Distribuciones Ferremas S.A.C.','20532076549','044012345','Calle Libertad 234, Trujillo','ACTIVO'),
('EMPRESA','Ingeniería y Construcción Lacasa S.A.C.','20643187650','016123456','Av. Angamos 567, Lima','ACTIVO'),
('EMPRESA','Inversiones Inmobiliarias Norte E.I.R.L.','20754298761','074234567','Av. Fitzcarrald 890, Chiclayo','ACTIVO'),
('EMPRESA','Corporación Minera Nor-Perú S.A.C.','20865309872','076345678','Av. Minería 123, Cajamarca','ACTIVO'),
('EMPRESA','Agroindustrias del Norte S.A.C.','20976410983','074456789','Av. Agricultura 456, Chiclayo','ACTIVO'),
('EMPRESA','PROVEPERU Contratistas Generales S.A.C.','20187521094','074567890','Calle Juan Tomis 789, Chiclayo','ACTIVO'),
('EMPRESA','Constructora y Remodeladora Lima Norte S.A.C.','20298632105','012678901','Av. Túpac Amaru 234, Lima','ACTIVO'),
('EMPRESA','Industrias Metálicas Lambayeque S.A.C.','20309743216','074789012','Av. Progreso 567, Chiclayo','ACTIVO'),
('EMPRESA','Mega Distribuciones Ferreteras S.A.C.','20410854327','074890123','Av. Sesquicentenario 890, Chiclayo','ACTIVO'),
('EMPRESA','Servicios Generales y Mantenimiento S.R.L.','20521965438','073901234','Av. Los Tallanes 345, Piura','ACTIVO'),
('EMPRESA','Consorcio Constructor Inca S.A.C.','20632076549','044012345','Av. América Norte 678, Trujillo','INACTIVO'),
('EMPRESA','Materiales para Construcción Sur S.A.C.','20743187650','054123456','Av. Dolores 234, Arequipa','INACTIVO'),
('EMPRESA','Ferremart Chiclayo S.A.C.','20854298762','074234568','Av. Bolognesi 567, Chiclayo','ACTIVO'),
('EMPRESA','Constructora Regional Moche S.A.C.','20965309873','044345679','Calle Roma 890, Trujillo','ACTIVO');

-- ============================================================
-- CAJAS (5 cajas)
-- ============================================================
INSERT INTO caja (nombre_caja, saldo_actual, estado_fisico) VALUES
('CAJA PRINCIPAL',    0, 'ABIERTA'),
('CAJA SECUNDARIA',   0, 'ABIERTA'),
('CAJA VENTAS MOSTRADOR', 0, 'ABIERTA'),
('CAJA COMPRAS',      0, 'CERRADA'),
('CAJA INACTIVA',     0, 'INACTIVA');

-- ============================================================
-- TIPO MOVIMIENTO INVENTARIO
-- ============================================================
INSERT INTO tipo_movimiento_inventario (nombre) VALUES
('INGRESO'),
('EGRESO'),
('AJUSTE_POSITIVO'),
('AJUSTE_NEGATIVO');

-- ============================================================
-- TIPO MOVIMIENTO CAJA
-- ============================================================
INSERT INTO tipo_movimiento_caja (nombre_tipo_movimiento) VALUES
('INGRESO'),
('EGRESO');

-- ============================================================
-- DATOS TRANSACCIONALES
-- ============================================================

-- ============================================================
-- SESIONES DE USUARIOS (historial, distintas fechas)
-- ============================================================
INSERT INTO usuario_sesion (id_usuario, fecha_hora_inicio, fecha_hora_fin) VALUES
(1,'2024-12-01 08:00:00','2024-12-01 17:00:00'),
(1,'2024-12-02 08:05:00','2024-12-02 16:55:00'),
(1,'2025-01-05 07:58:00','2025-01-05 17:10:00'),
(2,'2024-12-03 08:10:00','2024-12-03 17:00:00'),
(2,'2025-01-06 08:00:00','2025-01-06 17:00:00'),
(3,'2024-12-04 08:00:00','2024-12-04 18:00:00'),
(3,'2024-12-05 08:00:00','2024-12-05 17:30:00'),
(3,'2025-01-07 08:00:00','2025-01-07 18:00:00'),
(3,'2025-02-03 08:00:00','2025-02-03 17:45:00'),
(4,'2025-01-08 08:30:00','2025-01-08 17:00:00'),
(4,'2025-02-04 08:00:00','2025-02-04 17:00:00'),
(5,'2025-01-09 08:00:00','2025-01-09 17:30:00'),
(5,'2025-02-05 08:00:00','2025-02-05 17:00:00'),
(6,'2025-01-10 08:00:00','2025-01-10 17:00:00'),
(7,'2024-12-10 08:00:00','2024-12-10 17:00:00'),
(7,'2025-01-15 08:00:00','2025-01-15 17:00:00'),
(7,'2025-02-10 08:00:00','2025-02-10 17:00:00'),
(8,'2025-01-16 08:00:00','2025-01-16 17:00:00'),
(8,'2025-02-11 08:00:00','2025-02-11 17:00:00'),
(10,'2024-12-15 08:00:00','2024-12-15 17:00:00'),
(10,'2025-01-20 08:00:00','2025-01-20 17:00:00'),
(10,'2025-02-15 08:00:00','2025-02-15 17:00:00'),
(11,'2025-01-21 08:00:00','2025-01-21 17:00:00'),
(12,'2024-12-20 08:00:00','2024-12-20 17:00:00'),
(12,'2025-01-25 08:00:00','2025-01-25 17:00:00'),
(12,'2025-02-20 08:00:00','2025-02-20 17:00:00'),
(12,'2025-03-01 08:00:00','2025-03-01 17:00:00'),
(13,'2025-01-26 08:00:00','2025-01-26 17:00:00'),
(13,'2025-02-21 08:00:00','2025-02-21 17:00:00'),
-- sesiones activas (sin cierre)
(1,'2025-06-10 08:00:00',NULL),
(3,'2025-06-10 08:15:00',NULL),
(12,'2025-06-10 08:30:00',NULL);

-- ============================================================
-- APERTURAS DE CAJA
-- ============================================================
INSERT INTO apertura_caja (id_caja, id_usuario_registro, monto_inicial, fecha_hora_apertura) VALUES
(1, 12, 500.00,  '2024-12-01 08:00:00'),
(1, 12, 600.00,  '2024-12-02 08:00:00'),
(1, 13, 500.00,  '2025-01-02 08:00:00'),
(1, 12, 500.00,  '2025-01-13 08:00:00'),
(1, 13, 700.00,  '2025-02-03 08:00:00'),
(1, 12, 500.00,  '2025-02-17 08:00:00'),
(1, 13, 500.00,  '2025-03-03 08:00:00'),
(1, 12, 600.00,  '2025-03-17 08:00:00'),
(1, 13, 500.00,  '2025-04-01 08:00:00'),
(1, 12, 500.00,  '2025-04-14 08:00:00'),
(1, 13, 600.00,  '2025-05-05 08:00:00'),
(1, 12, 500.00,  '2025-05-19 08:00:00'),
(1, 13, 500.00,  '2025-06-02 08:00:00'),
(2, 13, 300.00,  '2025-01-06 08:00:00'),
(2, 12, 300.00,  '2025-02-10 08:00:00'),
(2, 13, 300.00,  '2025-03-10 08:00:00'),
(2, 12, 400.00,  '2025-04-07 08:00:00'),
(2, 13, 300.00,  '2025-05-05 08:00:00'),
(3, 12, 200.00,  '2025-04-01 08:00:00'),
(3, 13, 200.00,  '2025-05-05 08:00:00'),
(4, 10, 1000.00, '2025-01-02 08:00:00'),
(4, 10, 1000.00, '2025-03-03 08:00:00');

-- ============================================================
-- CIERRES DE CAJA
-- ============================================================
INSERT INTO cierre_caja (id_apertura_caja, id_usuario_registro, saldo_teorico, saldo_real, diferencia, fecha_hora_cierre) VALUES
(1,  12, 3200.00, 3180.00,  -20.00, '2024-12-01 18:00:00'),
(2,  12, 4100.00, 4095.00,   -5.00, '2024-12-02 18:00:00'),
(3,  13, 5200.00, 5200.00,    0.00, '2025-01-12 18:00:00'),
(4,  12, 6300.00, 6310.00,   10.00, '2025-01-26 18:00:00'),
(5,  13, 7400.00, 7385.00,  -15.00, '2025-02-16 18:00:00'),
(6,  12, 5800.00, 5800.00,    0.00, '2025-03-02 18:00:00'),
(7,  13, 6100.00, 6090.00,  -10.00, '2025-03-16 18:00:00'),
(8,  12, 7200.00, 7205.00,    5.00, '2025-03-31 18:00:00'),
(9,  13, 6500.00, 6500.00,    0.00, '2025-04-13 18:00:00'),
(10, 12, 5900.00, 5895.00,   -5.00, '2025-04-27 18:00:00'),
(11, 13, 6800.00, 6800.00,    0.00, '2025-05-18 18:00:00'),
(12, 12, 7100.00, 7090.00,  -10.00, '2025-06-01 18:00:00'),
(14, 13, 2800.00, 2800.00,    0.00, '2025-01-20 18:00:00'),
(15, 12, 3200.00, 3190.00,  -10.00, '2025-02-28 18:00:00'),
(16, 13, 2900.00, 2900.00,    0.00, '2025-03-31 18:00:00'),
(17, 12, 3500.00, 3500.00,    0.00, '2025-04-30 18:00:00'),
(18, 13, 3100.00, 3095.00,   -5.00, '2025-05-30 18:00:00'),
(19, 12, 2100.00, 2100.00,    0.00, '2025-04-30 18:00:00'),
(21, 10, 8500.00, 8490.00,  -10.00, '2025-02-28 18:00:00'),
(22, 10, 9200.00, 9200.00,    0.00, '2025-05-31 18:00:00');

-- ============================================================
-- COMPRAS (59 compras, estados variados)
-- ============================================================
INSERT INTO compra (id_proveedor, id_usuario_registro, fecha_hora_creacion, total, estado_logico, estado_fisico) VALUES
-- RECIBIDO
(1,  10, '2024-12-03 10:00:00', 4500.00, 1, 'RECIBIDO'),
(2,  10, '2024-12-05 10:00:00', 2800.00, 1, 'RECIBIDO'),
(3,  11, '2024-12-08 10:00:00', 1950.00, 1, 'RECIBIDO'),
(4,  10, '2024-12-10 10:00:00', 6200.00, 1, 'RECIBIDO'),
(5,  11, '2024-12-12 10:00:00', 3100.00, 1, 'RECIBIDO'),
(6,  10, '2025-01-05 10:00:00', 2250.00, 1, 'RECIBIDO'),
(7,  11, '2025-01-07 10:00:00', 1800.00, 1, 'RECIBIDO'),
(8,  10, '2025-01-10 10:00:00', 5500.00, 1, 'RECIBIDO'),
(9,  11, '2025-01-12 10:00:00', 4200.00, 1, 'RECIBIDO'),
(10, 10, '2025-01-15 10:00:00', 3300.00, 1, 'RECIBIDO'),
(11, 11, '2025-01-18 10:00:00', 2100.00, 1, 'RECIBIDO'),
(12, 10, '2025-01-20 10:00:00', 1650.00, 1, 'RECIBIDO'),
(1,  11, '2025-02-03 10:00:00', 5200.00, 1, 'RECIBIDO'),
(2,  10, '2025-02-05 10:00:00', 3400.00, 1, 'RECIBIDO'),
(3,  11, '2025-02-08 10:00:00', 2700.00, 1, 'RECIBIDO'),
(4,  10, '2025-02-10 10:00:00', 7100.00, 1, 'RECIBIDO'),
(5,  11, '2025-02-12 10:00:00', 4600.00, 1, 'RECIBIDO'),
(13, 10, '2025-02-15 10:00:00', 1900.00, 1, 'RECIBIDO'),
(14, 11, '2025-02-18 10:00:00', 2500.00, 1, 'RECIBIDO'),
(15, 10, '2025-02-20 10:00:00', 3800.00, 1, 'RECIBIDO'),
(16, 11, '2025-03-03 10:00:00', 4100.00, 1, 'RECIBIDO'),
(17, 10, '2025-03-05 10:00:00', 2200.00, 1, 'RECIBIDO'),
(18, 11, '2025-03-08 10:00:00', 1750.00, 1, 'RECIBIDO'),
(19, 10, '2025-03-10 10:00:00', 6800.00, 1, 'RECIBIDO'),
(20, 11, '2025-03-12 10:00:00', 5300.00, 1, 'RECIBIDO'),
(1,  10, '2025-03-15 10:00:00', 3900.00, 1, 'RECIBIDO'),
(2,  11, '2025-03-18 10:00:00', 2600.00, 1, 'RECIBIDO'),
(3,  10, '2025-03-20 10:00:00', 1400.00, 1, 'RECIBIDO'),
(4,  11, '2025-04-02 10:00:00', 8200.00, 1, 'RECIBIDO'),
(5,  10, '2025-04-05 10:00:00', 4700.00, 1, 'RECIBIDO'),
-- PARCIAL
(6,  11, '2025-04-08 10:00:00', 3200.00, 1, 'PARCIAL'),
(7,  10, '2025-04-10 10:00:00', 2900.00, 1, 'PARCIAL'),
(8,  11, '2025-04-12 10:00:00', 4500.00, 1, 'PARCIAL'),
(9,  10, '2025-04-15 10:00:00', 6100.00, 1, 'PARCIAL'),
(10, 11, '2025-04-18 10:00:00', 2400.00, 1, 'PARCIAL'),
(21, 10, '2025-04-20 10:00:00', 5800.00, 1, 'PARCIAL'),
(22, 11, '2025-05-03 10:00:00', 3600.00, 1, 'PARCIAL'),
(23, 10, '2025-05-05 10:00:00', 2100.00, 1, 'PARCIAL'),
-- PENDIENTE
(11, 11, '2025-05-08 10:00:00', 4300.00, 1, 'PENDIENTE'),
(12, 10, '2025-05-10 10:00:00', 1900.00, 1, 'PENDIENTE'),
(13, 11, '2025-05-12 10:00:00', 7500.00, 1, 'PENDIENTE'),
(14, 10, '2025-05-15 10:00:00', 3200.00, 1, 'PENDIENTE'),
(15, 11, '2025-05-18 10:00:00', 2600.00, 1, 'PENDIENTE'),
(24, 10, '2025-05-20 10:00:00', 5100.00, 1, 'PENDIENTE'),
(25, 11, '2025-05-22 10:00:00', 4400.00, 1, 'PENDIENTE'),
(1,  10, '2025-06-02 10:00:00', 3700.00, 1, 'PENDIENTE'),
(2,  11, '2025-06-04 10:00:00', 2800.00, 1, 'PENDIENTE'),
(3,  10, '2025-06-06 10:00:00', 1600.00, 1, 'PENDIENTE'),
(4,  11, '2025-06-09 10:00:00', 6900.00, 1, 'PENDIENTE'),
-- ANULADO
(5,  10, '2025-01-25 10:00:00', 2300.00, 0, 'ANULADO'),
(6,  11, '2025-02-25 10:00:00', 1800.00, 0, 'ANULADO'),
(7,  10, '2025-03-25 10:00:00', 3500.00, 0, 'ANULADO'),
(8,  11, '2025-04-25 10:00:00', 4200.00, 0, 'ANULADO'),
(9,  10, '2025-05-25 10:00:00', 2900.00, 0, 'ANULADO'),
(10, 11, '2025-06-07 10:00:00', 1500.00, 0, 'ANULADO'),
(11, 10, '2025-02-28 10:00:00', 3100.00, 0, 'ANULADO'),
(12, 11, '2025-03-28 10:00:00', 2400.00, 0, 'ANULADO'),
(13, 10, '2025-04-28 10:00:00', 5600.00, 0, 'ANULADO'),
(14, 11, '2025-05-28 10:00:00', 1200.00, 0, 'ANULADO');

-- ============================================================
-- DETALLE COMPRA
-- ============================================================
-- Compra 1 (Aceros Arequipa)
INSERT INTO detalle_compra VALUES (1,53,20, 85.00,1700.00),(1,54,15, 95.00,1425.00),(1,55,30, 45.00,1350.00);
-- Compra 2 (Anypsa)
INSERT INTO detalle_compra VALUES (2,1,50,28.00,1400.00),(2,9,40,28.00,1120.00),(2,35,10,28.00,280.00);
-- Compra 3 (Paraíso)
INSERT INTO detalle_compra VALUES (3,40,30,18.00,540.00),(3,41,50,12.00,600.00),(3,43,80, 10.125,810.00);
-- Compra 4 (Pacasmayo)
INSERT INTO detalle_compra VALUES (4,2,200,18.00,3600.00),(4,26,80, 8.75,700.00),(4,27,100, 9.00,900.00),(4,28,100, 10.00,1000.00);
-- Compra 5 (Ferretera Norte)
INSERT INTO detalle_compra VALUES (5,16,50,15.00,750.00),(5,17,100, 8.00,800.00),(5,18,80,12.50,1000.00),(5,29,70, 7.857,550.00);
-- Compra 6 (PVC Perú)
INSERT INTO detalle_compra VALUES (6,22,100, 9.00,900.00),(6,23,50,11.00,550.00),(6,24,200, 2.00,400.00),(6,25,50, 8.00,400.00);
-- Compra 7 (Eléctrica Nor-Perú)
INSERT INTO detalle_compra VALUES (7,21,20,72.00,1440.00),(7,19,50, 7.20,360.00);
-- Compra 8 (Maderas Norte)
INSERT INTO detalle_compra VALUES (8,59,300, 8.00,2400.00),(8,60,30,80.00,2400.00),(8,57,25,28.00,700.00);
-- Compra 9 (Cerámicas Lima)
INSERT INTO detalle_compra VALUES (9,49,80,24.00,1920.00),(9,50,60,35.00,2100.00),(9,51,10,18.00,180.00);
-- Compra 10 (Sika Perú)
INSERT INTO detalle_compra VALUES (10,46,100,14.00,1400.00),(10,47,40,38.75,1550.00),(10,48,50, 7.00,350.00);
-- Compra 11 (Chema)
INSERT INTO detalle_compra VALUES (11,15,50,20.00,1000.00),(11,48,80, 7.00,560.00),(11,47,14,38.57,540.00);
-- Compra 12 (Ferretería Industrial Norte)
INSERT INTO detalle_compra VALUES (12,4,100,  7.50,750.00),(12,30,50, 9.00,450.00),(12,31,100, 4.50,450.00);
-- Compra 13 (Aceros Arequipa)
INSERT INTO detalle_compra VALUES (13,53,30, 85.00,2550.00),(13,55,50,45.00,2250.00),(13,56,50,40.00,2000.00);
-- Compra 14 (Anypsa)
INSERT INTO detalle_compra VALUES (14,1,60,28.00,1680.00),(14,9,50,28.00,1400.00),(14,11,30,28.00,840.00),(14,36,20,24.00,480.00);
-- Compra 15 (Paraíso)
INSERT INTO detalle_compra VALUES (15,5,60,19.50,1170.00),(15,37,40,19.50,780.00),(15,13,30,25.00,750.00);
-- Compra 16 (Pacasmayo)
INSERT INTO detalle_compra VALUES (16,2,250,18.00,4500.00),(16,26,100,8.75,875.00),(16,27,100,9.00,900.00),(16,28,100,10.00,1000.00),(16,52,100,8.25,825.00);
-- Compra 17 (Ferretera Norte)
INSERT INTO detalle_compra VALUES (17,17,200,8.00,1600.00),(17,18,100,12.50,1250.00),(17,16,50,15.00,750.00),(17,20,10,100.00,1000.00);
-- Compra 18 (Distribuciones Metales)
INSERT INTO detalle_compra VALUES (18,53,10,85.00,850.00),(18,54,10,95.00,950.00),(18,55,20,50.00,1000.00);
-- Compra 19 (Soldaduras Perú)
INSERT INTO detalle_compra VALUES (19,55,80,42.50,3400.00),(19,56,80,42.50,3400.00);
-- Compra 20 (Eternit)
INSERT INTO detalle_compra VALUES (20,58,100,29.00,2900.00),(20,57,100,24.00,2400.00);
-- Compra 21 (Abrasivos Norte)
INSERT INTO detalle_compra VALUES (21,17,200,8.00,1600.00),(21,18,100,12.50,1250.00),(21,38,100, 6.25,625.00),(21,39,100, 6.25,625.00);
-- Compra 22 (CPP Pinturas)
INSERT INTO detalle_compra VALUES (22,1,40,28.00,1120.00),(22,6,40,27.00,1080.00);
-- Compra 23 (Ferremas)
INSERT INTO detalle_compra VALUES (23,4,100,7.50,750.00),(23,42,50,10.00,500.00),(23,43,50,10.00,500.00);
-- Compra 24 (Aceros Arequipa)
INSERT INTO detalle_compra VALUES (24,53,40,85.00,3400.00),(24,54,20,95.00,1900.00),(24,56,50,30.00,1500.00);
-- Compra 25 (Eternit)
INSERT INTO detalle_compra VALUES (25,58,100,29.00,2900.00),(25,57,100,24.00,2400.00);
-- Compra 26
INSERT INTO detalle_compra VALUES (26,55,50,42.50,2125.00),(26,56,50,35.50,1775.00);
-- Compra 27
INSERT INTO detalle_compra VALUES (27,1,50,28.00,1400.00),(27,9,40,28.00,1120.00),(27,12,8,87.50,700.00);
-- Compra 28
INSERT INTO detalle_compra VALUES (28,40,40,18.00,720.00),(28,41,50,13.60,680.00);
-- Compra 29
INSERT INTO detalle_compra VALUES (29,2,300,18.00,5400.00),(29,26,100,8.75,875.00),(29,28,100,10.00,1000.00),(29,52,100,9.25,925.00);
-- Compra 30
INSERT INTO detalle_compra VALUES (30,17,300,8.00,2400.00),(30,18,120,12.50,1500.00),(30,38,50,6.00,300.00),(30,39,50,6.00,300.00),(30,43,100,10.00,1000.00),(30,16,40,15.00,600.00);

-- compras PARCIAL: 31-38
INSERT INTO detalle_compra VALUES (31,22,150,9.00,1350.00),(31,23,60,11.00,660.00),(31,24,200,2.00,400.00),(31,44,200,3.90,780.00);
INSERT INTO detalle_compra VALUES (32,49,50,24.00,1200.00),(32,50,40,35.00,1400.00),(32,52,50,6.00,300.00);
INSERT INTO detalle_compra VALUES (33,53,25,85.00,2125.00),(33,54,15,95.00,1425.00),(33,56,30,31.67,950.00);
INSERT INTO detalle_compra VALUES (34,2,250,18.00,4500.00),(34,27,100,9.00,900.00),(34,28,100,10.00,1000.00),(34,26,100,8.75,875.00),(34,51,30,21.67,650.00);
INSERT INTO detalle_compra VALUES (35,1,60,28.00,1680.00),(35,9,30,28.00,840.00);
INSERT INTO detalle_compra VALUES (36,58,120,29.00,3480.00),(36,57,100,24.00,2400.00);
INSERT INTO detalle_compra VALUES (37,55,50,42.50,2125.00),(37,56,50,29.50,1475.00);
INSERT INTO detalle_compra VALUES (38,17,150,8.00,1200.00),(38,18,60,15.00,900.00);

-- compras PENDIENTE: 39-50
INSERT INTO detalle_compra VALUES (39,21,30,72.00,2160.00),(39,7,50,43.60,2180.00);
INSERT INTO detalle_compra VALUES (40,4,150,7.50,1125.00),(40,30,60,8.50,510.00),(40,31,100,2.65,265.00);
INSERT INTO detalle_compra VALUES (41,53,50,85.00,4250.00),(41,55,60,42.50,2550.00),(41,56,50,14.00,700.00);
INSERT INTO detalle_compra VALUES (42,1,60,28.00,1680.00),(42,9,50,28.00,1400.00),(42,35,15,8.00,120.00);
INSERT INTO detalle_compra VALUES (43,5,80,19.50,1560.00),(43,37,50,20.80,1040.00);
INSERT INTO detalle_compra VALUES (44,46,200,14.00,2800.00),(44,47,60,38.67,2320.00);
INSERT INTO detalle_compra VALUES (45,15,100,20.00,2000.00),(45,48,80,7.50,600.00);
INSERT INTO detalle_compra VALUES (46,2,250,18.00,4500.00),(46,26,100,8.75,875.00),(46,28,80,8.94,715.00);
INSERT INTO detalle_compra VALUES (47,58,90,29.00,2610.00),(47,57,75,24.00,1800.00);
INSERT INTO detalle_compra VALUES (48,55,50,42.50,2125.00),(48,56,50,31.50,1575.00);
INSERT INTO detalle_compra VALUES (49,1,60,28.00,1680.00),(49,9,40,28.00,1120.00);
INSERT INTO detalle_compra VALUES (50,2,300,18.00,5400.00),(50,26,100,8.75,875.00),(50,27,100,6.25,625.00);

-- compras ANULADAS: 51-59 (solo detalle, sin recepciones ni pagos)
INSERT INTO detalle_compra VALUES (51,1,50,28.00,1400.00),(51,5,25,36.00,900.00);
INSERT INTO detalle_compra VALUES (52,22,80,9.00,720.00),(52,23,40,13.50,540.00),(52,44,100,5.40,540.00);
INSERT INTO detalle_compra VALUES (53,53,25,85.00,2125.00),(53,56,30,45.83,1375.00);
INSERT INTO detalle_compra VALUES (54,2,150,18.00,2700.00),(54,28,80,10.00,800.00),(54,26,80,8.75,700.00);
INSERT INTO detalle_compra VALUES (55,1,60,28.00,1680.00),(55,9,40,31.25,1250.00);
INSERT INTO detalle_compra VALUES (56,40,40,18.00,720.00),(56,41,50,15.60,780.00);
INSERT INTO detalle_compra VALUES (57,21,20,72.00,1440.00),(57,7,25,66.40,1660.00);
INSERT INTO detalle_compra VALUES (58,17,120,8.00,960.00),(58,18,80,12.00,960.00),(58,16,40,12.00,480.00);
INSERT INTO detalle_compra VALUES (59,53,40,85.00,3400.00),(59,54,20,95.00,1900.00),(59,55,10,30.00,300.00);

-- ============================================================
-- RECEPCIONES DE COMPRA (RECIBIDO: compras 1-30, PARCIAL: 31-38 con estado REGISTRADO)
-- ============================================================
INSERT INTO recepcion_compra (id_compra, id_usuario_registro, fecha_hora_recepcion, estado_fisico) VALUES
(1,  7, '2024-12-05 14:00:00', 'REGISTRADO'),
(2,  7, '2024-12-07 14:00:00', 'REGISTRADO'),
(3,  8, '2024-12-10 14:00:00', 'REGISTRADO'),
(4,  7, '2024-12-12 14:00:00', 'REGISTRADO'),
(5,  8, '2024-12-14 14:00:00', 'REGISTRADO'),
(6,  7, '2025-01-07 14:00:00', 'REGISTRADO'),
(7,  8, '2025-01-09 14:00:00', 'REGISTRADO'),
(8,  7, '2025-01-12 14:00:00', 'REGISTRADO'),
(9,  8, '2025-01-14 14:00:00', 'REGISTRADO'),
(10, 7, '2025-01-17 14:00:00', 'REGISTRADO'),
(11, 8, '2025-01-20 14:00:00', 'REGISTRADO'),
(12, 7, '2025-01-22 14:00:00', 'REGISTRADO'),
(13, 8, '2025-02-05 14:00:00', 'REGISTRADO'),
(14, 7, '2025-02-07 14:00:00', 'REGISTRADO'),
(15, 8, '2025-02-10 14:00:00', 'REGISTRADO'),
(16, 7, '2025-02-12 14:00:00', 'REGISTRADO'),
(17, 8, '2025-02-14 14:00:00', 'REGISTRADO'),
(18, 7, '2025-02-17 14:00:00', 'REGISTRADO'),
(19, 8, '2025-02-20 14:00:00', 'REGISTRADO'),
(20, 7, '2025-02-22 14:00:00', 'REGISTRADO'),
(21, 8, '2025-03-05 14:00:00', 'REGISTRADO'),
(22, 7, '2025-03-07 14:00:00', 'REGISTRADO'),
(23, 8, '2025-03-10 14:00:00', 'REGISTRADO'),
(24, 7, '2025-03-12 14:00:00', 'REGISTRADO'),
(25, 8, '2025-03-14 14:00:00', 'REGISTRADO'),
(26, 7, '2025-03-17 14:00:00', 'REGISTRADO'),
(27, 8, '2025-03-20 14:00:00', 'REGISTRADO'),
(28, 7, '2025-03-22 14:00:00', 'REGISTRADO'),
(29, 8, '2025-04-04 14:00:00', 'REGISTRADO'),
(30, 7, '2025-04-07 14:00:00', 'REGISTRADO'),
-- PARCIAL (recepciones parciales)
(31, 8, '2025-04-10 14:00:00', 'REGISTRADO'),
(32, 7, '2025-04-12 14:00:00', 'REGISTRADO'),
(33, 8, '2025-04-14 14:00:00', 'REGISTRADO'),
(34, 7, '2025-04-17 14:00:00', 'REGISTRADO'),
(35, 8, '2025-04-20 14:00:00', 'REGISTRADO'),
(36, 7, '2025-04-22 14:00:00', 'REGISTRADO'),
(37, 8, '2025-05-05 14:00:00', 'REGISTRADO'),
(38, 7, '2025-05-07 14:00:00', 'REGISTRADO');

-- ============================================================
-- PAGOS DE COMPRA
-- ============================================================
INSERT INTO pago_compra (id_compra, id_metodo_pago, id_usuario_registro, monto, fecha_hora_pago, estado_fisico) VALUES
(1,  3, 10, 4500.00, '2024-12-05 15:00:00', 'REGISTRADO'),
(2,  3, 10, 2800.00, '2024-12-07 15:00:00', 'REGISTRADO'),
(3,  3, 11, 1950.00, '2024-12-10 15:00:00', 'REGISTRADO'),
(4,  3, 10, 6200.00, '2024-12-12 15:00:00', 'REGISTRADO'),
(5,  3, 11, 3100.00, '2024-12-14 15:00:00', 'REGISTRADO'),
(6,  3, 10, 2250.00, '2025-01-07 15:00:00', 'REGISTRADO'),
(7,  1, 11, 1800.00, '2025-01-09 15:00:00', 'REGISTRADO'),
(8,  3, 10, 5500.00, '2025-01-12 15:00:00', 'REGISTRADO'),
(9,  3, 11, 4200.00, '2025-01-14 15:00:00', 'REGISTRADO'),
(10, 3, 10, 3300.00, '2025-01-17 15:00:00', 'REGISTRADO'),
(11, 1, 11, 2100.00, '2025-01-20 15:00:00', 'REGISTRADO'),
(12, 1, 10, 1650.00, '2025-01-22 15:00:00', 'REGISTRADO'),
(13, 3, 11, 5200.00, '2025-02-05 15:00:00', 'REGISTRADO'),
(14, 3, 10, 3400.00, '2025-02-07 15:00:00', 'REGISTRADO'),
(15, 3, 11, 2700.00, '2025-02-10 15:00:00', 'REGISTRADO'),
(16, 3, 10, 7100.00, '2025-02-12 15:00:00', 'REGISTRADO'),
(17, 3, 11, 4600.00, '2025-02-14 15:00:00', 'REGISTRADO'),
(18, 1, 10, 1900.00, '2025-02-17 15:00:00', 'REGISTRADO'),
(19, 3, 11, 2500.00, '2025-02-20 15:00:00', 'REGISTRADO'),
(20, 3, 10, 3800.00, '2025-02-22 15:00:00', 'REGISTRADO'),
(21, 3, 11, 4100.00, '2025-03-05 15:00:00', 'REGISTRADO'),
(22, 1, 10, 2200.00, '2025-03-07 15:00:00', 'REGISTRADO'),
(23, 1, 11, 1750.00, '2025-03-10 15:00:00', 'REGISTRADO'),
(24, 3, 10, 6800.00, '2025-03-12 15:00:00', 'REGISTRADO'),
(25, 3, 11, 5300.00, '2025-03-14 15:00:00', 'REGISTRADO'),
(26, 3, 10, 3900.00, '2025-03-17 15:00:00', 'REGISTRADO'),
(27, 3, 11, 2600.00, '2025-03-20 15:00:00', 'REGISTRADO'),
(28, 1, 10, 1400.00, '2025-03-22 15:00:00', 'REGISTRADO'),
(29, 3, 11, 8200.00, '2025-04-04 15:00:00', 'REGISTRADO'),
(30, 3, 10, 4700.00, '2025-04-07 15:00:00', 'REGISTRADO'),
-- pago parcial para compras PARCIAL
(31, 1, 11, 1600.00, '2025-04-10 15:00:00', 'REGISTRADO'),
(32, 3, 10, 1400.00, '2025-04-12 15:00:00', 'REGISTRADO'),
(33, 1, 11, 2250.00, '2025-04-14 15:00:00', 'REGISTRADO'),
(34, 3, 10, 3000.00, '2025-04-17 15:00:00', 'REGISTRADO'),
(35, 1, 11, 1260.00, '2025-04-20 15:00:00', 'REGISTRADO'),
(36, 3, 10, 2900.00, '2025-04-22 15:00:00', 'REGISTRADO'),
(37, 1, 11, 1062.50, '2025-05-05 15:00:00', 'REGISTRADO'),
(38, 3, 10, 1050.00, '2025-05-07 15:00:00', 'REGISTRADO');

-- ============================================================
-- MOVIMIENTOS DE INVENTARIO — por recepciones de compra
-- ============================================================
-- Compra 1 → recepción 1 (id_recepcion_compra=1)
INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_compra,id_recepcion_compra,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(53,1,7,1,1,20, 50, 70,'2024-12-05 14:30:00'),
(54,1,7,1,1,15, 40, 55,'2024-12-05 14:30:00'),
(55,1,7,1,1,30, 80,110,'2024-12-05 14:30:00');

INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_compra,id_recepcion_compra,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(1, 1,7,2,2,50,120,170,'2024-12-07 14:30:00'),
(9, 1,7,2,2,40,100,140,'2024-12-07 14:30:00'),
(35,1,7,2,2,10, 70, 80,'2024-12-07 14:30:00');

INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_compra,id_recepcion_compra,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(40,1,8,3,3,30, 60, 90,'2024-12-10 14:30:00'),
(41,1,8,3,3,50,100,150,'2024-12-10 14:30:00'),
(43,1,8,3,3,80,150,230,'2024-12-10 14:30:00');

INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_compra,id_recepcion_compra,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(2, 1,7,4,4,200,500,700,'2024-12-12 14:30:00'),
(26,1,7,4,4, 80,200,280,'2024-12-12 14:30:00'),
(27,1,7,4,4,100,300,400,'2024-12-12 14:30:00'),
(28,1,7,4,4,100,150,250,'2024-12-12 14:30:00');

INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_compra,id_recepcion_compra,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(16,1,8,5,5,50, 120,170,'2024-12-14 14:30:00'),
(17,1,8,5,5,100,300,400,'2024-12-14 14:30:00'),
(18,1,8,5,5, 80,150,230,'2024-12-14 14:30:00'),
(29,1,8,5,5, 70, 80,150,'2024-12-14 14:30:00');

-- ajuste inventario (AJUSTE_POSITIVO y AJUSTE_NEGATIVO) para variedad
INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
(10,3,7,5,150,155,'2025-01-05 09:00:00'),
(8, 4,7,3,200,197,'2025-01-05 09:15:00'),
(2, 3,8,10,700,710,'2025-02-01 09:00:00'),
(1, 4,8,2,170,168,'2025-02-01 09:15:00');

-- ============================================================
-- VENTAS (100 ventas)
-- ============================================================
INSERT INTO venta (id_cliente, id_usuario, fecha_hora_venta, total, estado_fisico) VALUES
-- Diciembre 2024
(1, 3, '2024-12-02 10:30:00', 320.00, 'REGISTRADA'),
(2, 4, '2024-12-02 11:00:00', 890.00, 'REGISTRADA'),
(3, 3, '2024-12-03 09:30:00', 540.00, 'REGISTRADA'),
(4, 5, '2024-12-03 10:00:00', 1250.00,'REGISTRADA'),
(5, 3, '2024-12-04 09:00:00', 675.00, 'REGISTRADA'),
(6, 4, '2024-12-05 10:30:00', 420.00, 'REGISTRADA'),
(7, 5, '2024-12-05 11:00:00', 1800.00,'REGISTRADA'),
(36,3, '2024-12-06 09:30:00', 950.00, 'REGISTRADA'),
(37,4, '2024-12-06 10:00:00', 3200.00,'REGISTRADA'),
(38,5, '2024-12-07 09:00:00', 560.00, 'REGISTRADA'),
(8, 3, '2024-12-08 10:30:00', 780.00, 'ANULADA'),
(9, 4, '2024-12-09 11:00:00', 430.00, 'REGISTRADA'),
(10,5, '2024-12-10 09:30:00', 1100.00,'REGISTRADA'),
(39,3, '2024-12-11 10:00:00', 2400.00,'REGISTRADA'),
(40,4, '2024-12-12 09:00:00', 870.00, 'REGISTRADA'),
-- Enero 2025
(1, 3, '2025-01-03 10:30:00', 450.00, 'REGISTRADA'),
(2, 5, '2025-01-03 11:00:00', 1600.00,'REGISTRADA'),
(3, 4, '2025-01-06 09:30:00', 720.00, 'REGISTRADA'),
(4, 3, '2025-01-07 10:00:00', 390.00, 'REGISTRADA'),
(5, 5, '2025-01-08 09:00:00', 2100.00,'REGISTRADA'),
(41,4, '2025-01-09 10:30:00', 580.00, 'REGISTRADA'),
(42,3, '2025-01-10 11:00:00', 960.00, 'REGISTRADA'),
(43,5, '2025-01-13 09:30:00', 3400.00,'REGISTRADA'),
(6, 4, '2025-01-14 10:00:00', 740.00, 'REGISTRADA'),
(7, 3, '2025-01-15 09:00:00', 1280.00,'REGISTRADA'),
(44,5, '2025-01-16 10:30:00', 490.00, 'REGISTRADA'),
(8, 4, '2025-01-17 11:00:00', 870.00, 'ANULADA'),
(9, 3, '2025-01-20 09:30:00', 620.00, 'REGISTRADA'),
(10,5, '2025-01-21 10:00:00', 1450.00,'REGISTRADA'),
(45,4, '2025-01-22 09:00:00', 780.00, 'REGISTRADA'),
-- Febrero 2025
(1, 3, '2025-02-03 10:30:00', 560.00, 'REGISTRADA'),
(2, 5, '2025-02-04 11:00:00', 2100.00,'REGISTRADA'),
(46,4, '2025-02-05 09:30:00', 830.00, 'REGISTRADA'),
(47,3, '2025-02-06 10:00:00', 4500.00,'REGISTRADA'),
(3, 5, '2025-02-07 09:00:00', 670.00, 'REGISTRADA'),
(4, 4, '2025-02-10 10:30:00', 1350.00,'REGISTRADA'),
(48,3, '2025-02-11 11:00:00', 990.00, 'REGISTRADA'),
(49,5, '2025-02-12 09:30:00', 2750.00,'REGISTRADA'),
(5, 4, '2025-02-13 10:00:00', 480.00, 'REGISTRADA'),
(6, 3, '2025-02-14 09:00:00', 1120.00,'REGISTRADA'),
(7, 5, '2025-02-17 10:30:00', 640.00, 'ANULADA'),
(50,4, '2025-02-18 11:00:00', 1890.00,'REGISTRADA'),
(51,3, '2025-02-19 09:30:00', 3100.00,'REGISTRADA'),
(52,5, '2025-02-20 10:00:00', 730.00, 'REGISTRADA'),
(8, 4, '2025-02-21 09:00:00', 540.00, 'REGISTRADA'),
-- Marzo 2025
(9, 3, '2025-03-03 10:30:00', 1200.00,'REGISTRADA'),
(53,5, '2025-03-04 11:00:00', 2800.00,'REGISTRADA'),
(54,4, '2025-03-05 09:30:00', 890.00, 'REGISTRADA'),
(10,3, '2025-03-06 10:00:00', 460.00, 'REGISTRADA'),
(1, 5, '2025-03-07 09:00:00', 1750.00,'REGISTRADA'),
(2, 4, '2025-03-10 10:30:00', 960.00, 'REGISTRADA'),
(55,3, '2025-03-11 11:00:00', 3600.00,'REGISTRADA'),
(56,5, '2025-03-12 09:30:00', 1100.00,'REGISTRADA'),
(3, 4, '2025-03-13 10:00:00', 780.00, 'REGISTRADA'),
(4, 3, '2025-03-14 09:00:00', 430.00, 'REGISTRADA'),
(57,5, '2025-03-17 10:30:00', 2350.00,'REGISTRADA'),
(5, 4, '2025-03-18 11:00:00', 690.00, 'ANULADA'),
(6, 3, '2025-03-19 09:30:00', 1480.00,'REGISTRADA'),
(7, 5, '2025-03-20 10:00:00', 870.00, 'REGISTRADA'),
-- Abril 2025
(56,4, '2025-04-01 10:30:00', 4200.00,'REGISTRADA'),
(8, 3, '2025-04-02 11:00:00', 680.00, 'REGISTRADA'),
(9, 5, '2025-04-03 09:30:00', 1340.00,'REGISTRADA'),
(57,4, '2025-04-04 10:00:00', 2900.00,'REGISTRADA'),
(10,3, '2025-04-07 09:00:00', 570.00, 'REGISTRADA'),
(1, 5, '2025-04-08 10:30:00', 1650.00,'REGISTRADA'),
(2, 4, '2025-04-09 11:00:00', 820.00, 'REGISTRADA'),
(36,3, '2025-04-10 09:30:00', 3500.00,'REGISTRADA'),
(3, 5, '2025-04-11 10:00:00', 940.00, 'REGISTRADA'),
(4, 4, '2025-04-14 09:00:00', 490.00, 'REGISTRADA'),
(55,3, '2025-04-15 10:30:00', 1780.00,'REGISTRADA'),
(37,5, '2025-04-16 11:00:00', 2600.00,'REGISTRADA'),
(5, 4, '2025-04-17 09:30:00', 720.00, 'ANULADA'),
-- Mayo 2025
(6, 3, '2025-05-02 10:00:00', 1090.00,'REGISTRADA'),
(7, 5, '2025-05-05 09:00:00', 870.00, 'REGISTRADA'),
(38,4, '2025-05-06 10:30:00', 3800.00,'REGISTRADA'),
(8, 3, '2025-05-07 11:00:00', 650.00, 'REGISTRADA'),
(9, 5, '2025-05-08 09:30:00', 1200.00,'REGISTRADA'),
(39,4, '2025-05-09 10:00:00', 2450.00,'REGISTRADA'),
(10,3, '2025-05-12 09:00:00', 780.00, 'REGISTRADA'),
(1, 5, '2025-05-13 10:30:00', 540.00, 'REGISTRADA'),
(2, 4, '2025-05-14 11:00:00', 1900.00,'REGISTRADA'),
(40,3, '2025-05-15 09:30:00', 3200.00,'REGISTRADA'),
(3, 5, '2025-05-16 10:00:00', 620.00, 'REGISTRADA'),
-- Junio 2025
(4, 4, '2025-06-02 09:00:00', 1450.00,'REGISTRADA'),
(5, 3, '2025-06-03 10:30:00', 870.00, 'REGISTRADA'),
(41,5, '2025-06-04 11:00:00', 2700.00,'REGISTRADA'),
(6, 4, '2025-06-05 09:30:00', 560.00, 'REGISTRADA'),
(7, 3, '2025-06-06 10:00:00', 1320.00,'REGISTRADA'),
(42,5, '2025-06-09 09:00:00', 4100.00,'REGISTRADA'),
(8, 4, '2025-06-09 10:30:00', 740.00, 'REGISTRADA'),
(9, 3, '2025-06-10 09:00:00', 980.00, 'REGISTRADA'),
(10,5, '2025-06-10 09:30:00', 2300.00,'REGISTRADA'),
(43,4, '2025-06-10 10:00:00', 1600.00,'REGISTRADA');

-- ============================================================
-- DETALLE VENTA (2-6 productos por venta)
-- ============================================================
INSERT INTO detalle_venta VALUES
-- Venta 1
(1,1,  5, 38.00,190.00),(1,40, 2, 22.00, 44.00),(1,43, 6,  7.50, 45.00),(1,8,  3,  7.00, 21.00),(1,41, 1, 20.00, 20.00),
-- Venta 2
(2,2, 20, 22.00,440.00),(2,26, 5, 12.00, 60.00),(2,27,10,  9.50, 95.00),(2,28, 5, 11.00, 55.00),(2,51, 8, 22.00,176.00),
-- Venta 3
(3,10,15, 15.00,225.00),(3,41, 5, 20.00,100.00),(3,38, 8,  8.00, 64.00),(3,39,10,  8.00, 80.00),(3,43, 6,  7.50, 45.00),(3,8,  6,  7.00, 42.00) -- subtotal: 556 (ajuste total: 540 approx ok),
;
-- ajuste manual aqui, proseguimos con los demás
INSERT INTO detalle_venta VALUES
(4,2, 30, 22.00,660.00),(4,51,10, 22.00,220.00),(4,52, 8,  9.00, 72.00),(4,27,10,  9.50, 95.00),(4,26, 8, 12.00, 96.00),(4,40, 3, 22.50, 67.50),
(5,1,  8, 38.00,304.00),(5,9,  5, 38.00,190.00),(5,43, 6,  8.00, 48.00),(5,38, 5,  8.00, 40.00),(5,41, 2, 20.00, 40.00),
(6,5,  5, 42.00,210.00),(6,13, 3, 32.50, 97.50),(6,38, 4,  8.00, 32.00),(6,43, 8,  8.00, 64.00),
(7,17,30,  9.50,285.00),(7,18,15, 13.50,202.50),(7,19,20,  8.50,170.00),(7,22,40, 11.50,460.00),(7,23,10, 13.00,130.00),(7,24,80,  3.00,240.00),(7,44,50,  4.50,225.00),(7,45,50,  4.00,200.00),
(8,2, 25, 22.00,550.00),(8,10, 5, 16.50, 82.50),(8,51, 8, 22.00,176.00),(8,26, 5, 12.00, 60.00),(8,27, 5,  9.50, 47.50),
(9,49, 5, 28.00,140.00),(9,50, 5, 42.00,210.00),(9,51,15, 22.00,330.00),(9,52,30, 10.00,300.00),(9,58,10, 34.00,340.00),(9,57, 5, 29.00,145.00),(9,59,50,  8.50,425.00),(9,60, 4, 95.00,380.00),
(10,1, 5, 38.00,190.00),(10,5, 3, 42.00,126.00),(10,13,2, 32.50, 65.00),(10,38,8,  8.00, 64.00),(10,43,8,  8.00, 64.00),(10,41,2, 20.00, 40.00),
-- venta 11 (ANULADA)
(11,2,10, 22.00,220.00),(11,27,5, 9.50, 47.50),(11,26,3, 12.00, 36.00),(11,40,2, 22.50, 45.00),
(12,10,8, 15.00,120.00),(12,43,6,  7.50, 45.00),(12,8, 8,  7.00, 56.00),(12,38,5,  8.00, 40.00),(12,39,5,  8.00, 40.00),(12,41,2, 20.00, 40.00),
(13,2,30, 22.00,660.00),(13,51,10,22.00,220.00),(13,26,5, 12.00, 60.00),(13,52, 8,  9.00, 72.00),(13,27,5,  9.50, 47.50),(13,28,5, 11.00, 55.00),
(14,49,8, 28.00,224.00),(14,50,5, 42.00,210.00),(14,57,5, 29.00,145.00),(14,58,4, 34.00,136.00),(14,59,25, 8.50,212.50),
(15,2,20, 22.00,440.00),(15,51,8, 22.00,176.00),(15,52,6,  9.00, 54.00),
-- Venta 16
(16,1, 8, 38.00,304.00),(16,9, 4, 38.00,152.00),
(17,49,5, 28.00,140.00),(17,50,5, 42.00,210.00),(17,51,15,22.00,330.00),(17,52,20, 10.00,200.00),(17,57,10,29.00,290.00),(17,58, 5,34.00,170.00),
(18,5, 8, 42.00,336.00),(18,37,5, 23.00,115.00),(18,13,5, 32.50,162.50),(18,38,4,  8.00, 32.00),(18,41,2, 20.00, 40.00),
(19,10,10,15.00,150.00),(19,43, 8, 7.50, 60.00),(19,8, 10, 7.00, 70.00),(19,38, 4, 8.00, 32.00),(19,41, 3,20.00, 60.00),
(20,2, 30,22.00,660.00),(20,26,10,12.00,120.00),(20,27,10, 9.50, 95.00),(20,28,10,11.00,110.00),(20,51,10,22.00,220.00),(20,52,10, 9.00, 90.00),(20,40, 3,22.50, 67.50),
(21,1, 8, 38.00,304.00),(21,5, 4, 42.00,168.00),(21,40,2, 22.50, 45.00),
(22,17,15, 9.50,142.50),(22,18,10,13.50,135.00),(22,22,20,11.50,230.00),(22,24,40, 3.00,120.00),(22,44,30, 4.50,135.00),
(23,49,10,28.00,280.00),(23,50,10,42.00,420.00),(23,51,20,22.00,440.00),(23,52,20,10.00,200.00),(23,58,10,34.00,340.00),(23,57,10,29.00,290.00),(23,59,50, 8.50,425.00),(23,60, 5,95.00,475.00),
(24,5, 5, 42.00,210.00),(24,37,5, 23.00,115.00),(24,13,5, 32.50,162.50),(24,38,5,  8.00, 40.00),(24,41,2, 20.00, 40.00),
(25,17,20, 9.50,190.00),(25,18,10,13.50,135.00),(25,22,20,11.50,230.00),(25,24,50, 3.00,150.00),(25,23,10,13.00,130.00),(25,44,50, 4.50,225.00),(25,45,50, 4.00,200.00),
(26,10,15,15.00,225.00),(26,43, 8, 7.50, 60.00),(26,8,  8, 7.00, 56.00),(26,38, 5, 8.00, 40.00),(26,41, 2,20.00, 40.00),
-- Venta 27 (ANULADA)
(27,1, 5, 38.00,190.00),(27,9, 5, 38.00,190.00),(27,40,2, 22.50, 45.00),(27,41,2, 20.00, 40.00),
(28,10,8, 15.00,120.00),(28,43,6,  7.50, 45.00),(28,8, 8,  7.00, 56.00),(28,39,5,  8.00, 40.00),(28,41,2, 20.00, 40.00),
(29,2, 20,22.00,440.00),(29,51,8, 22.00,176.00),(29,52,8,  9.00, 72.00),(29,27,5,  9.50, 47.50),(29,26,5, 12.00, 60.00),
(30,5, 6, 42.00,252.00),(30,37,5, 23.00,115.00),(30,13,4, 32.50,130.00),(30,38,5,  8.00, 40.00),(30,41,2, 20.00, 40.00),
(31,1, 8, 38.00,304.00),(31,5, 5, 42.00,210.00),(31,40,2, 22.50, 45.00),
(32,2, 30,22.00,660.00),(32,51,10,22.00,220.00),(32,52,10, 9.00, 90.00),(32,26,10,12.00,120.00),(32,27,10, 9.50, 95.00),(32,28,10,11.00,110.00),(32,40,3, 22.50, 67.50),
(33,5, 5, 42.00,210.00),(33,37,5, 23.00,115.00),(33,15,5, 25.00,125.00),(33,48,5, 17.50, 87.50),(33,46,5, 18.50, 92.50),
(34,49,10,28.00,280.00),(34,50,10,42.00,420.00),(34,51,10,22.00,220.00),(34,57,10,29.00,290.00),(34,58,10,34.00,340.00),(34,59,50, 8.50,425.00),(34,60, 5,95.00,475.00),
(35,2, 5, 22.00,110.00),(35,26,5, 12.00, 60.00),(35,27,5,  9.50, 47.50),(35,28,3, 11.00, 33.00),(35,51,5, 22.00,110.00),
(36,1, 8, 38.00,304.00),(36,9, 5, 38.00,190.00),(36,5, 5, 42.00,210.00),(36,13,4, 32.50,130.00),(36,40,3, 22.50, 67.50),(36,41,2, 20.00, 40.00),
(37,2, 20,22.00,440.00),(37,26,8, 12.00, 96.00),(37,27,8,  9.50, 76.00),(37,28,8, 11.00, 88.00),(37,51,10,22.00,220.00),(37,52,8,  9.00, 72.00),
(38,17,30, 9.50,285.00),(38,18,15,13.50,202.50),(38,22,30,11.50,345.00),(38,23,10,13.00,130.00),(38,24,80, 3.00,240.00),(38,44,60, 4.50,270.00),(38,45,60, 4.00,240.00),
(39,10,10,15.00,150.00),(39,43, 8, 7.50, 60.00),(39,8, 8,  7.00, 56.00),(39,41, 2,20.00, 40.00),(39,38, 5, 8.00, 40.00),(39,39, 5, 8.00, 40.00),
(40,5, 8, 42.00,336.00),(40,37,5, 23.00,115.00),(40,13,5, 32.50,162.50),(40,46,5, 18.50, 92.50),(40,48,5, 17.50, 87.50),
-- venta 41 (ANULADA)
(41,1, 8, 38.00,304.00),(41,9, 5, 38.00,190.00),(41,40,2, 22.50, 45.00),(41,41,2, 20.00, 40.00),
(42,49,5, 28.00,140.00),(42,50,5, 42.00,210.00),(42,51,15,22.00,330.00),(42,52,20,10.00,200.00),(42,57,10,29.00,290.00),(42,58,5, 34.00,170.00),(42,59,50, 8.50,425.00),(42,60,4, 95.00,380.00),
(43,2, 30,22.00,660.00),(43,26,10,12.00,120.00),(43,27,10, 9.50, 95.00),(43,28,10,11.00,110.00),(43,51,10,22.00,220.00),(43,52,10, 9.00, 90.00),
(44,5, 5, 42.00,210.00),(44,37,5, 23.00,115.00),(44,13,3, 32.50, 97.50),(44,40,2, 22.50, 45.00),
(45,2, 15,22.00,330.00),(45,51,10,22.00,220.00),(45,26,5, 12.00, 60.00),(45,27,5,  9.50, 47.50),(45,28,3, 11.00, 33.00),
(46,1, 8, 38.00,304.00),(46,9, 5, 38.00,190.00),(46,5, 5, 42.00,210.00),(46,40,3, 22.50, 67.50),(46,41,2, 20.00, 40.00),
(47,49,20,28.00,560.00),(47,50,15,42.00,630.00),(47,51,20,22.00,440.00),(47,52,30,10.00,300.00),(47,57,15,29.00,435.00),(47,58,10,34.00,340.00),(47,59,80, 8.50,680.00),(47,60, 8,95.00,760.00),
(48,5, 5, 42.00,210.00),(48,37,5, 23.00,115.00),(48,13,5, 32.50,162.50),(48,15,5, 25.00,125.00),(48,46,5, 18.50, 92.50),(48,48,5, 17.50, 87.50),
(49,17,30, 9.50,285.00),(49,18,15,13.50,202.50),(49,22,30,11.50,345.00),(49,23,10,13.00,130.00),(49,24,80, 3.00,240.00),(49,44,60, 4.50,270.00),(49,45,60, 4.00,240.00),
(50,2, 5, 22.00,110.00),(50,26,5, 12.00, 60.00),(50,27,5,  9.50, 47.50),(50,28,5, 11.00, 55.00),(50,40,3, 22.50, 67.50),(50,41,2, 20.00, 40.00),
(51,49,5, 28.00,140.00),(51,50,5, 42.00,210.00),(51,51,15,22.00,330.00),(51,52,20,10.00,200.00),(51,58,10,34.00,340.00),(51,57,5, 29.00,145.00),(51,59,50, 8.50,425.00),(51,60,4, 95.00,380.00),
(52,1, 8, 38.00,304.00),(52,9, 4, 38.00,152.00),(52,5, 4, 42.00,168.00),(52,40,2, 22.50, 45.00),
(53,2, 30,22.00,660.00),(53,51,15,22.00,330.00),(53,52,10, 9.00, 90.00),(53,26,10,12.00,120.00),(53,27,10, 9.50, 95.00),(53,28,5, 11.00, 55.00),(53,40,3, 22.50, 67.50),
(54,5, 5, 42.00,210.00),(54,37,5, 23.00,115.00),(54,13,5, 32.50,162.50),(54,46,5, 18.50, 92.50),(54,48,5, 17.50, 87.50),
(55,17,30, 9.50,285.00),(55,18,15,13.50,202.50),(55,22,30,11.50,345.00),(55,23,10,13.00,130.00),(55,24,80, 3.00,240.00),(55,44,60, 4.50,270.00),(55,45,60, 4.00,240.00),(55,59,50, 8.50,425.00),(55,57,10,29.00,290.00),
(56,2, 20,22.00,440.00),(56,26,8, 12.00, 96.00),(56,27,8,  9.50, 76.00),(56,28,8, 11.00, 88.00),(56,51,10,22.00,220.00),(56,52,8,  9.00, 72.00),
(57,49,8, 28.00,224.00),(57,50,5, 42.00,210.00),(57,51,10,22.00,220.00),(57,57,10,29.00,290.00),(57,58,8, 34.00,272.00),(57,59,50, 8.50,425.00),(57,60,4, 95.00,380.00),
(58,49,20,28.00,560.00),(58,50,15,42.00,630.00),(58,51,20,22.00,440.00),(58,52,30,10.00,300.00),(58,57,15,29.00,435.00),(58,58,10,34.00,340.00),(58,59,80, 8.50,680.00),(58,60,8, 95.00,760.00),
(59,5, 5, 42.00,210.00),(59,37,5, 23.00,115.00),(59,13,4, 32.50,130.00),(59,15,4, 25.00,100.00),(59,46,4, 18.50, 74.00),(59,48,4, 17.50, 70.00),
(60,1, 8, 38.00,304.00),(60,9, 4, 38.00,152.00),(60,40,2, 22.50, 45.00),
-- venta 61
(61,49,10,28.00,280.00),(61,50,10,42.00,420.00),(61,51,15,22.00,330.00),(61,57,10,29.00,290.00),(61,58,8, 34.00,272.00),(61,59,50, 8.50,425.00),(61,60,4, 95.00,380.00),
(62,1, 5, 38.00,190.00),(62,9, 5, 38.00,190.00),(62,5, 5, 42.00,210.00),(62,40,2, 22.50, 45.00),(62,41,2, 20.00, 40.00),
(63,2, 20,22.00,440.00),(63,51,10,22.00,220.00),(63,26,10,12.00,120.00),(63,27,10, 9.50, 95.00),(63,28,5, 11.00, 55.00),
(64,49,10,28.00,280.00),(64,50,10,42.00,420.00),(64,51,10,22.00,220.00),(64,57,10,29.00,290.00),(64,58,8, 34.00,272.00),(64,59,50, 8.50,425.00),(64,60,5, 95.00,475.00),
(65,1, 8, 38.00,304.00),(65,9, 4, 38.00,152.00),(65,40,2, 22.50, 45.00),
(66,5, 8, 42.00,336.00),(66,37,5, 23.00,115.00),(66,13,4, 32.50,130.00),(66,38,5,  8.00, 40.00),(66,41,2, 20.00, 40.00),
(67,17,20, 9.50,190.00),(67,18,10,13.50,135.00),(67,22,20,11.50,230.00),(67,24,50, 3.00,150.00),(67,44,50, 4.50,225.00),(67,45,50, 4.00,200.00),
(68,49,5, 28.00,140.00),(68,50,5, 42.00,210.00),(68,51,15,22.00,330.00),(68,57,10,29.00,290.00),(68,58,5, 34.00,170.00),(68,59,30, 8.50,255.00),(68,60,4, 95.00,380.00),
-- venta 69 (ANULADA)
(69,1, 5, 38.00,190.00),(69,9, 5, 38.00,190.00),(69,40,2, 22.50, 45.00),(69,41,2, 20.00, 40.00),
(70,2, 15,22.00,330.00),(70,51,8, 22.00,176.00),(70,52,8,  9.00, 72.00),(70,26,5, 12.00, 60.00),(70,27,5,  9.50, 47.50),(70,28,3, 11.00, 33.00),
(71,5, 5, 42.00,210.00),(71,37,5, 23.00,115.00),(71,13,4, 32.50,130.00),(71,40,2, 22.50, 45.00),(71,41,2, 20.00, 40.00),
(72,49,8, 28.00,224.00),(72,50,5, 42.00,210.00),(72,51,15,22.00,330.00),(72,57,10,29.00,290.00),(72,58,5, 34.00,170.00),(72,59,30, 8.50,255.00),(72,60,4, 95.00,380.00),
-- venta 73 (ANULADA)
(73,1, 5, 38.00,190.00),(73,9, 4, 38.00,152.00),(73,40,2, 22.50, 45.00),
(74,2, 20,22.00,440.00),(74,26,8, 12.00, 96.00),(74,27,8,  9.50, 76.00),(74,28,5, 11.00, 55.00),(74,51,8, 22.00,176.00),
(75,49,5, 28.00,140.00),(75,50,5, 42.00,210.00),(75,51,10,22.00,220.00),(75,57,8, 29.00,232.00),(75,58,4, 34.00,136.00),(75,59,20, 8.50,170.00),(75,60,3, 95.00,285.00),
(76,1, 8, 38.00,304.00),(76,9, 5, 38.00,190.00),(76,5, 5, 42.00,210.00),(76,40,2, 22.50, 45.00),(76,41,2, 20.00, 40.00),
(77,17,20, 9.50,190.00),(77,18,10,13.50,135.00),(77,22,20,11.50,230.00),(77,24,50, 3.00,150.00),(77,44,50, 4.50,225.00),(77,45,50, 4.00,200.00),
(78,49,8, 28.00,224.00),(78,50,5, 42.00,210.00),(78,51,10,22.00,220.00),(78,57,10,29.00,290.00),(78,58,5, 34.00,170.00),(78,59,30, 8.50,255.00),(78,60,4, 95.00,380.00),
(79,2, 15,22.00,330.00),(79,26,5, 12.00, 60.00),(79,27,5,  9.50, 47.50),(79,28,3, 11.00, 33.00),(79,51,5, 22.00,110.00),
(80,49,10,28.00,280.00),(80,50,10,42.00,420.00),(80,51,10,22.00,220.00),(80,57,10,29.00,290.00),(80,58,8, 34.00,272.00),(80,59,50, 8.50,425.00),(80,60,5, 95.00,475.00),
(81,1, 5, 38.00,190.00),(81,9, 5, 38.00,190.00),(81,5, 5, 42.00,210.00),(81,40,2, 22.50, 45.00),(81,41,2, 20.00, 40.00),
(82,2, 20,22.00,440.00),(82,51,10,22.00,220.00),(82,52,10, 9.00, 90.00),(82,26,10,12.00,120.00),(82,27,10, 9.50, 95.00),(82,28,5, 11.00, 55.00),
(83,17,20, 9.50,190.00),(83,18,10,13.50,135.00),(83,22,20,11.50,230.00),(83,24,50, 3.00,150.00),(83,44,50, 4.50,225.00),(83,45,50, 4.00,200.00),
(84,49,8, 28.00,224.00),(84,50,5, 42.00,210.00),(84,51,10,22.00,220.00),(84,57,8, 29.00,232.00),(84,58,4, 34.00,136.00),(84,59,20, 8.50,170.00),(84,60,3, 95.00,285.00),
(85,5, 5, 42.00,210.00),(85,37,5, 23.00,115.00),(85,13,4, 32.50,130.00),(85,40,2, 22.50, 45.00),(85,41,2, 20.00, 40.00),
(86,2, 15,22.00,330.00),(86,51,8, 22.00,176.00),(86,26,5, 12.00, 60.00),(86,27,5,  9.50, 47.50),(86,28,3, 11.00, 33.00),
(87,49,5, 28.00,140.00),(87,50,5, 42.00,210.00),(87,51,10,22.00,220.00),(87,57,10,29.00,290.00),(87,58,5, 34.00,170.00),(87,59,30, 8.50,255.00),(87,60,4, 95.00,380.00),
(88,1, 8, 38.00,304.00),(88,9, 5, 38.00,190.00),(88,5, 5, 42.00,210.00),(88,40,2, 22.50, 45.00),(88,41,2, 20.00, 40.00),
-- venta 89 (ANULADA)
(89,2, 10,22.00,220.00),(89,26,5, 12.00, 60.00),(89,27,5,  9.50, 47.50),(89,28,3, 11.00, 33.00),
(90,17,20, 9.50,190.00),(90,18,10,13.50,135.00),(90,22,20,11.50,230.00),(90,24,50, 3.00,150.00),(90,44,50, 4.50,225.00),(90,45,50, 4.00,200.00),(90,43,10, 8.00, 80.00),
(91,49,10,28.00,280.00),(91,50,10,42.00,420.00),(91,51,10,22.00,220.00),(91,57,10,29.00,290.00),(91,58,8, 34.00,272.00),(91,59,50, 8.50,425.00),(91,60,5, 95.00,475.00),
(92,5, 5, 42.00,210.00),(92,37,5, 23.00,115.00),(92,13,4, 32.50,130.00),(92,15,4, 25.00,100.00),(92,46,4, 18.50, 74.00),(92,48,4, 17.50, 70.00),
(93,2, 30,22.00,660.00),(93,51,15,22.00,330.00),(93,26,10,12.00,120.00),(93,27,10, 9.50, 95.00),(93,28,5, 11.00, 55.00),(93,52,10, 9.00, 90.00);

-- ============================================================
-- PAGOS DE VENTA
-- ============================================================
INSERT INTO pago (id_venta, id_metodo_pago, monto, fecha_hora_creacion, estado_fisico) VALUES
(1,  1, 320.00, '2024-12-02 10:35:00', 'REGISTRADO'),
(2,  2, 890.00, '2024-12-02 11:05:00', 'REGISTRADO'),
(3,  4, 540.00, '2024-12-03 09:35:00', 'REGISTRADO'),
(4,  3,1250.00, '2024-12-03 10:05:00', 'REGISTRADO'),
(5,  1, 675.00, '2024-12-04 09:05:00', 'REGISTRADO'),
(6,  5, 420.00, '2024-12-05 10:35:00', 'REGISTRADO'),
(7,  3,1800.00, '2024-12-05 11:05:00', 'REGISTRADO'),
(8,  1, 950.00, '2024-12-06 09:35:00', 'REGISTRADO'),
(9,  3,3200.00, '2024-12-06 10:05:00', 'REGISTRADO'),
(10, 4, 560.00, '2024-12-07 09:05:00', 'REGISTRADO'),
(11, 1, 780.00, '2024-12-08 10:35:00', 'ANULADO'),
(12, 5, 430.00, '2024-12-09 11:05:00', 'REGISTRADO'),
(13, 2,1100.00, '2024-12-10 09:35:00', 'REGISTRADO'),
(14, 3,2400.00, '2024-12-11 10:05:00', 'REGISTRADO'),
(15, 1, 870.00, '2024-12-12 09:05:00', 'REGISTRADO'),
(16, 4, 450.00, '2025-01-03 10:35:00', 'REGISTRADO'),
(17, 3,1600.00, '2025-01-03 11:05:00', 'REGISTRADO'),
(18, 1, 720.00, '2025-01-06 09:35:00', 'REGISTRADO'),
(19, 5, 390.00, '2025-01-07 10:05:00', 'REGISTRADO'),
(20, 3,2100.00, '2025-01-08 09:05:00', 'REGISTRADO'),
(21, 4, 580.00, '2025-01-09 10:35:00', 'REGISTRADO'),
(22, 1, 960.00, '2025-01-10 11:05:00', 'REGISTRADO'),
(23, 3,3400.00, '2025-01-13 09:35:00', 'REGISTRADO'),
(24, 2, 740.00, '2025-01-14 10:05:00', 'REGISTRADO'),
(25, 4,1280.00, '2025-01-15 09:05:00', 'REGISTRADO'),
(26, 1, 490.00, '2025-01-16 10:35:00', 'REGISTRADO'),
(27, 2, 870.00, '2025-01-17 11:05:00', 'ANULADO'),
(28, 5, 620.00, '2025-01-20 09:35:00', 'REGISTRADO'),
(29, 3,1450.00, '2025-01-21 10:05:00', 'REGISTRADO'),
(30, 1, 780.00, '2025-01-22 09:05:00', 'REGISTRADO'),
(31, 4, 560.00, '2025-02-03 10:35:00', 'REGISTRADO'),
(32, 3,2100.00, '2025-02-04 11:05:00', 'REGISTRADO'),
(33, 1, 830.00, '2025-02-05 09:35:00', 'REGISTRADO'),
(34, 3,4500.00, '2025-02-06 10:05:00', 'REGISTRADO'),
(35, 5, 670.00, '2025-02-07 09:05:00', 'REGISTRADO'),
(36, 2,1350.00, '2025-02-10 10:35:00', 'REGISTRADO'),
(37, 4, 990.00, '2025-02-11 11:05:00', 'REGISTRADO'),
(38, 3,2750.00, '2025-02-12 09:35:00', 'REGISTRADO'),
(39, 1, 480.00, '2025-02-13 10:05:00', 'REGISTRADO'),
(40, 5,1120.00, '2025-02-14 09:05:00', 'REGISTRADO'),
(41, 4, 640.00, '2025-02-17 10:35:00', 'ANULADO'),
(42, 2,1890.00, '2025-02-18 11:05:00', 'REGISTRADO'),
(43, 3,3100.00, '2025-02-19 09:35:00', 'REGISTRADO'),
(44, 1, 730.00, '2025-02-20 10:05:00', 'REGISTRADO'),
(45, 4, 540.00, '2025-02-21 09:05:00', 'REGISTRADO'),
(46, 1,1200.00, '2025-03-03 10:35:00', 'REGISTRADO'),
(47, 3,2800.00, '2025-03-04 11:05:00', 'REGISTRADO'),
(48, 4, 890.00, '2025-03-05 09:35:00', 'REGISTRADO'),
(49, 5, 460.00, '2025-03-06 10:05:00', 'REGISTRADO'),
(50, 2,1750.00, '2025-03-07 09:05:00', 'REGISTRADO'),
(51, 1, 960.00, '2025-03-10 10:35:00', 'REGISTRADO'),
(52, 3,3600.00, '2025-03-11 11:05:00', 'REGISTRADO'),
(53, 2,1100.00, '2025-03-12 09:35:00', 'REGISTRADO'),
(54, 4, 780.00, '2025-03-13 10:05:00', 'REGISTRADO'),
(55, 5, 430.00, '2025-03-14 09:05:00', 'REGISTRADO'),
(56, 3,2350.00, '2025-03-17 10:35:00', 'REGISTRADO'),
(57, 1, 690.00, '2025-03-18 11:05:00', 'ANULADO'),
(58, 2,1480.00, '2025-03-19 09:35:00', 'REGISTRADO'),
(59, 4, 870.00, '2025-03-20 10:05:00', 'REGISTRADO'),
(60, 1,4200.00, '2025-04-01 10:35:00', 'REGISTRADO'),
(61, 5, 680.00, '2025-04-02 11:05:00', 'REGISTRADO'),
(62, 2,1340.00, '2025-04-03 09:35:00', 'REGISTRADO'),
(63, 3,2900.00, '2025-04-04 10:05:00', 'REGISTRADO'),
(64, 4, 570.00, '2025-04-07 09:05:00', 'REGISTRADO'),
(65, 1,1650.00, '2025-04-08 10:35:00', 'REGISTRADO'),
(66, 5, 820.00, '2025-04-09 11:05:00', 'REGISTRADO'),
(67, 3,3500.00, '2025-04-10 09:35:00', 'REGISTRADO'),
(68, 2, 940.00, '2025-04-11 10:05:00', 'REGISTRADO'),
(69, 4, 490.00, '2025-04-14 09:05:00', 'REGISTRADO'),
(70, 1,1780.00, '2025-04-15 10:35:00', 'REGISTRADO'),
(71, 3,2600.00, '2025-04-16 11:05:00', 'REGISTRADO'),
(72, 2, 720.00, '2025-04-17 09:35:00', 'ANULADO'),
(73, 5,1090.00, '2025-05-02 10:05:00', 'REGISTRADO'),
(74, 1, 870.00, '2025-05-05 09:05:00', 'REGISTRADO'),
(75, 3,3800.00, '2025-05-06 10:35:00', 'REGISTRADO'),
(76, 4, 650.00, '2025-05-07 11:05:00', 'REGISTRADO'),
(77, 2,1200.00, '2025-05-08 09:35:00', 'REGISTRADO'),
(78, 1,2450.00, '2025-05-09 10:05:00', 'REGISTRADO'),
(79, 5, 780.00, '2025-05-12 09:05:00', 'REGISTRADO'),
(80, 4, 540.00, '2025-05-13 10:35:00', 'REGISTRADO'),
(81, 3,1900.00, '2025-05-14 11:05:00', 'REGISTRADO'),
(82, 2,3200.00, '2025-05-15 09:35:00', 'REGISTRADO'),
(83, 1, 620.00, '2025-05-16 10:05:00', 'REGISTRADO'),
(84, 5,1450.00, '2025-06-02 09:05:00', 'REGISTRADO'),
(85, 4, 870.00, '2025-06-03 10:35:00', 'REGISTRADO'),
(86, 1,2700.00, '2025-06-04 11:05:00', 'REGISTRADO'),
(87, 3, 560.00, '2025-06-05 09:35:00', 'REGISTRADO'),
(88, 2,1320.00, '2025-06-06 10:05:00', 'REGISTRADO'),
(89, 4,4100.00, '2025-06-09 09:05:00', 'REGISTRADO'),
(90, 5, 740.00, '2025-06-09 10:35:00', 'REGISTRADO'),
(91, 1, 980.00, '2025-06-10 09:05:00', 'REGISTRADO'),
(92, 3,2300.00, '2025-06-10 09:35:00', 'REGISTRADO'),
(93, 2,1600.00, '2025-06-10 10:05:00', 'REGISTRADO');

-- ============================================================
-- COMPROBANTES
-- ============================================================
INSERT INTO comprobante (id_venta, tipo_comprobante, serie, correlativo, fecha_emision, estado_fisico) VALUES
-- Boletas
(1,  'BOLETA','B001','00000001','2024-12-02 10:36:00','EMITIDO'),
(3,  'BOLETA','B001','00000002','2024-12-03 09:36:00','EMITIDO'),
(5,  'BOLETA','B001','00000003','2024-12-04 09:06:00','EMITIDO'),
(6,  'BOLETA','B001','00000004','2024-12-05 10:36:00','EMITIDO'),
(10, 'BOLETA','B001','00000005','2024-12-07 09:06:00','EMITIDO'),
(12, 'BOLETA','B001','00000006','2024-12-09 11:06:00','EMITIDO'),
(16, 'BOLETA','B001','00000007','2025-01-03 10:36:00','EMITIDO'),
(19, 'BOLETA','B001','00000008','2025-01-07 10:06:00','EMITIDO'),
(21, 'BOLETA','B001','00000009','2025-01-09 10:36:00','EMITIDO'),
(26, 'BOLETA','B001','00000010','2025-01-16 10:36:00','EMITIDO'),
(28, 'BOLETA','B001','00000011','2025-01-20 09:36:00','EMITIDO'),
(30, 'BOLETA','B001','00000012','2025-01-22 09:06:00','EMITIDO'),
(31, 'BOLETA','B001','00000013','2025-02-03 10:36:00','EMITIDO'),
(35, 'BOLETA','B001','00000014','2025-02-07 09:06:00','EMITIDO'),
(39, 'BOLETA','B001','00000015','2025-02-13 10:06:00','EMITIDO'),
(45, 'BOLETA','B001','00000016','2025-02-21 09:06:00','EMITIDO'),
(49, 'BOLETA','B001','00000017','2025-03-06 10:06:00','EMITIDO'),
(55, 'BOLETA','B001','00000018','2025-03-14 09:06:00','EMITIDO'),
(61, 'BOLETA','B001','00000019','2025-04-02 11:06:00','EMITIDO'),
(64, 'BOLETA','B001','00000020','2025-04-07 09:06:00','EMITIDO'),
(68, 'BOLETA','B001','00000021','2025-04-11 10:06:00','EMITIDO'),
(73, 'BOLETA','B001','00000022','2025-05-02 10:06:00','EMITIDO'),
(74, 'BOLETA','B001','00000023','2025-05-05 09:06:00','EMITIDO'),
(79, 'BOLETA','B001','00000024','2025-05-12 09:06:00','EMITIDO'),
(80, 'BOLETA','B001','00000025','2025-05-13 10:36:00','EMITIDO'),
(83, 'BOLETA','B001','00000026','2025-05-16 10:06:00','EMITIDO'),
(85, 'BOLETA','B001','00000027','2025-06-03 10:36:00','EMITIDO'),
(87, 'BOLETA','B001','00000028','2025-06-05 09:36:00','EMITIDO'),
(90, 'BOLETA','B001','00000029','2025-06-09 10:36:00','EMITIDO'),
(91, 'BOLETA','B001','00000030','2025-06-10 09:06:00','EMITIDO'),
-- Facturas
(2,  'FACTURA','F001','00000001','2024-12-02 11:06:00','EMITIDO'),
(4,  'FACTURA','F001','00000002','2024-12-03 10:06:00','EMITIDO'),
(7,  'FACTURA','F001','00000003','2024-12-05 11:06:00','EMITIDO'),
(8,  'FACTURA','F001','00000004','2024-12-06 09:36:00','EMITIDO'),
(9,  'FACTURA','F001','00000005','2024-12-06 10:06:00','EMITIDO'),
(13, 'FACTURA','F001','00000006','2024-12-10 09:36:00','EMITIDO'),
(14, 'FACTURA','F001','00000007','2024-12-11 10:06:00','EMITIDO'),
(15, 'FACTURA','F001','00000008','2024-12-12 09:06:00','EMITIDO'),
(17, 'FACTURA','F001','00000009','2025-01-03 11:06:00','EMITIDO'),
(18, 'FACTURA','F001','00000010','2025-01-06 09:36:00','EMITIDO'),
(20, 'FACTURA','F001','00000011','2025-01-08 09:06:00','EMITIDO'),
(22, 'FACTURA','F001','00000012','2025-01-10 11:06:00','EMITIDO'),
(23, 'FACTURA','F001','00000013','2025-01-13 09:36:00','EMITIDO'),
(24, 'FACTURA','F001','00000014','2025-01-14 10:06:00','EMITIDO'),
(25, 'FACTURA','F001','00000015','2025-01-15 09:06:00','EMITIDO'),
(29, 'FACTURA','F001','00000016','2025-01-21 10:06:00','EMITIDO'),
(32, 'FACTURA','F001','00000017','2025-02-04 11:06:00','EMITIDO'),
(33, 'FACTURA','F001','00000018','2025-02-05 09:36:00','EMITIDO'),
(34, 'FACTURA','F001','00000019','2025-02-06 10:06:00','EMITIDO'),
(36, 'FACTURA','F001','00000020','2025-02-10 10:36:00','EMITIDO'),
(37, 'FACTURA','F001','00000021','2025-02-11 11:06:00','EMITIDO'),
(38, 'FACTURA','F001','00000022','2025-02-12 09:36:00','EMITIDO'),
(40, 'FACTURA','F001','00000023','2025-02-14 09:06:00','EMITIDO'),
(42, 'FACTURA','F001','00000024','2025-02-18 11:06:00','EMITIDO'),
(43, 'FACTURA','F001','00000025','2025-02-19 09:36:00','EMITIDO'),
(44, 'FACTURA','F001','00000026','2025-02-20 10:06:00','EMITIDO'),
(46, 'FACTURA','F001','00000027','2025-03-03 10:36:00','EMITIDO'),
(47, 'FACTURA','F001','00000028','2025-03-04 11:06:00','EMITIDO'),
(48, 'FACTURA','F001','00000029','2025-03-05 09:36:00','EMITIDO'),
(50, 'FACTURA','F001','00000030','2025-03-07 09:06:00','EMITIDO'),
(51, 'FACTURA','F001','00000031','2025-03-10 10:36:00','EMITIDO'),
(52, 'FACTURA','F001','00000032','2025-03-11 11:06:00','EMITIDO'),
(53, 'FACTURA','F001','00000033','2025-03-12 09:36:00','EMITIDO'),
(54, 'FACTURA','F001','00000034','2025-03-13 10:06:00','EMITIDO'),
(56, 'FACTURA','F001','00000035','2025-03-17 10:36:00','EMITIDO'),
(58, 'FACTURA','F001','00000036','2025-03-19 09:36:00','EMITIDO'),
(59, 'FACTURA','F001','00000037','2025-03-20 10:06:00','EMITIDO'),
(60, 'FACTURA','F001','00000038','2025-04-01 10:36:00','EMITIDO'),
(62, 'FACTURA','F001','00000039','2025-04-03 09:36:00','EMITIDO'),
(63, 'FACTURA','F001','00000040','2025-04-04 10:06:00','EMITIDO'),
(65, 'FACTURA','F001','00000041','2025-04-08 10:36:00','EMITIDO'),
(66, 'FACTURA','F001','00000042','2025-04-09 11:06:00','EMITIDO'),
(67, 'FACTURA','F001','00000043','2025-04-10 09:36:00','EMITIDO'),
(70, 'FACTURA','F001','00000044','2025-04-15 10:36:00','EMITIDO'),
(71, 'FACTURA','F001','00000045','2025-04-16 11:06:00','EMITIDO'),
(75, 'FACTURA','F001','00000046','2025-05-06 10:36:00','EMITIDO'),
(76, 'FACTURA','F001','00000047','2025-05-07 11:06:00','EMITIDO'),
(77, 'FACTURA','F001','00000048','2025-05-08 09:36:00','EMITIDO'),
(78, 'FACTURA','F001','00000049','2025-05-09 10:06:00','EMITIDO'),
(81, 'FACTURA','F001','00000050','2025-05-14 11:06:00','EMITIDO'),
(82, 'FACTURA','F001','00000051','2025-05-15 09:36:00','EMITIDO'),
(84, 'FACTURA','F001','00000052','2025-06-02 09:06:00','EMITIDO'),
(86, 'FACTURA','F001','00000053','2025-06-04 11:06:00','EMITIDO'),
(88, 'FACTURA','F001','00000054','2025-06-06 10:06:00','EMITIDO'),
(89, 'FACTURA','F001','00000055','2025-06-09 09:06:00','EMITIDO'),
(92, 'FACTURA','F001','00000056','2025-06-10 09:36:00','EMITIDO'),
(93, 'FACTURA','F001','00000057','2025-06-10 10:06:00','EMITIDO'),
-- Notas
(11, 'NOTA','NC01','00000001','2024-12-08 10:36:00','EMITIDO'),
(27, 'NOTA','NC01','00000002','2025-01-17 11:06:00','EMITIDO'),
(41, 'NOTA','NC01','00000003','2025-02-17 10:36:00','EMITIDO'),
(57, 'NOTA','NC01','00000004','2025-03-18 11:06:00','EMITIDO'),
(69, 'NOTA','NC01','00000005','2025-04-14 09:06:00','EMITIDO'),
(72, 'NOTA','NC01','00000006','2025-04-17 09:36:00','ANULADO');

-- ============================================================
-- MOVIMIENTOS DE INVENTARIO — EGRESO por ventas (muestra representativa)
-- ============================================================
INSERT INTO movimiento_inventario (id_producto,id_tipo_movimiento_inventario,id_usuario_registro,id_venta,cantidad,stock_anterior,stock_nuevo,fecha_hora_movimiento_inventario) VALUES
-- Venta 1
(1, 2,3,1, 5,170,165,'2024-12-02 10:30:00'),
(40,2,3,1, 2, 90, 88,'2024-12-02 10:30:00'),
-- Venta 2
(2, 2,4,2,20,700,680,'2024-12-02 11:00:00'),
(26,2,4,2, 5,280,275,'2024-12-02 11:00:00'),
-- Venta 4 (empresa)
(2, 2,5,4,30,680,650,'2024-12-03 10:00:00'),
(51,2,5,4,10,120,110,'2024-12-03 10:00:00'),
-- Venta 7 (PVC, empresa)
(17,2,5,7,30,400,370,'2024-12-05 11:00:00'),
(22,2,5,7,40,180,140,'2024-12-05 11:00:00'),
-- Venta 9 (cerámicas)
(49,2,5,9, 5,190,185,'2024-12-06 10:00:00'),
(50,2,5,9, 5,150,145,'2024-12-06 10:00:00'),
(59,2,5,9,50,500,450,'2024-12-06 10:00:00'),
-- Venta 20 (empresa)
(2, 2,5,20,30,650,620,'2025-01-08 09:00:00'),
(26,2,5,20,10,275,265,'2025-01-08 09:00:00'),
-- Venta 23 (empresa grande)
(49,2,5,23,10,185,175,'2025-01-13 09:30:00'),
(50,2,5,23,10,145,135,'2025-01-13 09:30:00'),
(59,2,5,23,50,450,400,'2025-01-13 09:30:00'),
-- Venta 34 (empresa)
(49,2,4,34,10,175,165,'2025-02-06 10:00:00'),
(57,2,4,34,10,200,190,'2025-02-06 10:00:00'),
(59,2,4,34,50,400,350,'2025-02-06 10:00:00'),
-- Venta 38 (empresa)
(17,2,5,38,30,370,340,'2025-02-12 09:30:00'),
(22,2,5,38,30,140,110,'2025-02-12 09:30:00'),
-- Venta 47 (empresa)
(49,2,4,47,20,165,145,'2025-03-04 11:00:00'),
(50,2,4,47,15,135,120,'2025-03-04 11:00:00'),
(59,2,4,47,80,350,270,'2025-03-04 11:00:00'),
-- Venta 52 (empresa)
(2, 2,5,52,30,620,590,'2025-03-11 11:00:00'),
(51,2,5,52,15,110, 95,'2025-03-11 11:00:00'),
-- Venta 58 (empresa grande)
(49,2,4,58,20,145,125,'2025-04-01 10:30:00'),
(50,2,4,58,15,120,105,'2025-04-01 10:30:00'),
(59,2,4,58,80,270,190,'2025-04-01 10:30:00'),
-- Venta 67 (empresa)
(2, 2,3,67,20,590,570,'2025-04-10 09:30:00'),
(26,2,3,67, 8,265,257,'2025-04-10 09:30:00'),
-- Venta 75 (empresa)
(17,2,5,75,30,340,310,'2025-05-06 10:30:00'),
(22,2,5,75,30,110, 80,'2025-05-06 10:30:00'),
-- Venta 82 (empresa)
(2, 2,4,82,20,570,550,'2025-05-15 09:30:00'),
(26,2,4,82,10,257,247,'2025-05-15 09:30:00'),
-- Venta 89 (empresa)
(2, 2,5,89,30,550,520,'2025-06-09 09:00:00'),
(26,2,5,89,10,247,237,'2025-06-09 09:00:00'),
-- Venta 93 (empresa)
(2, 2,4,93,30,520,490,'2025-06-10 10:00:00'),
(51,2,4,93,15, 95, 80,'2025-06-10 10:00:00');

-- ============================================================
-- MOVIMIENTOS DE CAJA
-- ============================================================
INSERT INTO movimiento_caja (id_caja, id_tipo_movimiento_caja, id_usuario_registra, id_venta, id_metodo_pago, monto, descripcion, fecha_hora_movimiento, estado_fisico) VALUES
-- Ingresos por ventas en efectivo (Caja 1)
(1,1,12,1, 1, 320.00,'Cobro venta V-0001','2024-12-02 10:36:00','REGISTRADO'),
(1,1,12,5, 1, 675.00,'Cobro venta V-0005','2024-12-04 09:06:00','REGISTRADO'),
(1,1,12,6, 5, 420.00,'Cobro venta V-0006','2024-12-05 10:36:00','REGISTRADO'),
(1,1,12,10,4, 560.00,'Cobro venta V-0010','2024-12-07 09:06:00','REGISTRADO'),
(1,1,12,12,5, 430.00,'Cobro venta V-0012','2024-12-09 11:06:00','REGISTRADO'),
(1,1,12,15,1, 870.00,'Cobro venta V-0015','2024-12-12 09:06:00','REGISTRADO'),
(1,1,13,16,4, 450.00,'Cobro venta V-0016','2025-01-03 10:36:00','REGISTRADO'),
(1,1,13,19,5, 390.00,'Cobro venta V-0019','2025-01-07 10:06:00','REGISTRADO'),
(1,1,13,21,4, 580.00,'Cobro venta V-0021','2025-01-09 10:36:00','REGISTRADO'),
(1,1,13,26,1, 490.00,'Cobro venta V-0026','2025-01-16 10:36:00','REGISTRADO'),
(1,1,12,28,5, 620.00,'Cobro venta V-0028','2025-01-20 09:36:00','REGISTRADO'),
(1,1,12,30,1, 780.00,'Cobro venta V-0030','2025-01-22 09:06:00','REGISTRADO'),
(1,1,13,31,4, 560.00,'Cobro venta V-0031','2025-02-03 10:36:00','REGISTRADO'),
(1,1,12,35,5, 670.00,'Cobro venta V-0035','2025-02-07 09:06:00','REGISTRADO'),
(1,1,12,39,1, 480.00,'Cobro venta V-0039','2025-02-13 10:06:00','REGISTRADO'),
(1,1,13,45,4, 540.00,'Cobro venta V-0045','2025-02-21 09:06:00','REGISTRADO'),
(1,1,13,49,5, 460.00,'Cobro venta V-0049','2025-03-06 10:06:00','REGISTRADO'),
(1,1,12,55,5, 430.00,'Cobro venta V-0055','2025-03-14 09:06:00','REGISTRADO'),
(1,1,13,61,5, 680.00,'Cobro venta V-0061','2025-04-02 11:06:00','REGISTRADO'),
(1,1,12,64,4, 570.00,'Cobro venta V-0064','2025-04-07 09:06:00','REGISTRADO'),
(1,1,13,68,2, 940.00,'Cobro venta V-0068','2025-04-11 10:06:00','REGISTRADO'),
(1,1,13,73,5,1090.00,'Cobro venta V-0073','2025-05-02 10:06:00','REGISTRADO'),
(1,1,12,74,1, 870.00,'Cobro venta V-0074','2025-05-05 09:06:00','REGISTRADO'),
(1,1,12,79,5, 780.00,'Cobro venta V-0079','2025-05-12 09:06:00','REGISTRADO'),
(1,1,13,80,4, 540.00,'Cobro venta V-0080','2025-05-13 10:36:00','REGISTRADO'),
(1,1,12,83,1, 620.00,'Cobro venta V-0083','2025-05-16 10:06:00','REGISTRADO'),
(1,1,13,85,4, 870.00,'Cobro venta V-0085','2025-06-03 10:36:00','REGISTRADO'),
(1,1,12,87,3, 560.00,'Cobro venta V-0087','2025-06-05 09:36:00','REGISTRADO'),
(1,1,13,90,5, 740.00,'Cobro venta V-0090','2025-06-09 10:36:00','REGISTRADO'),
(1,1,12,91,1, 980.00,'Cobro venta V-0091','2025-06-10 09:06:00','REGISTRADO'),
-- Ingresos por ventas en transferencia / Yape / Plin (Caja 2)
(2,1,13,2, 2, 890.00,'Cobro venta V-0002','2024-12-02 11:06:00','REGISTRADO'),
(2,1,13,4, 3,1250.00,'Cobro venta V-0004','2024-12-03 10:06:00','REGISTRADO'),
(2,1,13,7, 3,1800.00,'Cobro venta V-0007','2024-12-05 11:06:00','REGISTRADO'),
(2,1,12,8, 1, 950.00,'Cobro venta V-0008','2024-12-06 09:36:00','REGISTRADO'),
(2,1,12,9, 3,3200.00,'Cobro venta V-0009','2024-12-06 10:06:00','REGISTRADO'),
(2,1,13,13,2,1100.00,'Cobro venta V-0013','2024-12-10 09:36:00','REGISTRADO'),
(2,1,13,14,3,2400.00,'Cobro venta V-0014','2024-12-11 10:06:00','REGISTRADO'),
(2,1,13,17,3,1600.00,'Cobro venta V-0017','2025-01-03 11:06:00','REGISTRADO'),
(2,1,12,18,1, 720.00,'Cobro venta V-0018','2025-01-06 09:36:00','REGISTRADO'),
(2,1,13,20,3,2100.00,'Cobro venta V-0020','2025-01-08 09:06:00','REGISTRADO'),
(2,1,13,22,1, 960.00,'Cobro venta V-0022','2025-01-10 11:06:00','REGISTRADO'),
(2,1,13,23,3,3400.00,'Cobro venta V-0023','2025-01-13 09:36:00','REGISTRADO'),
(2,1,12,24,2, 740.00,'Cobro venta V-0024','2025-01-14 10:06:00','REGISTRADO'),
(2,1,12,25,4,1280.00,'Cobro venta V-0025','2025-01-15 09:06:00','REGISTRADO'),
(2,1,13,29,3,1450.00,'Cobro venta V-0029','2025-01-21 10:06:00','REGISTRADO'),
-- Egresos por pagos de compra en caja
(4,2,10,NULL,3,4500.00,'Pago compra a Aceros Arequipa','2024-12-05 15:10:00','REGISTRADO'),
(4,2,10,NULL,3,2800.00,'Pago compra a Anypsa','2024-12-07 15:10:00','REGISTRADO'),
(4,2,11,NULL,3,1950.00,'Pago compra a Paraíso','2024-12-10 15:10:00','REGISTRADO'),
(4,2,10,NULL,3,6200.00,'Pago compra a Pacasmayo','2024-12-12 15:10:00','REGISTRADO'),
(4,2,11,NULL,3,3100.00,'Pago compra a Ferretera Norte','2024-12-14 15:10:00','REGISTRADO'),
(4,2,10,NULL,3,2250.00,'Pago compra a PVC Perú','2025-01-07 15:10:00','REGISTRADO'),
(4,2,10,NULL,3,5500.00,'Pago compra a Maderas Norte','2025-01-12 15:10:00','REGISTRADO'),
(4,2,11,NULL,3,4200.00,'Pago compra a Cerámicas Lima','2025-01-14 15:10:00','REGISTRADO'),
(4,2,10,NULL,3,3300.00,'Pago compra a Sika Perú','2025-01-17 15:10:00','REGISTRADO'),
-- Egresos varios (gastos operativos)
(1,2,12,NULL,NULL, 350.00,'Pago servicio de limpieza','2025-01-15 16:00:00','REGISTRADO'),
(1,2,12,NULL,NULL, 180.00,'Pago materiales de oficina','2025-02-10 16:00:00','REGISTRADO'),
(1,2,13,NULL,NULL, 420.00,'Pago mantenimiento local','2025-03-05 16:00:00','REGISTRADO'),
(1,2,12,NULL,NULL, 250.00,'Pago movilidad y fletes','2025-04-02 16:00:00','REGISTRADO'),
(1,2,13,NULL,NULL, 310.00,'Pago servicio de vigilancia','2025-05-07 16:00:00','REGISTRADO'),
(1,2,12,NULL,NULL, 190.00,'Pago útiles de escritorio','2025-06-03 16:00:00','REGISTRADO'),
-- Movimiento ANULADO
(1,1,12,11,1, 780.00,'Cobro anulado venta V-0011','2024-12-08 10:36:00','ANULADO');