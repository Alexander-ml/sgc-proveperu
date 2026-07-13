import { useCallback, useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  listarClientesConHistorial,
  obtenerHistorialComprasCliente,
} from '../../services/clienteService';

const COLORES_AVATAR = [
  '#155dfc',
  '#8b5cf6',
  '#059669',
  '#f97316',
  '#db2777',
  '#4f46e5',
  '#0891b2',
];

const HistorialComprasPage = () => {
  const [clientes, setClientes] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [busquedaAplicada, setBusquedaAplicada] = useState('');
  const [clienteSeleccionadoId, setClienteSeleccionadoId] = useState(null);
  const [historial, setHistorial] = useState(null);
  const [cargandoClientes, setCargandoClientes] = useState(true);
  const [cargandoHistorial, setCargandoHistorial] = useState(false);
  const [error, setError] = useState('');

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return '-';

    return new Date(fecha).toLocaleDateString('es-PE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const obtenerIniciales = (cliente) => {
    if (cliente?.iniciales) return cliente.iniciales;

    const nombre = cliente?.nombreCliente || 'Cliente';
    const partes = nombre.trim().split(' ');

    if (partes.length === 1) return partes[0].slice(0, 2).toUpperCase();

    return `${partes[0]?.[0] || ''}${partes[1]?.[0] || ''}`.toUpperCase();
  };

  const obtenerColorAvatar = (index) => {
    return COLORES_AVATAR[index % COLORES_AVATAR.length];
  };

  const extraerMensajeError = (err, fallback) => {
    return err?.response?.data?.message || fallback;
  };

  const cargarClientes = useCallback(async () => {
    try {
      setCargandoClientes(true);
      setError('');

      const response = await listarClientesConHistorial({
        busqueda: busquedaAplicada,
      });

      setClientes(response.data || []);
    } catch (err) {
      console.error('Error cargando historial:', err);
      setError(
        extraerMensajeError(err, 'No se pudo cargar el historial de clientes.')
      );
      setClientes([]);
    } finally {
      setCargandoClientes(false);
    }
  }, [busquedaAplicada]);

  useEffect(() => {
    cargarClientes();
  }, [cargarClientes]);

  const buscarClientes = (e) => {
    e.preventDefault();
    setBusquedaAplicada(busqueda.trim());
  };

  const seleccionarCliente = async (cliente) => {
    try {
      setClienteSeleccionadoId(cliente.idCliente);
      setCargandoHistorial(true);
      setHistorial(null);

      const response = await obtenerHistorialComprasCliente(cliente.idCliente);

      setHistorial(response.data);
    } catch (err) {
      console.error('Error cargando historial de cliente:', err);
      alert(extraerMensajeError(err, 'No se pudo cargar el historial.'));
      setClienteSeleccionadoId(null);
    } finally {
      setCargandoHistorial(false);
    }
  };

  return (
    <MainLayout>
      <div className="purchase-history-page">
        <div className="purchase-history-title">
          <h2>Historial de Compras</h2>
          <p>Historial de compras por cliente</p>
        </div>

        {error && (
          <div className="alert alert-danger">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {error}
          </div>
        )}

        <div className="purchase-history-layout">
          <aside className="purchase-history-sidebar">
            <form onSubmit={buscarClientes}>
              <div className="purchase-history-search">
                <i className="bi bi-search"></i>
                <input
                  type="text"
                  placeholder="Buscar cliente..."
                  value={busqueda}
                  onChange={(e) => setBusqueda(e.target.value)}
                />
              </div>
            </form>

            <p className="purchase-history-count">
              {clientes.length} cliente(s) con historial
            </p>

            <div className="purchase-history-client-list">
              {cargandoClientes ? (
                <div className="d-flex align-items-center gap-2 text-muted py-3">
                  <div className="spinner-border spinner-border-sm text-primary" />
                  <span>Cargando clientes...</span>
                </div>
              ) : clientes.length === 0 ? (
                <div className="purchase-history-empty-list">
                  No se encontraron clientes con historial.
                </div>
              ) : (
                clientes.map((cliente, index) => (
                  <button
                    type="button"
                    key={cliente.idCliente}
                    className={`purchase-history-client ${
                      clienteSeleccionadoId === cliente.idCliente ? 'active' : ''
                    }`}
                    onClick={() => seleccionarCliente(cliente)}
                  >
                    <span
                      className="purchase-history-avatar"
                      style={{ background: obtenerColorAvatar(index) }}
                    >
                      {obtenerIniciales(cliente)}
                    </span>

                    <span className="purchase-history-client-info">
                      <strong>{cliente.nombreCliente}</strong>
                      <small>
                        {cliente.numeroCompras ?? 0} compras
                        <b>{formatearMoneda(cliente.montoTotal)}</b>
                      </small>
                    </span>

                    <i className="bi bi-chevron-right"></i>
                  </button>
                ))
              )}
            </div>
          </aside>

          <section className="purchase-history-content">
            {cargandoHistorial ? (
              <div className="purchase-history-placeholder">
                <div className="spinner-border text-primary mb-3" />
                <h5>Cargando historial</h5>
                <p>Estamos consultando las compras del cliente seleccionado.</p>
              </div>
            ) : !historial ? (
              <div className="purchase-history-placeholder">
                <i className="bi bi-clipboard-check"></i>
                <h5>Selecciona un cliente</h5>
                <p>
                  Elige un cliente de la lista para ver su historial completo de
                  compras.
                </p>
              </div>
            ) : (
              <DetalleHistorial
                historial={historial}
                formatearMoneda={formatearMoneda}
                formatearFecha={formatearFecha}
              />
            )}
          </section>
        </div>
      </div>
    </MainLayout>
  );
};

const DetalleHistorial = ({ historial, formatearMoneda, formatearFecha }) => {
  const obtenerInicialesDetalle = () => {
    if (historial.iniciales) return historial.iniciales;

    const partes = (historial.nombreCliente || 'Cliente').trim().split(' ');

    if (partes.length === 1) return partes[0].slice(0, 2).toUpperCase();

    return `${partes[0]?.[0] || ''}${partes[1]?.[0] || ''}`.toUpperCase();
  };

  return (
    <div className="purchase-history-detail">
      <div className="purchase-history-summary-card">
        <div className="purchase-history-summary-main">
          <span className="purchase-history-avatar purchase-history-avatar-lg">
            {obtenerInicialesDetalle()}
          </span>

          <div>
            <h3>{historial.nombreCliente}</h3>
            <p>
              {historial.tipoDocumento || 'Documento'}:{' '}
              {historial.numeroDocumento || '-'}
            </p>

            <div className="purchase-history-metrics">
              <div className="metric metric-blue">
                <span>Compras</span>
                <strong>{historial.numeroCompras ?? 0}</strong>
              </div>

              <div className="metric metric-green">
                <span>Total gastado</span>
                <strong>{formatearMoneda(historial.montoTotal)}</strong>
              </div>

              <div className="metric metric-orange">
                <span>Ticket prom.</span>
                <strong>{formatearMoneda(historial.ticketPromedio)}</strong>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div className="purchase-history-card">
        <h5>
          <i className="bi bi-clipboard-check text-primary me-2"></i>
          Historial de Compras
        </h5>

        <div className="purchase-history-purchases">
          {(historial.compras || []).length === 0 ? (
            <div className="purchase-history-placeholder compact">
              <i className="bi bi-inbox"></i>
              <h5>Sin compras registradas</h5>
              <p>Este cliente no tiene compras en el período consultado.</p>
            </div>
          ) : (
            historial.compras.map((compra) => (
              <CompraHistorialItem
                key={compra.idVenta}
                compra={compra}
                formatearMoneda={formatearMoneda}
                formatearFecha={formatearFecha}
              />
            ))
          )}
        </div>
      </div>
    </div>
  );
};

const CompraHistorialItem = ({ compra, formatearMoneda, formatearFecha }) => {
  return (
    <article className="purchase-history-purchase">
      <div className="purchase-history-purchase-header">
        <div className="d-flex align-items-center gap-2 flex-wrap">
          <strong className="purchase-history-sale-code">
            {compra.codigoVenta}
          </strong>

          <BadgeStatus
            variant={compra.estadoVenta === 'ANULADA' ? 'danger' : 'success'}
          >
            {compra.estadoVenta === 'ANULADA' ? 'Anulada' : 'Completada'}
          </BadgeStatus>
        </div>

        <div className="purchase-history-purchase-total">
          <strong>{formatearMoneda(compra.total)}</strong>
          <span>
            {formatearFecha(compra.fechaHoraVenta)}
            {compra.metodoPago ? ` · ${compra.metodoPago}` : ''}
          </span>
        </div>
      </div>

      <div className="purchase-history-products">
        {(compra.productos || []).map((producto) => (
          <div key={`${compra.idVenta}-${producto.idProducto}`}>
            <span>
              <i className="bi bi-bag-check"></i>
              {producto.nombreProducto} × {producto.cantidad}
            </span>

            <strong>{formatearMoneda(producto.subtotal)}</strong>
          </div>
        ))}
      </div>

      <div className="purchase-history-purchase-footer">
        <span>
          Comprobante:{' '}
          {compra.tipoComprobante || compra.numeroComprobante
            ? `${compra.tipoComprobante || ''} ${
                compra.numeroComprobante || ''
              }`.trim()
            : 'Sin comprobante'}
        </span>

        <span>Atendido por: {compra.atendidoPor || '-'}</span>
      </div>
    </article>
  );
};

export default HistorialComprasPage;