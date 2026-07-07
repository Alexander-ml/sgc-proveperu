const PRODUCTOS_MOCK = [
  {
    idProducto: 1,
    codigo: 'TEL-001',
    nombre: 'Tela Terciopelo Azul',
    unidad: 'Metro',
    categoria: 'Telas y Tapicería',
    stockActual: 45,
    stockMinimo: 10,
    precioCosto: 12.5,
    precioVenta: 18,
  },
  {
    idProducto: 2,
    codigo: 'TEL-002',
    nombre: 'Cuero Sintético Negro',
    unidad: 'Metro',
    categoria: 'Telas y Tapicería',
    stockActual: 30,
    stockMinimo: 8,
    precioCosto: 22,
    precioVenta: 32,
  },
  {
    idProducto: 3,
    codigo: 'TEL-003',
    nombre: 'Tela Chenille Beige',
    unidad: 'Metro',
    categoria: 'Telas y Tapicería',
    stockActual: 60,
    stockMinimo: 12,
    precioCosto: 15,
    precioVenta: 22,
  },
  {
    idProducto: 4,
    codigo: 'ESP-001',
    nombre: 'Espuma HR-40 10cm',
    unidad: 'Plancha',
    categoria: 'Espumas y Rellenos',
    stockActual: 6,
    stockMinimo: 8,
    precioCosto: 45,
    precioVenta: 68,
  },
  {
    idProducto: 5,
    codigo: 'PEG-001',
    nombre: 'Pegamento de Contacto 1L',
    unidad: 'Unidad',
    categoria: 'Pegamentos',
    stockActual: 0,
    stockMinimo: 5,
    precioCosto: 18,
    precioVenta: 28,
  },
];

const MOVIMIENTOS_MOCK = [
  {
    idMovimiento: 1,
    fechaHora: '2026-05-06T09:15:00',
    producto: 'Tela Terciopelo Azul',
    tipo: 'SALIDA',
    cantidad: 20,
    motivo: 'Venta V-2026-001',
    stockAnterior: 65,
    stockActual: 45,
    usuario: 'Iris Arroyo',
  },
  {
    idMovimiento: 2,
    fechaHora: '2026-05-06T09:15:00',
    producto: 'Pegamento de Contacto 1L',
    tipo: 'SALIDA',
    cantidad: 5,
    motivo: 'Venta V-2026-001',
    stockAnterior: 30,
    stockActual: 25,
    usuario: 'Iris Arroyo',
  },
  {
    idMovimiento: 3,
    fechaHora: '2026-05-06T10:30:00',
    producto: 'Tablero MDF 15mm',
    tipo: 'SALIDA',
    cantidad: 2,
    motivo: 'Venta V-2026-002',
    stockAnterior: 14,
    stockActual: 12,
    usuario: 'Iris Arroyo',
  },
  {
    idMovimiento: 4,
    fechaHora: '2026-05-05T14:45:00',
    producto: 'Cuero Sintético Negro',
    tipo: 'SALIDA',
    cantidad: 30,
    motivo: 'Venta V-2026-003',
    stockAnterior: 60,
    stockActual: 30,
    usuario: 'Iris Arroyo',
  },
];

const obtenerProductosGuardados = () => {
  const guardados = localStorage.getItem('productos_mock');

  if (!guardados) {
    localStorage.setItem('productos_mock', JSON.stringify(PRODUCTOS_MOCK));
    return PRODUCTOS_MOCK;
  }

  return JSON.parse(guardados);
};

const guardarProductos = (productos) => {
  localStorage.setItem('productos_mock', JSON.stringify(productos));
};

const calcularEstadoProducto = (producto) => {
  if (Number(producto.stockActual) <= 0) return 'SIN_STOCK';
  if (Number(producto.stockActual) < Number(producto.stockMinimo)) return 'STOCK_BAJO';
  return 'DISPONIBLE';
};

export const obtenerResumenInventario = async () => {
  const productos = obtenerProductosGuardados();

  const totalProductos = productos.length;
  const sinStock = productos.filter((p) => calcularEstadoProducto(p) === 'SIN_STOCK').length;
  const stockBajo = productos.filter((p) => calcularEstadoProducto(p) === 'STOCK_BAJO').length;
  const disponible = productos.filter((p) => calcularEstadoProducto(p) === 'DISPONIBLE').length;

  return {
    data: {
      totalProductos,
      sinStock,
      stockBajo,
      disponible,
    },
  };
};

export const listarProductosInventario = async ({ busqueda = '', categoria = '', estado = '' }) => {
  let productos = obtenerProductosGuardados();

  productos = productos.map((producto) => ({
    ...producto,
    estado: calcularEstadoProducto(producto),
  }));

  if (busqueda) {
    const texto = busqueda.toLowerCase();

    productos = productos.filter(
      (p) =>
        p.nombre.toLowerCase().includes(texto) ||
        p.codigo.toLowerCase().includes(texto)
    );
  }

  if (categoria) {
    productos = productos.filter((p) => p.categoria === categoria);
  }

  if (estado) {
    productos = productos.filter((p) => p.estado === estado);
  }

  return {
    data: productos,
  };
};

export const listarMovimientosInventario = async () => {
  return {
    data: MOVIMIENTOS_MOCK,
  };
};

export const crearProductoInventario = async (producto) => {
  const productos = obtenerProductosGuardados();

  const nuevoProducto = {
    ...producto,
    idProducto: Date.now(),
    stockActual: Number(producto.stockActual),
    stockMinimo: Number(producto.stockMinimo),
    precioCosto: Number(producto.precioCosto),
    precioVenta: Number(producto.precioVenta),
  };

  const actualizados = [nuevoProducto, ...productos];

  guardarProductos(actualizados);

  return {
    data: nuevoProducto,
    message: 'Producto creado temporalmente',
  };
};