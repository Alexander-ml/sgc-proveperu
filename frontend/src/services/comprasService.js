const COMPRAS_MOCK = [
  {
    idCompra: 1,
    numeroCompra: 'C-2026-001',
    fecha: '2026-05-01',
    proveedor: 'Distribuidora Textil Lima',
    productos: 3,
    total: 3200.0,
    metodoPago: 'Transferencia',
    estado: 'RECIBIDA',
    registradoPor: 'César Medina',
  },
  {
    idCompra: 2,
    numeroCompra: 'C-2026-002',
    fecha: '2026-04-28',
    proveedor: 'Paraíso Peru S.A.C.',
    productos: 3,
    total: 1890.0,
    metodoPago: 'Transferencia',
    estado: 'RECIBIDA',
    registradoPor: 'César Medina',
  },
  {
    idCompra: 3,
    numeroCompra: 'C-2026-003',
    fecha: '2026-04-25',
    proveedor: 'Maderera El Pino SAC',
    productos: 2,
    total: 1375.0,
    metodoPago: 'Depósito',
    estado: 'RECIBIDA',
    registradoPor: 'César Medina',
  },
  {
    idCompra: 4,
    numeroCompra: 'C-2026-004',
    fecha: '2026-04-22',
    proveedor: 'Aceros Arequipa S.A.',
    productos: 3,
    total: 960.0,
    metodoPago: 'Transferencia',
    estado: 'PENDIENTE',
    registradoPor: 'César Medina',
  },
];

const PROVEEDORES_MOCK = [
  {
    idProveedor: 1,
    nombre: 'Paraíso Peru S.A.C.',
    ruc: '20100123456',
    telefono: '044-234567',
    correo: 'ventas@paraiso.com.pe',
    categoria: 'Espumas y rellenos',
    contacto: 'Carlos Quispe',
    estado: 'ACTIVO',
  },
  {
    idProveedor: 2,
    nombre: 'Distribuidora Textil Lima',
    ruc: '20200345678',
    telefono: '01-4523678',
    correo: 'pedidos@textilesLima.com',
    categoria: 'Telas y tapicería',
    contacto: 'María López',
    estado: 'ACTIVO',
  },
  {
    idProveedor: 3,
    nombre: 'Anypsa Corporation',
    ruc: '20300456789',
    telefono: '01-3456789',
    correo: 'comercial@anypsa.com.pe',
    categoria: 'Pinturas y acabados',
    contacto: 'Roberto Díaz',
    estado: 'ACTIVO',
  },
  {
    idProveedor: 4,
    nombre: 'Maderera El Pino SAC',
    ruc: '20400567890',
    telefono: '044-345678',
    correo: 'ventas@madereraelpino.com',
    categoria: 'Maderas y tableros',
    contacto: 'Luis Rojas',
    estado: 'ACTIVO',
  },
  {
    idProveedor: 5,
    nombre: 'Aceros Arequipa S.A.',
    ruc: '20500678901',
    telefono: '054-456789',
    correo: 'ventas@acerosarequipa.com',
    categoria: 'Aceros y construcción',
    contacto: 'Fernando Salas',
    estado: 'ACTIVO',
  },
];

const obtenerComprasGuardadas = () => {
  const comprasGuardadas = localStorage.getItem('compras_mock');

  if (!comprasGuardadas) {
    localStorage.setItem('compras_mock', JSON.stringify(COMPRAS_MOCK));
    return COMPRAS_MOCK;
  }

  return JSON.parse(comprasGuardadas);
};

const guardarCompras = (compras) => {
  localStorage.setItem('compras_mock', JSON.stringify(compras));
};

const obtenerProveedoresGuardados = () => {
  const proveedoresGuardados = localStorage.getItem('proveedores_mock');

  if (!proveedoresGuardados) {
    localStorage.setItem('proveedores_mock', JSON.stringify(PROVEEDORES_MOCK));
    return PROVEEDORES_MOCK;
  }

  return JSON.parse(proveedoresGuardados);
};

const guardarProveedores = (proveedores) => {
  localStorage.setItem('proveedores_mock', JSON.stringify(proveedores));
};

export const obtenerResumenCompras = async () => {
  const compras = obtenerComprasGuardadas();

  const totalCompras = compras.length;

  const montoTotal = compras.reduce(
    (total, compra) => total + Number(compra.total || 0),
    0
  );

  const recibidas = compras.filter(
    (compra) => compra.estado === 'RECIBIDA'
  ).length;

  const pendientes = compras.filter(
    (compra) => compra.estado === 'PENDIENTE'
  ).length;

  return {
    data: {
      totalCompras,
      montoTotal,
      recibidas,
      pendientes,
    },
  };
};

export const listarCompras = async ({ busqueda = '', estado = '' } = {}) => {
  let compras = obtenerComprasGuardadas();

  if (busqueda) {
    const texto = busqueda.toLowerCase();

    compras = compras.filter(
      (compra) =>
        compra.numeroCompra.toLowerCase().includes(texto) ||
        compra.proveedor.toLowerCase().includes(texto)
    );
  }

  if (estado) {
    compras = compras.filter((compra) => compra.estado === estado);
  }

  return {
    data: compras,
  };
};

export const listarProveedores = async () => {
  const proveedores = obtenerProveedoresGuardados();

  return {
    data: proveedores,
  };
};

export const crearCompra = async (compra) => {
  const compras = obtenerComprasGuardadas();
  const proveedores = obtenerProveedoresGuardados();

  const proveedorEncontrado = proveedores.find(
    (proveedor) => Number(proveedor.idProveedor) === Number(compra.idProveedor)
  );

  const nuevaCompra = {
    idCompra: Date.now(),
    numeroCompra: `C-2026-${String(compras.length + 1).padStart(3, '0')}`,
    fecha: new Date().toISOString().slice(0, 10),
    proveedor: proveedorEncontrado?.nombre || 'Proveedor no seleccionado',
    productos: Number(compra.productos || 0),
    total: Number(compra.total || 0),
    metodoPago: compra.metodoPago || 'Transferencia',
    estado: 'PENDIENTE',
    registradoPor: 'César Medina',
    notas: compra.notas || '',
  };

  const actualizadas = [nuevaCompra, ...compras];

  guardarCompras(actualizadas);

  return {
    data: nuevaCompra,
    message: 'Compra registrada temporalmente',
  };
};

export const crearProveedor = async (proveedor) => {
  const proveedores = obtenerProveedoresGuardados();

  const nuevoProveedor = {
    idProveedor: Date.now(),
    nombre: proveedor.nombre,
    ruc: proveedor.ruc,
    telefono: proveedor.telefono,
    correo: proveedor.correo,
    categoria: proveedor.categoria,
    contacto: proveedor.contacto,
    estado: 'ACTIVO',
  };

  const actualizados = [nuevoProveedor, ...proveedores];

  guardarProveedores(actualizados);

  return {
    data: nuevoProveedor,
    message: 'Proveedor registrado temporalmente',
  };
};

export const obtenerDetalleCompra = async (idCompra) => {
  const compras = obtenerComprasGuardadas();

  const compra = compras.find(
    (item) => Number(item.idCompra) === Number(idCompra)
  );

  return {
    data: compra || null,
  };
};