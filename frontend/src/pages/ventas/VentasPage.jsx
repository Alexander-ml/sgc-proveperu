import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

/* Datos de prueba (frontend). Luego se reemplazan por el servicio real. */
const VENTAS = [
  {
    id: 'V-2026-001',
    fecha: '2026-05-06',
    hora: '09:15:00',
    cliente: 'Tapicería El Buen Mueble',
    total: 890.0,
    pago: 'Transferencia',
    comprobante: { tipo: 'Factura', numero: 'F001-0045' },
    estado: 'Completada',
    vendedor: 'Iris Arroyo',
    productos: [
      { nombre: 'Tela Terciopelo Azul', cantidad: 20, precio: 18.0 },
      { nombre: 'Pegamento de Contacto 1L', cantidad: 5, precio: 28.0 },
      { nombre: 'Espuma HR-40 10cm', cantidad: 3, precio: 68.0 },
      { nombre: 'Bisagras Codo 35mm', cantidad: 93, precio: 2.0 },
    ],
  },
  {
    id: 'V-2026-002',
    fecha: '2026-05-06',
    hora: '10:30:00',
    cliente: 'Sin cliente',
    total: 234.0,
    pago: 'Efectivo',
    comprobante: { tipo: 'Boleta', numero: 'B001-0123' },
    estado: 'Completada',
    vendedor: 'Iris Arroyo',
    productos: [
      { nombre: 'Espuma HR-40 4 pulgadas', cantidad: 1, precio: 120.0 },
      { nombre: 'Fibra Napa Siliconada 1kg', cantidad: 4, precio: 28.5 },
    ],
  },
  {
    id: 'V-2026-003',
    fecha: '2026-05-05',
    hora: '14:45:00',
    cliente: 'Carpintería Hnos. García',
    total: 1560.0,
    pago: 'Yape',
    comprobante: { tipo: 'Factura', numero: 'F001-0044' },
    estado: 'Completada',
    vendedor: 'Iris Arroyo',
    productos: [
      { nombre: 'Tela Chenille (metro)', cantidad: 30, precio: 32.0 },
      { nombre: 'Espuma HR-40 4 pulgadas', cantidad: 5, precio: 120.0 },
    ],
  },
  {
    id: 'V-2026-004',
    fecha: '2026-05-05',
    hora: '11:00:00',
    cliente: 'Muebles Modernos S.A.C.',
    total: 2340.0,
    pago: 'Transferencia',
    comprobante: { tipo: 'Factura', numero: 'F001-0043' },
    estado: 'Completada',
    vendedor: 'César Medina',
    productos: [
      { nombre: 'Triplay Lupuna 1.2x2.4x4mm', cantidad: 52, precio: 45.0 },
    ],
  },
  {
    id: 'V-2026-005',
    fecha: '2026-05-04',
    hora: '09:30:00',
    cliente: 'Fernández Mobiliario E.I.R.L.',
    total: 780.0,
    pago: 'POS',
    comprobante: { tipo: 'Factura', numero: 'F001-0042' },
    estado: 'Completada',
    vendedor: 'Iris Arroyo',
    productos: [
      { nombre: 'Triplay Lupuna 1.2x2.4x4mm', cantidad: 12, precio: 45.0 },
      { nombre: 'Espuma HR-40 4 pulgadas', cantidad: 2, precio: 120.0 },
    ],
  },
  {
    id: 'V-2026-006',
    fecha: '2026-05-04',
    hora: '16:20:00',
    cliente: 'Sin cliente',
    total: 345.0,
    pago: 'Efectivo',
    comprobante: { tipo: 'Boleta', numero: 'B001-0124' },
    estado: 'Pendiente',
    vendedor: 'César Medina',
    productos: [
      { nombre: 'Fibra Napa Siliconada 1kg', cantidad: 10, precio: 28.5 },
      { nombre: 'Hilo de coser industrial', cantidad: 4, precio: 15.0 },
    ],
  },
];

const PAGO_COLORS = {
  Transferencia: 'primary',
  Efectivo: 'success',
  Yape: 'info',
  POS: 'warning',
};

const ESTADO_COLORS = {
  Completada: 'success',
  Pendiente: 'warning',
  Anulada: 'danger',
};

const formatearMonto = (valor) =>
  valor.toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const VentasPage = () => {
  const navigate = useNavigate();

  const [busqueda, setBusqueda] = useState('');
  const [filtroEstado, setFiltroEstado] = useState('');
  const [filtroPago, setFiltroPago] = useState('');
  const [ventaSeleccionada, setVentaSeleccionada] = useState(null);

  const ventasFiltradas = useMemo(() => {
    const texto = busqueda.trim().toLowerCase();

    return VENTAS.filter((venta) => {
      const coincideTexto =
        !texto ||
        venta.id.toLowerCase().includes(texto) ||
        venta.cliente.toLowerCase().includes(texto) ||
        venta.comprobante.numero.toLowerCase().includes(texto);

      const coincideEstado = !filtroEstado || venta.estado === filtroEstado;
      const coincidePago = !filtroPago || venta.pago === filtroPago;

      return coincideTexto && coincideEstado && coincidePago;
    });
  }, [busqueda, filtroEstado, filtroPago]);

  const resumen = useMemo(() => {
    const total = ventasFiltradas.length;
    const ingresos = ventasFiltradas.reduce((acc, venta) => acc + venta.total, 0);
    const completadas = ventasFiltradas.filter((v) => v.estado === 'Completada').length;
    const ticketPromedio = total > 0 ? ingresos / total : 0;

    return { total, ingresos, completadas, ticketPromedio };
  }, [ventasFiltradas]);

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">Inicio &gt; Gestión de Ventas</p>
        <h4 className="fw-bold mb-1">Gestión de Ventas</h4>
        <p className="text-muted mb-0">Registro y control de ventas</p>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total ventas"
            value={resumen.total}
            description="Registradas"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Ingresos totales"
            value={`S/ ${formatearMonto(resumen.ingresos)}`}
            description="En el período"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Completadas"
            value={resumen.completadas}
            description="Transacciones"
            color="info"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Ticket promedio"
            value={`S/ ${formatearMonto(resumen.ticketPromedio)}`}
            description="Por venta"
            color="warning"
          />
        </div>
      </div>

      <div className="card border-0 shadow-sm">
        <div className="card-body">
          <div className="d-flex flex-wrap gap-2 align-items-center justify-content-between mb-3">
            <div className="d-flex flex-wrap gap-2 flex-grow-1">
              <input
                type="text"
                className="form-control"
                style={{ maxWidth: '320px' }}
                placeholder="Buscar por N°, cliente o comprobante..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />

              <select
                className="form-select"
                style={{ maxWidth: '200px' }}
                value={filtroEstado}
                onChange={(e) => setFiltroEstado(e.target.value)}
              >
                <option value="">Todos los estados</option>
                <option value="Completada">Completada</option>
                <option value="Pendiente">Pendiente</option>
                <option value="Anulada">Anulada</option>
              </select>

              <select
                className="form-select"
                style={{ maxWidth: '200px' }}
                value={filtroPago}
                onChange={(e) => setFiltroPago(e.target.value)}
              >
                <option value="">Todos los pagos</option>
                <option value="Efectivo">Efectivo</option>
                <option value="Transferencia">Transferencia</option>
                <option value="Yape">Yape</option>
                <option value="POS">POS</option>
              </select>
            </div>

            <button
              type="button"
              className="btn btn-primary"
              onClick={() => navigate('/ventas/nueva')}
            >
              + Nueva Venta
            </button>
          </div>

          <div className="table-responsive">
            <table className="table align-middle">
              <thead>
                <tr className="text-muted small">
                  <th>N° Venta</th>
                  <th>Fecha y Hora</th>
                  <th>Cliente</th>
                  <th className="text-end">Total</th>
                  <th>Pago</th>
                  <th>Comprobante</th>
                  <th>Estado</th>
                  <th>Vendedor</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {ventasFiltradas.length === 0 ? (
                  <tr>
                    <td colSpan={9}>
                      <div className="alert alert-info mb-0">
                        No se encontraron ventas con los filtros aplicados.
                      </div>
                    </td>
                  </tr>
                ) : (
                  ventasFiltradas.map((venta) => (
                    <tr key={venta.id}>
                      <td>
                        <span className="fw-semibold text-primary">{venta.id}</span>
                      </td>

                      <td>
                        <div>{venta.fecha}</div>
                        <div className="text-muted small">{venta.hora.slice(0, 5)}</div>
                      </td>

                      <td>{venta.cliente}</td>

                      <td className="text-end fw-semibold">
                        S/ {formatearMonto(venta.total)}
                      </td>

                      <td>
                        <BadgeStatus variant={PAGO_COLORS[venta.pago] || 'secondary'}>
                          {venta.pago}
                        </BadgeStatus>
                      </td>

                      <td>
                        <div>{venta.comprobante.tipo}</div>
                        <div className="text-muted small">{venta.comprobante.numero}</div>
                      </td>

                      <td>
                        <BadgeStatus variant={ESTADO_COLORS[venta.estado] || 'secondary'}>
                          {venta.estado}
                        </BadgeStatus>
                      </td>

                      <td>{venta.vendedor}</td>

                      <td className="text-end">
                        <button
                          type="button"
                          className="btn btn-sm btn-link text-muted"
                          title="Ver detalle"
                          onClick={() => setVentaSeleccionada(venta)}
                        >
                          👁
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      </div>

      {ventaSeleccionada && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
          onClick={() => setVentaSeleccionada(null)}
        >
          <div
            className="modal-dialog modal-lg modal-dialog-centered modal-dialog-scrollable"
            onClick={(e) => e.stopPropagation()}
          >
            <div className="modal-content">
              <div className="modal-header border-0 pb-0">
                <div>
                  <h5 className="modal-title fw-bold">
                    📄 Detalle de Venta — {ventaSeleccionada.id}
                  </h5>
                  <p className="text-muted mb-0">
                    {ventaSeleccionada.fecha} {ventaSeleccionada.hora} ·{' '}
                    {ventaSeleccionada.comprobante.tipo}{' '}
                    {ventaSeleccionada.comprobante.numero}
                  </p>
                </div>

                <button
                  type="button"
                  className="btn-close"
                  onClick={() => setVentaSeleccionada(null)}
                ></button>
              </div>

              <div className="modal-body">
                <div className="row g-3 mb-3">
                  <div className="col-6">
                    <p className="text-muted small mb-1">Cliente</p>
                    <p className="fw-semibold mb-0">{ventaSeleccionada.cliente}</p>
                  </div>

                  <div className="col-6">
                    <p className="text-muted small mb-1">Estado</p>
                    <BadgeStatus
                      variant={ESTADO_COLORS[ventaSeleccionada.estado] || 'secondary'}
                    >
                      {ventaSeleccionada.estado}
                    </BadgeStatus>
                  </div>

                  <div className="col-6">
                    <p className="text-muted small mb-1">Método de pago</p>
                    <p className="mb-0">{ventaSeleccionada.pago}</p>
                  </div>

                  <div className="col-6">
                    <p className="text-muted small mb-1">Vendedor</p>
                    <p className="mb-0">{ventaSeleccionada.vendedor}</p>
                  </div>
                </div>

                <hr />

                <p className="text-uppercase text-muted small fw-bold mb-2">Productos</p>

                <div className="table-responsive">
                  <table className="table align-middle">
                    <thead>
                      <tr className="text-muted small">
                        <th>Producto</th>
                        <th className="text-end">Cant.</th>
                        <th className="text-end">P. Unit.</th>
                        <th className="text-end">Subtotal</th>
                      </tr>
                    </thead>
                    <tbody>
                      {ventaSeleccionada.productos.map((producto, index) => (
                        <tr key={index}>
                          <td>{producto.nombre}</td>
                          <td className="text-end">{producto.cantidad}</td>
                          <td className="text-end">S/ {formatearMonto(producto.precio)}</td>
                          <td className="text-end fw-semibold">
                            S/ {formatearMonto(producto.precio * producto.cantidad)}
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>

                <div className="d-flex justify-content-end">
                  <div className="bg-light rounded p-3" style={{ minWidth: '260px' }}>
                    <div className="d-flex justify-content-between mb-1">
                      <span className="text-muted">Subtotal:</span>
                      <span>S/ {formatearMonto(ventaSeleccionada.total)}</span>
                    </div>

                    <div className="d-flex justify-content-between">
                      <strong>TOTAL:</strong>
                      <strong className="text-primary">
                        S/ {formatearMonto(ventaSeleccionada.total)}
                      </strong>
                    </div>
                  </div>
                </div>
              </div>

              <div className="modal-footer border-0">
                <button type="button" className="btn btn-outline-secondary">
                  🖨 Imprimir comprobante
                </button>

                <button
                  type="button"
                  className="btn btn-outline-secondary"
                  onClick={() => setVentaSeleccionada(null)}
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
};

export default VentasPage;
