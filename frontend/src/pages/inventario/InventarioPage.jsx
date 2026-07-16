import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  obtenerResumenInventario,
  listarProductosInventario,
  listarMovimientosInventario,
  crearProductoInventario,
} from '../../services/inventarioService';

const UNIDADES_MEDIDA = [
  { value: 'UNIDAD', label: 'Unidad' },
  { value: 'KG', label: 'Kilogramo' },
  { value: 'GALON', label: 'Galon' },
  { value: 'CUARTO', label: 'Cuarto' },
  { value: 'BOLSA', label: 'Bolsa' },
  { value: 'METRO', label: 'Metro' },
  { value: 'M2', label: 'Metro cuadrado' },
  { value: 'CAJA', label: 'Caja' },
  { value: 'ROLLO', label: 'Rollo' },
  { value: 'JUEGO', label: 'Juego' },
  { value: 'BARRA', label: 'Barra' },
  { value: 'PIE', label: 'Pie' },
  { value: 'PLANCHA', label: 'Plancha' },
];

const PRODUCTO_INICIAL = {
  codigo: '',
  nombre: '',
  descripcion: '',
  unidad: 'UNIDAD',
  stockActual: 0,
  stockMinimo: 0,
};

const InventarioPage = () => {
  const [tabActivo, setTabActivo] = useState('productos');

  const [resumen, setResumen] = useState(null);
  const [productos, setProductos] = useState([]);
  const [movimientos, setMovimientos] = useState([]);

  const [busqueda, setBusqueda] = useState('');
  const [estado, setEstado] = useState('');

  const [cargando, setCargando] = useState(true);
  const [guardando, setGuardando] = useState(false);
  const [error, setError] = useState('');
  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);

  const [nuevoProducto, setNuevoProducto] = useState(PRODUCTO_INICIAL);

  const formatearNumero = (valor) => {
    const numero = Number(valor || 0);

    return numero.toLocaleString('es-PE', {
      minimumFractionDigits: Number.isInteger(numero) ? 0 : 2,
      maximumFractionDigits: 2,
    });
  };

  const formatearFecha = (fechaHora) => {
    if (!fechaHora) return '-';

    const fecha = new Date(fechaHora);

    return fecha.toLocaleDateString('es-PE', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const formatearHora = (fechaHora) => {
    if (!fechaHora) return '';

    const fecha = new Date(fechaHora);

    return fecha.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const obtenerUnidadTexto = (unidad) => {
    return UNIDADES_MEDIDA.find((item) => item.value === unidad)?.label || unidad || '-';
  };

  const obtenerEstadoTexto = (estadoProducto) => {
    if (estadoProducto === 'SIN_STOCK') return 'Sin stock';
    if (estadoProducto === 'STOCK_BAJO') return 'Stock bajo';
    return 'Disponible';
  };

  const obtenerEstadoVariant = (estadoProducto) => {
    if (estadoProducto === 'SIN_STOCK') return 'danger';
    if (estadoProducto === 'STOCK_BAJO') return 'warning';
    return 'success';
  };

  const calcularPorcentajeStock = (producto) => {
    const minimo = Number(producto.stockMinimo || 0);
    const actual = Number(producto.stockActual || 0);

    if (minimo <= 0) return actual > 0 ? 100 : 0;

    const porcentaje = Math.round((actual / minimo) * 100);
    return Math.min(Math.max(porcentaje, 0), 100);
  };

  const cargarDatos = async () => {
    setCargando(true);
    setError('');

    const [resumenResult, productosResult, movimientosResult] =
      await Promise.allSettled([
        obtenerResumenInventario(),
        listarProductosInventario({
          busqueda,
          estado,
        }),
        listarMovimientosInventario(),
      ]);

    if (resumenResult.status === 'fulfilled') {
      setResumen(resumenResult.value.data);
    } else {
      console.error('Error cargando dashboard de inventario:', resumenResult.reason);
      setError('No se pudo cargar el resumen de inventario.');
    }

    if (productosResult.status === 'fulfilled') {
      setProductos(productosResult.value.data || []);
    } else {
      console.error('Error cargando productos de inventario:', productosResult.reason);
      setProductos([]);
      setError('No se pudo cargar la lista de productos.');
    }

    if (movimientosResult.status === 'fulfilled') {
      setMovimientos(movimientosResult.value.data || []);
    } else {
      console.error('Error cargando movimientos de inventario:', movimientosResult.reason);
      setMovimientos([]);
    }

    setCargando(false);
  };

  useEffect(() => {
    cargarDatos();
  }, [estado]);

  const buscarProductos = (e) => {
    e.preventDefault();
    cargarDatos();
  };

  const limpiarFiltros = () => {
    setBusqueda('');
    setEstado('');
  };

  const handleChangeCrear = (e) => {
    const { name, value } = e.target;

    setNuevoProducto({
      ...nuevoProducto,
      [name]: value,
    });
  };

  const cerrarModalCrear = () => {
    setMostrarModalCrear(false);
    setNuevoProducto(PRODUCTO_INICIAL);
  };

  const handleCrearProducto = async (e) => {
    e.preventDefault();

    try {
      setGuardando(true);
      await crearProductoInventario(nuevoProducto);
      cerrarModalCrear();
      await cargarDatos();
    } catch (errorCrear) {
      console.error('Error creando producto:', errorCrear);

      const mensaje =
        errorCrear.response?.data?.message ||
        errorCrear.response?.data?.error ||
        'No se pudo crear el producto';

      alert(mensaje);
    } finally {
      setGuardando(false);
    }
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando inventario...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Inventario
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-box-seam me-2 text-primary"></i>
          Inventario
        </h4>

        <p className="page-subtitle mb-0">Control de stock y productos</p>
      </div>

      {error && (
        <div className="alert alert-warning">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {error}
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Productos"
            value={resumen?.totalProductos ?? 0}
            description="En catalogo activo"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Sin Stock"
            value={resumen?.sinStock ?? 0}
            description="Requieren reposicion"
            color="danger"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Stock Bajo"
            value={resumen?.stockBajo ?? 0}
            description="Debajo del minimo"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Disponible"
            value={resumen?.disponible ?? 0}
            description="Sobre el nivel minimo"
            color="success"
          />
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3 gap-3 flex-wrap">
        <div className="app-tabs d-flex gap-1 flex-wrap">
          <button
            type="button"
            className={`app-tab-btn ${tabActivo === 'productos' ? 'active' : ''}`}
            onClick={() => setTabActivo('productos')}
          >
            <i className="bi bi-box-seam me-2"></i>
            Productos y Stock
          </button>

          <button
            type="button"
            className={`app-tab-btn ${tabActivo === 'movimientos' ? 'active' : ''}`}
            onClick={() => setTabActivo('movimientos')}
          >
            <i className="bi bi-clock-history me-2"></i>
            Historial de Movimientos
          </button>
        </div>

        {tabActivo === 'productos' && (
          <button
            type="button"
            className="btn btn-primary app-btn-primary"
            onClick={() => setMostrarModalCrear(true)}
          >
            <i className="bi bi-plus-lg me-2"></i>
            Nuevo Producto
          </button>
        )}
      </div>

      {tabActivo === 'productos' && (
        <>
          <form onSubmit={buscarProductos} className="mb-3">
            <div className="row g-2">
              <div className="col-12 col-lg-6">
                <div className="input-group">
                  <span className="input-group-text bg-white">
                    <i className="bi bi-search text-muted"></i>
                  </span>

                  <input
                    type="text"
                    className="form-control app-input"
                    placeholder="Buscar por nombre o codigo..."
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                  />
                </div>
              </div>

              <div className="col-12 col-md-4 col-lg-3">
                <select
                  className="form-select app-select"
                  value={estado}
                  onChange={(e) => setEstado(e.target.value)}
                >
                  <option value="">Todos</option>
                  <option value="DISPONIBLE">Disponible</option>
                  <option value="STOCK_BAJO">Stock bajo</option>
                  <option value="SIN_STOCK">Sin stock</option>
                </select>
              </div>

              <div className="col-12 col-md-4 col-lg-2">
                <button className="btn btn-outline-primary w-100" type="submit">
                  <i className="bi bi-search me-2"></i>
                  Buscar
                </button>
              </div>

              <div className="col-12 col-md-4 col-lg-1">
                <button
                  className="btn btn-outline-secondary w-100"
                  type="button"
                  onClick={limpiarFiltros}
                  title="Limpiar filtros"
                >
                  <i className="bi bi-eraser"></i>
                </button>
              </div>
            </div>
          </form>

          <div className="app-card">
            <div className="table-responsive">
              <table className="table align-middle mb-0 app-table inventory-table">
                <thead>
                  <tr>
                    <th>Codigo</th>
                    <th>Producto</th>
                    <th>Descripcion</th>
                    <th>Nivel de Stock</th>
                    <th className="text-center">Stock Actual</th>
                    <th className="text-center">Minimo</th>
                    <th>Unidad</th>
                    <th>Estado</th>
                    <th>Actualizado</th>
                    <th className="text-center">Accion</th>
                  </tr>
                </thead>

                <tbody>
                  {productos.length === 0 ? (
                    <tr>
                      <td colSpan="10" className="text-center py-5 text-muted">
                        <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                        No se encontraron productos.
                      </td>
                    </tr>
                  ) : (
                    productos.map((producto) => {
                      const porcentaje = calcularPorcentajeStock(producto);

                      return (
                        <tr key={producto.idProducto}>
                          <td className="text-muted fw-semibold">
                            {producto.codigo}
                          </td>

                          <td>
                            <strong>{producto.nombre}</strong>
                            <br />
                            <span className="text-muted small">
                              ID: {producto.idProducto}
                            </span>
                          </td>

                          <td className="text-muted">
                            {producto.descripcion || 'Sin descripcion'}
                          </td>

                          <td>
                            <div className="d-flex align-items-center gap-2">
                              <div
                                className="progress flex-grow-1"
                                style={{ height: '7px', minWidth: '110px' }}
                              >
                                <div
                                  className={`progress-bar ${
                                    producto.estado === 'DISPONIBLE'
                                      ? 'bg-success'
                                      : producto.estado === 'STOCK_BAJO'
                                      ? 'bg-warning'
                                      : 'bg-danger'
                                  }`}
                                  style={{ width: `${porcentaje}%` }}
                                ></div>
                              </div>

                              <span className="text-muted small">
                                {porcentaje}%
                              </span>
                            </div>
                          </td>

                          <td className="text-center fw-bold">
                            {formatearNumero(producto.stockActual)}
                          </td>

                          <td className="text-center text-muted">
                            {formatearNumero(producto.stockMinimo)}
                          </td>

                          <td>{obtenerUnidadTexto(producto.unidad)}</td>

                          <td>
                            <BadgeStatus
                              variant={obtenerEstadoVariant(producto.estado)}
                            >
                              {obtenerEstadoTexto(producto.estado)}
                            </BadgeStatus>
                          </td>

                          <td>
                            <strong>{formatearFecha(producto.fechaHoraActualizacion)}</strong>
                            <br />
                            <span className="text-muted small">
                              {formatearHora(producto.fechaHoraActualizacion)}
                            </span>
                          </td>

                          <td className="text-center">
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-secondary"
                              title="Pendiente de endpoint para ajustar stock"
                              onClick={() =>
                                alert('Pendiente de backend: endpoint para ajuste manual de stock')
                              }
                            >
                              <i className="bi bi-arrow-clockwise"></i>
                            </button>
                          </td>
                        </tr>
                      );
                    })
                  )}
                </tbody>
              </table>
            </div>
          </div>

          <div className="alert alert-info mt-3">
            <i className="bi bi-info-circle me-2"></i>
            Pendiente de backend: categoria, precio costo, precio venta, editar producto,
            eliminar producto y ajuste manual de stock.
          </div>
        </>
      )}

      {tabActivo === 'movimientos' && (
        <div className="app-card p-3">
          <h6 className="fw-bold mb-4">
            <i className="bi bi-clock-history me-2 text-primary"></i>
            Historial de Movimientos de Inventario
          </h6>

          <div className="table-responsive">
            <table className="table align-middle app-table">
              <thead>
                <tr>
                  <th>Fecha y Hora</th>
                  <th>Producto</th>
                  <th>Tipo</th>
                  <th className="text-center">Cantidad</th>
                  <th>Motivo / Referencia</th>
                  <th className="text-center">Stock Anterior</th>
                  <th className="text-center">Stock Actual</th>
                  <th>Usuario</th>
                </tr>
              </thead>

              <tbody>
                {movimientos.length === 0 ? (
                  <tr>
                    <td colSpan="8" className="text-center py-5 text-muted">
                      <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                      No hay movimientos para mostrar.
                    </td>
                  </tr>
                ) : (
                  movimientos.map((movimiento) => (
                    <tr key={movimiento.idMovimiento}>
                      <td>
                        <strong>{formatearFecha(movimiento.fechaHora)}</strong>
                        <br />
                        <span className="text-muted small">
                          {formatearHora(movimiento.fechaHora)}
                        </span>
                      </td>

                      <td>{movimiento.producto}</td>

                      <td>
                        <span
                          className={`badge app-badge ${
                            movimiento.tipo === 'ENTRADA'
                              ? 'bg-success bg-opacity-10 text-success'
                              : 'bg-primary bg-opacity-10 text-primary'
                          }`}
                        >
                          {movimiento.tipo === 'ENTRADA' ? 'Entrada' : 'Salida'}
                        </span>
                      </td>

                      <td className="text-center fw-bold text-primary">
                        {movimiento.tipo === 'ENTRADA' ? '+' : '-'}
                        {formatearNumero(movimiento.cantidad)}
                      </td>

                      <td>{movimiento.motivo}</td>

                      <td className="text-center text-muted">
                        {formatearNumero(movimiento.stockAnterior)}
                      </td>

                      <td className="text-center fw-bold">
                        {formatearNumero(movimiento.stockActual)}
                      </td>

                      <td>{movimiento.usuario}</td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {mostrarModalCrear && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleCrearProducto}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-plus-lg me-2 text-primary"></i>
                    Nuevo Producto
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalCrear}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-12 col-md-4">
                      <label className="form-label">Codigo *</label>
                      <input
                        type="text"
                        name="codigo"
                        className="form-control app-input"
                        value={nuevoProducto.codigo}
                        onChange={handleChangeCrear}
                        placeholder="Ej: TEL-001"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-8">
                      <label className="form-label">Producto *</label>
                      <input
                        type="text"
                        name="nombre"
                        className="form-control app-input"
                        value={nuevoProducto.nombre}
                        onChange={handleChangeCrear}
                        placeholder="Ej: Tela Terciopelo Azul"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label">Unidad *</label>
                      <select
                        name="unidad"
                        className="form-select app-select"
                        value={nuevoProducto.unidad}
                        onChange={handleChangeCrear}
                        required
                      >
                        {UNIDADES_MEDIDA.map((unidad) => (
                          <option key={unidad.value} value={unidad.value}>
                            {unidad.label}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label">Cantidad inicial *</label>
                      <input
                        type="number"
                        name="stockActual"
                        className="form-control app-input"
                        value={nuevoProducto.stockActual}
                        onChange={handleChangeCrear}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label">Stock minimo *</label>
                      <input
                        type="number"
                        name="stockMinimo"
                        className="form-control app-input"
                        value={nuevoProducto.stockMinimo}
                        onChange={handleChangeCrear}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Descripcion</label>
                      <textarea
                        name="descripcion"
                        className="form-control app-input"
                        value={nuevoProducto.descripcion}
                        onChange={handleChangeCrear}
                        rows="3"
                        placeholder="Descripcion breve del producto"
                      ></textarea>
                    </div>
                  </div>

                  <div className="alert alert-info mt-3 mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Este formulario usa el endpoint real POST /api/inventario/productos.
                    Categoria, precios y ajuste manual quedan pendientes porque aun no
                    existen en el backend.
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalCrear}
                    disabled={guardando}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                    disabled={guardando}
                  >
                    <i className="bi bi-save me-2"></i>
                    {guardando ? 'Guardando...' : 'Guardar Producto'}
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

export default InventarioPage;