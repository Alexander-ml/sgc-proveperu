import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';

import {
  obtenerCajaActual,
  obtenerResumenCaja,
  listarMovimientosCaja,
  registrarEgresoCaja,
  cerrarCajaActual,
  abrirCajaActual,
} from '../../services/cajaService';

const CajaPage = () => {
  const [caja, setCaja] = useState(null);
  const [resumen, setResumen] = useState(null);
  const [movimientos, setMovimientos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [mostrarModalEgreso, setMostrarModalEgreso] = useState(false);

  const [nuevoEgreso, setNuevoEgreso] = useState({
    concepto: '',
    monto: '',
    metodo: 'Efectivo',
  });

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const formatearFechaLarga = (fecha) => {
    if (!fecha) return '-';

    const date = new Date(`${fecha}T00:00:00`);

    return date.toLocaleDateString('es-PE', {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  const cargarDatos = async () => {
    try {
      setCargando(true);

      const cajaResponse = await obtenerCajaActual();
      const resumenResponse = await obtenerResumenCaja();
      const movimientosResponse = await listarMovimientosCaja();

      setCaja(cajaResponse.data);
      setResumen(resumenResponse.data);
      setMovimientos(movimientosResponse.data || []);
    } catch (error) {
      console.error('Error cargando caja:', error);
      alert('No se pudo cargar caja/pagos');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const obtenerClaseMovimiento = (tipo) => {
    if (tipo === 'INGRESO') return 'text-success';
    if (tipo === 'EGRESO') return 'text-danger';
    return 'text-primary';
  };

  const obtenerBadgeMovimiento = (tipo) => {
    if (tipo === 'INGRESO') {
      return 'bg-success bg-opacity-10 text-success';
    }

    if (tipo === 'EGRESO') {
      return 'bg-danger bg-opacity-10 text-danger';
    }

    return 'bg-primary bg-opacity-10 text-primary';
  };

  const obtenerIconoMovimiento = (tipo) => {
    if (tipo === 'INGRESO') return 'bi-arrow-up-circle';
    if (tipo === 'EGRESO') return 'bi-arrow-down-circle';
    return 'bi-unlock';
  };

  const obtenerTextoMovimiento = (tipo) => {
    if (tipo === 'INGRESO') return 'Ingreso';
    if (tipo === 'EGRESO') return 'Egreso';
    return 'Apertura';
  };

  const obtenerSignoMonto = (tipo) => {
    if (tipo === 'INGRESO') return '+';
    if (tipo === 'EGRESO') return '-';
    return '+';
  };

  const handleChangeEgreso = (e) => {
    const { name, value } = e.target;

    setNuevoEgreso({
      ...nuevoEgreso,
      [name]: value,
    });
  };

  const cerrarModalEgreso = () => {
    setMostrarModalEgreso(false);

    setNuevoEgreso({
      concepto: '',
      monto: '',
      metodo: 'Efectivo',
    });
  };

  const handleRegistrarEgreso = async (e) => {
    e.preventDefault();

    if (Number(nuevoEgreso.monto) <= 0) {
      alert('El monto debe ser mayor a 0');
      return;
    }

    if (Number(nuevoEgreso.monto) > Number(resumen?.saldoActual || 0)) {
      alert('El egreso no puede ser mayor al saldo disponible');
      return;
    }

    try {
      await registrarEgresoCaja(nuevoEgreso);
      cerrarModalEgreso();
      cargarDatos();
    } catch (error) {
      console.error('Error registrando egreso:', error);
      alert('No se pudo registrar el egreso');
    }
  };

  const handleCerrarCaja = async () => {
    const confirmar = window.confirm('¿Seguro que deseas cerrar la caja?');

    if (!confirmar) return;

    try {
      await cerrarCajaActual();
      cargarDatos();
    } catch (error) {
      console.error('Error cerrando caja:', error);
      alert('No se pudo cerrar la caja');
    }
  };

  const handleAbrirCaja = async () => {
    try {
      await abrirCajaActual();
      cargarDatos();
    } catch (error) {
      console.error('Error abriendo caja:', error);
      alert('No se pudo abrir la caja');
    }
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando caja...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Caja / Pagos
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-wallet2 me-2 text-primary"></i>
          Caja / Pagos
        </h4>

        <p className="page-subtitle mb-0">Control de flujo de caja diario</p>
      </div>

      {caja?.estado === 'ABIERTA' ? (
        <div className="alert alert-success d-flex justify-content-between align-items-center mb-4">
          <div className="d-flex align-items-center gap-3">
            <div
              className="rounded-circle bg-success bg-opacity-10 text-success d-flex align-items-center justify-content-center"
              style={{ width: '52px', height: '52px' }}
            >
              <i className="bi bi-unlock fs-4"></i>
            </div>

            <div>
              <h6 className="fw-bold mb-1">
                Caja Abierta — {formatearFechaLarga(caja.fecha)}
              </h6>

              <p className="mb-0">
                Apertura: {formatearMoneda(caja.montoApertura)} — Abierta por{' '}
                {caja.responsableApertura} a las {caja.horaApertura}
              </p>
            </div>
          </div>

          <button
            type="button"
            className="btn btn-outline-success"
            onClick={handleCerrarCaja}
          >
            <i className="bi bi-lock me-2"></i>
            Cerrar Caja
          </button>
        </div>
      ) : (
        <div className="alert alert-warning d-flex justify-content-between align-items-center mb-4">
          <div>
            <h6 className="fw-bold mb-1">Caja Cerrada</h6>
            <p className="mb-0">No hay una caja abierta actualmente.</p>
          </div>

          <button
            type="button"
            className="btn btn-warning"
            onClick={handleAbrirCaja}
          >
            <i className="bi bi-unlock me-2"></i>
            Abrir Caja
          </button>
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Saldo Actual"
            value={formatearMoneda(resumen?.saldoActual ?? 0)}
            description="Balance calculado"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Ingresos"
            value={formatearMoneda(resumen?.totalIngresos ?? 0)}
            description={`${resumen?.cantidadIngresos ?? 0} transacciones`}
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Egresos"
            value={formatearMoneda(resumen?.totalEgresos ?? 0)}
            description={`${resumen?.cantidadEgresos ?? 0} transacciones`}
            color="danger"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Monto Apertura"
            value={formatearMoneda(resumen?.montoApertura ?? 0)}
            description="Inicio del día"
            color="info"
          />
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <h6 className="fw-bold mb-0">
          Movimientos del día — {movimientos.length} registros
        </h6>

        <button
          type="button"
          className="btn btn-outline-danger"
          onClick={() => setMostrarModalEgreso(true)}
          disabled={caja?.estado !== 'ABIERTA'}
        >
          <i className="bi bi-plus-lg me-2"></i>
          Registrar Egreso
        </button>
      </div>

      <div className="app-card">
        <div className="table-responsive">
          <table className="table align-middle mb-0 app-table">
            <thead>
              <tr>
                <th>Hora</th>
                <th>Tipo</th>
                <th>Concepto / Referencia</th>
                <th>Método</th>
                <th className="text-end">Monto</th>
                <th className="text-end">Saldo</th>
                <th>Responsable</th>
              </tr>
            </thead>

            <tbody>
              {movimientos.length === 0 ? (
                <tr>
                  <td colSpan="7" className="text-center py-5 text-muted">
                    <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                    No hay movimientos registrados.
                  </td>
                </tr>
              ) : (
                movimientos.map((movimiento) => (
                  <tr key={movimiento.idMovimiento}>
                    <td>
                      <i className="bi bi-clock me-2 text-muted"></i>
                      {movimiento.hora}
                    </td>

                    <td>
                      <span
                        className={`badge app-badge ${obtenerBadgeMovimiento(
                          movimiento.tipo
                        )}`}
                      >
                        <i
                          className={`bi ${obtenerIconoMovimiento(
                            movimiento.tipo
                          )} me-1`}
                        ></i>
                        {obtenerTextoMovimiento(movimiento.tipo)}
                      </span>
                    </td>

                    <td>
                      <strong>{movimiento.concepto}</strong>
                      {movimiento.referencia && (
                        <>
                          <br />
                          <span className="text-muted small">
                            {movimiento.referencia}
                          </span>
                        </>
                      )}
                    </td>

                    <td>{movimiento.metodo}</td>

                    <td
                      className={`text-end fw-bold ${obtenerClaseMovimiento(
                        movimiento.tipo
                      )}`}
                    >
                      {obtenerSignoMonto(movimiento.tipo)}
                      {formatearMoneda(movimiento.monto)}
                    </td>

                    <td className="text-end fw-bold">
                      {formatearMoneda(movimiento.saldo)}
                    </td>

                    <td>{movimiento.responsable}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>

        <div className="card-footer bg-white d-flex justify-content-between align-items-center flex-wrap gap-2">
          <span className="text-muted small">
            Actualizado al {new Date().toLocaleTimeString('es-PE')}
          </span>

          <div className="d-flex gap-3 flex-wrap">
            <strong className="text-success">
              Ingresos: {formatearMoneda(resumen?.totalIngresos ?? 0)}
            </strong>

            <strong className="text-danger">
              Egresos: {formatearMoneda(resumen?.totalEgresos ?? 0)}
            </strong>

            <strong className="text-primary">
              Saldo: {formatearMoneda(resumen?.saldoActual ?? 0)}
            </strong>
          </div>
        </div>
      </div>

      {mostrarModalEgreso && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-md modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleRegistrarEgreso}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-arrow-down-circle me-2 text-danger"></i>
                    Registrar Egreso
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalEgreso}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">Concepto / Motivo *</label>
                    <input
                      type="text"
                      name="concepto"
                      className="form-control app-input"
                      value={nuevoEgreso.concepto}
                      onChange={handleChangeEgreso}
                      placeholder="Ej: Pago de servicio de luz"
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Monto (S/) *</label>
                    <input
                      type="number"
                      name="monto"
                      className="form-control app-input"
                      value={nuevoEgreso.monto}
                      onChange={handleChangeEgreso}
                      placeholder="0.00"
                      min="0.01"
                      step="0.01"
                      required
                    />

                    <small className="text-muted">
                      Saldo disponible: {formatearMoneda(resumen?.saldoActual)}
                    </small>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Método de pago</label>
                    <select
                      name="metodo"
                      className="form-select app-select"
                      value={nuevoEgreso.metodo}
                      onChange={handleChangeEgreso}
                    >
                      <option value="Efectivo">Efectivo</option>
                      <option value="Transferencia">Transferencia</option>
                      <option value="Yape">Yape</option>
                      <option value="POS">POS</option>
                    </select>
                  </div>

                  <div className="alert alert-info mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Este egreso se guarda temporalmente. Cuando tengas endpoint,
                    se reemplazará por una llamada real al backend.
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalEgreso}
                  >
                    Cancelar
                  </button>

                  <button type="submit" className="btn btn-danger">
                    <i className="bi bi-save me-2"></i>
                    Registrar Egreso
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
};

export default CajaPage;