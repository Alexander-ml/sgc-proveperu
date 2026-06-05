/* Instancia central de Axios. Toda llamada HTTP del sistema usa esta instancia.
Se configura con la URL base. Los interceptores de JWT se agregan en siguiente fase*/
import axios from 'axios';
import { API_BASE_URL } from '../utils/constants/apiConstants';

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para enviar el token JWT en cada petición
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para controlar sesión expirada o token inválido
axiosInstance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('rol');
      localStorage.removeItem('usuario');
      window.location.href = '/login';
    }

    return Promise.reject(error);
  }
);

export default axiosInstance;