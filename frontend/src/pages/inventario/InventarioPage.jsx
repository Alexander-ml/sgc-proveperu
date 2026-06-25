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

const CATEGORIAS = [
  'Telas y Tapicería',
  'Espumas y Rellenos',
  'Pegamentos',
  'Maderas y Tableros',
  'Accesorios',
];

const InventarioPage = () => {
  const [tabActivo, setTabActivo] = useState('productos');

  const [resumen, setResumen] = useState(null);
  const [productos, setProductos] = useState([]);
  const [movimientos, setMovimientos] = useState([]);

  const [busqueda, setBusqueda] = useState('');
  const [categoria, setCategoria] = useState('');
  const [estado, setEstado] = useState('');

  const [cargando, setCargando] = useState(true);
  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);

  const [nuevoProducto, setNuevoProducto] = useState({
    codigo: '',
    nombre: '',
    unidad: '',
    categoria: 'Telas y Tapicería',
    stockActual: 0,
    stockMinimo: 0,
    precioCosto: 0,
    precioVenta: 0,
  });

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
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
    if (!fechaHora) return '-';

    const fecha = new Date(fechaHora);

    return fecha.toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
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
    const minimo = Number(producto.stockMinimo || 1);
    const actual = Number(producto.stockActual || 0);

    const porcentaje = Math.round((actual / minimo) * 100);

    return Math.min(porcentaje, 100);
  };

  const cargarDatos = async () => {
    try {
      setCargando(true);

      const resumenResponse = await obtenerResumenInventario();
      const productosResponse = await listarProductosInventario({
        busqueda,
        categoria,
        estado,
      });
      const movimientosResponse = await listarMovimientosInventario();

      setResumen(resumenResponse.data);
      setProductos(productosResponse.data || []);
      setMovimientos(movimientosResponse.data || []);
    } catch (error) {
      console.error('Error cargando inventario:', error);
      alert('No se pudo cargar el inventario');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, [categoria, estado]);

  const buscarProductos = (e) => {
    e.preventDefault();
    cargarDatos();
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

    setNuevoProducto({
      codigo: '',
      nombre: '',
      unidad: '',
      categoria: 'Telas y Tapicería',
      stockActual: 0,
      stockMinimo: 0,
      precioCosto: 0,
      precioVenta: 0,
    });
  };

  const handleCrearProducto = async (e) => {
    e.preventDefault();

    try {
      await crearProductoInventario(nuevoProducto);
      cerrarModalCrear();
      cargarDatos();
    } catch (error) {
      console.error('Error creando producto:', error);
      alert('No se pudo crear el producto');
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

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Productos"
            value={resumen?.totalProductos ?? 0}
            description="En catálogo activo"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Sin Stock"
            value={resumen?.sinStock ?? 0}
            description="Requieren reabastecimiento urgente"
            color="danger"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Stock Bajo"
            value={resumen?.stockBajo ?? 0}
            description="Por debajo del mínimo"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Disponible"
            value={resumen?.disponible ?? 0}
            description="Sobre el nivel mínimo"
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
              <div className="col-12 col-lg-5">
                <div className="input-group">
                  <span className="input-group-text bg-white">
                    <i className="bi bi-search text-muted"></i>
                  </span>

                  <input
                    type="text"
                    className="form-control app-input"
                    placeholder="Buscar por nombre o código..."
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                  />
                </div>
              </div>

              <div className="col-12 col-md-4 col-lg-3">
                <select
                  className="form-select app-select"
                  value={categoria}
                  onChange={(e) => setCategoria(e.target.value)}
                >
                  <option value="">Todas las categorías</option>
                  {CATEGORIAS.map((cat) => (
                    <option key={cat} value={cat}>
                      {cat}
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-12 col-md-4 col-lg-2">
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
            </div>
          </form>

          <div className="app-card">
            <div className="table-responsive">
              <table className="table align-middle mb-0 app-table">
                <thead>
                  <tr>
                    <th>Código</th>
                    <th>Producto</th>
                    <th>Categoría</th>
                    <th>Nivel de Stock</th>
                    <th className="text-center">Stock Actual</th>
                    <th className="text-center">Mínimo</th>
                    <th className="text-end">P. Costo</th>
                    <th className="text-end">P. Venta</th>
                    <th>Estado</th>
                    <th className="text-center">Acción</th>
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
                              {producto.unidad}
                            </span>
                          </td>

                          <td>{producto.categoria}</td>

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
                            {producto.stockActual}
                          </td>

                          <td className="text-center text-muted">
                            {producto.stockMinimo}
                          </td>

                          <td className="text-end">
                            {formatearMoneda(producto.precioCosto)}
                          </td>

                          <td className="text-end fw-bold">
                            {formatearMoneda(producto.precioVenta)}
                          </td>

                          <td>
                            <BadgeStatus
                              variant={obtenerEstadoVariant(producto.estado)}
                            >
                              {obtenerEstadoTexto(producto.estado)}
                            </BadgeStatus>
                          </td>

                          <td className="text-center">
                            <button
                              type="button"
                              className="btn btn-sm btn-outline-secondary"
                              title="Actualizar stock"
                              onClick={() =>
                                alert('Pendiente de endpoint para actualizar stock')
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
                {movimientos.map((movimiento) => (
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
                      {movimiento.cantidad}
                    </td>

                    <td>{movimiento.motivo}</td>

                    <td className="text-center text-muted">
                      {movimiento.stockAnterior}
                    </td>

                    <td className="text-center fw-bold">
                      {movimiento.stockActual}
                    </td>

                    <td>{movimiento.usuario}</td>
                  </tr>
                ))}
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
                      <label className="form-label">Código</label>
                      <input
                        type="text"
                        name="codigo"
                        className="form-control app-input"
                        value={nuevoProducto.codigo}
                        onChange={handleChangeCrear}
                        required
                      />
                    </div>

                    <div className="col-12 col-md-8">
                      <label className="form-label">Producto</label>
                      <input
                        type="text"
                        name="nombre"
                        className="form-control app-input"
                        value={nuevoProducto.nombre}
                        onChange={handleChangeCrear}
                        required
                      />
                    </div>

                    <div className="col-12 col-md-4">
                      <label className="form-label">Unidad</label>
                      <input
                        type="text"
                        name="unidad"
                        className="form-control app-input"
                        value={nuevoProducto.unidad}
                        onChange={handleChangeCrear}
                        placeholder="Metro, Unidad, Plancha..."
                        required
                      />
                    </div>

                    <div className="col-12 col-md-8">
                      <label className="form-label">Categoría</label>
                      <select
                        name="categoria"
                        className="form-select app-select"
                        value={nuevoProducto.categoria}
                        onChange={handleChangeCrear}
                        required
                      >
                        {CATEGORIAS.map((cat) => (
                          <option key={cat} value={cat}>
                            {cat}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Stock actual</label>
                      <input
                        type="number"
                        name="stockActual"
                        className="form-control app-input"
                        value={nuevoProducto.stockActual}
                        onChange={handleChangeCrear}
                        min="0"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Stock mínimo</label>
                      <input
                        type="number"
                        name="stockMinimo"
                        className="form-control app-input"
                        value={nuevoProducto.stockMinimo}
                        onChange={handleChangeCrear}
                        min="0"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Precio costo</label>
                      <input
                        type="number"
                        name="precioCosto"
                        className="form-control app-input"
                        value={nuevoProducto.precioCosto}
                        onChange={handleChangeCrear}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Precio venta</label>
                      <input
                        type="number"
                        name="precioVenta"
                        className="form-control app-input"
                        value={nuevoProducto.precioVenta}
                        onChange={handleChangeCrear}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>
                  </div>

                  <div className="alert alert-info mt-3 mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Este registro se guarda temporalmente en el navegador. Cuando
                    tengas el endpoint, se cambiará por una llamada real al backend.
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalCrear}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-save me-2"></i>
                    Guardar Producto
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