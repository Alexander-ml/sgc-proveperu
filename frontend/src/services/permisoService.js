import axiosInstance from './axiosInstance';

export const listarPermisos = async () => {
  const response = await axiosInstance.get('/permisos');
  return response.data;
};