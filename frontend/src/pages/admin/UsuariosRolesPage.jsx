import { useEffect, useState } from 'react';
import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';
import {
  obtenerDashboardUsuarios,
  listarUsuarios,
  crearUsuario,
  editarUsuario,
  obtenerUsuarioPorId,
  activarUsuario,
  suspenderUsuario,
} from '../../services/usuarioService';

const ROLES = [
  { idRol: 1, nombre: 'ADMIN' },
  { idRol: 2, nombre: 'VENDEDOR' },
  { idRol: 3, nombre: 'ALMACEN' },
  { idRol: 4, nombre: 'COMPRAS' },
  { idRol: 5, nombre: 'CAJERO' },
];

const UsuariosRolesPage = () => {
  const [dashboard, setDashboard] = useState(null);
  const [usuarios, setUsuarios] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);

  const [nuevoUsuario, setNuevoUsuario] = useState({
    nombreCompleto: '',
    usuarioLogin: '',
    idRol: 2,
    password: '',
  });

  const [usuarioEditando, setUsuarioEditando] = useState({
    id: null,
    nombreCompleto: '',
    usuarioLogin: '',
    idRol: 2,
    estado: 'ACTIVO',
  });

  const cargarDatos = async () => {
    try {
      setCargando(true);
      setError('');

      const dashboardResponse = await obtenerDashboardUsuarios();
      const usuariosResponse = await listarUsuarios(busqueda);

      setDashboard(dashboardResponse.data);
      setUsuarios(usuariosResponse.data || []);
    } catch (error) {
      console.error('Error cargando usuarios:', error);
      setError('No se pudo cargar la información de usuarios.');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const buscarUsuarios = async (e) => {
    e.preventDefault();
    cargarDatos();
  };

  const obtenerIdUsuario = (usuario) => {
    return usuario.id || usuario.idUsuario || usuario.id_usuario;
  };

  const obtenerIdRolDesdeNombre = (rol) => {
    const rolEncontrado = ROLES.find((item) => item.nombre === rol);
    return rolEncontrado ? rolEncontrado.idRol : 2;
  };

  const handleChangeCrear = (e) => {
    const { name, value } = e.target;

    setNuevoUsuario({
      ...nuevoUsuario,
      [name]: name === 'idRol' ? Number(value) : value,
    });
  };

  const cerrarModalCrear = () => {
    setNuevoUsuario({
      nombreCompleto: '',
      usuarioLogin: '',
      idRol: 2,
      password: '',
    });

    setMostrarModalCrear(false);
  };

  const handleCrearUsuario = async (e) => {
    e.preventDefault();

    try {
      await crearUsuario(nuevoUsuario);
      cerrarModalCrear();
      cargarDatos();
    } catch (error) {
      console.error('Error creando usuario:', error);
      alert(error.response?.data?.message || 'No se pudo crear el usuario');
    }
  };

  const abrirModalEditar = async (usuario) => {
    try {
      const id = obtenerIdUsuario(usuario);
      const response = await obtenerUsuarioPorId(id);
      const detalle = response.data;

      setUsuarioEditando({
        id,
        nombreCompleto: detalle.nombreCompleto || '',
        usuarioLogin: detalle.usuarioLogin || '',
        idRol: detalle.idRol || obtenerIdRolDesdeNombre(detalle.rol),
        estado: detalle.estado || 'ACTIVO',
      });

      setMostrarModalEditar(true);
    } catch (error) {
      console.error('Error obteniendo usuario:', error);

      const id = obtenerIdUsuario(usuario);

      setUsuarioEditando({
        id,
        nombreCompleto: usuario.nombreCompleto || '',
        usuarioLogin: usuario.usuarioLogin || '',
        idRol: usuario.idRol || obtenerIdRolDesdeNombre(usuario.rol),
        estado: usuario.estado || 'ACTIVO',
      });

      setMostrarModalEditar(true);
    }
  };

  const cerrarModalEditar = () => {
    setMostrarModalEditar(false);

    setUsuarioEditando({
      id: null,
      nombreCompleto: '',
      usuarioLogin: '',
      idRol: 2,
      estado: 'ACTIVO',
    });
  };

  const handleChangeEditar = (e) => {
    const { name, value } = e.target;

    setUsuarioEditando({
      ...usuarioEditando,
      [name]: name === 'idRol' ? Number(value) : value,
    });
  };

  const handleEditarUsuario = async (e) => {
    e.preventDefault();

    try {
      const request = {
        nombreCompleto: usuarioEditando.nombreCompleto,
        usuarioLogin: usuarioEditando.usuarioLogin,
        idRol: usuarioEditando.idRol,
        estado: usuarioEditando.estado,
      };

      await editarUsuario(usuarioEditando.id, request);

      cerrarModalEditar();
      cargarDatos();
    } catch (error) {
      console.error('Error editando usuario:', error);
      alert(error.response?.data?.message || 'No se pudo editar el usuario');
    }
  };

  const cambiarEstadoUsuario = async (usuario) => {
    try {
      const id = obtenerIdUsuario(usuario);

      if (usuario.estado === 'ACTIVO') {
        await suspenderUsuario(id);
      } else {
        await activarUsuario(id);
      }

      cargarDatos();
    } catch (error) {
      console.error('Error cambiando estado:', error);
      alert('No se pudo cambiar el estado del usuario');
    }
  };

  if (cargando) {
    return (
      <MainLayout>
        <p>Cargando usuarios...</p>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">Inicio &gt; Usuarios y Roles</p>
        <h4 className="fw-bold mb-1">USUARIOS Y ROLES</h4>
        <p className="text-muted mb-0">Administración de accesos y permisos</p>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Total Usuarios"
            value={dashboard?.totalUsuarios ?? 0}
            description="Usuarios registrados"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Usuarios Activos"
            value={dashboard?.usuariosActivos ?? 0}
            description="Usuarios habilitados"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Roles Definidos"
            value={dashboard?.rolesDefinidos ?? 0}
            description="Roles del sistema"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Módulos Disponibles"
            value={dashboard?.modulosDisponibles ?? 0}
            description="Módulos con permisos"
            color="info"
          />
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3">
        <div className="btn-group">
          <button type="button" className="btn btn-light shadow-sm active">
            Usuarios
          </button>
          <button type="button" className="btn btn-light shadow-sm">
            Roles y permisos
          </button>
          <button type="button" className="btn btn-light shadow-sm">
            Historial de accesos
          </button>
        </div>

        <button
          type="button"
          className="btn btn-primary"
          onClick={() => setMostrarModalCrear(true)}
        >
          + Nuevo Usuario
        </button>
      </div>

      <form onSubmit={buscarUsuarios} className="mb-3">
        <div className="d-flex gap-2">
          <input
            type="text"
            className="form-control"
            placeholder="Buscar usuario"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />

          <button className="btn btn-outline-primary" type="submit">
            Buscar
          </button>
        </div>
      </form>

      <div className="row g-3">
        {usuarios.length === 0 ? (
          <div className="col-12">
            <div className="alert alert-info">
              No se encontraron usuarios registrados.
            </div>
          </div>
        ) : (
          usuarios.map((usuario) => (
            <div className="col-12 col-md-6" key={obtenerIdUsuario(usuario)}>
              <div className="card border-0 shadow-sm h-100">
                <div className="card-body">
                  <div className="d-flex justify-content-between align-items-start">
                    <div>
                      <h6 className="fw-bold mb-1">
                        {usuario.nombreCompleto}
                      </h6>

                      <p className="text-muted small mb-2">
                        {usuario.usuarioLogin}
                      </p>
                    </div>

                    <BadgeStatus
                      variant={
                        usuario.estado === 'ACTIVO' ? 'success' : 'danger'
                      }
                    >
                      {usuario.estado}
                    </BadgeStatus>
                  </div>

                  <p className="mb-1">
                    <strong>Rol:</strong> {usuario.rol}
                  </p>

                  <p className="text-muted small mb-3">
                    {usuario.descripcionRol || 'Usuario del sistema'}
                  </p>

                  <div className="d-flex justify-content-end gap-2">
                    <button
                      type="button"
                      className="btn btn-sm btn-outline-secondary"
                      onClick={() => abrirModalEditar(usuario)}
                    >
                      Editar
                    </button>

                    <button
                      type="button"
                      className={`btn btn-sm ${
                        usuario.estado === 'ACTIVO'
                          ? 'btn-outline-danger'
                          : 'btn-outline-success'
                      }`}
                      onClick={() => cambiarEstadoUsuario(usuario)}
                    >
                      {usuario.estado === 'ACTIVO' ? 'Suspender' : 'Activar'}
                    </button>
                  </div>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {mostrarModalCrear && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleCrearUsuario}>
                <div className="modal-header">
                  <h5 className="modal-title">Nuevo Usuario</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalCrear}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">Nombre completo</label>
                    <input
                      type="text"
                      name="nombreCompleto"
                      className="form-control"
                      value={nuevoUsuario.nombreCompleto}
                      onChange={handleChangeCrear}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">
                      Correo electrónico / usuario
                    </label>
                    <input
                      type="email"
                      name="usuarioLogin"
                      className="form-control"
                      value={nuevoUsuario.usuarioLogin}
                      onChange={handleChangeCrear}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Rol asignado</label>
                    <select
                      name="idRol"
                      className="form-select"
                      value={nuevoUsuario.idRol}
                      onChange={handleChangeCrear}
                      required
                    >
                      {ROLES.map((rol) => (
                        <option key={rol.idRol} value={rol.idRol}>
                          {rol.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Contraseña inicial</label>
                    <input
                      type="password"
                      name="password"
                      className="form-control"
                      value={nuevoUsuario.password}
                      onChange={handleChangeCrear}
                      required
                      minLength={8}
                    />
                    <small className="text-muted">
                      La contraseña debe tener mínimo 8 caracteres.
                    </small>
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

                  <button type="submit" className="btn btn-primary">
                    Crear Usuario
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {mostrarModalEditar && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-lg modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleEditarUsuario}>
                <div className="modal-header">
                  <h5 className="modal-title">Editar Usuario</h5>
                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalEditar}
                  ></button>
                </div>

                <div className="modal-body">
                  <div className="mb-3">
                    <label className="form-label">Nombre completo</label>
                    <input
                      type="text"
                      name="nombreCompleto"
                      className="form-control"
                      value={usuarioEditando.nombreCompleto}
                      onChange={handleChangeEditar}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">
                      Correo electrónico / usuario
                    </label>
                    <input
                      type="email"
                      name="usuarioLogin"
                      className="form-control"
                      value={usuarioEditando.usuarioLogin}
                      onChange={handleChangeEditar}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Rol asignado</label>
                    <select
                      name="idRol"
                      className="form-select"
                      value={usuarioEditando.idRol}
                      onChange={handleChangeEditar}
                      required
                    >
                      {ROLES.map((rol) => (
                        <option key={rol.idRol} value={rol.idRol}>
                          {rol.nombre}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Estado</label>
                    <select
                      name="estado"
                      className="form-select"
                      value={usuarioEditando.estado}
                      onChange={handleChangeEditar}
                      required
                    >
                      <option value="ACTIVO">ACTIVO</option>
                      <option value="SUSPENDIDO">SUSPENDIDO</option>
                    </select>
                  </div>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalEditar}
                  >
                    Cancelar
                  </button>

                  <button type="submit" className="btn btn-primary">
                    Guardar Cambios
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

export default UsuariosRolesPage;