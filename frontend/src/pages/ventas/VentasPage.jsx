import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  listarVentas,
  obtenerDetalleVenta,
} from '../../services/ventaService';

const METODOS_PAGO = [
  { id: '', nombre: 'Todos los pagos' },
  { id: 1, nombre: 'Efectivo' },
  { id: 2, nombre: 'Transferencia' },
  { id: 3, nombre: 'Yape' },
  { id: 4, nombre: 'POS' },
];

const VentasPage = () => {
  const [ventas, setVentas] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [estadoVenta, setEstadoVenta] = useState('');
  const [metodoPagoId, setMetodoPagoId] = useState('');
  const [pagina, setPagina] = useState(0);
  const [totalPaginas, setTotalPaginas] = useState(0);
  const [totalElementos, setTotalElementos] = useState(0);

  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const [mostrarDetalle, setMostrarDetalle] = useState(false);
  const [detalleVenta, setDetalleVenta] = useState(null);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const formatearFecha = (fecha) => {
    if (!fecha) return '-';

    const date = new Date(fecha);

    return date.toLocaleDateString('es-PE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const formatearHora = (fecha) => {
    if (!fecha) return '';

    const date = new Date(fecha);

    return date.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const obtenerNombreCliente = (cliente) => {
    if (!cliente) return 'Sin cliente';

    return (
      cliente.nombreCompleto ||
      cliente.razonSocial ||
      cliente.nombre ||
      cliente.nombreCliente ||
      cliente.nombreRazonSocial ||
      'Sin cliente'
    );
  };

  const obtenerNombreVendedor = (vendedor) => {
    if (!vendedor) return '-';

    return (
      vendedor.nombreCompleto ||
      vendedor.nombre ||
      vendedor.usuarioLogin ||
      vendedor.nombreUsuario ||
      'Vendedor'
    );
  };

  const obtenerMetodoPago = (venta) => {
    if (!venta.metodosPago || venta.metodosPago.length === 0) {
      return 'Sin pago';
    }

    return venta.metodosPago
      .map(
        (pago) =>
          pago.nombreMetodoPago ||
          pago.metodoPago ||
          pago.nombre ||
          pago.descripcion
      )
      .filter(Boolean)
      .join(', ');
  };

  const obtenerComprobante = (comprobante) => {
    if (!comprobante) {
      return {
        tipo: 'Sin comprobante',
        numero: '-',
      };
    }

    const tipo =
      comprobante.tipoComprobante ||
      comprobante.tipo ||
      comprobante.nombreTipoComprobante ||
      'Comprobante';

    const numero =
      comprobante.numeroComprobante ||
      comprobante.numero ||
      comprobante.numeroDocumento ||
      `${comprobante.serie || ''}-${comprobante.correlativo || ''}`;

    return {
      tipo,
      numero,
    };
  };

  const obtenerNombreProducto = (producto) => {
    return (
      producto.nombreProducto ||
      producto.nombre ||
      producto.descripcion ||
      producto.producto ||
      'Producto'
    );
  };

  const obtenerPrecioProducto = (producto) => {
    return producto.precioUnitario || producto.precio || producto.precioVenta || 0;
  };

  const obtenerSubtotalProducto = (producto) => {
    return producto.subtotal || producto.subtotalProducto || producto.importe || 0;
  };

  const obtenerNombrePago = (pago) => {
    return (
      pago.nombreMetodoPago ||
      pago.metodoPago ||
      pago.nombre ||
      pago.descripcion ||
      'Pago'
    );
  };

  const cargarVentas = async () => {
    try {
      setCargando(true);
      setError('');

      const response = await listarVentas({
        q: busqueda,
        estadoVenta,
        metodoPagoId,
        page: pagina,
        size: 20,
        sort: 'fechaHoraVenta',
        direction: 'DESC',
      });

      const pageData = response.data;

      setVentas(pageData?.content || []);
      setTotalPaginas(pageData?.totalPages || 0);
      setTotalElementos(pageData?.totalElements || 0);
    } catch (error) {
      console.error('Error cargando ventas:', error);
      setError('No se pudo cargar el listado de ventas.');
      setVentas([]);
      setTotalPaginas(0);
      setTotalElementos(0);
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarVentas();
  }, [pagina, estadoVenta, metodoPagoId]);

  const buscarVentas = (e) => {
    e.preventDefault();
    setPagina(0);
    cargarVentas();
  };

  const abrirDetalle = async (idVenta) => {
    try {
      setCargandoDetalle(true);
      setMostrarDetalle(true);
      setDetalleVenta(null);

      const response = await obtenerDetalleVenta(idVenta);

      setDetalleVenta(response.data);
    } catch (error) {
      console.error('Error obteniendo detalle de venta:', error);
      alert('No se pudo cargar el detalle de la venta.');
      setMostrarDetalle(false);
    } finally {
      setCargandoDetalle(false);
    }
  };

  const cerrarDetalle = () => {
    setMostrarDetalle(false);
    setDetalleVenta(null);
  };

  const ingresosPagina = ventas.reduce(
    (total, venta) => total + Number(venta.total || 0),
    0
  );

  const ventasRegistradas = ventas.filter(
    (venta) => venta.estadoVenta === 'REGISTRADA'
  ).length;

  const ticketPromedio =
    ventas.length > 0 ? ingresosPagina / ventas.length : 0;

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando ventas...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Gestión de Ventas
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-cart-check me-2 text-primary"></i>
          Gestión de Ventas
        </h4>

        <p className="page-subtitle mb-0">Registro y control de ventas</p>
      </div>

      {error && (
        <div className="alert alert-danger">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {error}
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total ventas"
            value={totalElementos}
            description="Registradas"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Ingresos visibles"
            value={formatearMoneda(ingresosPagina)}
            description="En la página actual"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Registradas"
            value={ventasRegistradas}
            description="Ventas activas"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Ticket promedio"
            value={formatearMoneda(ticketPromedio)}
            description="Por venta visible"
            color="info"
          />
        </div>
      </div>

      <form onSubmit={buscarVentas} className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-12 col-lg-4">
            <div className="input-group">
              <span className="input-group-text bg-white">
                <i className="bi bi-search text-muted"></i>
              </span>

              <input
                type="text"
                className="form-control app-input"
                placeholder="Buscar por N°, cliente o comprobante..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <select
              className="form-select app-select"
              value={estadoVenta}
              onChange={(e) => {
                setEstadoVenta(e.target.value);
                setPagina(0);
              }}
            >
              <option value="">Todos los estados</option>
              <option value="REGISTRADA">Registrada</option>
              <option value="ANULADA">Anulada</option>
            </select>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <select
              className="form-select app-select"
              value={metodoPagoId}
              onChange={(e) => {
                setMetodoPagoId(e.target.value);
                setPagina(0);
              }}
            >
              {METODOS_PAGO.map((metodo) => (
                <option key={metodo.id} value={metodo.id}>
                  {metodo.nombre}
                </option>
              ))}
            </select>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <button type="submit" className="btn btn-outline-primary w-100">
              <i className="bi bi-search me-2"></i>
              Buscar
            </button>
          </div>

          <div className="col-12 col-lg-2 text-lg-end">
            <button
              type="button"
              className="btn btn-primary app-btn-primary w-100"
              onClick={() =>
                alert('Nueva venta pendiente de endpoint POST /api/ventas')
              }
            >
              <i className="bi bi-plus-lg me-2"></i>
              Nueva Venta
            </button>
          </div>
        </div>
      </form>

      <div className="app-card">
        <div className="table-responsive">
          <table className="table align-middle mb-0 app-table">
            <thead>
              <tr>
                <th>N° Venta</th>
                <th>Fecha y Hora</th>
                <th>Cliente</th>
                <th className="text-end">Total</th>
                <th>Pago</th>
                <th>Comprobante</th>
                <th>Estado</th>
                <th>Vendedor</th>
                <th className="text-center">Ver</th>
              </tr>
            </thead>

            <tbody>
              {ventas.length === 0 ? (
                <tr>
                  <td colSpan="9" className="text-center py-5 text-muted">
                    <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                    No se encontraron ventas registradas.
                  </td>
                </tr>
              ) : (
                ventas.map((venta) => {
                  const comprobante = obtenerComprobante(venta.comprobante);

                  return (
                    <tr key={venta.idVenta}>
                      <td>
                        <strong className="text-primary">
                          {venta.numeroVenta}
                        </strong>
                      </td>

                      <td>
                        <strong>{formatearFecha(venta.fechaHoraVenta)}</strong>
                        <br />
                        <span className="text-muted small">
                          {formatearHora(venta.fechaHoraVenta)}
                        </span>
                      </td>

                      <td>
                        <i className="bi bi-person me-2 text-muted"></i>
                        {obtenerNombreCliente(venta.cliente)}
                      </td>

                      <td className="text-end fw-bold">
                        {formatearMoneda(venta.total)}
                      </td>

                      <td>
                        <span className="badge bg-primary bg-opacity-10 text-primary app-badge">
                          <i className="bi bi-credit-card me-1"></i>
                          {obtenerMetodoPago(venta)}
                        </span>
                      </td>

                      <td>
                        <i className="bi bi-receipt me-2 text-muted"></i>
                        {comprobante.tipo}
                        <br />
                        <span className="text-muted small ms-4">
                          {comprobante.numero}
                        </span>
                      </td>

                      <td>
                        <BadgeStatus
                          variant={
                            venta.estadoVenta === 'REGISTRADA'
                              ? 'success'
                              : 'danger'
                          }
                        >
                          {venta.estadoVenta}
                        </BadgeStatus>
                      </td>

                      <td>
                        <i className="bi bi-person-badge me-2 text-muted"></i>
                        {obtenerNombreVendedor(venta.vendedor)}
                      </td>

                      <td className="text-center">
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-primary"
                          onClick={() => abrirDetalle(venta.idVenta)}
                          title="Ver detalle"
                        >
                          <i className="bi bi-eye"></i>
                        </button>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>

        <div className="card-footer bg-white d-flex justify-content-between align-items-center flex-wrap gap-2">
          <span className="text-muted small">
            <i className="bi bi-list-check me-1"></i>
            {totalElementos} resultado(s) encontrado(s)
          </span>

          <div className="d-flex gap-2 align-items-center">
            <button
              type="button"
              className="btn btn-sm btn-outline-secondary"
              disabled={pagina === 0}
              onClick={() => setPagina(pagina - 1)}
            >
              <i className="bi bi-chevron-left me-1"></i>
              Anterior
            </button>

            <span className="small text-muted">
              Página {pagina + 1} de {totalPaginas || 1}
            </span>

            <button
              type="button"
              className="btn btn-sm btn-outline-secondary"
              disabled={pagina + 1 >= totalPaginas}
              onClick={() => setPagina(pagina + 1)}
            >
              Siguiente
              <i className="bi bi-chevron-right ms-1"></i>
            </button>
          </div>
        </div>
      </div>

      {mostrarDetalle && (
        <div className="sales-detail-overlay">
          <div className="sales-detail-modal">
            <div className="sales-detail-header">
              <h5 className="sales-detail-title">
                <i className="bi bi-receipt me-2 text-primary"></i>
                Detalle de venta {detalleVenta?.numeroVenta || ''}
              </h5>

              <button
                type="button"
                className="btn-close"
                onClick={cerrarDetalle}
              ></button>
            </div>

            <div className="sales-detail-body">
              {cargandoDetalle ? (
                <div className="d-flex align-items-center gap-2">
                  <div className="spinner-border spinner-border-sm text-primary" />
                  <span>Cargando detalle...</span>
                </div>
              ) : detalleVenta ? (
                <>
                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-4">
                      <div className="sales-detail-info-card">
                        <span>
                          <i className="bi bi-person me-1"></i>
                          Cliente
                        </span>

                        <h6 className="fw-bold">
                          {obtenerNombreCliente(detalleVenta.cliente)}
                        </h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="sales-detail-info-card">
                        <span>
                          <i className="bi bi-person-badge me-1"></i>
                          Vendedor
                        </span>

                        <h6 className="fw-bold">
                          {obtenerNombreVendedor(detalleVenta.vendedor)}
                        </h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="sales-detail-info-card">
                        <span>
                          <i className="bi bi-check-circle me-1"></i>
                          Estado
                        </span>

                        <h6 className="fw-bold">{detalleVenta.estadoVenta}</h6>
                      </div>
                    </div>
                  </div>

                  <h6 className="fw-bold mb-2">
                    <i className="bi bi-box-seam me-2 text-primary"></i>
                    Productos vendidos
                  </h6>

                  <div className="table-responsive mb-4">
                    <table className="table table-sm align-middle app-table">
                      <thead>
                        <tr>
                          <th>Producto</th>
                          <th className="text-end">Cantidad</th>
                          <th className="text-end">Precio</th>
                          <th className="text-end">Subtotal</th>
                        </tr>
                      </thead>

                      <tbody>
                        {(detalleVenta.productos || []).length === 0 ? (
                          <tr>
                            <td colSpan="4" className="text-center text-muted">
                              No hay productos registrados.
                            </td>
                          </tr>
                        ) : (
                          (detalleVenta.productos || []).map(
                            (producto, index) => (
                              <tr key={index}>
                                <td>{obtenerNombreProducto(producto)}</td>

                                <td className="text-end">
                                  {producto.cantidad}
                                </td>

                                <td className="text-end">
                                  {formatearMoneda(
                                    obtenerPrecioProducto(producto)
                                  )}
                                </td>

                                <td className="text-end fw-bold">
                                  {formatearMoneda(
                                    obtenerSubtotalProducto(producto)
                                  )}
                                </td>
                              </tr>
                            )
                          )
                        )}
                      </tbody>
                    </table>
                  </div>

                  <h6 className="fw-bold mb-2">
                    <i className="bi bi-credit-card me-2 text-primary"></i>
                    Pagos
                  </h6>

                  <div className="table-responsive mb-4">
                    <table className="table table-sm align-middle app-table">
                      <thead>
                        <tr>
                          <th>Método</th>
                          <th className="text-end">Monto</th>
                        </tr>
                      </thead>

                      <tbody>
                        {(detalleVenta.pagos || []).length === 0 ? (
                          <tr>
                            <td colSpan="2" className="text-center text-muted">
                              No hay pagos registrados.
                            </td>
                          </tr>
                        ) : (
                          (detalleVenta.pagos || []).map((pago, index) => (
                            <tr key={index}>
                              <td>{obtenerNombrePago(pago)}</td>

                              <td className="text-end fw-bold">
                                {formatearMoneda(pago.monto)}
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>

                  <div className="row justify-content-end">
                    <div className="col-12 col-md-5">
                      <div className="sales-summary-box">
                        <div className="d-flex justify-content-between mb-2">
                          <span>Subtotal</span>
                          <strong>
                            {formatearMoneda(detalleVenta.subtotalGeneral)}
                          </strong>
                        </div>

                        <div className="d-flex justify-content-between mb-2">
                          <span>Total</span>
                          <strong>{formatearMoneda(detalleVenta.total)}</strong>
                        </div>

                        <div className="d-flex justify-content-between mb-2">
                          <span>Monto pagado</span>
                          <strong>
                            {formatearMoneda(detalleVenta.montoPagadoTotal)}
                          </strong>
                        </div>

                        <hr />

                        <div className="d-flex justify-content-between">
                          <span>Cambio</span>
                          <strong className="text-primary fs-6">
                            {formatearMoneda(detalleVenta.cambio)}
                          </strong>
                        </div>
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                <p>No se encontró información del detalle.</p>
              )}
            </div>

            <div className="sales-detail-footer">
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={cerrarDetalle}
              >
                <i className="bi bi-x-circle me-2"></i>
                Cerrar
              </button>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
};

export default VentasPage;