import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  obtenerResumenCompras,
  listarCompras,
  listarProveedores,
  crearCompra,
  crearProveedor,
} from '../../services/comprasService';

const ComprasPage = () => {
  const [tabActivo, setTabActivo] = useState('compras');

  const [resumen, setResumen] = useState(null);
  const [compras, setCompras] = useState([]);
  const [proveedores, setProveedores] = useState([]);

  const [busqueda, setBusqueda] = useState('');
  const [estado, setEstado] = useState('');

  const [cargando, setCargando] = useState(true);
  const [mostrarModalCompra, setMostrarModalCompra] = useState(false);
  const [mostrarModalProveedor, setMostrarModalProveedor] = useState(false);

  const [nuevaCompra, setNuevaCompra] = useState({
    idProveedor: '',
    metodoPago: 'Transferencia',
    productos: 0,
    total: 0,
    notas: '',
  });

  const [nuevoProveedor, setNuevoProveedor] = useState({
    nombre: '',
    ruc: '',
    telefono: '',
    correo: '',
    categoria: '',
    contacto: '',
  });

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

  const obtenerEstadoTexto = (estadoCompra) => {
    if (estadoCompra === 'RECIBIDA') return 'Recibida';
    if (estadoCompra === 'PENDIENTE') return 'Pendiente';
    return estadoCompra || 'Sin estado';
  };

  const obtenerEstadoVariant = (estadoCompra) => {
    if (estadoCompra === 'RECIBIDA') return 'success';
    if (estadoCompra === 'PENDIENTE') return 'warning';
    return 'secondary';
  };

  const obtenerNombreProveedor = (proveedor) => {
    return (
      proveedor.nombre ||
      proveedor.razonSocial ||
      proveedor.nombreProveedor ||
      'Proveedor sin nombre'
    );
  };

  const cargarDatos = async () => {
    try {
      setCargando(true);

      const resumenResponse = await obtenerResumenCompras();
      const comprasResponse = await listarCompras({
        busqueda,
        estado,
      });
      const proveedoresResponse = await listarProveedores();

      setResumen(resumenResponse.data);
      setCompras(comprasResponse.data || []);
      setProveedores(proveedoresResponse.data || []);
    } catch (error) {
      console.error('Error cargando compras:', error);
      alert('No se pudo cargar el módulo de compras');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, [estado]);

  const buscarCompras = (e) => {
    e.preventDefault();
    cargarDatos();
  };

  const handleChangeCompra = (e) => {
    const { name, value } = e.target;

    setNuevaCompra({
      ...nuevaCompra,
      [name]: value,
    });
  };

  const handleChangeProveedor = (e) => {
    const { name, value } = e.target;

    setNuevoProveedor({
      ...nuevoProveedor,
      [name]: value,
    });
  };

  const cerrarModalCompra = () => {
    setMostrarModalCompra(false);

    setNuevaCompra({
      idProveedor: '',
      metodoPago: 'Transferencia',
      productos: 0,
      total: 0,
      notas: '',
    });
  };

  const cerrarModalProveedor = () => {
    setMostrarModalProveedor(false);

    setNuevoProveedor({
      nombre: '',
      ruc: '',
      telefono: '',
      correo: '',
      categoria: '',
      contacto: '',
    });
  };

  const handleCrearCompra = async (e) => {
    e.preventDefault();

    try {
      await crearCompra(nuevaCompra);
      cerrarModalCompra();
      cargarDatos();
    } catch (error) {
      console.error('Error creando compra:', error);
      alert('No se pudo registrar la compra');
    }
  };

  const handleCrearProveedor = async (e) => {
    e.preventDefault();

    try {
      await crearProveedor(nuevoProveedor);
      cerrarModalProveedor();
      cargarDatos();
    } catch (error) {
      console.error('Error creando proveedor:', error);
      alert('No se pudo registrar el proveedor');
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
            value={formatearMoneda(resumen?.montoTotal ?? 0)}
            description="Invertido en stock"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Recibidas"
            value={resumen?.recibidas ?? 0}
            description="Completadas"
            color="info"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Pendientes"
            value={resumen?.pendientes ?? 0}
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
            onClick={() => setMostrarModalCompra(true)}
          >
            <i className="bi bi-plus-lg me-2"></i>
            Nueva Compra
          </button>
        )}

        {tabActivo === 'proveedores' && (
          <button
            type="button"
            className="btn btn-primary app-btn-primary"
            onClick={() => setMostrarModalProveedor(true)}
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
                  onChange={(e) => setEstado(e.target.value)}
                >
                  <option value="">Todos</option>
                  <option value="RECIBIDA">Recibida</option>
                  <option value="PENDIENTE">Pendiente</option>
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
              <table className="table align-middle mb-0 app-table">
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

                        <td>{formatearFecha(compra.fecha)}</td>

                        <td>{compra.proveedor}</td>

                        <td className="text-center fw-bold">
                          {compra.productos}
                        </td>

                        <td className="text-end fw-bold">
                          {formatearMoneda(compra.total)}
                        </td>

                        <td>{compra.metodoPago}</td>

                        <td>
                          <BadgeStatus
                            variant={obtenerEstadoVariant(compra.estado)}
                          >
                            {obtenerEstadoTexto(compra.estado)}
                          </BadgeStatus>
                        </td>

                        <td>{compra.registradoPor}</td>

                        <td className="text-center">
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-primary"
                            onClick={() =>
                              alert(
                                'Detalle de compra pendiente de endpoint real'
                              )
                            }
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
                      {proveedor.telefono || '-'}
                    </p>

                    <p className="mb-2">
                      <i className="bi bi-envelope me-2 text-primary"></i>
                      {proveedor.correo || '-'}
                    </p>

                    <p className="mb-3">
                      <i className="bi bi-tag me-2 text-primary"></i>
                      {proveedor.categoria || '-'}
                    </p>

                    <hr />

                    <div className="d-flex justify-content-between align-items-center">
                      <span className="text-muted small">
                        Contacto: {proveedor.contacto || '-'}
                      </span>

                      <button
                        type="button"
                        className="btn btn-link text-decoration-none p-0"
                        onClick={() => {
                          setTabActivo('compras');
                          setBusqueda(obtenerNombreProveedor(proveedor));
                          setTimeout(() => cargarDatos(), 0);
                        }}
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
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
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
                  <div className="row g-3">
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
                        {proveedores.map((proveedor) => (
                          <option
                            key={proveedor.idProveedor}
                            value={proveedor.idProveedor}
                          >
                            {obtenerNombreProveedor(proveedor)}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Método de Pago</label>
                      <select
                        name="metodoPago"
                        className="form-select app-select"
                        value={nuevaCompra.metodoPago}
                        onChange={handleChangeCompra}
                      >
                        <option value="Transferencia">Transferencia Bancaria</option>
                        <option value="Depósito">Depósito</option>
                        <option value="Efectivo">Efectivo</option>
                        <option value="Crédito">Crédito</option>
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Cantidad de productos</label>
                      <input
                        type="number"
                        name="productos"
                        className="form-control app-input"
                        value={nuevaCompra.productos}
                        onChange={handleChangeCompra}
                        min="1"
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Total</label>
                      <input
                        type="number"
                        name="total"
                        className="form-control app-input"
                        value={nuevaCompra.total}
                        onChange={handleChangeCompra}
                        min="0"
                        step="0.01"
                        required
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Notas adicionales</label>
                      <textarea
                        name="notas"
                        className="form-control app-input"
                        rows="3"
                        placeholder="Observaciones, condiciones de entrega..."
                        value={nuevaCompra.notas}
                        onChange={handleChangeCompra}
                      ></textarea>
                    </div>
                  </div>

                  <div className="alert alert-info mt-3 mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Esta compra se guarda temporalmente en el navegador. Cuando
                    tengas endpoint real, solo se actualizará el service.
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalCompra}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-save me-2"></i>
                    Registrar Compra
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {mostrarModalProveedor && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
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
                      <label className="form-label">
                        Nombre / Razón Social *
                      </label>
                      <input
                        type="text"
                        name="nombre"
                        className="form-control app-input"
                        value={nuevoProveedor.nombre}
                        onChange={handleChangeProveedor}
                        placeholder="Ej: Distribuidora Textil Lima"
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
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Correo</label>
                      <input
                        type="email"
                        name="correo"
                        className="form-control app-input"
                        value={nuevoProveedor.correo}
                        onChange={handleChangeProveedor}
                        placeholder="ventas@proveedor.com"
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Categoría</label>
                      <input
                        type="text"
                        name="categoria"
                        className="form-control app-input"
                        value={nuevoProveedor.categoria}
                        onChange={handleChangeProveedor}
                        placeholder="Telas, espumas, aceros..."
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Contacto</label>
                      <input
                        type="text"
                        name="contacto"
                        className="form-control app-input"
                        value={nuevoProveedor.contacto}
                        onChange={handleChangeProveedor}
                        placeholder="Nombre del contacto"
                      />
                    </div>
                  </div>

                  <div className="alert alert-info mt-3 mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Este proveedor se guarda temporalmente en el navegador.
                    Luego se podrá conectar al endpoint real.
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalProveedor}
                  >
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-save me-2"></i>
                    Guardar Proveedor
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

export default ComprasPage;