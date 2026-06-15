import axiosInstance from './axiosInstance';

export const listarVentas = async ({
  q = '',
  estadoVenta = '',
  metodoPagoId = '',
  page = 0,
  size = 20,
  sort = 'fechaHoraVenta',
  direction = 'DESC',
}) => {
  const response = await axiosInstance.get('/ventas', {
    params: {
      q: q || undefined,
      estadoVenta: estadoVenta || undefined,
      metodoPagoId: metodoPagoId || undefined,
      page,
      size,
      sort,
      direction,
    },
  });

  return response.data;
};

export const obtenerDetalleVenta = async (idVenta) => {
  const response = await axiosInstance.get(`/detalleventas/${idVenta}`);
  return response.data;
};