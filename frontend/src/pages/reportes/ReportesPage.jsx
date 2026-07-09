import { useCallback, useEffect, useMemo, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import {
  obtenerReporteVentas,
  obtenerReporteInventario,
  obtenerReporteFinanciero,
} from '../../services/reportesService';

const ReportesPage = () => {
  const [tabActivo, setTabActivo] = useState('ventas');
  const [periodo, setPeriodo] = useState('mes');
  const [cargando, setCargando] = useState(true);
  const [reporteVentas, setReporteVentas] = useState(null);
  const [reporteInventario, setReporteInventario] = useState(null);
  const [reporteFinanciero, setReporteFinanciero] = useState(null);

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const periodoTexto = useMemo(() => {
    if (periodo === 'semana') return 'Esta semana';
    if (periodo === 'anio') return 'Este año';
    return 'Este mes';
  }, [periodo]);

  const cargarReportes = useCallback(async () => {
    try {
      setCargando(true);

      const [ventas, inventario, financiero] = await Promise.all([
        obtenerReporteVentas(periodo),
        obtenerReporteInventario(periodo),
        obtenerReporteFinanciero(periodo),
      ]);

      setReporteVentas(ventas);
      setReporteInventario(inventario);
      setReporteFinanciero(financiero);
    } finally {
      setCargando(false);
    }
  }, [periodo]);

  useEffect(() => {
    cargarReportes();
  }, [cargarReportes]);

  const exportarReporte = () => {
    const filas = [];

    if (tabActivo === 'ventas') {
      filas.push(['Reporte de ventas']);
      filas.push(['Métrica', 'Valor']);
      filas.push(['Total ingresos', reporteVentas?.resumen?.totalIngresos || 0]);
      filas.push(['N° ventas', reporteVentas?.resumen?.numeroVentas || 0]);
      filas.push(['Ticket promedio', reporteVentas?.resumen?.ticketPromedio || 0]);
      filas.push(['Venta más alta', reporteVentas?.resumen?.ventaMasAlta || 0]);
      filas.push([]);
      filas.push(['N° Venta', 'Fecha', 'Cliente', 'Productos', 'Pago', 'Total']);

      (reporteVentas?.detalleVentas || []).forEach((venta) => {
        filas.push([
          venta.numeroVenta,
          venta.fecha,
          venta.cliente,
          venta.productos,
          venta.pago,
          venta.total,
        ]);
      });
    }

    if (tabActivo === 'inventario') {
      filas.push(['Reporte de inventario']);
      filas.push(['Métrica', 'Valor']);
      filas.push(['Total productos', reporteInventario?.resumen?.totalProductos || 0]);
      filas.push(['Disponible', reporteInventario?.resumen?.disponible || 0]);
      filas.push(['Stock bajo', reporteInventario?.resumen?.stockBajo || 0]);
      filas.push(['Sin stock', reporteInventario?.resumen?.sinStock || 0]);
      filas.push(['Valor inventario', reporteInventario?.resumen?.valorInventario || 0]);
    }

    if (tabActivo === 'financiero') {
      filas.push(['Reporte financiero']);
      filas.push(['Métrica', 'Valor']);
      filas.push(['Ingresos', reporteFinanciero?.resumen?.ingresos || 0]);
      filas.push(['Egresos', reporteFinanciero?.resumen?.egresos || 0]);
      filas.push(['Saldo', reporteFinanciero?.resumen?.saldo || 0]);
      filas.push([
        'Utilidad estimada',
        reporteFinanciero?.resumen?.utilidadEstimada || 0,
      ]);
    }

    const csv = filas
      .map((fila) =>
        fila
          .map((celda) => `"${String(celda ?? '').replaceAll('"', '""')}"`)
          .join(',')
      )
      .join('\n');

    const blob = new Blob([csv], {
      type: 'text/csv;charset=utf-8;',
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');

    link.href = url;
    link.download = `reporte-${tabActivo}.csv`;
    link.click();

    URL.revokeObjectURL(url);
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando reportes...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="reports-page">
        <div className="reports-header">
          <div>
            <h2>Sistema ProvePeru</h2>
            <p>Reportes gerenciales y análisis del negocio</p>
          </div>

          <button
            type="button"
            className="btn btn-outline-primary"
            onClick={exportarReporte}
          >
            <i className="bi bi-download me-2"></i>
            Exportar reporte
          </button>
        </div>

        <div className="reports-toolbar">
          <div className="reports-period">
            <i className="bi bi-calendar3"></i>

            <select value={periodo} onChange={(e) => setPeriodo(e.target.value)}>
              <option value="semana">Esta semana</option>
              <option value="mes">Este mes</option>
              <option value="anio">Este año</option>
            </select>
          </div>

          <span className="reports-period-pill">{periodoTexto}</span>
        </div>

        <div className="reports-tabs">
          <button
            type="button"
            className={tabActivo === 'ventas' ? 'active' : ''}
            onClick={() => setTabActivo('ventas')}
          >
            <i className="bi bi-bar-chart-line"></i>
            Ventas
          </button>

          <button
            type="button"
            className={tabActivo === 'inventario' ? 'active' : ''}
            onClick={() => setTabActivo('inventario')}
          >
            <i className="bi bi-boxes"></i>
            Inventario
          </button>

          <button
            type="button"
            className={tabActivo === 'financiero' ? 'active' : ''}
            onClick={() => setTabActivo('financiero')}
          >
            <i className="bi bi-graph-up-arrow"></i>
            Financiero
          </button>
        </div>

        {tabActivo === 'ventas' && (
          <ReporteVentas reporte={reporteVentas} formatearMoneda={formatearMoneda} />
        )}

        {tabActivo === 'inventario' && (
          <ReporteInventario
            reporte={reporteInventario}
            formatearMoneda={formatearMoneda}
          />
        )}

        {tabActivo === 'financiero' && (
          <ReporteFinanciero
            reporte={reporteFinanciero}
            formatearMoneda={formatearMoneda}
          />
        )}
      </div>
    </MainLayout>
  );
};

const ReportCard = ({ title, value, description, tone = 'primary' }) => {
  return (
    <div className="reports-card">
      <span>{title}</span>
      <strong className={`reports-value reports-value-${tone}`}>{value}</strong>
      <small>{description}</small>
    </div>
  );
};

const LineChart = ({ data, formatearMoneda }) => {
  const values = data.map((item) => Number(item.total || 0));
  const maxValue = Math.max(...values, 1);

  const points = data.map((item, index) => {
    const x = 48 + index * (640 / Math.max(data.length - 1, 1));
    const y = 230 - (Number(item.total || 0) / maxValue) * 180;

    return { ...item, x, y };
  });

  const path = points
    .map((point, index) => `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`)
    .join(' ');

  return (
    <svg className="reports-line-chart" viewBox="0 0 760 290">
      {[0, 1, 2, 3, 4].map((item) => {
        const y = 230 - item * 45;
        const value = (maxValue / 4) * item;

        return (
          <g key={item}>
            <line x1="48" y1={y} x2="720" y2={y} />
            <text x="8" y={y + 4}>
              {formatearMoneda(value).replace('.00', '')}
            </text>
          </g>
        );
      })}

      <path d={path} />

      {points.map((point) => (
        <g key={point.label}>
          <circle cx={point.x} cy={point.y} r="4" />
          <text x={point.x - 24} y="262">
            {point.label}
          </text>
        </g>
      ))}
    </svg>
  );
};

const HorizontalBarChart = ({ data }) => {
  const maxValue = Math.max(...data.map((item) => Number(item.total || 0)), 1);

  return (
    <div className="reports-bar-chart">
      {data.map((item) => (
        <div className="reports-bar-row" key={item.label}>
          <span>{item.label}</span>

          <div>
            <div
              className="reports-bar-fill"
              style={{
                width: `${(Number(item.total || 0) / maxValue) * 100}%`,
              }}
            ></div>
          </div>

          <strong>{item.total}</strong>
        </div>
      ))}
    </div>
  );
};

const DonutChart = ({ data }) => {
  const total = data.reduce((sum, item) => sum + Number(item.total || 0), 0) || 1;

  let acumulado = 0;

  const gradient = data
    .map((item) => {
      const inicio = (acumulado / total) * 100;
      acumulado += Number(item.total || 0);
      const fin = (acumulado / total) * 100;

      return `${item.color} ${inicio}% ${fin}%`;
    })
    .join(', ');

  return (
    <div className="reports-donut-layout">
      <div
        className="reports-donut"
        style={{
          background: `conic-gradient(${gradient})`,
        }}
      ></div>

      <div className="reports-donut-legend">
        {data.map((item) => (
          <div key={item.label}>
            <span style={{ background: item.color }}></span>
            <p>{item.label}</p>
            <strong>{item.total}%</strong>
          </div>
        ))}
      </div>
    </div>
  );
};

const ReporteVentas = ({ reporte, formatearMoneda }) => {
  return (
    <>
      <div className="reports-grid-4">
        <ReportCard
          title="Total Ingresos"
          value={formatearMoneda(reporte.resumen.totalIngresos)}
          description="Por ventas del período"
          tone="primary"
        />
        <ReportCard
          title="N° de Ventas"
          value={reporte.resumen.numeroVentas}
          description="Transacciones realizadas"
          tone="dark"
        />
        <ReportCard
          title="Ticket Promedio"
          value={formatearMoneda(reporte.resumen.ticketPromedio)}
          description="Por venta"
          tone="dark"
        />
        <ReportCard
          title="Venta Más Alta"
          value={formatearMoneda(reporte.resumen.ventaMasAlta)}
          description="En el período"
          tone="success"
        />
      </div>

      <div className="reports-grid-2">
        <section className="reports-panel">
          <h5>Tendencia de Ventas</h5>
          <p>Ingresos diarios (S/) - Últimos 7 días</p>
          <LineChart
            data={reporte.tendenciaVentas}
            formatearMoneda={formatearMoneda}
          />
        </section>

        <section className="reports-panel">
          <h5>Productos Más Vendidos</h5>
          <p>Por volumen de unidades</p>
          <HorizontalBarChart data={reporte.productosMasVendidos} />
        </section>
      </div>

      <div className="reports-grid-payment">
        <section className="reports-panel">
          <h5>Por Método de Pago</h5>
          <DonutChart data={reporte.metodosPago} />
        </section>

        <section className="reports-panel">
          <h5>Rendimiento por Vendedor</h5>
          <p>Ventas y totales del período</p>

          <div className="table-responsive">
            <table className="table align-middle reports-table">
              <thead>
                <tr>
                  <th>Vendedor</th>
                  <th>N° Ventas</th>
                  <th className="text-end">Total Generado</th>
                  <th className="text-end">Ticket Prom.</th>
                  <th>Participación</th>
                </tr>
              </thead>

              <tbody>
                {reporte.vendedores.map((vendedor) => (
                  <tr key={vendedor.vendedor}>
                    <td>{vendedor.vendedor}</td>
                    <td>{vendedor.numeroVentas}</td>
                    <td className="text-end fw-bold">
                      {formatearMoneda(vendedor.totalGenerado)}
                    </td>
                    <td className="text-end">
                      {formatearMoneda(vendedor.ticketPromedio)}
                    </td>
                    <td>
                      <div className="reports-progress">
                        <div style={{ width: `${vendedor.participacion}%` }}></div>
                      </div>
                      <span className="reports-percent">
                        {vendedor.participacion}%
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>

      <section className="reports-panel">
        <h5>Detalle de Ventas del Período</h5>

        <div className="table-responsive">
          <table className="table align-middle reports-table">
            <thead>
              <tr>
                <th>N° Venta</th>
                <th>Fecha</th>
                <th>Cliente</th>
                <th>Productos</th>
                <th>Pago</th>
                <th className="text-end">Total</th>
              </tr>
            </thead>

            <tbody>
              {reporte.detalleVentas.map((venta) => (
                <tr key={venta.numeroVenta}>
                  <td>
                    <strong className="text-primary">{venta.numeroVenta}</strong>
                  </td>
                  <td>{venta.fecha}</td>
                  <td>{venta.cliente}</td>
                  <td>{venta.productos}</td>
                  <td>{venta.pago}</td>
                  <td className="text-end fw-bold">
                    {formatearMoneda(venta.total)}
                  </td>
                </tr>
              ))}
            </tbody>

            <tfoot>
              <tr>
                <td colSpan="5" className="fw-bold">
                  TOTAL DEL PERÍODO
                </td>
                <td className="text-end fw-bold text-primary">
                  {formatearMoneda(reporte.resumen.totalIngresos)}
                </td>
              </tr>
            </tfoot>
          </table>
        </div>
      </section>
    </>
  );
};

const ReporteInventario = ({ reporte, formatearMoneda }) => {
  return (
    <>
      <div className="reports-grid-4">
        <ReportCard
          title="Total Productos"
          value={reporte.resumen.totalProductos}
          description="Productos monitoreados"
          tone="primary"
        />
        <ReportCard
          title="Disponible"
          value={reporte.resumen.disponible}
          description="Sobre stock mínimo"
          tone="success"
        />
        <ReportCard
          title="Stock Bajo"
          value={reporte.resumen.stockBajo}
          description="Requieren reposición"
          tone="warning"
        />
        <ReportCard
          title="Valor Inventario"
          value={formatearMoneda(reporte.resumen.valorInventario)}
          description="Estimado en stock"
          tone="dark"
        />
      </div>

      <div className="reports-grid-2">
        <section className="reports-panel">
          <h5>Stock por Categoría</h5>
          <p>Unidades disponibles</p>
          <HorizontalBarChart data={reporte.stockPorCategoria} />
        </section>

        <section className="reports-panel">
          <h5>Estado del Inventario</h5>
          <DonutChart data={reporte.estadosStock} />
        </section>
      </div>
    </>
  );
};

const ReporteFinanciero = ({ reporte, formatearMoneda }) => {
  return (
    <>
      <div className="reports-grid-4">
        <ReportCard
          title="Ingresos"
          value={formatearMoneda(reporte.resumen.ingresos)}
          description="Entradas de caja"
          tone="success"
        />
        <ReportCard
          title="Egresos"
          value={formatearMoneda(reporte.resumen.egresos)}
          description="Salidas registradas"
          tone="danger"
        />
        <ReportCard
          title="Saldo Actual"
          value={formatearMoneda(reporte.resumen.saldo)}
          description="Disponible"
          tone="primary"
        />
        <ReportCard
          title="Utilidad Estimada"
          value={formatearMoneda(reporte.resumen.utilidadEstimada)}
          description="Ingresos menos egresos"
          tone="dark"
        />
      </div>

      <div className="reports-grid-2">
        <section className="reports-panel">
          <h5>Flujo de Caja</h5>
          <p>Resumen financiero</p>
          <HorizontalBarChart data={reporte.flujoCaja} />
        </section>

        <section className="reports-panel">
          <h5>Distribución Financiera</h5>
          <DonutChart data={reporte.distribucion} />
        </section>
      </div>
    </>
  );
};

export default ReportesPage;