import axiosInstance from './axiosInstance';

export const loginRequest = async (credenciales) => {
  console.log('BASE URL:', axiosInstance.defaults.baseURL);
  console.log('URL FINAL:', `${axiosInstance.defaults.baseURL}/auth/login`);
  console.log('DATOS:', credenciales);

  const response = await axiosInstance.post('/auth/login', credenciales);
  return response.data;
};