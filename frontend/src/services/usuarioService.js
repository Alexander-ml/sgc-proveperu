import axiosInstance from './axiosInstance';

export const obtenerDashboardUsuarios = async () => {
  const response = await axiosInstance.get('/usuarios/dashboard');
  return response.data;
};

export const listarUsuarios = async (nombre = '') => {
  const response = await axiosInstance.get('/usuarios', {
    params: nombre ? { nombre } : {},
  });

  return response.data;
};

export const crearUsuario = async (usuario) => {
  const response = await axiosInstance.post('/usuarios', usuario);
  return response.data;
};

export const obtenerUsuarioPorId = async (id) => {
  const response = await axiosInstance.get(`/usuarios/${id}`);
  return response.data;
};

export const editarUsuario = async (id, usuario) => {
  const response = await axiosInstance.put(`/usuarios/${id}`, usuario);
  return response.data;
};

export const suspenderUsuario = async (id) => {
  const response = await axiosInstance.patch(`/usuarios/${id}/suspender`);
  return response.data;
};

export const activarUsuario = async (id) => {
  const response = await axiosInstance.patch(`/usuarios/${id}/activar`);
  return response.data;
};