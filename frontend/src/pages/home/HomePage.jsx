import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import MainLayout from '../../componentes/layout/MainLayout';

import {
  listarVentas,
  obtenerDetalleVenta,
} from '../../services/ventaService';

import { obtenerResumenClientes } from '../../services/clienteService';
import { obtenerResumenCaja, listarMovimientosCaja } from '../../services/cajaService';

import {
  obtenerResumenInventario,
  listarProductosInventario,
} from '../../services/inventarioService';

const COLORS = ['#22c55e', '#3b82f6', '#a855f7', '#f97316', '#06b6d4', '#f59e0b'];

const HomePage = () => {
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [ventas, setVentas] = useState([]);
  const [detallesVentas, setDetallesVentas] = useState([]);
  const [resumenCaja, setResumenCaja] = useState(null);
  const [movimientosCaja, setMovimientosCaja] = useState([]);
  const [resumenClientes, setResumenClientes] = useState(null);
  const [resumenInventario, setResumenInventario] = useState(null);
  const [productosInventario, setProductosInventario] = useState([]);

  const obtenerData = (response) => response?.data ?? response;

  const obtenerArray = (response) => {
    const data = obtenerData(response);

    if (Array.isArray(data)) return data;
    if (Array.isArray(data?.content)) return data.content;
    if (Array.isArray(data?.data)) return data.data;
    if (Array.isArray(data?.data?.content)) return data.data.content;

    return [];
  };

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const formatearHora = (fecha) => {
    if (!fecha) return '--:--';

    return new Date(fecha).toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const formatearMes = (fecha) => {
    const base = fecha ? new Date(fecha) : new Date();

    return base.toLocaleDateString('es-PE', {
      month: 'long',
      year: 'numeric',
    });
  };

  const formatearDiaCorto = (fecha) => {
    return new Date(fecha).toLocaleDateString('es-PE', {
      day: '2-digit',
      month: 'short',
    });
  };

  const obtenerNombreCliente = (cliente) => {
    if (!cliente) return 'Sin cliente';

    return (
      cliente.nombreCompleto ||
      cliente.nombreCliente ||
      cliente.razonSocial ||
      cliente.nombre ||
      cliente.nombreRazonSocial ||
      'Sin cliente'
    );
  };

  const obtenerMetodoPago = (venta) => {
    if (!venta?.metodosPago || venta.metodosPago.length === 0) return 'Sin pago';

    return venta.metodosPago
      .map(
        (pago) =>
          pago.nombreMetodoPago ||
          pago.metodoPagoNombre ||
          pago.metodoPago ||
          pago.nombre
      )
      .filter(Boolean)
      .join(', ');
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

  const cargarPanel = async () => {
    try {
      setCargando(true);
      setError('');

      const [
        ventasResponse,
        cajaResponse,
        movimientosCajaResponse,
        clientesResponse,
        inventarioResponse,
        productosResponse,
      ] = await Promise.allSettled([
        listarVentas({
          page: 0,
          size: 30,
          sort: 'fechaHoraVenta',
          direction: 'DESC',
        }),
        obtenerResumenCaja(),
        listarMovimientosCaja(),
        obtenerResumenClientes(),
        obtenerResumenInventario(),
        listarProductosInventario({}),
      ]);

      const ventasListado =
        ventasResponse.status === 'fulfilled'
          ? obtenerArray(ventasResponse.value)
          : [];

      setVentas(ventasListado);

      setResumenCaja(
        cajaResponse.status === 'fulfilled'
          ? obtenerData(cajaResponse.value)
          : null
      );

      setMovimientosCaja(
        movimientosCajaResponse.status === 'fulfilled'
          ? obtenerArray(movimientosCajaResponse.value)
          : []
      );

      setResumenClientes(
        clientesResponse.status === 'fulfilled'
          ? obtenerData(clientesResponse.value)
          : null
      );

      setResumenInventario(
        inventarioResponse.status === 'fulfilled'
          ? obtenerData(inventarioResponse.value)
          : null
      );

      setProductosInventario(
        productosResponse.status === 'fulfilled'
          ? obtenerArray(productosResponse.value)
          : []
      );

      const detalles = await Promise.allSettled(
        ventasListado.slice(0, 12).map((venta) => obtenerDetalleVenta(venta.idVenta))
      );

      setDetallesVentas(
        detalles
          .filter((detalle) => detalle.status === 'fulfilled')
          .map((detalle) => obtenerData(detalle.value))
          .filter(Boolean)
      );
    } catch (err) {
      console.error('Error cargando panel principal:', err);
      setError('No se pudo cargar el panel principal.');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarPanel();
  }, []);

  const fechaReferencia = useMemo(() => {
    const fechas = ventas
      .map((venta) => venta.fechaHoraVenta)
      .filter(Boolean)
      .map((fecha) => new Date(fecha));

    if (fechas.length === 0) return new Date();

    return new Date(Math.max(...fechas.map((fecha) => fecha.getTime())));
  }, [ventas]);

  const ventasHoy = useMemo(() => {
    const hoy = new Date().toISOString().slice(0, 10);

    return ventas.filter(
      (venta) =>
        venta.estadoVenta === 'REGISTRADA' &&
        String(venta.fechaHoraVenta || '').slice(0, 10) === hoy
    );
  }, [ventas]);

  const ventasMes = useMemo(() => {
    const mes = fechaReferencia.toISOString().slice(0, 7);

    return ventas.filter(
      (venta) =>
        venta.estadoVenta === 'REGISTRADA' &&
        String(venta.fechaHoraVenta || '').slice(0, 7) === mes
    );
  }, [ventas, fechaReferencia]);

  const tendenciaVentas = useMemo(() => {
    const dias = [];

    for (let index = 6; index >= 0; index -= 1) {
      const fecha = new Date(fechaReferencia);
      fecha.setDate(fecha.getDate() - index);

      const key = fecha.toISOString().slice(0, 10);
      const total = ventas
        .filter(
          (venta) =>
            venta.estadoVenta === 'REGISTRADA' &&
            String(venta.fechaHoraVenta || '').slice(0, 10) === key
        )
        .reduce((sum, venta) => sum + Number(venta.total || 0), 0);

      dias.push({
        label: formatearDiaCorto(fecha),
        total,
      });
    }

    return dias;
  }, [ventas, fechaReferencia]);

  const metodosPago = useMemo(() => {
    const map = new Map();

    ventasMes.forEach((venta) => {
      if (!venta.metodosPago || venta.metodosPago.length === 0) return;

      venta.metodosPago.forEach((pago) => {
        const nombre =
          pago.nombreMetodoPago ||
          pago.metodoPagoNombre ||
          pago.metodoPago ||
          'Sin pago';

        const monto = Number(pago.monto || 0) || Number(venta.total || 0);

        map.set(nombre, (map.get(nombre) || 0) + monto);
      });
    });

    return [...map.entries()].map(([label, value], index) => ({
      label,
      value,
      color: COLORS[index % COLORS.length],
    }));
  }, [ventasMes]);

  const productosMasVendidos = useMemo(() => {
    const map = new Map();

    detallesVentas.forEach((detalle) => {
      (detalle.productos || []).forEach((producto) => {
        const nombre = obtenerNombreProducto(producto);
        const cantidad = Number(producto.cantidad || 0);

        map.set(nombre, (map.get(nombre) || 0) + cantidad);
      });
    });

    return [...map.entries()]
      .map(([label, value]) => ({ label, value }))
      .sort((a, b) => b.value - a.value)
      .slice(0, 5);
  }, [detallesVentas]);

  const alertasInventario = useMemo(() => {
    return productosInventario
      .filter(
        (producto) =>
          producto.estado === 'SIN_STOCK' ||
          producto.estado === 'STOCK_BAJO' ||
          Number(producto.stockActual || 0) <= Number(producto.stockMinimo || 0)
      )
      .slice(0, 4);
  }, [productosInventario]);

  const totalVentasHoy = ventasHoy.reduce(
    (sum, venta) => sum + Number(venta.total || 0),
    0
  );

  const totalVentasMes = ventasMes.reduce(
    (sum, venta) => sum + Number(venta.total || 0),
    0
  );

  const totalClientes = resumenClientes?.totalClientes ?? 0;
  const sinStock = resumenInventario?.sinStock ?? 0;
  const stockBajo = resumenInventario?.stockBajo ?? 0;
  const totalAlertas = sinStock + stockBajo;

  if (cargando) {
    return (
      <MainLayout>
        <div className="dashboard-loading">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando panel principal...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="dashboard-page">
        <header className="dashboard-title">
          <h1>Panel Principal</h1>
          <p>Resumen general del negocio</p>
        </header>

        {error && (
          <div className="alert alert-danger">
            <i className="bi bi-exclamation-triangle me-2"></i>
            {error}
          </div>
        )}

        <section className="dashboard-stats">
          <MetricCard
            icon="bi-cart3"
            tone="blue"
            title="Ventas de Hoy"
            value={formatearMoneda(totalVentasHoy)}
            description={`${ventasHoy.length} transaccion(es)`}
            badge="Hoy"
          />

          <MetricCard
            icon="bi-wallet2"
            tone="green"
            title="Saldo en Caja"
            value={formatearMoneda(resumenCaja?.saldoActual)}
            description="Balance actual del dia"
            badge={resumenCaja?.estadoCaja === 'ABIERTA' ? 'Abierta' : 'Cerrada'}
          />

          <MetricCard
            icon="bi-box-seam"
            tone="orange"
            title="Alertas de Stock"
            value={totalAlertas}
            description={`${sinStock} sin stock · ${stockBajo} bajo minimo`}
            badge={sinStock > 0 ? `${sinStock} criticos` : 'Controlado'}
          />

          <MetricCard
            icon="bi-people"
            tone="purple"
            title="Clientes Activos"
            value={totalClientes}
            description="Clientes registrados"
            badge={`+${resumenClientes?.clientesFrecuentes ?? 0} frecuentes`}
          />

          <MetricCard
            icon="bi-graph-up-arrow"
            tone="indigo"
            title="Ventas del Mes"
            value={formatearMoneda(totalVentasMes)}
            description={`${ventasMes.length} transaccion(es) en ${formatearMes(fechaReferencia)}`}
            badge="Mes actual"
          />
        </section>

        <section className="dashboard-grid-top">
          <DashboardCard
            title="Ventas — Ultimos 7 dias"
            subtitle="Ingresos diarios en soles (S/)"
            actionText={formatearMes(fechaReferencia)}
          >
            <LineChart data={tendenciaVentas} />
          </DashboardCard>

          <DashboardCard
            title="Metodos de Pago"
            subtitle="Distribucion del mes actual"
          >
            <DonutChart data={metodosPago} />
          </DashboardCard>
        </section>

        <section className="dashboard-grid-bottom">
          <DashboardCard
            title="Productos Mas Vendidos"
            subtitle={`Por volumen — ${formatearMes(fechaReferencia)}`}
          >
            <HorizontalBarChart data={productosMasVendidos} />
          </DashboardCard>

          <DashboardCard
            title="Ultimas Ventas"
            subtitle="Transacciones recientes registradas"
            linkTo="/ventas"
            linkText="Ver todas"
          >
            <div className="dashboard-sales-list">
              {ventas.slice(0, 5).map((venta) => (
                <div className="dashboard-sale-item" key={venta.idVenta}>
                  <div className="dashboard-sale-icon">
                    <i className="bi bi-cart3"></i>
                  </div>

                  <div className="dashboard-sale-info">
                    <div className="dashboard-sale-title">
                      <strong>{venta.numeroVenta}</strong>
                      <span
                        className={`dashboard-status ${
                          venta.estadoVenta === 'REGISTRADA'
                            ? 'is-success'
                            : 'is-danger'
                        }`}
                      >
                        {venta.estadoVenta === 'REGISTRADA'
                          ? 'Completada'
                          : 'Anulada'}
                      </span>
                    </div>

                    <p>
                      {obtenerNombreCliente(venta.cliente)} · {obtenerMetodoPago(venta)}
                    </p>
                  </div>

                  <div className="dashboard-sale-total">
                    <strong>{formatearMoneda(venta.total)}</strong>
                    <span>
                      <i className="bi bi-clock"></i>
                      {formatearHora(venta.fechaHoraVenta)}
                    </span>
                  </div>
                </div>
              ))}

              {ventas.length === 0 && (
                <div className="dashboard-empty">
                  <i className="bi bi-receipt"></i>
                  No hay ventas registradas.
                </div>
              )}
            </div>
          </DashboardCard>
        </section>

        <section className="dashboard-alerts">
          <div className="dashboard-alerts-header">
            <h3>
              <i className="bi bi-exclamation-triangle"></i>
              Alertas de Inventario ({alertasInventario.length} productos)
            </h3>

            <Link to="/inventario">
              Ver inventario
              <i className="bi bi-chevron-right"></i>
            </Link>
          </div>

          <div className="dashboard-alerts-grid">
            {alertasInventario.length === 0 ? (
              <div className="dashboard-alert-ok">
                <strong>Inventario estable</strong>
                <span>No hay productos con alerta de stock.</span>
              </div>
            ) : (
              alertasInventario.map((producto) => {
                const critico = Number(producto.stockActual || 0) <= 0;

                return (
                  <div
                    className={`dashboard-alert-product ${
                      critico ? 'is-critical' : ''
                    }`}
                    key={producto.idProducto}
                  >
                    <span></span>
                    <div>
                      <strong>{producto.nombre || producto.nombreProducto}</strong>
                      <p>
                        {critico
                          ? 'Sin stock disponible'
                          : `Stock: ${producto.stockActual} / Min: ${producto.stockMinimo}`}
                      </p>
                    </div>
                  </div>
                );
              })
            )}
          </div>
        </section>
      </div>
    </MainLayout>
  );
};

const MetricCard = ({ icon, tone, title, value, description, badge }) => {
  return (
    <article className={`dashboard-metric dashboard-metric-${tone}`}>
      <div className="dashboard-metric-top">
        <div className="dashboard-metric-icon">
          <i className={`bi ${icon}`}></i>
        </div>

        <span className="dashboard-metric-badge">{badge}</span>
      </div>

      <p>{title}</p>
      <h2>{value}</h2>
      <span>{description}</span>
    </article>
  );
};

const DashboardCard = ({
  title,
  subtitle,
  actionText,
  linkTo,
  linkText,
  children,
}) => {
  return (
    <article className="dashboard-card">
      <div className="dashboard-card-header">
        <div>
          <h3>{title}</h3>
          <p>{subtitle}</p>
        </div>

        {actionText && <span className="dashboard-card-chip">{actionText}</span>}

        {linkTo && (
          <Link className="dashboard-card-link" to={linkTo}>
            {linkText}
            <i className="bi bi-chevron-right"></i>
          </Link>
        )}
      </div>

      {children}
    </article>
  );
};

const LineChart = ({ data }) => {
  const width = 880;
  const height = 260;
  const padding = { top: 20, right: 22, bottom: 38, left: 66 };
  const chartWidth = width - padding.left - padding.right;
  const chartHeight = height - padding.top - padding.bottom;
  const maxValue = Math.max(...data.map((item) => item.total), 1000);
  const roundedMax = Math.ceil(maxValue / 1000) * 1000;
  const steps = [0, 0.25, 0.5, 0.75, 1];

  const points = data.map((item, index) => {
    const x =
      padding.left + (chartWidth / Math.max(data.length - 1, 1)) * index;
    const y =
      padding.top +
      chartHeight -
      (Number(item.total || 0) / roundedMax) * chartHeight;

    return { ...item, x, y };
  });

  const path = points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ');

  const areaPath =
    points.length > 0
      ? `${path} L ${points[points.length - 1].x} ${
          padding.top + chartHeight
        } L ${points[0].x} ${padding.top + chartHeight} Z`
      : '';

  return (
    <div className="dashboard-line-chart">
      <svg viewBox={`0 0 ${width} ${height}`} role="img">
        <defs>
          <linearGradient id="salesArea" x1="0" x2="0" y1="0" y2="1">
            <stop offset="0%" stopColor="#3b82f6" stopOpacity="0.22" />
            <stop offset="100%" stopColor="#3b82f6" stopOpacity="0" />
          </linearGradient>
        </defs>

        {steps.map((step) => {
          const y = padding.top + chartHeight - step * chartHeight;
          const value = Math.round(roundedMax * step);

          return (
            <g key={step}>
              <line
                x1={padding.left}
                x2={width - padding.right}
                y1={y}
                y2={y}
                className="chart-grid-line"
              />
              <text x="18" y={y + 5} className="chart-axis-label">
                S/{value}
              </text>
            </g>
          );
        })}

        {points.map((point) => (
          <line
            key={`x-${point.label}`}
            x1={point.x}
            x2={point.x}
            y1={padding.top}
            y2={padding.top + chartHeight}
            className="chart-grid-line"
          />
        ))}

        <path d={areaPath} fill="url(#salesArea)" />
        <path d={path} className="chart-line" />

        {points.map((point) => (
          <g key={point.label}>
            <circle cx={point.x} cy={point.y} r="4" className="chart-dot" />
            <text
              x={point.x}
              y={height - 10}
              textAnchor="middle"
              className="chart-axis-label"
            >
              {point.label}
            </text>
          </g>
        ))}
      </svg>
    </div>
  );
};

const DonutChart = ({ data }) => {
  const total = data.reduce((sum, item) => sum + Number(item.value || 0), 0);
  const radius = 58;
  const circumference = 2 * Math.PI * radius;
  let offset = 0;

  if (total <= 0) {
    return (
      <div className="dashboard-donut-wrap">
        <div className="dashboard-donut-empty">Sin pagos</div>
      </div>
    );
  }

  return (
    <div className="dashboard-donut-wrap">
      <svg className="dashboard-donut" viewBox="0 0 170 170">
        <circle
          cx="85"
          cy="85"
          r={radius}
          fill="none"
          stroke="#eef3fb"
          strokeWidth="24"
        />

        {data.map((item) => {
          const percent = Number(item.value || 0) / total;
          const dash = percent * circumference;
          const circle = (
            <circle
              key={item.label}
              cx="85"
              cy="85"
              r={radius}
              fill="none"
              stroke={item.color}
              strokeWidth="24"
              strokeDasharray={`${dash} ${circumference - dash}`}
              strokeDashoffset={-offset}
              strokeLinecap="butt"
              transform="rotate(-90 85 85)"
            />
          );

          offset += dash;
          return circle;
        })}

        <circle cx="85" cy="85" r="34" fill="#ffffff" />
      </svg>

      <div className="dashboard-donut-legend">
        {data.map((item) => {
          const percent = total > 0 ? Math.round((item.value / total) * 100) : 0;

          return (
            <div key={item.label}>
              <span style={{ background: item.color }}></span>
              <p>{item.label}</p>
              <strong>{percent}%</strong>
            </div>
          );
        })}
      </div>
    </div>
  );
};

const HorizontalBarChart = ({ data }) => {
  const max = Math.max(...data.map((item) => item.value), 1);

  if (data.length === 0) {
    return (
      <div className="dashboard-empty dashboard-empty-tall">
        <i className="bi bi-bar-chart"></i>
        No hay detalle suficiente para calcular productos vendidos.
      </div>
    );
  }

  return (
    <div className="dashboard-bar-chart">
      {data.map((item) => (
        <div className="dashboard-bar-row" key={item.label}>
          <span>{item.label}</span>

          <div>
            <strong style={{ width: `${(item.value / max) * 100}%` }}></strong>
          </div>

          <em>{item.value}</em>
        </div>
      ))}
    </div>
  );
};

export default HomePage;