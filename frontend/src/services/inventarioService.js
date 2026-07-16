import axiosInstance from './axiosInstance';

const extraerData = (response) => response?.data?.data ?? response?.data ?? response;

const toNumber = (value) => {
  const numero = Number(value);
  return Number.isNaN(numero) ? 0 : numero;
};

const mapearProducto = (producto = {}) => ({
  idProducto: producto.idProducto,
  codigo: producto.codigoProducto || producto.codigo || '-',
  nombre: producto.nombreProducto || producto.nombre || '-',
  descripcion: producto.descripcion || '',
  unidad: producto.unidadMedida || producto.unidad || 'UNIDAD',
  stockActual: toNumber(producto.cantidadActual ?? producto.stockActual),
  stockMinimo: toNumber(producto.stockMinimo),
  estado: producto.estadoStock || producto.estado || 'DISPONIBLE',
  estadoProducto: producto.estadoProducto || 'ACTIVO',
  fechaHoraActualizacion: producto.fechaHoraActualizacion || null,

  // Pendiente de backend:
  categoria: 'Por anadir',
  precioCosto: null,
  precioVenta: null,
});

const mapearMovimiento = (movimiento = {}) => {
  const tipoNombre =
    movimiento.tipoMovimientoInventario?.nombre ||
    movimiento.tipo ||
    movimiento.nombreTipoMovimientoInventario ||
    'MOVIMIENTO';

  const esEntrada =
    tipoNombre === 'INGRESO' || tipoNombre === 'AJUSTE_POSITIVO';

  return {
    idMovimiento:
      movimiento.idMovimientoInventario ||
      movimiento.idMovimiento ||
      `${movimiento.idProducto || 'mov'}-${movimiento.fechaHoraMovimientoInventario || Date.now()}`,
    fechaHora:
      movimiento.fechaHoraMovimientoInventario ||
      movimiento.fechaHora ||
      null,
    producto:
      movimiento.producto?.nombreProducto ||
      movimiento.producto?.nombre ||
      movimiento.producto ||
      '-',
    tipo: esEntrada ? 'ENTRADA' : 'SALIDA',
    tipoOriginal: tipoNombre,
    cantidad: toNumber(movimiento.cantidad),
    motivo:
      movimiento.compra?.numeroCompra ||
      movimiento.venta?.numeroVenta ||
      movimiento.recepcionCompra?.idRecepcionCompra ||
      tipoNombre,
    stockAnterior: toNumber(movimiento.stockAnterior),
    stockActual: toNumber(movimiento.stockNuevo ?? movimiento.stockActual),
    usuario:
      movimiento.usuarioRegistro?.nombreCompleto ||
      movimiento.usuarioRegistro?.usuarioLogin ||
      movimiento.usuario ||
      '-',
    estado: movimiento.estadoFisico || 'REGISTRADO',
  };
};

export const obtenerResumenInventario = async () => {
  const response = await axiosInstance.get('/inventario/dashboard');
  const data = extraerData(response) || {};

  const sinStock = data.productosSinStock ?? data.sinStock ?? 0;
  const stockBajo = data.productosStockBajo ?? data.stockBajo ?? 0;
  const disponible = data.productosDisponibles ?? data.disponible ?? 0;

  return {
    data: {
      totalProductos: data.totalProductos ?? 0,
      sinStock,
      stockBajo,
      disponible,
      productosSinStock: sinStock,
      productosStockBajo: stockBajo,
      productosDisponibles: disponible,
    },
  };
};

export const listarProductosInventario = async ({
  busqueda = '',
  estado = '',
  buscar = '',
  estadoStock = '',
} = {}) => {
  const response = await axiosInstance.get('/inventario/productos', {
    params: {
      buscar: busqueda || buscar || undefined,
      estadoStock: estado || estadoStock || undefined,
    },
  });

  const data = extraerData(response);
  const productos = Array.isArray(data) ? data : [];

  return {
    data: productos.map(mapearProducto),
  };
};

export const listarMovimientosInventario = async () => {
  const response = await axiosInstance.get('/movimiento-inventario/listar');
  const data = extraerData(response);
  const movimientos = Array.isArray(data) ? data : [];

  return {
    data: movimientos.map(mapearMovimiento),
  };
};

export const crearProductoInventario = async (producto) => {
  const payload = {
    codigoProducto: producto.codigo?.trim(),
    nombreProducto: producto.nombre?.trim(),
    descripcion: producto.descripcion?.trim() || null,
    unidadMedida: producto.unidad || producto.unidadMedida || 'UNIDAD',
    cantidadInicial: Number(producto.stockActual ?? producto.cantidadInicial ?? 0),
    stockMinimo: Number(producto.stockMinimo ?? 0),
  };

  const response = await axiosInstance.post('/inventario/productos', payload);
  const data = extraerData(response);

  return {
    data: mapearProducto(data),
    message: response?.data?.message || 'Producto registrado correctamente',
  };
};