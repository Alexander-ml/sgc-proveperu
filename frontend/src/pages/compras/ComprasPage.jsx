import { useCallback, useEffect, useMemo, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  obtenerResumenCompras,
  obtenerOpcionesCompra,
  listarCompras,
  listarProveedores,
  listarComprasPorProveedor,
  crearCompra,
  crearProveedor,
  obtenerDetalleCompra,
  cambiarEstadoCompra,
} from '../../services/comprasService';

const ESTADOS_COMPRA = [
  { value: '', label: 'Todos' },
  { value: 'PENDIENTE', label: 'Pendiente' },
  { value: 'PARCIAL', label: 'Parcial' },
  { value: 'RECIBIDO', label: 'Recibida' },
  { value: 'ANULADO', label: 'Anulada' },
];

const ACCIONES_ESTADO_COMPRA = {
  PENDIENTE: [
    {
      estado: 'PARCIAL',
      texto: 'Pasar a parcial',
      icono: 'bi-hourglass-split',
      clase: 'btn-outline-info',
    },
    {
      estado: 'ANULADO',
      texto: 'Anular',
      icono: 'bi-x-circle',
      clase: 'btn-outline-danger',
    },
  ],
  PARCIAL: [
    {
      estado: 'RECIBIDO',
      texto: 'Marcar recibida',
      icono: 'bi-check2-circle',
      clase: 'btn-outline-success',
    },
    {
      estado: 'ANULADO',
      texto: 'Anular',
      icono: 'bi-x-circle',
      clase: 'btn-outline-danger',
    },
  ],
};

const crearProductoCompraVacio = () => ({
  idProducto: '',
  cantidad: 1,
  precioUnitarioCompra: '',
});

const ComprasPage = () => {
  const [tabActivo, setTabActivo] = useState('compras');
  const [resumen, setResumen] = useState(null);
  const [compras, setCompras] = useState([]);
  const [proveedores, setProveedores] = useState([]);
  const [opciones, setOpciones] = useState({
    proveedores: [],
    metodosPago: [],
    productos: [],
  });

  const [busqueda, setBusqueda] = useState('');
  const [estado, setEstado] = useState('');
  const [filtros, setFiltros] = useState({ busqueda: '', estado: '' });

  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');

  const [mostrarModalCompra, setMostrarModalCompra] = useState(false);
  const [mostrarModalProveedor, setMostrarModalProveedor] = useState(false);
  const [guardandoCompra, setGuardandoCompra] = useState(false);
  const [guardandoProveedor, setGuardandoProveedor] = useState(false);

  const [mostrarDetalle, setMostrarDetalle] = useState(false);
  const [detalleCompra, setDetalleCompra] = useState(null);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);
  const [actualizandoEstadoId, setActualizandoEstadoId] = useState(null);

  const [nuevaCompra, setNuevaCompra] = useState({
    idProveedor: '',
    idMetodoPago: '',
    productos: [crearProductoCompraVacio()],
  });

  const [nuevoProveedor, setNuevoProveedor] = useState({
    razonSocial: '',
    ruc: '',
    telefono: '',
    direccion: '',
  });

  const totalNuevaCompra = useMemo(() => {
    return nuevaCompra.productos.reduce((total, producto) => {
      const cantidad = Number(producto.cantidad || 0);
      const precio = Number(producto.precioUnitarioCompra || 0);

      return total + cantidad * precio;
    }, 0);
  }, [nuevaCompra.productos]);

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

  const formatearHora = (fecha) => {
    if (!fecha) return '';

    return new Date(fecha).toLocaleTimeString('es-PE', {
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  const extraerMensajeError = (err, fallback) => {
    return err?.response?.data?.message || fallback;
  };

  const normalizarEstadoCompra = (estadoCompra) => {
    const estadoNormalizado = String(estadoCompra || '').toUpperCase();

    if (estadoNormalizado === 'RECIBIDA') return 'RECIBIDO';
    if (estadoNormalizado === 'RECIBIDAS') return 'RECIBIDO';

    return estadoNormalizado || 'PENDIENTE';
  };

  const obtenerEstadoTexto = (estadoCompra) => {
    const estadoNormalizado = normalizarEstadoCompra(estadoCompra);

    if (estadoNormalizado === 'RECIBIDO') return 'Recibida';
    if (estadoNormalizado === 'PENDIENTE') return 'Pendiente';
    if (estadoNormalizado === 'PARCIAL') return 'Parcial';
    if (estadoNormalizado === 'ANULADO') return 'Anulada';

    return 'Sin estado';
  };

  const obtenerEstadoVariant = (estadoCompra) => {
    const estadoNormalizado = normalizarEstadoCompra(estadoCompra);

    if (estadoNormalizado === 'RECIBIDO') return 'success';
    if (estadoNormalizado === 'PENDIENTE') return 'warning';
    if (estadoNormalizado === 'PARCIAL') return 'info';
    if (estadoNormalizado === 'ANULADO') return 'danger';

    return 'secondary';
  };

  const obtenerNombreProveedor = (proveedor) => {
    return (
      proveedor?.razonSocial ||
      proveedor?.nombre ||
      proveedor?.nombreProveedor ||
      'Proveedor sin nombre'
    );
  };

  const obtenerNombreProducto = (producto) => {
    return (
      producto?.nombreProducto ||
      producto?.producto ||
      producto?.nombre ||
      'Producto'
    );
  };

  const obtenerPrecioDetalleProducto = (producto) => {
    return (
      producto?.precioCompra ||
      producto?.precioUnitarioCompra ||
      producto?.precioUnitario ||
      0
    );
  };

  const obtenerSubtotalDetalleProducto = (producto) => {
    return producto?.subtotal || 0;
  };

  const calcularSubtotalProducto = (producto) => {
    const cantidad = Number(producto.cantidad || 0);
    const precio = Number(producto.precioUnitarioCompra || 0);

    return cantidad * precio;
  };

  const cargarDatos = useCallback(
    async (mostrarIndicador = true) => {
      try {
        if (mostrarIndicador) setCargando(true);

        setError('');

        const [
          resumenResponse,
          comprasResponse,
          proveedoresResponse,
          opcionesResponse,
        ] = await Promise.all([
          obtenerResumenCompras(),
          listarCompras({
            busqueda: filtros.busqueda,
            estado: filtros.estado,
          }),
          listarProveedores(),
          obtenerOpcionesCompra(),
        ]);

        setResumen(resumenResponse.data);
        setCompras(comprasResponse.data || []);
        setProveedores(proveedoresResponse.data || []);
        setOpciones(
          opcionesResponse.data || {
            proveedores: [],
            metodosPago: [],
            productos: [],
          }
        );
      } catch (err) {
        console.error('Error cargando compras:', err);
        setError(
          extraerMensajeError(err, 'No se pudo cargar el módulo de compras.')
        );
        setCompras([]);
        setProveedores([]);
      } finally {
        if (mostrarIndicador) setCargando(false);
      }
    },
    [filtros]
  );

  useEffect(() => {
    cargarDatos(true);
  }, [cargarDatos]);

  const buscarCompras = (e) => {
    e.preventDefault();

    setFiltros({
      busqueda: busqueda.trim(),
      estado,
    });
  };

  const handleChangeCompra = (e) => {
    const { name, value } = e.target;

    setNuevaCompra((compraActual) => ({
      ...compraActual,
      [name]: value,
    }));
  };

  const handleChangeProveedor = (e) => {
    const { name, value } = e.target;

    setNuevoProveedor((proveedorActual) => ({
      ...proveedorActual,
      [name]: value,
    }));
  };

  const handleChangeProductoCompra = (index, campo, valor) => {
    setNuevaCompra((compraActual) => ({
      ...compraActual,
      productos: compraActual.productos.map((producto, productoIndex) =>
        productoIndex === index ? { ...producto, [campo]: valor } : producto
      ),
    }));
  };

  const agregarProductoCompra = () => {
    setNuevaCompra((compraActual) => ({
      ...compraActual,
      productos: [...compraActual.productos, crearProductoCompraVacio()],
    }));
  };

  const quitarProductoCompra = (index) => {
    setNuevaCompra((compraActual) => {
      if (compraActual.productos.length === 1) {
        return {
          ...compraActual,
          productos: [crearProductoCompraVacio()],
        };
      }

      return {
        ...compraActual,
        productos: compraActual.productos.filter(
          (_, productoIndex) => productoIndex !== index
        ),
      };
    });
  };

  const abrirModalCompra = () => {
    setNuevaCompra({
      idProveedor: '',
      idMetodoPago: opciones.metodosPago?.[0]?.idMetodoPago || '',
      productos: [crearProductoCompraVacio()],
    });

    setMostrarModalCompra(true);
  };

  const cerrarModalCompra = () => {
    setMostrarModalCompra(false);

    setNuevaCompra({
      idProveedor: '',
      idMetodoPago: '',
      productos: [crearProductoCompraVacio()],
    });
  };

  const abrirModalProveedor = () => {
    setNuevoProveedor({
      razonSocial: '',
      ruc: '',
      telefono: '',
      direccion: '',
    });

    setMostrarModalProveedor(true);
  };

  const cerrarModalProveedor = () => {
    setMostrarModalProveedor(false);

    setNuevoProveedor({
      razonSocial: '',
      ruc: '',
      telefono: '',
      direccion: '',
    });
  };

  const handleCrearCompra = async (e) => {
    e.preventDefault();

    const productosPayload = nuevaCompra.productos
      .filter(
        (producto) =>
          producto.idProducto &&
          Number(producto.cantidad) > 0 &&
          Number(producto.precioUnitarioCompra) >= 0
      )
      .map((producto) => ({
        idProducto: Number(producto.idProducto),
        cantidad: Number(producto.cantidad),
        precioUnitarioCompra: Number(producto.precioUnitarioCompra),
      }));

    if (!nuevaCompra.idProveedor) {
      alert('Debe seleccionar un proveedor.');
      return;
    }

    if (!nuevaCompra.idMetodoPago) {
      alert('Debe seleccionar un método de pago.');
      return;
    }

    if (productosPayload.length === 0) {
      alert('Debe agregar al menos un producto válido.');
      return;
    }

    const productosUnicos = new Set(
      productosPayload.map((producto) => producto.idProducto)
    );

    if (productosUnicos.size !== productosPayload.length) {
      alert('No puede repetir el mismo producto en la compra.');
      return;
    }

    try {
      setGuardandoCompra(true);
      setMensaje('');

      const response = await crearCompra({
        idProveedor: Number(nuevaCompra.idProveedor),
        idMetodoPago: Number(nuevaCompra.idMetodoPago),
        productos: productosPayload,
      });

      setMensaje(response.message || 'Compra registrada correctamente.');
      cerrarModalCompra();
      await cargarDatos(false);
    } catch (err) {
      console.error('Error creando compra:', err);
      alert(extraerMensajeError(err, 'No se pudo registrar la compra.'));
    } finally {
      setGuardandoCompra(false);
    }
  };

  const handleCrearProveedor = async (e) => {
    e.preventDefault();

    try {
      setGuardandoProveedor(true);
      setMensaje('');

      const response = await crearProveedor({
        razonSocial: nuevoProveedor.razonSocial.trim(),
        ruc: nuevoProveedor.ruc.trim(),
        telefono: nuevoProveedor.telefono.trim() || null,
        direccion: nuevoProveedor.direccion.trim() || null,
      });

      setMensaje(response.message || 'Proveedor registrado correctamente.');
      cerrarModalProveedor();
      await cargarDatos(false);
    } catch (err) {
      console.error('Error creando proveedor:', err);
      alert(extraerMensajeError(err, 'No se pudo registrar el proveedor.'));
    } finally {
      setGuardandoProveedor(false);
    }
  };

  const abrirDetalle = async (idCompra) => {
    try {
      setMostrarDetalle(true);
      setCargandoDetalle(true);
      setDetalleCompra(null);

      const response = await obtenerDetalleCompra(idCompra);

      setDetalleCompra(response.data);
    } catch (err) {
      console.error('Error obteniendo detalle de compra:', err);
      alert(extraerMensajeError(err, 'No se pudo cargar el detalle.'));
      setMostrarDetalle(false);
    } finally {
      setCargandoDetalle(false);
    }
  };

  const cerrarDetalle = () => {
    setMostrarDetalle(false);
    setDetalleCompra(null);
  };

  const handleCambiarEstado = async (compra, accion) => {
    if (!accion) return;

    const confirmar = window.confirm(
      `¿Cambiar la compra ${compra.numeroCompra} a ${obtenerEstadoTexto(
        accion.estado
      )}?`
    );

    if (!confirmar) return;

    try {
      setActualizandoEstadoId(compra.idCompra);
      setMensaje('');

      const response = await cambiarEstadoCompra(compra.idCompra, accion.estado);

      if (
        detalleCompra &&
        Number(detalleCompra.idCompra) === Number(compra.idCompra)
      ) {
        setDetalleCompra(response.data);
      }

      setMensaje(response.message || 'Estado actualizado correctamente.');
      await cargarDatos(false);
    } catch (err) {
      console.error('Error cambiando estado de compra:', err);
      alert(extraerMensajeError(err, 'No se pudo cambiar el estado.'));
    } finally {
      setActualizandoEstadoId(null);
    }
  };

  const renderEstadoCompra = (compra) => {
    const estadoNormalizado = normalizarEstadoCompra(compra.estado);
    const acciones = ACCIONES_ESTADO_COMPRA[estadoNormalizado] || [];
    const estaActualizando = actualizandoEstadoId === compra.idCompra;

    return (
      <div className="purchase-status-actions">
        <BadgeStatus variant={obtenerEstadoVariant(compra.estado)}>
          {obtenerEstadoTexto(compra.estado)}
        </BadgeStatus>

        {acciones.map((accion) => (
          <button
            key={accion.estado}
            type="button"
            className={`btn btn-sm purchase-status-next ${accion.clase}`}
            disabled={estaActualizando}
            onClick={() => handleCambiarEstado(compra, accion)}
          >
            {estaActualizando ? (
              <span className="spinner-border spinner-border-sm me-1" />
            ) : (
              <i className={`bi ${accion.icono} me-1`}></i>
            )}
            {accion.texto}
          </button>
        ))}
      </div>
    );
  };

  const filtrarComprasPorProveedor = async (proveedor) => {
  const nombreProveedor = obtenerNombreProveedor(proveedor);

  try {
    setCargando(true);
    setTabActivo('compras');
    setBusqueda(nombreProveedor);
    setEstado('');
    setFiltros({
      busqueda: nombreProveedor,
      estado: '',
    });

    const response = await listarComprasPorProveedor(proveedor.idProveedor);
    setCompras(response.data || []);
  } catch (error) {
    console.error('Error listando compras por proveedor:', error);
    alert('No se pudieron cargar las compras del proveedor');
  } finally {
    setCargando(false);
  }
};

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando compras...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Compras
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-bag-check me-2 text-primary"></i>
          Compras
        </h4>

        <p className="page-subtitle mb-0">
          Gestión de órdenes de compra y proveedores
        </p>
      </div>

      {error && (
        <div className="alert alert-danger">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {error}
        </div>
      )}

      {mensaje && (
        <div className="alert alert-success d-flex justify-content-between align-items-center">
          <span>
            <i className="bi bi-check-circle me-2"></i>
            {mensaje}
          </span>

          <button
            type="button"
            className="btn-close"
            onClick={() => setMensaje('')}
          ></button>
        </div>
      )}

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total compras"
            value={resumen?.totalCompras ?? 0}
            description="Registradas"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Monto total"
            value={formatearMoneda(resumen?.montoTotalInvertido ?? 0)}
            description="Invertido en stock"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Recibidas"
            value={resumen?.comprasRecibidas ?? 0}
            description="Completadas"
            color="info"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Pendientes"
            value={resumen?.comprasPendientes ?? 0}
            description="Por recibir"
            color="warning"
          />
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3 gap-3 flex-wrap">
        <div className="app-tabs d-flex gap-1 flex-wrap">
          <button
            type="button"
            className={`app-tab-btn ${
              tabActivo === 'compras' ? 'active' : ''
            }`}
            onClick={() => setTabActivo('compras')}
          >
            <i className="bi bi-truck me-2"></i>
            Órdenes de Compra
          </button>

          <button
            type="button"
            className={`app-tab-btn ${
              tabActivo === 'proveedores' ? 'active' : ''
            }`}
            onClick={() => setTabActivo('proveedores')}
          >
            <i className="bi bi-box-seam me-2"></i>
            Proveedores
          </button>
        </div>

        {tabActivo === 'compras' && (
          <button
            type="button"
            className="btn btn-primary app-btn-primary"
            onClick={abrirModalCompra}
          >
            <i className="bi bi-plus-lg me-2"></i>
            Nueva Compra
          </button>
        )}

        {tabActivo === 'proveedores' && (
          <button
            type="button"
            className="btn btn-primary app-btn-primary"
            onClick={abrirModalProveedor}
          >
            <i className="bi bi-plus-lg me-2"></i>
            Nuevo Proveedor
          </button>
        )}
      </div>

      {tabActivo === 'compras' && (
        <>
          <form onSubmit={buscarCompras} className="mb-3">
            <div className="row g-2">
              <div className="col-12 col-md-6 col-lg-4">
                <div className="input-group">
                  <span className="input-group-text bg-white">
                    <i className="bi bi-search text-muted"></i>
                  </span>

                  <input
                    type="text"
                    className="form-control app-input"
                    placeholder="Buscar por N° o proveedor..."
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                  />
                </div>
              </div>

              <div className="col-12 col-md-3 col-lg-2">
                <select
                  className="form-select app-select"
                  value={estado}
                  onChange={(e) => {
                    const nuevoEstado = e.target.value;

                    setEstado(nuevoEstado);
                    setFiltros({
                      busqueda: busqueda.trim(),
                      estado: nuevoEstado,
                    });
                  }}
                >
                  {ESTADOS_COMPRA.map((estadoCompra) => (
                    <option key={estadoCompra.value} value={estadoCompra.value}>
                      {estadoCompra.label}
                    </option>
                  ))}
                </select>
              </div>

              <div className="col-12 col-md-3 col-lg-2">
                <button className="btn btn-outline-primary w-100" type="submit">
                  <i className="bi bi-search me-2"></i>
                  Buscar
                </button>
              </div>
            </div>
          </form>

          <div className="app-card">
            <div className="table-responsive">
              <table className="table align-middle mb-0 app-table purchases-table">
                <thead>
                  <tr>
                    <th>N° Compra</th>
                    <th>Fecha</th>
                    <th>Proveedor</th>
                    <th className="text-center">Productos</th>
                    <th className="text-end">Total</th>
                    <th>Método Pago</th>
                    <th>Estado</th>
                    <th>Registrado por</th>
                    <th className="text-center">Ver</th>
                  </tr>
                </thead>

                <tbody>
                  {compras.length === 0 ? (
                    <tr>
                      <td colSpan="9" className="text-center py-5 text-muted">
                        <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                        No se encontraron compras registradas.
                      </td>
                    </tr>
                  ) : (
                    compras.map((compra) => (
                      <tr key={compra.idCompra}>
                        <td>
                          <strong className="text-primary">
                            {compra.numeroCompra}
                          </strong>
                        </td>

                        <td>
                          <strong>{formatearFecha(compra.fecha)}</strong>
                          <br />
                          <span className="text-muted small">
                            {formatearHora(compra.fecha)}
                          </span>
                        </td>

                        <td>{compra.proveedor}</td>

                        <td className="text-center fw-bold">
                          {compra.productos}
                        </td>

                        <td className="text-end fw-bold">
                          {formatearMoneda(compra.total)}
                        </td>

                        <td>{compra.metodoPago}</td>

                        <td>{renderEstadoCompra(compra)}</td>

                        <td>{compra.registradoPor}</td>

                        <td className="text-center">
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => abrirDetalle(compra.idCompra)}
                            title="Ver detalle"
                          >
                            <i className="bi bi-eye"></i>
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </>
      )}

      {tabActivo === 'proveedores' && (
        <>
          <div className="d-flex justify-content-between align-items-center mb-3">
            <p className="text-muted mb-0">
              {proveedores.length} proveedores registrados
            </p>
          </div>

          <div className="row g-3">
            {proveedores.length === 0 ? (
              <div className="col-12">
                <div className="alert alert-info">
                  <i className="bi bi-info-circle me-2"></i>
                  No hay proveedores registrados.
                </div>
              </div>
            ) : (
              proveedores.map((proveedor) => (
                <div
                  className="col-12 col-md-6 col-xl-4"
                  key={proveedor.idProveedor}
                >
                  <div className="app-card app-card-hover p-3 h-100">
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div
                        className="rounded bg-primary bg-opacity-10 text-primary d-flex align-items-center justify-content-center"
                        style={{ width: '48px', height: '48px' }}
                      >
                        <i className="bi bi-truck fs-5"></i>
                      </div>

                      <span className="badge bg-success bg-opacity-10 text-success app-badge">
                        {proveedor.estado || 'ACTIVO'}
                      </span>
                    </div>

                    <h6 className="fw-bold mb-1">
                      {obtenerNombreProveedor(proveedor)}
                    </h6>

                    <p className="text-muted mb-3">
                      RUC: {proveedor.ruc || '-'}
                    </p>

                    <hr />

                    <p className="mb-2">
                      <i className="bi bi-telephone me-2 text-primary"></i>
                      {proveedor.telefono || 'Sin teléfono'}
                    </p>

                    <p className="mb-3">
                      <i className="bi bi-geo-alt me-2 text-primary"></i>
                      {proveedor.direccion || 'Sin dirección'}
                    </p>

                    <hr />

                    <div className="d-flex justify-content-end align-items-center">
                      <button
                        type="button"
                        className="btn btn-link text-decoration-none p-0"
                        onClick={() => filtrarComprasPorProveedor(proveedor)}
                      >
                        Ver compras
                        <i className="bi bi-chevron-right ms-1"></i>
                      </button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}

      {mostrarModalCompra && (
        <div className="purchase-modal-overlay">
          <div className="purchase-modal purchase-modal-xl">
            <div className="modal-content">
              <form onSubmit={handleCrearCompra}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-plus-lg me-2 text-primary"></i>
                    Registrar Nueva Compra
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalCompra}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-6">
                      <label className="form-label">Proveedor *</label>
                      <select
                        name="idProveedor"
                        className="form-select app-select"
                        value={nuevaCompra.idProveedor}
                        onChange={handleChangeCompra}
                        required
                      >
                        <option value="">Seleccionar proveedor...</option>

                        {opciones.proveedores.map((proveedor) => (
                          <option
                            key={proveedor.idProveedor}
                            value={proveedor.idProveedor}
                          >
                            {proveedor.razonSocial}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Método de Pago *</label>
                      <select
                        name="idMetodoPago"
                        className="form-select app-select"
                        value={nuevaCompra.idMetodoPago}
                        onChange={handleChangeCompra}
                        required
                      >
                        <option value="">Seleccionar método...</option>

                        {opciones.metodosPago.map((metodoPago) => (
                          <option
                            key={metodoPago.idMetodoPago}
                            value={metodoPago.idMetodoPago}
                          >
                            {metodoPago.nombreMetodoPago}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div className="d-flex justify-content-between align-items-center mb-2">
                    <label className="form-label mb-0">Productos *</label>

                    <button
                      type="button"
                      className="btn btn-sm btn-outline-primary"
                      onClick={agregarProductoCompra}
                    >
                      <i className="bi bi-plus-lg me-1"></i>
                      Agregar producto
                    </button>
                  </div>

                  <div className="table-responsive border rounded">
                    <table className="table align-middle mb-0 app-table">
                      <thead>
                        <tr>
                          <th style={{ minWidth: '260px' }}>Producto</th>
                          <th style={{ width: '130px' }} className="text-end">
                            Cantidad
                          </th>
                          <th style={{ width: '160px' }} className="text-end">
                            P. Compra
                          </th>
                          <th style={{ width: '160px' }} className="text-end">
                            Subtotal
                          </th>
                          <th style={{ width: '70px' }}></th>
                        </tr>
                      </thead>

                      <tbody>
                        {nuevaCompra.productos.map((producto, index) => (
                          <tr key={index}>
                            <td>
                              <select
                                className="form-select app-select"
                                value={producto.idProducto}
                                onChange={(e) =>
                                  handleChangeProductoCompra(
                                    index,
                                    'idProducto',
                                    e.target.value
                                  )
                                }
                                required
                              >
                                <option value="">Seleccionar producto...</option>

                                {opciones.productos.map((productoOpcion) => (
                                  <option
                                    key={productoOpcion.idProducto}
                                    value={productoOpcion.idProducto}
                                  >
                                    {productoOpcion.codigoProducto} -{' '}
                                    {productoOpcion.nombreProducto}
                                  </option>
                                ))}
                              </select>
                            </td>

                            <td>
                              <input
                                type="number"
                                className="form-control app-input text-end"
                                min="0.01"
                                step="0.01"
                                value={producto.cantidad}
                                onChange={(e) =>
                                  handleChangeProductoCompra(
                                    index,
                                    'cantidad',
                                    e.target.value
                                  )
                                }
                                required
                              />
                            </td>

                            <td>
                              <input
                                type="number"
                                className="form-control app-input text-end"
                                min="0"
                                step="0.01"
                                value={producto.precioUnitarioCompra}
                                onChange={(e) =>
                                  handleChangeProductoCompra(
                                    index,
                                    'precioUnitarioCompra',
                                    e.target.value
                                  )
                                }
                                required
                              />
                            </td>

                            <td className="text-end fw-bold">
                              {formatearMoneda(
                                calcularSubtotalProducto(producto)
                              )}
                            </td>

                            <td className="text-center">
                              <button
                                type="button"
                                className="btn btn-sm btn-outline-danger"
                                onClick={() => quitarProductoCompra(index)}
                                title="Quitar producto"
                              >
                                <i className="bi bi-x-lg"></i>
                              </button>
                            </td>
                          </tr>
                        ))}
                      </tbody>

                      <tfoot>
                        <tr>
                          <td colSpan="3" className="text-end fw-bold">
                            Total:
                          </td>

                          <td className="text-end fw-bold text-primary">
                            {formatearMoneda(totalNuevaCompra)}
                          </td>

                          <td></td>
                        </tr>
                      </tfoot>
                    </table>
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalCompra}
                    disabled={guardandoCompra}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                    disabled={guardandoCompra}
                  >
                    {guardandoCompra ? (
                      <span className="spinner-border spinner-border-sm me-2" />
                    ) : (
                      <i className="bi bi-save me-2"></i>
                    )}
                    Registrar Compra
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {mostrarModalProveedor && (
        <div className="purchase-modal-overlay">
          <div className="purchase-modal purchase-provider-modal">
            <div className="modal-content">
              <form onSubmit={handleCrearProveedor}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-plus-lg me-2 text-primary"></i>
                    Registrar Nuevo Proveedor
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalProveedor}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="row g-3">
                    <div className="col-12">
                      <label className="form-label">Razón Social *</label>
                      <input
                        type="text"
                        name="razonSocial"
                        className="form-control app-input"
                        value={nuevoProveedor.razonSocial}
                        onChange={handleChangeProveedor}
                        placeholder="Ej: Distribuidora Textil Lima"
                        maxLength="100"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">RUC *</label>
                      <input
                        type="text"
                        name="ruc"
                        className="form-control app-input"
                        value={nuevoProveedor.ruc}
                        onChange={handleChangeProveedor}
                        placeholder="20123456789"
                        maxLength="11"
                        pattern="[0-9]{11}"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Teléfono</label>
                      <input
                        type="text"
                        name="telefono"
                        className="form-control app-input"
                        value={nuevoProveedor.telefono}
                        onChange={handleChangeProveedor}
                        placeholder="044-123456"
                        maxLength="20"
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Dirección</label>
                      <input
                        type="text"
                        name="direccion"
                        className="form-control app-input"
                        value={nuevoProveedor.direccion}
                        onChange={handleChangeProveedor}
                        placeholder="Dirección del proveedor"
                        maxLength="200"
                      />
                    </div>
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalProveedor}
                    disabled={guardandoProveedor}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                    disabled={guardandoProveedor}
                  >
                    {guardandoProveedor ? (
                      <span className="spinner-border spinner-border-sm me-2" />
                    ) : (
                      <i className="bi bi-save me-2"></i>
                    )}
                    Guardar Proveedor
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {mostrarDetalle && (
        <div className="app-detail-overlay">
          <div className="app-detail-modal">
            <div className="app-detail-header">
              <h5 className="app-detail-title">
                <i className="bi bi-receipt me-2 text-primary"></i>
                Detalle de compra {detalleCompra?.numeroCompra || ''}
              </h5>

              <button
                type="button"
                className="btn-close"
                onClick={cerrarDetalle}
              ></button>
            </div>

            <div className="app-detail-body">
              {cargandoDetalle ? (
                <div className="d-flex align-items-center gap-2">
                  <div className="spinner-border spinner-border-sm text-primary" />
                  <span>Cargando detalle...</span>
                </div>
              ) : detalleCompra ? (
                <>
                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-3">
                      <div className="app-detail-info-card">
                        <span>
                          <i className="bi bi-building me-1"></i>
                          Proveedor
                        </span>

                        <h6 className="fw-bold">{detalleCompra.proveedor}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-3">
                      <div className="app-detail-info-card">
                        <span>
                          <i className="bi bi-calendar-event me-1"></i>
                          Fecha
                        </span>

                        <h6 className="fw-bold">
                          {formatearFecha(detalleCompra.fecha)}
                        </h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-3">
                      <div className="app-detail-info-card">
                        <span>
                          <i className="bi bi-credit-card me-1"></i>
                          Método de pago
                        </span>

                        <h6 className="fw-bold">{detalleCompra.metodoPago}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-3">
                      <div className="app-detail-info-card">
                        <span>
                          <i className="bi bi-check-circle me-1"></i>
                          Estado
                        </span>

                        <div className="mt-2">
                          {renderEstadoCompra(detalleCompra)}
                        </div>
                      </div>
                    </div>
                  </div>

                  <h6 className="fw-bold mb-2">
                    <i className="bi bi-box-seam me-2 text-primary"></i>
                    Productos comprados
                  </h6>

                  <div className="table-responsive mb-4">
                    <table className="table align-middle app-table purchases-table">
                      <thead>
                        <tr>
                          <th>Producto</th>
                          <th className="text-end">Cantidad</th>
                          <th className="text-end">P. Compra</th>
                          <th className="text-end">Subtotal</th>
                        </tr>
                      </thead>

                      <tbody>
                        {(detalleCompra.productos || []).length === 0 ? (
                          <tr>
                            <td colSpan="4" className="text-center text-muted">
                              No hay productos registrados.
                            </td>
                          </tr>
                        ) : (
                          detalleCompra.productos.map((producto, index) => (
                            <tr key={index}>
                              <td>{obtenerNombreProducto(producto)}</td>

                              <td className="text-end">{producto.cantidad}</td>

                              <td className="text-end">
                                {formatearMoneda(
                                  obtenerPrecioDetalleProducto(producto)
                                )}
                              </td>

                              <td className="text-end fw-bold">
                                {formatearMoneda(
                                  obtenerSubtotalDetalleProducto(producto)
                                )}
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>

                  <div className="row justify-content-end">
                    <div className="col-12 col-md-5">
                      <div className="app-detail-summary-box">
                        <div className="d-flex justify-content-between mb-2">
                          <span>Registrado por</span>
                          <strong>{detalleCompra.registradoPor}</strong>
                        </div>

                        <hr />

                        <div className="d-flex justify-content-between">
                          <span className="fw-bold">Total</span>
                          <strong className="text-primary fs-6">
                            {formatearMoneda(detalleCompra.total)}
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

            <div className="app-detail-footer">
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

export default ComprasPage;