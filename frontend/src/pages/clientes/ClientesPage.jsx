import { useCallback, useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

import {
  obtenerResumenClientes,
  listarClientes,
  crearCliente,
  editarCliente,
  activarCliente,
  desactivarCliente,
  obtenerClientePorId,
  obtenerHistorialComprasCliente,
} from '../../services/clienteService';

const crearFormularioVacio = () => ({
  tipoCliente: 'EMPRESA',
  nombreCliente: '',
  numeroDocumento: '',
  telefono: '',
  direccion: '',
});

const ClientesPage = () => {
  const [resumen, setResumen] = useState(null);
  const [clientes, setClientes] = useState([]);

  const [busqueda, setBusqueda] = useState('');
  const [tipoCliente, setTipoCliente] = useState('');
  const [filtros, setFiltros] = useState({
    busqueda: '',
    tipoCliente: '',
  });

  const [vista, setVista] = useState('tarjetas');
  const [cargando, setCargando] = useState(true);
  const [mensaje, setMensaje] = useState('');
  const [error, setError] = useState('');

  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);
  const [mostrarDetalle, setMostrarDetalle] = useState(false);
  const [mostrarHistorial, setMostrarHistorial] = useState(false);

  const [guardando, setGuardando] = useState(false);
  const [cargandoDetalle, setCargandoDetalle] = useState(false);
  const [cargandoHistorial, setCargandoHistorial] = useState(false);

  const [clienteSeleccionado, setClienteSeleccionado] = useState(null);
  const [historialCliente, setHistorialCliente] = useState(null);

  const [formularioCliente, setFormularioCliente] = useState(
    crearFormularioVacio()
  );

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

  const obtenerNombreCliente = (cliente) => {
    return cliente?.nombreCliente || cliente?.nombre || 'Cliente sin nombre';
  };

  const obtenerIniciales = (cliente) => {
    if (cliente?.iniciales) return cliente.iniciales;

    const nombre = obtenerNombreCliente(cliente);
    const partes = nombre.trim().split(' ');

    if (partes.length === 1) {
      return partes[0].slice(0, 2).toUpperCase();
    }

    return `${partes[0]?.[0] || ''}${partes[1]?.[0] || ''}`.toUpperCase();
  };

  const obtenerTipoTexto = (tipo) => {
    if (tipo === 'EMPRESA') return 'Empresa';
    if (tipo === 'PERSONA') return 'Persona';
    return tipo || '-';
  };

  const obtenerDocumentoTexto = (cliente) => {
    const tipoDocumento =
      cliente?.tipoDocumento || (cliente?.tipoCliente === 'EMPRESA' ? 'RUC' : 'DNI');

    return `${tipoDocumento}: ${cliente?.numeroDocumento || '-'}`;
  };

  const obtenerEstadoVariant = (estado) => {
    return estado === 'INACTIVO' ? 'secondary' : 'success';
  };

  const obtenerTipoBadge = (tipo) => {
    if (tipo === 'EMPRESA') {
      return 'bg-primary bg-opacity-10 text-primary border border-primary border-opacity-25';
    }

    return 'bg-purple bg-opacity-10 text-purple border border-purple border-opacity-25';
  };

  const esFrecuente = (cliente) => Number(cliente?.numeroCompras || 0) > 10;

  const cargarDatos = useCallback(
    async (mostrarIndicador = true) => {
      try {
        if (mostrarIndicador) setCargando(true);

        setError('');

        const [resumenResponse, clientesResponse] = await Promise.all([
          obtenerResumenClientes(),
          listarClientes({
            busqueda: filtros.busqueda,
            tipoCliente: filtros.tipoCliente,
          }),
        ]);

        setResumen(resumenResponse.data);
        setClientes(clientesResponse.data || []);
      } catch (err) {
        console.error('Error cargando clientes:', err);
        setError(extraerMensajeError(err, 'No se pudo cargar clientes.'));
        setClientes([]);
      } finally {
        if (mostrarIndicador) setCargando(false);
      }
    },
    [filtros]
  );

  useEffect(() => {
    cargarDatos(true);
  }, [cargarDatos]);

  const buscarClientes = (e) => {
    e.preventDefault();

    setFiltros({
      busqueda: busqueda.trim(),
      tipoCliente,
    });
  };

  const handleChangeFormulario = (e) => {
    const { name, value } = e.target;

    setFormularioCliente((actual) => ({
      ...actual,
      [name]: value,
    }));
  };

  const abrirModalCrear = () => {
    setFormularioCliente(crearFormularioVacio());
    setMostrarModalCrear(true);
  };

  const cerrarModalCrear = () => {
    setMostrarModalCrear(false);
    setFormularioCliente(crearFormularioVacio());
  };

  const abrirModalEditar = async (idCliente) => {
    try {
      setCargandoDetalle(true);

      const response = await obtenerClientePorId(idCliente);
      const cliente = response.data;

      setClienteSeleccionado(cliente);
      setFormularioCliente({
        tipoCliente: cliente.tipoCliente || 'EMPRESA',
        nombreCliente: cliente.nombreCliente || '',
        numeroDocumento: cliente.numeroDocumento || '',
        telefono: cliente.telefono || '',
        direccion: cliente.direccion || '',
      });

      setMostrarModalEditar(true);
    } catch (err) {
      console.error('Error obteniendo cliente:', err);
      alert(extraerMensajeError(err, 'No se pudo cargar el cliente.'));
    } finally {
      setCargandoDetalle(false);
    }
  };

  const cerrarModalEditar = () => {
    setMostrarModalEditar(false);
    setClienteSeleccionado(null);
    setFormularioCliente(crearFormularioVacio());
  };

  const validarFormulario = () => {
    const documento = formularioCliente.numeroDocumento.trim();

    if (!formularioCliente.nombreCliente.trim()) {
      alert('Debe ingresar el nombre o razón social.');
      return false;
    }

    if (!documento) {
      alert('Debe ingresar el número de documento.');
      return false;
    }

    if (formularioCliente.tipoCliente === 'EMPRESA' && documento.length !== 11) {
      alert('El RUC debe tener 11 dígitos.');
      return false;
    }

    if (formularioCliente.tipoCliente === 'PERSONA' && documento.length !== 8) {
      alert('El DNI debe tener 8 dígitos.');
      return false;
    }

    return true;
  };

  const construirPayload = () => ({
    tipoCliente: formularioCliente.tipoCliente,
    nombreCliente: formularioCliente.nombreCliente.trim(),
    numeroDocumento: formularioCliente.numeroDocumento.trim(),
    telefono: formularioCliente.telefono.trim() || null,
    direccion: formularioCliente.direccion.trim() || null,
  });

  const handleCrearCliente = async (e) => {
    e.preventDefault();

    if (!validarFormulario()) return;

    try {
      setGuardando(true);
      setMensaje('');

      const response = await crearCliente(construirPayload());

      setMensaje(response.message || 'Cliente registrado correctamente.');
      cerrarModalCrear();
      await cargarDatos(false);
    } catch (err) {
      console.error('Error creando cliente:', err);
      alert(extraerMensajeError(err, 'No se pudo registrar el cliente.'));
    } finally {
      setGuardando(false);
    }
  };

  const handleEditarCliente = async (e) => {
    e.preventDefault();

    if (!validarFormulario() || !clienteSeleccionado) return;

    try {
      setGuardando(true);
      setMensaje('');

      const response = await editarCliente(
        clienteSeleccionado.idCliente,
        construirPayload()
      );

      setMensaje(response.message || 'Cliente actualizado correctamente.');
      cerrarModalEditar();
      await cargarDatos(false);
    } catch (err) {
      console.error('Error editando cliente:', err);
      alert(extraerMensajeError(err, 'No se pudo actualizar el cliente.'));
    } finally {
      setGuardando(false);
    }
  };

  const abrirDetalle = async (idCliente) => {
    try {
      setMostrarDetalle(true);
      setCargandoDetalle(true);
      setClienteSeleccionado(null);

      const response = await obtenerClientePorId(idCliente);

      setClienteSeleccionado(response.data);
    } catch (err) {
      console.error('Error obteniendo detalle:', err);
      alert(extraerMensajeError(err, 'No se pudo cargar el detalle.'));
      setMostrarDetalle(false);
    } finally {
      setCargandoDetalle(false);
    }
  };

  const cerrarDetalle = () => {
    setMostrarDetalle(false);
    setClienteSeleccionado(null);
  };

  const abrirHistorial = async (idCliente) => {
    try {
      setMostrarHistorial(true);
      setCargandoHistorial(true);
      setHistorialCliente(null);

      const response = await obtenerHistorialComprasCliente(idCliente);

      setHistorialCliente(response.data);
    } catch (err) {
      console.error('Error obteniendo historial:', err);
      alert(extraerMensajeError(err, 'No se pudo cargar el historial.'));
      setMostrarHistorial(false);
    } finally {
      setCargandoHistorial(false);
    }
  };

  const cerrarHistorial = () => {
    setMostrarHistorial(false);
    setHistorialCliente(null);
  };

  const cambiarEstadoCliente = async (cliente) => {
    const estaActivo = cliente.estado !== 'INACTIVO';

    const confirmar = window.confirm(
      estaActivo
        ? `¿Desactivar al cliente ${obtenerNombreCliente(cliente)}?`
        : `¿Activar al cliente ${obtenerNombreCliente(cliente)}?`
    );

    if (!confirmar) return;

    try {
      setMensaje('');

      const response = estaActivo
        ? await desactivarCliente(cliente.idCliente)
        : await activarCliente(cliente.idCliente);

      setMensaje(response.message || 'Estado del cliente actualizado.');
      await cargarDatos(false);
    } catch (err) {
      console.error('Error cambiando estado:', err);
      alert(extraerMensajeError(err, 'No se pudo cambiar el estado.'));
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

        <p className="page-subtitle mb-0">
          CRM - Administración de clientes e historial de compras
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
            title="Total Clientes"
            value={resumen?.totalClientes ?? 0}
            description="Registrados en el sistema"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Empresas / Talleres"
            value={resumen?.empresasTalleres ?? 0}
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
                placeholder="Buscar por nombre, RUC, DNI o teléfono..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
              />
            </div>
          </div>

          <div className="col-12 col-md-4 col-lg-2">
            <select
              className="form-select app-select"
              value={tipoCliente}
              onChange={(e) => {
                const nuevoTipo = e.target.value;

                setTipoCliente(nuevoTipo);
                setFiltros({
                  busqueda: busqueda.trim(),
                  tipoCliente: nuevoTipo,
                });
              }}
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
              onClick={abrirModalCrear}
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
                      className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold flex-shrink-0"
                      style={{ width: '48px', height: '48px' }}
                    >
                      {obtenerIniciales(cliente)}
                    </div>

                    <div className="flex-grow-1">
                      <div className="d-flex align-items-center gap-2 flex-wrap">
                        <h6 className="fw-bold mb-0">
                          {obtenerNombreCliente(cliente)}
                        </h6>

                        {esFrecuente(cliente) && (
                          <i
                            className="bi bi-star-fill text-warning"
                            title="Cliente frecuente"
                          ></i>
                        )}
                      </div>

                      <div className="d-flex align-items-center gap-2 mt-1 flex-wrap">
                        <span
                          className={`badge app-badge ${obtenerTipoBadge(
                            cliente.tipoCliente
                          )}`}
                        >
                          {obtenerTipoTexto(cliente.tipoCliente)}
                        </span>

                        <span className="text-muted small">
                          {obtenerDocumentoTexto(cliente)}
                        </span>
                      </div>
                    </div>
                  </div>

                  <p className="mb-2">
                    <i className="bi bi-telephone me-2 text-muted"></i>
                    {cliente.telefono || 'Sin teléfono'}
                  </p>

                  <p className="mb-3">
                    <i className="bi bi-geo-alt me-2 text-muted"></i>
                    {cliente.direccion || 'Sin dirección'}
                  </p>

                  <hr />

                  <div className="row g-2 mb-3">
                    <div className="col-6">
                      <div className="bg-light rounded p-2 text-center">
                        <span className="text-muted small d-block">
                          Compras
                        </span>
                        <strong>{cliente.numeroCompras ?? 0}</strong>
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

                  <p className="text-muted small text-center mb-3">
                    Última compra: {formatearFecha(cliente.ultimaCompra)}
                  </p>

                  <div className="d-flex justify-content-between gap-2 flex-wrap">
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-primary"
                      onClick={() => abrirDetalle(cliente.idCliente)}
                    >
                      <i className="bi bi-eye me-1"></i>
                      Ver
                    </button>

                    <button
                      type="button"
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => abrirModalEditar(cliente.idCliente)}
                    >
                      <i className="bi bi-pencil me-1"></i>
                      Editar
                    </button>

                    <button
                      type="button"
                      className="btn btn-sm btn-outline-info"
                      onClick={() => abrirHistorial(cliente.idCliente)}
                    >
                      <i className="bi bi-clock-history me-1"></i>
                      Historial
                    </button>
                  </div>
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
                  <th className="text-center">Acciones</th>
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
                            className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold flex-shrink-0"
                            style={{
                              width: '34px',
                              height: '34px',
                              fontSize: '12px',
                            }}
                          >
                            {obtenerIniciales(cliente)}
                          </div>

                          <strong>{obtenerNombreCliente(cliente)}</strong>
                        </div>
                      </td>

                      <td>
                        <span
                          className={`badge app-badge ${obtenerTipoBadge(
                            cliente.tipoCliente
                          )}`}
                        >
                          {obtenerTipoTexto(cliente.tipoCliente)}
                        </span>
                      </td>

                      <td>{obtenerDocumentoTexto(cliente)}</td>

                      <td>{cliente.telefono || '-'}</td>

                      <td className="text-center fw-bold">
                        {cliente.numeroCompras ?? 0}
                      </td>

                      <td className="text-end fw-bold text-primary">
                        {formatearMoneda(cliente.montoTotal)}
                      </td>

                      <td>{formatearFecha(cliente.ultimaCompra)}</td>

                      <td>
                        <BadgeStatus variant={obtenerEstadoVariant(cliente.estado)}>
                          {cliente.estado || 'ACTIVO'}
                        </BadgeStatus>
                      </td>

                      <td className="text-center">
                        <div className="d-flex justify-content-center gap-2">
                          <button
                            type="button"
                            className="btn btn-sm btn-outline-primary"
                            onClick={() => abrirDetalle(cliente.idCliente)}
                            title="Ver detalle"
                          >
                            <i className="bi bi-eye"></i>
                          </button>

                          <button
                            type="button"
                            className="btn btn-sm btn-outline-secondary"
                            onClick={() => abrirModalEditar(cliente.idCliente)}
                            title="Editar"
                          >
                            <i className="bi bi-pencil"></i>
                          </button>

                          <button
                            type="button"
                            className="btn btn-sm btn-outline-info"
                            onClick={() => abrirHistorial(cliente.idCliente)}
                            title="Historial"
                          >
                            <i className="bi bi-clock-history"></i>
                          </button>

                          <button
                            type="button"
                            className={
                              cliente.estado === 'INACTIVO'
                                ? 'btn btn-sm btn-outline-success'
                                : 'btn btn-sm btn-outline-danger'
                            }
                            onClick={() => cambiarEstadoCliente(cliente)}
                            title={
                              cliente.estado === 'INACTIVO'
                                ? 'Activar cliente'
                                : 'Desactivar cliente'
                            }
                          >
                            <i
                              className={
                                cliente.estado === 'INACTIVO'
                                  ? 'bi bi-check-circle'
                                  : 'bi bi-x-circle'
                              }
                            ></i>
                          </button>
                        </div>
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
        <FormularioClienteModal
          titulo="Registrar Nuevo Cliente"
          formularioCliente={formularioCliente}
          guardando={guardando}
          onChange={handleChangeFormulario}
          onClose={cerrarModalCrear}
          onSubmit={handleCrearCliente}
          textoBoton="Guardar Cliente"
        />
      )}

      {mostrarModalEditar && (
        <FormularioClienteModal
          titulo="Editar Cliente"
          formularioCliente={formularioCliente}
          guardando={guardando}
          onChange={handleChangeFormulario}
          onClose={cerrarModalEditar}
          onSubmit={handleEditarCliente}
          textoBoton="Actualizar Cliente"
        />
      )}

      {mostrarDetalle && (
        <div className="app-detail-overlay">
          <div className="app-detail-modal">
            <div className="app-detail-header">
              <h5 className="app-detail-title">
                <i className="bi bi-person-vcard me-2 text-primary"></i>
                Detalle de cliente
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
              ) : clienteSeleccionado ? (
                <>
                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Cliente</span>
                        <h6>{obtenerNombreCliente(clienteSeleccionado)}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Documento</span>
                        <h6>{obtenerDocumentoTexto(clienteSeleccionado)}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Estado</span>
                        <h6>{clienteSeleccionado.estado || 'ACTIVO'}</h6>
                      </div>
                    </div>
                  </div>

                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Teléfono</span>
                        <h6>{clienteSeleccionado.telefono || '-'}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-8">
                      <div className="app-detail-info-card">
                        <span>Dirección</span>
                        <h6>{clienteSeleccionado.direccion || '-'}</h6>
                      </div>
                    </div>
                  </div>

                  <div className="row g-3">
                    <div className="col-12 col-md-4">
                      <div className="app-detail-summary-box">
                        <span className="text-muted">Compras</span>
                        <h5 className="fw-bold mb-0">
                          {clienteSeleccionado.numeroCompras ?? 0}
                        </h5>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-summary-box">
                        <span className="text-muted">Monto total</span>
                        <h5 className="fw-bold text-primary mb-0">
                          {formatearMoneda(clienteSeleccionado.montoTotal)}
                        </h5>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-summary-box">
                        <span className="text-muted">Ticket promedio</span>
                        <h5 className="fw-bold text-primary mb-0">
                          {formatearMoneda(clienteSeleccionado.ticketPromedio)}
                        </h5>
                      </div>
                    </div>
                  </div>
                </>
              ) : (
                <p>No se encontró información del cliente.</p>
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

      {mostrarHistorial && (
        <div className="app-detail-overlay">
          <div className="app-detail-modal">
            <div className="app-detail-header">
              <h5 className="app-detail-title">
                <i className="bi bi-clock-history me-2 text-primary"></i>
                Historial de compras
              </h5>

              <button
                type="button"
                className="btn-close"
                onClick={cerrarHistorial}
              ></button>
            </div>

            <div className="app-detail-body">
              {cargandoHistorial ? (
                <div className="d-flex align-items-center gap-2">
                  <div className="spinner-border spinner-border-sm text-primary" />
                  <span>Cargando historial...</span>
                </div>
              ) : historialCliente ? (
                <>
                  <div className="row g-3 mb-3">
                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Cliente</span>
                        <h6>{historialCliente.nombreCliente}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Compras</span>
                        <h6>{historialCliente.numeroCompras ?? 0}</h6>
                      </div>
                    </div>

                    <div className="col-12 col-md-4">
                      <div className="app-detail-info-card">
                        <span>Monto total</span>
                        <h6>{formatearMoneda(historialCliente.montoTotal)}</h6>
                      </div>
                    </div>
                  </div>

                  <div className="table-responsive">
                    <table className="table table-sm align-middle app-table">
                      <thead>
                        <tr>
                          <th>Venta</th>
                          <th>Fecha</th>
                          <th>Pago</th>
                          <th>Comprobante</th>
                          <th>Estado</th>
                          <th className="text-end">Total</th>
                        </tr>
                      </thead>

                      <tbody>
                        {(historialCliente.compras || []).length === 0 ? (
                          <tr>
                            <td colSpan="6" className="text-center text-muted">
                              No hay compras registradas.
                            </td>
                          </tr>
                        ) : (
                          historialCliente.compras.map((compra) => (
                            <tr key={compra.idVenta}>
                              <td>
                                <strong className="text-primary">
                                  {compra.codigoVenta}
                                </strong>
                              </td>

                              <td>
                                {formatearFecha(compra.fechaHoraVenta)}
                                <br />
                                <span className="text-muted small">
                                  {formatearHora(compra.fechaHoraVenta)}
                                </span>
                              </td>

                              <td>{compra.metodoPago || '-'}</td>

                              <td>
                                {compra.tipoComprobante || '-'}
                                <br />
                                <span className="text-muted small">
                                  {compra.numeroComprobante || '-'}
                                </span>
                              </td>

                              <td>
                                <BadgeStatus
                                  variant={
                                    compra.estadoVenta === 'ANULADA'
                                      ? 'danger'
                                      : 'success'
                                  }
                                >
                                  {compra.estadoVenta}
                                </BadgeStatus>
                              </td>

                              <td className="text-end fw-bold">
                                {formatearMoneda(compra.total)}
                              </td>
                            </tr>
                          ))
                        )}
                      </tbody>
                    </table>
                  </div>
                </>
              ) : (
                <p>No se encontró historial.</p>
              )}
            </div>

            <div className="app-detail-footer">
              <button
                type="button"
                className="btn btn-secondary btn-sm"
                onClick={cerrarHistorial}
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

const FormularioClienteModal = ({
  titulo,
  formularioCliente,
  guardando,
  onChange,
  onClose,
  onSubmit,
  textoBoton,
}) => {
  const tipoDocumento =
    formularioCliente.tipoCliente === 'EMPRESA' ? 'RUC' : 'DNI';

  return (
    <div className="purchase-modal-overlay">
      <div className="purchase-modal purchase-provider-modal">
        <div className="modal-content">
          <form onSubmit={onSubmit}>
            <div className="modal-header">
              <h5 className="modal-title">
                <i className="bi bi-plus-lg me-2 text-primary"></i>
                {titulo}
              </h5>

              <button
                type="button"
                className="btn-close"
                onClick={onClose}
              ></button>
            </div>

            <div className="modal-body">
              <div className="row g-3">
                <div className="col-12 col-md-5">
                  <label className="form-label">Tipo de cliente *</label>
                  <select
                    name="tipoCliente"
                    className="form-select app-select"
                    value={formularioCliente.tipoCliente}
                    onChange={onChange}
                    required
                  >
                    <option value="EMPRESA">Empresa / Taller</option>
                    <option value="PERSONA">Persona Natural</option>
                  </select>
                </div>

                <div className="col-12 col-md-7">
                  <label className="form-label">{tipoDocumento} *</label>
                  <input
                    type="text"
                    name="numeroDocumento"
                    className="form-control app-input"
                    value={formularioCliente.numeroDocumento}
                    onChange={onChange}
                    placeholder={
                      formularioCliente.tipoCliente === 'EMPRESA'
                        ? '20123456789'
                        : '12345678'
                    }
                    maxLength={
                      formularioCliente.tipoCliente === 'EMPRESA' ? 11 : 8
                    }
                    pattern="[0-9]+"
                    required
                  />
                </div>

                <div className="col-12">
                  <label className="form-label">
                    Nombre completo / Razón social *
                  </label>
                  <input
                    type="text"
                    name="nombreCliente"
                    className="form-control app-input"
                    value={formularioCliente.nombreCliente}
                    onChange={onChange}
                    placeholder="Ej: Carpintería García E.I.R.L."
                    maxLength="150"
                    required
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label">Teléfono</label>
                  <input
                    type="text"
                    name="telefono"
                    className="form-control app-input"
                    value={formularioCliente.telefono}
                    onChange={onChange}
                    placeholder="9XX-XXXXXX"
                    maxLength="20"
                  />
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label">Dirección</label>
                  <input
                    type="text"
                    name="direccion"
                    className="form-control app-input"
                    value={formularioCliente.direccion}
                    onChange={onChange}
                    placeholder="Av. Principal 123"
                    maxLength="200"
                  />
                </div>
              </div>
            </div>

            <div className="modal-footer">
              <button
                type="button"
                className="btn btn-secondary"
                onClick={onClose}
                disabled={guardando}
              >
                Cancelar
              </button>

              <button
                type="submit"
                className="btn btn-primary app-btn-primary"
                disabled={guardando}
              >
                {guardando ? (
                  <span className="spinner-border spinner-border-sm me-2" />
                ) : (
                  <i className="bi bi-save me-2"></i>
                )}
                {textoBoton}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ClientesPage;