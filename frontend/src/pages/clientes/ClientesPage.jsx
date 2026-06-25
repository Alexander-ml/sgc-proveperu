import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  obtenerResumenClientes,
  listarClientes,
  crearCliente,
} from '../../services/clienteService';

const ClientesPage = () => {
  const [resumen, setResumen] = useState(null);
  const [clientes, setClientes] = useState([]);

  const [busqueda, setBusqueda] = useState('');
  const [tipoCliente, setTipoCliente] = useState('');
  const [vista, setVista] = useState('tarjetas');

  const [cargando, setCargando] = useState(true);
  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);

  const [nuevoCliente, setNuevoCliente] = useState({
    nombre: '',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '',
    telefono: '',
    correo: '',
    direccion: '',
  });

  const formatearMoneda = (valor) => {
    const numero = Number(valor || 0);

    return `S/ ${numero.toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2,
    })}`;
  };

  const obtenerIniciales = (nombre = '') => {
    const partes = nombre.trim().split(' ');

    if (partes.length === 1) {
      return partes[0].substring(0, 2).toUpperCase();
    }

    return `${partes[0][0] || ''}${partes[1][0] || ''}`.toUpperCase();
  };

  const obtenerTipoTexto = (tipo) => {
    if (tipo === 'EMPRESA') return 'Empresa';
    if (tipo === 'PERSONA') return 'Persona';
    return tipo || '-';
  };

  const obtenerTipoBadge = (tipo) => {
    if (tipo === 'EMPRESA') {
      return 'bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25';
    }

    return 'bg-purple bg-opacity-10 text-purple border border-purple border-opacity-25';
  };

  const cargarDatos = async () => {
    try {
      setCargando(true);

      const resumenResponse = await obtenerResumenClientes();
      const clientesResponse = await listarClientes({
        busqueda,
        tipoCliente,
      });

      setResumen(resumenResponse.data);
      setClientes(clientesResponse.data || []);
    } catch (error) {
      console.error('Error cargando clientes:', error);
      alert('No se pudo cargar clientes');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, [tipoCliente]);

  const buscarClientes = (e) => {
    e.preventDefault();
    cargarDatos();
  };

  const handleChangeCrear = (e) => {
    const { name, value } = e.target;

    let cambios = {
      ...nuevoCliente,
      [name]: value,
    };

    if (name === 'tipoCliente') {
      cambios = {
        ...cambios,
        tipoDocumento: value === 'EMPRESA' ? 'RUC' : 'DNI',
      };
    }

    setNuevoCliente(cambios);
  };

  const cerrarModalCrear = () => {
    setMostrarModalCrear(false);

    setNuevoCliente({
      nombre: '',
      tipoCliente: 'EMPRESA',
      tipoDocumento: 'RUC',
      numeroDocumento: '',
      telefono: '',
      correo: '',
      direccion: '',
    });
  };

  const handleCrearCliente = async (e) => {
    e.preventDefault();

    try {
      await crearCliente(nuevoCliente);

      cerrarModalCrear();
      cargarDatos();
    } catch (error) {
      console.error('Error creando cliente:', error);
      alert('No se pudo registrar el cliente');
    }
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando clientes...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Clientes
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-people me-2 text-primary"></i>
          Gestión de Clientes
        </h4>

        <p className="page-subtitle mb-0">CRM - Administración de clientes</p>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Clientes"
            value={resumen?.totalClientes ?? 0}
            description="Registrados en el sistema"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Empresas / Talleres"
            value={resumen?.empresas ?? 0}
            description="Con RUC registrado"
            color="info"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Personas Naturales"
            value={resumen?.personasNaturales ?? 0}
            description="Con DNI registrado"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Clientes Frecuentes"
            value={resumen?.clientesFrecuentes ?? 0}
            description="Más de 10 compras"
            color="success"
          />
        </div>
      </div>

      <form onSubmit={buscarClientes} className="mb-3">
        <div className="row g-2 align-items-center">
          <div className="col-12 col-lg-4">
            <div className="input-group">
              <span className="input-group-text bg-white">
                <i className="bi bi-search text-muted"></i>
              </span>

              <input
                type="text"
                className="form-control app-input"
                placeholder="Buscar por nombre, RUC, DNI o email..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <select
              className="form-select app-select"
              value={tipoCliente}
              onChange={(e) => setTipoCliente(e.target.value)}
            >
              <option value="">Todos</option>
              <option value="EMPRESA">Empresas / Talleres</option>
              <option value="PERSONA">Personas naturales</option>
            </select>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <button type="submit" className="btn btn-outline-primary w-100">
              <i className="bi bi-search me-2"></i>
              Buscar
            </button>
          </div>

          <div className="col-12 col-lg-4 text-lg-end">
            <button
              type="button"
              className="btn btn-primary app-btn-primary"
              onClick={() => setMostrarModalCrear(true)}
            >
              <i className="bi bi-plus-lg me-2"></i>
              Nuevo Cliente
            </button>
          </div>
        </div>
      </form>

      <div className="app-tabs d-inline-flex gap-1 flex-wrap mb-4">
        <button
          type="button"
          className={`app-tab-btn ${vista === 'tarjetas' ? 'active' : ''}`}
          onClick={() => setVista('tarjetas')}
        >
          <i className="bi bi-grid me-2"></i>
          Vista Tarjetas
        </button>

        <button
          type="button"
          className={`app-tab-btn ${vista === 'tabla' ? 'active' : ''}`}
          onClick={() => setVista('tabla')}
        >
          <i className="bi bi-table me-2"></i>
          Vista Tabla
        </button>
      </div>

      {vista === 'tarjetas' && (
        <div className="row g-3">
          {clientes.length === 0 ? (
            <div className="col-12">
              <div className="alert alert-info">
                <i className="bi bi-info-circle me-2"></i>
                No se encontraron clientes.
              </div>
            </div>
          ) : (
            clientes.map((cliente) => (
              <div
                className="col-12 col-md-6 col-xl-4"
                key={cliente.idCliente}
              >
                <div className="app-card app-card-hover p-3 h-100">
                  <div className="d-flex align-items-start gap-3 mb-3">
                    <div
                      className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                      style={{ width: '48px', height: '48px' }}
                    >
                      {obtenerIniciales(cliente.nombre)}
                    </div>

                    <div className="flex-grow-1">
                      <div className="d-flex align-items-center gap-2 flex-wrap">
                        <h6 className="fw-bold mb-0">{cliente.nombre}</h6>

                        {cliente.frecuente && (
                          <i
                            className="bi bi-star-fill text-warning"
                            title="Cliente frecuente"
                          ></i>
                        )}
                      </div>

                      <div className="d-flex align-items-center gap-2 mt-1">
                        <span className={`badge app-badge ${obtenerTipoBadge(cliente.tipoCliente)}`}>
                          {obtenerTipoTexto(cliente.tipoCliente)}
                        </span>

                        <span className="text-muted small">
                          {cliente.tipoDocumento}: {cliente.numeroDocumento}
                        </span>
                      </div>
                    </div>
                  </div>

                  <p className="mb-2">
                    <i className="bi bi-telephone me-2 text-muted"></i>
                    {cliente.telefono}
                  </p>

                  <p className="mb-2">
                    <i className="bi bi-envelope me-2 text-muted"></i>
                    {cliente.correo}
                  </p>

                  <p className="mb-3">
                    <i className="bi bi-geo-alt me-2 text-muted"></i>
                    {cliente.direccion}
                  </p>

                  <hr />

                  <div className="row g-2 mb-3">
                    <div className="col-6">
                      <div className="bg-light rounded p-2 text-center">
                        <span className="text-muted small d-block">
                          Compras
                        </span>
                        <strong>{cliente.numeroCompras}</strong>
                      </div>
                    </div>

                    <div className="col-6">
                      <div className="bg-light rounded p-2 text-center">
                        <span className="text-muted small d-block">
                          Total gastado
                        </span>
                        <strong className="text-primary">
                          {formatearMoneda(cliente.montoTotal)}
                        </strong>
                      </div>
                    </div>
                  </div>

                  <p className="text-muted small text-center mb-0">
                    Última compra: {cliente.ultimaCompra}
                  </p>
                </div>
              </div>
            ))
          )}
        </div>
      )}

      {vista === 'tabla' && (
        <div className="app-card">
          <div className="table-responsive">
            <table className="table align-middle mb-0 app-table">
              <thead>
                <tr>
                  <th>Cliente</th>
                  <th>Tipo</th>
                  <th>Documento</th>
                  <th>Teléfono</th>
                  <th className="text-center">N° Compras</th>
                  <th className="text-end">Monto Total</th>
                  <th>Última Compra</th>
                  <th>Estado</th>
                  <th className="text-center">Ver</th>
                </tr>
              </thead>

              <tbody>
                {clientes.length === 0 ? (
                  <tr>
                    <td colSpan="9" className="text-center py-5 text-muted">
                      <i className="bi bi-inbox fs-3 d-block mb-2"></i>
                      No se encontraron clientes.
                    </td>
                  </tr>
                ) : (
                  clientes.map((cliente) => (
                    <tr key={cliente.idCliente}>
                      <td>
                        <div className="d-flex align-items-center gap-2">
                          <div
                            className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                            style={{
                              width: '34px',
                              height: '34px',
                              fontSize: '12px',
                            }}
                          >
                            {obtenerIniciales(cliente.nombre)}
                          </div>

                          <div>
                            <strong>{cliente.nombre}</strong>
                            <br />
                            <span className="text-muted small">
                              {cliente.correo}
                            </span>
                          </div>
                        </div>
                      </td>

                      <td>
                        <span className={`badge app-badge ${obtenerTipoBadge(cliente.tipoCliente)}`}>
                          {obtenerTipoTexto(cliente.tipoCliente)}
                        </span>
                      </td>

                      <td>
                        {cliente.tipoDocumento}: {cliente.numeroDocumento}
                      </td>

                      <td>{cliente.telefono}</td>

                      <td className="text-center fw-bold">
                        {cliente.numeroCompras}
                      </td>

                      <td className="text-end fw-bold text-primary">
                        {formatearMoneda(cliente.montoTotal)}
                      </td>

                      <td>{cliente.ultimaCompra}</td>

                      <td>
                        <BadgeStatus variant="success">
                          {cliente.estado}
                        </BadgeStatus>
                      </td>

                      <td className="text-center">
                        <button
                          type="button"
                          className="btn btn-sm btn-outline-primary"
                          onClick={() =>
                            alert('Detalle de cliente pendiente de endpoint real')
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
      )}

      {mostrarModalCrear && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleCrearCliente}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-plus-lg me-2 text-primary"></i>
                    Registrar Nuevo Cliente
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalCrear}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">
                      Nombre completo / Razón social *
                    </label>
                    <input
                      type="text"
                      name="nombre"
                      className="form-control app-input"
                      value={nuevoCliente.nombre}
                      onChange={handleChangeCrear}
                      placeholder="Ej: Carpintería García E.I.R.L."
                      required
                    />
                  </div>

                  <div className="row g-3">
                    <div className="col-12 col-md-6">
                      <label className="form-label">Tipo de cliente</label>
                      <select
                        name="tipoCliente"
                        className="form-select app-select"
                        value={nuevoCliente.tipoCliente}
                        onChange={handleChangeCrear}
                      >
                        <option value="EMPRESA">Empresa / Taller</option>
                        <option value="PERSONA">Persona Natural</option>
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Tipo de documento</label>
                      <select
                        name="tipoDocumento"
                        className="form-select app-select"
                        value={nuevoCliente.tipoDocumento}
                        onChange={handleChangeCrear}
                      >
                        <option value="RUC">RUC</option>
                        <option value="DNI">DNI</option>
                      </select>
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">N° Documento *</label>
                      <input
                        type="text"
                        name="numeroDocumento"
                        className="form-control app-input"
                        value={nuevoCliente.numeroDocumento}
                        onChange={handleChangeCrear}
                        placeholder={
                          nuevoCliente.tipoDocumento === 'RUC'
                            ? '20123456789'
                            : '12345678'
                        }
                        required
                      />
                    </div>

                    <div className="col-12 col-md-6">
                      <label className="form-label">Teléfono</label>
                      <input
                        type="text"
                        name="telefono"
                        className="form-control app-input"
                        value={nuevoCliente.telefono}
                        onChange={handleChangeCrear}
                        placeholder="9XX-XXXXXX"
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Correo electrónico</label>
                      <input
                        type="email"
                        name="correo"
                        className="form-control app-input"
                        value={nuevoCliente.correo}
                        onChange={handleChangeCrear}
                        placeholder="cliente@ejemplo.com"
                      />
                    </div>

                    <div className="col-12">
                      <label className="form-label">Dirección</label>
                      <input
                        type="text"
                        name="direccion"
                        className="form-control app-input"
                        value={nuevoCliente.direccion}
                        onChange={handleChangeCrear}
                        placeholder="Av. Principal 123, Chiclayo"
                      />
                    </div>
                  </div>

                  <div className="alert alert-info mt-3 mb-0">
                    <i className="bi bi-info-circle me-2"></i>
                    Este cliente se guarda temporalmente en el navegador. Cuando
                    tengas endpoints reales, solo se cambiará el service.
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
                    Guardar Cliente
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

export default ClientesPage;