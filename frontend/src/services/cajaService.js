import axiosInstance from './axiosInstance';

export const CAJA_ID_DEFAULT = 1;

export const obtenerDashboardCaja = async (idCaja = CAJA_ID_DEFAULT) => {
  const response = await axiosInstance.get('/caja/dashboard', {
    params: { idCaja },
  });

  return response.data;
};

export const listarMovimientosCaja = async (idCaja = CAJA_ID_DEFAULT) => {
  const response = await axiosInstance.get('/caja/movimientos', {
    params: { idCaja },
  });

  return response.data;
};

export const registrarEgresoCaja = async (
  idCaja = CAJA_ID_DEFAULT,
  egreso
) => {
  const response = await axiosInstance.post(`/caja/${idCaja}/egresos`, {
    idMetodoPago: Number(egreso.idMetodoPago),
    monto: Number(egreso.monto),
    descripcion: egreso.descripcion?.trim() || null,
  });

  return response.data;
};

export const cerrarCaja = async (idCaja = CAJA_ID_DEFAULT, saldoReal) => {
  const response = await axiosInstance.post(`/caja/${idCaja}/cerrar`, {
    saldoReal: Number(saldoReal),
  });

  return response.data;
};

export const abrirCaja = async (idCaja = CAJA_ID_DEFAULT, montoInicial) => {
  const response = await axiosInstance.post(`/caja/${idCaja}/abrir`, {
    montoInicial: Number(montoInicial),
  });

  return response.data;
};

export const obtenerResumenCaja = obtenerDashboardCaja;
export const obtenerCajaActual = obtenerDashboardCaja;

export const cerrarCajaActual = async (
  saldoReal = 0,
  idCaja = CAJA_ID_DEFAULT
) => cerrarCaja(idCaja, saldoReal);

export const abrirCajaActual = async (
  montoInicial = 0,
  idCaja = CAJA_ID_DEFAULT
) => abrirCaja(idCaja, montoInicial);