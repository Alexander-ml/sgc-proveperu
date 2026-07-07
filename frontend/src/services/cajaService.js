const CAJA_MOCK = {
  idCaja: 1,
  fecha: '2026-06-25',
  estado: 'ABIERTA',
  montoApertura: 500,
  responsableApertura: 'Iris Arroyo',
  horaApertura: '08:00',
};

const MOVIMIENTOS_MOCK = [
  {
    idMovimiento: 1,
    hora: '08:00',
    tipo: 'APERTURA',
    concepto: 'Apertura de caja',
    referencia: '',
    metodo: 'Efectivo',
    monto: 500,
    saldo: 500,
    responsable: 'Iris Arroyo',
  },
  {
    idMovimiento: 2,
    hora: '09:15',
    tipo: 'INGRESO',
    concepto: 'Venta V-2026-001',
    referencia: 'Ref: V-2026-001',
    metodo: 'Transferencia',
    monto: 890,
    saldo: 1390,
    responsable: 'Iris Arroyo',
  },
  {
    idMovimiento: 3,
    hora: '10:30',
    tipo: 'INGRESO',
    concepto: 'Venta V-2026-002',
    referencia: 'Ref: V-2026-002',
    metodo: 'Efectivo',
    monto: 234,
    saldo: 1624,
    responsable: 'Iris Arroyo',
  },
  {
    idMovimiento: 4,
    hora: '11:00',
    tipo: 'EGRESO',
    concepto: 'Pago de servicio de luz',
    referencia: '',
    metodo: 'Efectivo',
    monto: 85,
    saldo: 1539,
    responsable: 'César Medina',
  },
  {
    idMovimiento: 5,
    hora: '12:30',
    tipo: 'EGRESO',
    concepto: 'Compra de útiles de oficina',
    referencia: '',
    metodo: 'Efectivo',
    monto: 45,
    saldo: 1494,
    responsable: 'Iris Arroyo',
  },
];

const obtenerCajaGuardada = () => {
  const cajaGuardada = localStorage.getItem('caja_mock');

  if (!cajaGuardada) {
    localStorage.setItem('caja_mock', JSON.stringify(CAJA_MOCK));
    return CAJA_MOCK;
  }

  return JSON.parse(cajaGuardada);
};

const guardarCaja = (caja) => {
  localStorage.setItem('caja_mock', JSON.stringify(caja));
};

const obtenerMovimientosGuardados = () => {
  const movimientosGuardados = localStorage.getItem('movimientos_caja_mock');

  if (!movimientosGuardados) {
    localStorage.setItem(
      'movimientos_caja_mock',
      JSON.stringify(MOVIMIENTOS_MOCK)
    );
    return MOVIMIENTOS_MOCK;
  }

  return JSON.parse(movimientosGuardados);
};

const guardarMovimientos = (movimientos) => {
  localStorage.setItem('movimientos_caja_mock', JSON.stringify(movimientos));
};

const calcularResumen = (movimientos) => {
  const ingresos = movimientos
    .filter((movimiento) => movimiento.tipo === 'INGRESO')
    .reduce((total, movimiento) => total + Number(movimiento.monto || 0), 0);

  const egresos = movimientos
    .filter((movimiento) => movimiento.tipo === 'EGRESO')
    .reduce((total, movimiento) => total + Number(movimiento.monto || 0), 0);

  const apertura = movimientos
    .filter((movimiento) => movimiento.tipo === 'APERTURA')
    .reduce((total, movimiento) => total + Number(movimiento.monto || 0), 0);

  const saldoActual = apertura + ingresos - egresos;

  const totalIngresos = movimientos.filter(
    (movimiento) => movimiento.tipo === 'INGRESO'
  ).length;

  const totalEgresos = movimientos.filter(
    (movimiento) => movimiento.tipo === 'EGRESO'
  ).length;

  return {
    saldoActual,
    totalIngresos: ingresos,
    totalEgresos: egresos,
    montoApertura: apertura,
    cantidadIngresos: totalIngresos,
    cantidadEgresos: totalEgresos,
  };
};

export const obtenerCajaActual = async () => {
  const caja = obtenerCajaGuardada();

  return {
    data: caja,
  };
};

export const obtenerResumenCaja = async () => {
  const movimientos = obtenerMovimientosGuardados();
  const resumen = calcularResumen(movimientos);

  return {
    data: resumen,
  };
};

export const listarMovimientosCaja = async () => {
  const movimientos = obtenerMovimientosGuardados();

  return {
    data: movimientos,
  };
};

export const registrarEgresoCaja = async (egreso) => {
  const movimientos = obtenerMovimientosGuardados();

  const resumenActual = calcularResumen(movimientos);
  const monto = Number(egreso.monto || 0);
  const nuevoSaldo = resumenActual.saldoActual - monto;

  const ahora = new Date();

  const nuevoMovimiento = {
    idMovimiento: Date.now(),
    hora: ahora.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    }),
    tipo: 'EGRESO',
    concepto: egreso.concepto,
    referencia: '',
    metodo: egreso.metodo || 'Efectivo',
    monto,
    saldo: nuevoSaldo,
    responsable: 'César Medina',
  };

  const actualizados = [...movimientos, nuevoMovimiento];

  guardarMovimientos(actualizados);

  return {
    data: nuevoMovimiento,
    message: 'Egreso registrado temporalmente',
  };
};

export const cerrarCajaActual = async () => {
  const caja = obtenerCajaGuardada();

  const cajaCerrada = {
    ...caja,
    estado: 'CERRADA',
    horaCierre: new Date().toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    }),
  };

  guardarCaja(cajaCerrada);

  return {
    data: cajaCerrada,
    message: 'Caja cerrada temporalmente',
  };
};

export const abrirCajaActual = async () => {
  const cajaAbierta = {
    idCaja: Date.now(),
    fecha: new Date().toISOString().slice(0, 10),
    estado: 'ABIERTA',
    montoApertura: 500,
    responsableApertura: 'Iris Arroyo',
    horaApertura: '08:00',
  };

  guardarCaja(cajaAbierta);
  guardarMovimientos(MOVIMIENTOS_MOCK);

  return {
    data: cajaAbierta,
    message: 'Caja abierta temporalmente',
  };
};