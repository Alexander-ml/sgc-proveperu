import axiosInstance from './axiosInstance';

export const loginRequest = async (credenciales) => {
  const response = await axiosInstance.post('/auth/login', credenciales);
  return response.data;
};