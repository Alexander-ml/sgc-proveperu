import axiosInstance from './axiosInstance';

export const listarRoles = async () => {
  const response = await axiosInstance.get('/roles');
  return response.data;
};

export const obtenerRolPorId = async (id) => {
  const response = await axiosInstance.get(`/roles/${id}`);
  return response.data;
};

export const actualizarPermisosRol = async (id, request) => {
  const response = await axiosInstance.patch(`/roles/${id}/permisos`, request);
  return response.data;
};