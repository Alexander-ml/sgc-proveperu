import axiosInstance from './axiosInstance';

export const obtenerResumenCompras = async () => {
  const response = await axiosInstance.get('/compras/dashboard');
  return response.data;
};

export const obtenerOpcionesCompra = async () => {
  const response = await axiosInstance.get('/compras/opciones');
  return response.data;
};

export const listarCompras = async ({ busqueda = '', estado = '' } = {}) => {
  const response = await axiosInstance.get('/compras', {
    params: {
      buscar: busqueda || undefined,
      estado: estado || undefined,
    },
  });

  return response.data;
};

export const listarProveedores = async () => {
  const response = await axiosInstance.get('/compras/proveedores');
  return response.data;
};

export const listarComprasPorProveedor = async (idProveedor) => {
  const response = await axiosInstance.get(
    `/compras/proveedores/${idProveedor}/compras`
  );

  return response.data;
};

export const crearCompra = async (compra) => {
  const response = await axiosInstance.post('/compras', compra);
  return response.data;
};

export const crearProveedor = async (proveedor) => {
  const response = await axiosInstance.post('/compras/proveedores', proveedor);
  return response.data;
};

export const obtenerDetalleCompra = async (idCompra) => {
  const response = await axiosInstance.get(`/compras/${idCompra}`);
  return response.data;
};

export const cambiarEstadoCompra = async (idCompra, estado) => {
  const response = await axiosInstance.patch(`/compras/${idCompra}/estado`, {
    estado,
  });

  return response.data;
};