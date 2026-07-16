import { useEffect, useMemo, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';

import {
  CAJA_ID_DEFAULT,
  abrirCaja,
  cerrarCaja,
  listarMovimientosCaja,
  obtenerDashboardCaja,
  registrarEgresoCaja,
} from '../../services/cajaService';

import { obtenerOpcionesCompra } from '../../services/comprasService';

const METODOS_PAGO_FALLBACK = [
  { idMetodoPago: 1, nombreMetodoPago: 'Efectivo' },
  { idMetodoPago: 2, nombreMetodoPago: 'Transferencia' },
  { idMetodoPago: 3, nombreMetodoPago: 'Yape' },
  { idMetodoPago: 4, nombreMetodoPago: 'POS' },
];

const estadoCajaCerrada = {
  idCaja: CAJA_ID_DEFAULT,
  nombreCaja: 'Caja Principal',
  estadoCaja: 'CERRADA',
  montoApertura: 0,
  saldoActual: 0,
  totalIngresos: 0,
  totalEgresos: 0,
  cantidadMovimientos: 0,
  abiertaPor: '',
  fechaHoraApertura: null,
};

const CajaPage = () => {
  const [dashboard, setDashboard] = useState(null);
  const [movimientos, setMovimientos] = useState([]);
  const [metodosPago, setMetodosPago] = useState(METODOS_PAGO_FALLBACK);
  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [mensajeError, setMensajeError] = useState('');

  const [modalEgreso, setModalEgreso] = useState(false);
  const [modalAbrir, setModalAbrir] = useState(false);
  const [modalCerrar, setModalCerrar] = useState(false);

  const [egresoForm, setEgresoForm] = useState({
    idMetodoPago: '',
    monto: '',
    descripcion: '',
  });

  const [abrirForm, setAbrirForm] = useState({
    montoInicial: '500',
  });

  const [cerrarForm, setCerrarForm] = useState({
    saldoReal: '',
  });

  const obtenerData = (response) => response?.data ?? response;

  const extraerMensajeError = (error) =>
    error?.response?.data?.message ||
    error?.response?.data?.mensaje ||
    error?.message ||
    'No se pudo completar la operación.';

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const formatearFechaHora = (fecha) => {
    if (!fecha) return '-';

    return new Date(fecha).toLocaleString('es-PE', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatearHora = (fecha) => {
    if (!fecha) return '-';

    return new Date(fecha).toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const cargarMetodosPago = async () => {
    try {
      const response = await obtenerOpcionesCompra();
      const data = obtenerData(response);
      const metodos = data?.metodosPago || [];

      if (metodos.length > 0) {
        setMetodosPago(metodos);
        setEgresoForm((prev) => ({
          ...prev,
          idMetodoPago: String(metodos[0].idMetodoPago),
        }));
      } else {
        setEgresoForm((prev) => ({
          ...prev,
          idMetodoPago: String(METODOS_PAGO_FALLBACK[0].idMetodoPago),
        }));
      }
    } catch {
      setMetodosPago(METODOS_PAGO_FALLBACK);
      setEgresoForm((prev) => ({
        ...prev,
        idMetodoPago: String(METODOS_PAGO_FALLBACK[0].idMetodoPago),
      }));
    }
  };

  const cargarCaja = async () => {
    try {
      setCargando(true);
      setMensajeError('');

      const [dashboardResponse, movimientosResponse] = await Promise.all([
        obtenerDashboardCaja(CAJA_ID_DEFAULT),
        listarMovimientosCaja(CAJA_ID_DEFAULT),
      ]);

      setDashboard(obtenerData(dashboardResponse));
      setMovimientos(obtenerData(movimientosResponse) || []);
    } catch (error) {
      setDashboard(estadoCajaCerrada);
      setMovimientos([]);
      setMensajeError(extraerMensajeError(error));
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarMetodosPago();
    cargarCaja();
  }, []);

  useEffect(() => {
    if (dashboard?.saldoActual !== undefined) {
      setCerrarForm({
        saldoReal: String(Number(dashboard.saldoActual || 0).toFixed(2)),
      });
    }
  }, [dashboard]);

  const cajaAbierta = dashboard?.estadoCaja === 'ABIERTA';

  const movimientosConSaldo = useMemo(() => {
    let saldo = Number(dashboard?.montoApertura || 0);

    return [...movimientos]
      .sort(
        (a, b) =>
          new Date(a.fechaHoraMovimiento) - new Date(b.fechaHoraMovimiento)
      )
      .map((movimiento) => {
        const monto = Number(movimiento.monto || 0);

        if (movimiento.tipoMovimiento === 'INGRESO') {
          saldo += monto;
        }

        if (movimiento.tipoMovimiento === 'EGRESO') {
          saldo -= monto;
        }

        return {
          ...movimiento,
          saldoCalculado: saldo,
        };
      })
      .reverse();
  }, [movimientos, dashboard]);

  const cantidadIngresos = movimientos.filter(
    (movimiento) => movimiento.tipoMovimiento === 'INGRESO'
  ).length;

  const cantidadEgresos = movimientos.filter(
    (movimiento) => movimiento.tipoMovimiento === 'EGRESO'
  ).length;

  const handleChangeEgreso = (event) => {
    const { name, value } = event.target;

    setEgresoForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleChangeAbrir = (event) => {
    const { name, value } = event.target;

    setAbrirForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleChangeCerrar = (event) => {
    const { name, value } = event.target;

    setCerrarForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const limpiarEgreso = () => {
    setEgresoForm({
      idMetodoPago: String(metodosPago[0]?.idMetodoPago || 1),
      monto: '',
      descripcion: '',
    });
  };

  const handleRegistrarEgreso = async (event) => {
    event.preventDefault();

    if (!cajaAbierta) {
      alert('La caja debe estar abierta para registrar egresos.');
      return;
    }

    if (!egresoForm.idMetodoPago) {
      alert('Selecciona un método de pago.');
      return;
    }

    if (Number(egresoForm.monto) <= 0) {
      alert('El monto debe ser mayor a 0.');
      return;
    }

    if (Number(egresoForm.monto) > Number(dashboard?.saldoActual || 0)) {
      alert('El egreso no puede ser mayor al saldo disponible.');
      return;
    }

    try {
      setGuardando(true);

      await registrarEgresoCaja(dashboard.idCaja, egresoForm);

      setModalEgreso(false);
      limpiarEgreso();
      await cargarCaja();
    } catch (error) {
      alert(extraerMensajeError(error));
    } finally {
      setGuardando(false);
    }
  };

  const handleAbrirCaja = async (event) => {
    event.preventDefault();

    if (Number(abrirForm.montoInicial) < 0) {
      alert('El monto inicial no puede ser negativo.');
      return;
    }

    try {
      setGuardando(true);

      await abrirCaja(dashboard?.idCaja || CAJA_ID_DEFAULT, abrirForm.montoInicial);

      setModalAbrir(false);
      await cargarCaja();
    } catch (error) {
      alert(extraerMensajeError(error));
    } finally {
      setGuardando(false);
    }
  };

  const handleCerrarCaja = async (event) => {
    event.preventDefault();

    if (Number(cerrarForm.saldoReal) < 0) {
      alert('El saldo real no puede ser negativo.');
      return;
    }

    try {
      setGuardando(true);

      await cerrarCaja(dashboard.idCaja, cerrarForm.saldoReal);

      setModalCerrar(false);
      await cargarCaja();
    } catch (error) {
      alert(extraerMensajeError(error));
    } finally {
      setGuardando(false);
    }
  };

  const obtenerClaseTipo = (tipo) => {
    if (tipo === 'INGRESO') return 'cash-pill-success';
    if (tipo === 'EGRESO') return 'cash-pill-danger';
    return 'cash-pill-neutral';
  };

  const obtenerIconoTipo = (tipo) => {
    if (tipo === 'INGRESO') return 'bi-arrow-down-left-circle';
    if (tipo === 'EGRESO') return 'bi-arrow-up-right-circle';
    return 'bi-dot';
  };

  const obtenerReferencia = (movimiento) => {
    if (movimiento.idVenta) return `Venta #${movimiento.idVenta}`;
    if (movimiento.idCompra) return `Compra #${movimiento.idCompra}`;
    return '';
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="cash-loading">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando caja...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="cash-page">
        <div className="cash-header">
          <div>
            <h1>Caja / Pagos</h1>
            <p>Control de flujo de caja diario</p>
          </div>

          <div className="cash-header-actions">
            <button
              type="button"
              className="btn btn-outline-danger"
              disabled={!cajaAbierta}
              onClick={() => setModalEgreso(true)}
            >
              <i className="bi bi-plus-lg me-2"></i>
              Registrar Egreso
            </button>

            {cajaAbierta ? (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => setModalCerrar(true)}
              >
                <i className="bi bi-lock me-2"></i>
                Cerrar Caja
              </button>
            ) : (
              <button
                type="button"
                className="btn btn-primary"
                onClick={() => setModalAbrir(true)}
              >
                <i className="bi bi-unlock me-2"></i>
                Abrir Caja
              </button>
            )}
          </div>
        </div>

        {mensajeError && !cajaAbierta && (
          <div className="cash-warning">
            <i className="bi bi-info-circle"></i>
            <span>{mensajeError}</span>
          </div>
        )}

        <div
          className={`cash-status ${cajaAbierta ? 'is-open' : 'is-closed'}`}
        >
          <div className="cash-status-icon">
            <i className={`bi ${cajaAbierta ? 'bi-unlock' : 'bi-lock'}`}></i>
          </div>

          <div>
            <h5>{cajaAbierta ? 'Caja abierta' : 'Caja cerrada'}</h5>
            <p>
              {cajaAbierta
                ? `${dashboard.nombreCaja} - Apertura: ${formatearFechaHora(
                    dashboard.fechaHoraApertura
                  )}`
                : 'No hay una apertura activa para esta caja.'}
            </p>
          </div>

          {cajaAbierta && (
            <span className="cash-status-user">
              Abierta por: {dashboard.abiertaPor || '-'}
            </span>
          )}
        </div>

        <div className="row g-3 mb-4">
          <div className="col-12 col-md-6 col-xl-3">
            <SummaryCard
              title="Saldo actual"
              value={formatearMoneda(dashboard?.saldoActual)}
              description="Disponible en caja"
              color="primary"
            />
          </div>

          <div className="col-12 col-md-6 col-xl-3">
            <SummaryCard
              title="Ingresos"
              value={formatearMoneda(dashboard?.totalIngresos)}
              description={`${cantidadIngresos} movimiento(s)`}
              color="success"
            />
          </div>

          <div className="col-12 col-md-6 col-xl-3">
            <SummaryCard
              title="Egresos"
              value={formatearMoneda(dashboard?.totalEgresos)}
              description={`${cantidadEgresos} movimiento(s)`}
              color="danger"
            />
          </div>

          <div className="col-12 col-md-6 col-xl-3">
            <SummaryCard
              title="Monto apertura"
              value={formatearMoneda(dashboard?.montoApertura)}
              description={`${dashboard?.cantidadMovimientos || 0} movimiento(s)`}
              color="info"
            />
          </div>
        </div>

        <div className="cash-table-card">
          <div className="cash-table-header">
            <div>
              <h5>Movimientos de Caja</h5>
              <p>Ingresos, egresos y operaciones vinculadas</p>
            </div>
          </div>

          <div className="table-responsive">
            <table className="table align-middle mb-0 cash-table">
              <thead>
                <tr>
                  <th>Hora</th>
                  <th>Tipo</th>
                  <th>Descripción</th>
                  <th>Método</th>
                  <th>Responsable</th>
                  <th className="text-end">Monto</th>
                  <th className="text-end">Saldo</th>
                </tr>
              </thead>

              <tbody>
                {movimientosConSaldo.length === 0 ? (
                  <tr>
                    <td colSpan="7" className="text-center py-5 text-muted">
                      <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                      No hay movimientos registrados.
                    </td>
                  </tr>
                ) : (
                  movimientosConSaldo.map((movimiento) => {
                    const referencia = obtenerReferencia(movimiento);
                    const esEgreso = movimiento.tipoMovimiento === 'EGRESO';

                    return (
                      <tr key={movimiento.idMovimientoCaja}>
                        <td>{formatearHora(movimiento.fechaHoraMovimiento)}</td>

                        <td>
                          <span
                            className={`cash-pill ${obtenerClaseTipo(
                              movimiento.tipoMovimiento
                            )}`}
                          >
                            <i
                              className={`bi ${obtenerIconoTipo(
                                movimiento.tipoMovimiento
                              )}`}
                            ></i>
                            {movimiento.tipoMovimiento}
                          </span>
                        </td>

                        <td>
                          <strong>
                            {movimiento.descripcion || 'Movimiento de caja'}
                          </strong>

                          {referencia && <small>{referencia}</small>}
                        </td>

                        <td>{movimiento.metodoPago || '-'}</td>
                        <td>{movimiento.registradoPor || '-'}</td>

                        <td
                          className={`text-end fw-bold ${
                            esEgreso ? 'text-danger' : 'text-success'
                          }`}
                        >
                          {esEgreso ? '-' : '+'}
                          {formatearMoneda(movimiento.monto)}
                        </td>

                        <td className="text-end fw-bold">
                          {formatearMoneda(movimiento.saldoCalculado)}
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {modalEgreso && (
        <div className="cash-modal-overlay">
          <div className="cash-modal">
            <form onSubmit={handleRegistrarEgreso}>
              <div className="cash-modal-header">
                <h5>
                  <i className="bi bi-arrow-up-right-circle text-danger"></i>
                  Registrar Egreso
                </h5>

                <button
                  type="button"
                  className="cash-modal-close"
                  onClick={() => setModalEgreso(false)}
                >
                  <i className="bi bi-x-lg"></i>
                </button>
              </div>

              <div className="cash-modal-body">
                <label>Método de pago *</label>
                <select
                  name="idMetodoPago"
                  value={egresoForm.idMetodoPago}
                  onChange={handleChangeEgreso}
                  required
                >
                  {metodosPago.map((metodo) => (
                    <option
                      key={metodo.idMetodoPago}
                      value={metodo.idMetodoPago}
                    >
                      {metodo.nombreMetodoPago}
                    </option>
                  ))}
                </select>

                <label>Monto *</label>
                <input
                  type="number"
                  name="monto"
                  value={egresoForm.monto}
                  onChange={handleChangeEgreso}
                  min="0.01"
                  step="0.01"
                  placeholder="Ej: 85.00"
                  required
                />

                <small>
                  Saldo disponible: {formatearMoneda(dashboard?.saldoActual)}
                </small>

                <label>Descripción</label>
                <textarea
                  name="descripcion"
                  value={egresoForm.descripcion}
                  onChange={handleChangeEgreso}
                  rows="3"
                  maxLength="300"
                  placeholder="Ej: Pago de servicio de luz"
                />
              </div>

              <div className="cash-modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setModalEgreso(false)}
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  className="btn btn-danger"
                  disabled={guardando}
                >
                  Registrar Egreso
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalAbrir && (
        <div className="cash-modal-overlay">
          <div className="cash-modal">
            <form onSubmit={handleAbrirCaja}>
              <div className="cash-modal-header">
                <h5>
                  <i className="bi bi-unlock text-primary"></i>
                  Abrir Caja
                </h5>

                <button
                  type="button"
                  className="cash-modal-close"
                  onClick={() => setModalAbrir(false)}
                >
                  <i className="bi bi-x-lg"></i>
                </button>
              </div>

              <div className="cash-modal-body">
                <label>Monto inicial *</label>
                <input
                  type="number"
                  name="montoInicial"
                  value={abrirForm.montoInicial}
                  onChange={handleChangeAbrir}
                  min="0"
                  step="0.01"
                  required
                />
              </div>

              <div className="cash-modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setModalAbrir(false)}
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  className="btn btn-primary"
                  disabled={guardando}
                >
                  Abrir Caja
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {modalCerrar && (
        <div className="cash-modal-overlay">
          <div className="cash-modal">
            <form onSubmit={handleCerrarCaja}>
              <div className="cash-modal-header">
                <h5>
                  <i className="bi bi-lock text-secondary"></i>
                  Cerrar Caja
                </h5>

                <button
                  type="button"
                  className="cash-modal-close"
                  onClick={() => setModalCerrar(false)}
                >
                  <i className="bi bi-x-lg"></i>
                </button>
              </div>

              <div className="cash-modal-body">
                <div className="cash-close-summary">
                  <span>Saldo teórico</span>
                  <strong>{formatearMoneda(dashboard?.saldoActual)}</strong>
                </div>

                <label>Saldo real contado *</label>
                <input
                  type="number"
                  name="saldoReal"
                  value={cerrarForm.saldoReal}
                  onChange={handleChangeCerrar}
                  min="0"
                  step="0.01"
                  required
                />
              </div>

              <div className="cash-modal-footer">
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => setModalCerrar(false)}
                >
                  Cancelar
                </button>

                <button
                  type="submit"
                  className="btn btn-dark"
                  disabled={guardando}
                >
                  Cerrar Caja
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </MainLayout>
  );
};

export default CajaPage;