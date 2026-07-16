import axiosInstance from './axiosInstance';

export const listarVentas = async ({
  q = '',
  clienteId = '',
  numeroVenta = '',
  tipoComprobante = '',
  estadoVenta = '',
  metodoPagoId = '',
  fechaInicio = '',
  fechaFin = '',
  page = 0,
  size = 20,
  sort = 'fechaHoraVenta',
  direction = 'DESC',
}) => {
  const response = await axiosInstance.get('/ventas', {
    params: {
      q: q || undefined,
      clienteId: clienteId || undefined,
      numeroVenta: numeroVenta || undefined,
      tipoComprobante: tipoComprobante || undefined,
      estadoVenta: estadoVenta || undefined,
      metodoPagoId: metodoPagoId || undefined,
      fechaInicio: fechaInicio || undefined,
      fechaFin: fechaFin || undefined,
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