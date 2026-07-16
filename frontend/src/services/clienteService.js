import axiosInstance from './axiosInstance';

export const obtenerResumenClientes = async () => {
  const response = await axiosInstance.get('/clientes/dashboard');
  return response.data;
};

export const listarClientes = async ({ busqueda = '', tipoCliente = '' } = {}) => {
  const response = await axiosInstance.get('/clientes', {
    params: {
      buscar: busqueda || undefined,
      tipo: tipoCliente || undefined,
    },
  });

  return response.data;
};

export const crearCliente = async (cliente) => {
  const response = await axiosInstance.post('/clientes', cliente);
  return response.data;
};

export const editarCliente = async (idCliente, cliente) => {
  const response = await axiosInstance.put(`/clientes/${idCliente}`, cliente);
  return response.data;
};

export const activarCliente = async (idCliente) => {
  const response = await axiosInstance.patch(`/clientes/${idCliente}/activar`);
  return response.data;
};

export const desactivarCliente = async (idCliente) => {
  const response = await axiosInstance.patch(`/clientes/${idCliente}/desactivar`);
  return response.data;
};

export const obtenerClientePorId = async (idCliente) => {
  const response = await axiosInstance.get(`/clientes/${idCliente}`);
  return response.data;
};

export const listarClientesConHistorial = async ({ busqueda = '' } = {}) => {
  const response = await axiosInstance.get('/clientes/historial', {
    params: {
      buscar: busqueda || undefined,
    },
  });

  return response.data;
};

export const obtenerHistorialComprasCliente = async (idCliente) => {
  const response = await axiosInstance.get(
    `/clientes/${idCliente}/historial-compras`
  );

  return response.data;
};