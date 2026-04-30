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

export default axiosInstance;