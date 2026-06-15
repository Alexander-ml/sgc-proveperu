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
  cambiarPasswordUsuario,
} from '../../services/usuarioService';

import {
  listarRoles,
  obtenerRolPorId,
  actualizarPermisosRol,
} from '../../services/rolService';

import { listarPermisos } from '../../services/permisoService';

const ROLES = [
  { idRol: 1, nombre: 'ADMIN' },
  { idRol: 2, nombre: 'VENDEDOR' },
  { idRol: 3, nombre: 'ALMACEN' },
  { idRol: 4, nombre: 'COMPRAS' },
  { idRol: 5, nombre: 'CAJERO' },
];

const PERMISOS_TEMPORALES = [
  {
    idPermiso: 1,
    modulo: 'USUARIOS',
    accion: 'CREAR',
    descripcion: 'Crear usuarios',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 2,
    modulo: 'USUARIOS',
    accion: 'LEER',
    descripcion: 'Consultar usuarios',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 3,
    modulo: 'USUARIOS',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar usuarios',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 4,
    modulo: 'USUARIOS',
    accion: 'ELIMINAR',
    descripcion: 'Eliminar usuarios',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 5,
    modulo: 'CLIENTES',
    accion: 'CREAR',
    descripcion: 'Registrar clientes',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 6,
    modulo: 'CLIENTES',
    accion: 'LEER',
    descripcion: 'Consultar clientes',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 7,
    modulo: 'CLIENTES',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar clientes',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 8,
    modulo: 'CLIENTES',
    accion: 'ELIMINAR',
    descripcion: 'Eliminar clientes',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 9,
    modulo: 'VENTAS',
    accion: 'CREAR',
    descripcion: 'Registrar ventas',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 10,
    modulo: 'VENTAS',
    accion: 'LEER',
    descripcion: 'Consultar ventas',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 11,
    modulo: 'VENTAS',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar ventas',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 12,
    modulo: 'VENTAS',
    accion: 'ELIMINAR',
    descripcion: 'Anular ventas',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 13,
    modulo: 'INVENTARIO',
    accion: 'CREAR',
    descripcion: 'Ingresar movimientos de inventario',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 14,
    modulo: 'INVENTARIO',
    accion: 'LEER',
    descripcion: 'Consultar inventario',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 15,
    modulo: 'INVENTARIO',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar inventario',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 16,
    modulo: 'INVENTARIO',
    accion: 'ELIMINAR',
    descripcion: 'Anular movimientos de inventario',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 17,
    modulo: 'COMPRAS',
    accion: 'CREAR',
    descripcion: 'Registrar órdenes de compra',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 18,
    modulo: 'COMPRAS',
    accion: 'LEER',
    descripcion: 'Consultar compras',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 19,
    modulo: 'COMPRAS',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar compras',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 20,
    modulo: 'COMPRAS',
    accion: 'ELIMINAR',
    descripcion: 'Anular compras',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 21,
    modulo: 'CAJA',
    accion: 'CREAR',
    descripcion: 'Registrar movimientos de caja',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 22,
    modulo: 'CAJA',
    accion: 'LEER',
    descripcion: 'Consultar caja',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 23,
    modulo: 'CAJA',
    accion: 'ACTUALIZAR',
    descripcion: 'Modificar movimientos de caja',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 24,
    modulo: 'CAJA',
    accion: 'ELIMINAR',
    descripcion: 'Anular movimientos de caja',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 25,
    modulo: 'PROCESOS_COMPARTIDOS',
    accion: 'CREAR',
    descripcion: 'Crear en módulos compartidos',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 26,
    modulo: 'PROCESOS_COMPARTIDOS',
    accion: 'LEER',
    descripcion: 'Leer módulos compartidos',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 27,
    modulo: 'PROCESOS_COMPARTIDOS',
    accion: 'ACTUALIZAR',
    descripcion: 'Actualizar módulos compartidos',
    estado: 'ACTIVO',
  },
  {
    idPermiso: 28,
    modulo: 'PROCESOS_COMPARTIDOS',
    accion: 'ELIMINAR',
    descripcion: 'Eliminar en módulos compartidos',
    estado: 'ACTIVO',
  },
];

const UsuariosRolesPage = () => {
  const [dashboard, setDashboard] = useState(null);
  const [usuarios, setUsuarios] = useState([]);
  const [busqueda, setBusqueda] = useState('');
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const [tabActivo, setTabActivo] = useState('usuarios');

  const [roles, setRoles] = useState([]);
  const [permisos, setPermisos] = useState([]);
  const [rolSeleccionado, setRolSeleccionado] = useState(null);
  const [permisosRolSeleccionados, setPermisosRolSeleccionados] = useState([]);
  const [guardandoPermisos, setGuardandoPermisos] = useState(false);

  const [mostrarModalCrear, setMostrarModalCrear] = useState(false);
  const [mostrarModalEditar, setMostrarModalEditar] = useState(false);

  const [mostrarModalPassword, setMostrarModalPassword] = useState(false);
  const [usuarioPassword, setUsuarioPassword] = useState(null);
  const [nuevaPassword, setNuevaPassword] = useState('');

  const [nuevoUsuario, setNuevoUsuario] = useState({
    nombreCompleto: '',
    usuarioLogin: '',
    nombreRol: 'VENDEDOR',
    password: '',
  });

  const [usuarioEditando, setUsuarioEditando] = useState({
    id: null,
    nombreCompleto: '',
    usuarioLogin: '',
    nombreRol: 'VENDEDOR',
    estado: 'ACTIVO',
  });

  const [historialAccesos] = useState([
    {
      fecha: '2026-05-06',
      hora: '08:45',
      usuario: 'César Medina',
      accion: 'Inicio de sesión',
      ip: '192.168.1.10',
    },
    {
      fecha: '2026-05-06',
      hora: '08:00',
      usuario: 'Iris Arroyo',
      accion: 'Inicio de sesión',
      ip: '192.168.1.11',
    },
    {
      fecha: '2026-05-06',
      hora: '07:55',
      usuario: 'Marco Hernández',
      accion: 'Inicio de sesión',
      ip: '192.168.1.15',
    },
    {
      fecha: '2026-05-05',
      hora: '14:45',
      usuario: 'Iris Arroyo',
      accion: 'Registró venta V-2026-003',
      ip: '192.168.1.11',
    },
  ]);

  const cargarDatos = async () => {
    try {
      setCargando(true);
      setError('');

      const dashboardResponse = await obtenerDashboardUsuarios();
      const usuariosResponse = await listarUsuarios(busqueda);

      setDashboard(dashboardResponse.data);
      setUsuarios(usuariosResponse.data || []);

      try {
        const rolesResponse = await listarRoles();
        setRoles(rolesResponse.data || []);
      } catch (errorRoles) {
        console.error('Error cargando roles:', errorRoles);
        setRoles([]);
      }

      try {
        const permisosResponse = await listarPermisos();
        setPermisos(permisosResponse.data || []);
      } catch (errorPermisos) {
        console.warn(
          'No se pudo cargar /api/permisos. Se usarán permisos temporales.',
          errorPermisos
        );

        setPermisos(PERMISOS_TEMPORALES);
      }
    } catch (error) {
      console.error('Error cargando usuarios:', error);
      setError('No se pudo cargar la información de usuarios.');
      setDashboard(null);
      setUsuarios([]);
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
    return usuario.idUsuario || usuario.id || usuario.id_usuario;
  };

  const obtenerIniciales = (nombre = '') => {
    const partes = nombre.trim().split(' ');

    if (partes.length === 1) {
      return partes[0].substring(0, 2).toUpperCase();
    }

    return `${partes[0][0] || ''}${partes[1][0] || ''}`.toUpperCase();
  };

  const obtenerNombreRolVisible = (rol) => {
    if (!rol) return 'Sin rol';

    if (rol === 'ADMIN') return 'Administrador';
    if (rol === 'VENDEDOR') return 'Vendedor';
    if (rol === 'ALMACEN') return 'Almacenero';
    if (rol === 'COMPRAS') return 'Compras';
    if (rol === 'CAJERO') return 'Cajero';

    return rol;
  };

  const obtenerClavePermiso = (permiso) => {
    return `${permiso.modulo} - ${permiso.accion}`;
  };

  const obtenerIdsPermisosAsignados = (detalleRol, permisosDisponibles) => {
    const permisosRol = detalleRol?.permisos || detalleRol?.listaPermisos || [];

    return permisosDisponibles
      .filter((permiso) =>
        permisosRol.some((permisoRol) => {
          if (typeof permisoRol === 'string') {
            return permisoRol === obtenerClavePermiso(permiso);
          }

          return (
            permisoRol.idPermiso === permiso.idPermiso ||
            permisoRol.codigoPermiso === permiso.codigoPermiso ||
            (permisoRol.modulo === permiso.modulo &&
              permisoRol.accion === permiso.accion)
          );
        })
      )
      .map((permiso) => permiso.idPermiso);
  };

  const seleccionarRol = async (idRol) => {
    try {
      const response = await obtenerRolPorId(idRol);
      const detalleRol = response.data;

      setRolSeleccionado(detalleRol);
      setPermisosRolSeleccionados(
        obtenerIdsPermisosAsignados(detalleRol, permisos)
      );
    } catch (error) {
      console.error('Error obteniendo detalle del rol:', error);

      const rolLocal = roles.find((rol) => rol.idRol === idRol);

      setRolSeleccionado({
        ...rolLocal,
        permisos: [],
      });

      setPermisosRolSeleccionados([]);
    }
  };

  const cambiarSeleccionPermiso = (idPermiso) => {
    setPermisosRolSeleccionados((permisosActuales) => {
      if (permisosActuales.includes(idPermiso)) {
        return permisosActuales.filter((id) => id !== idPermiso);
      }

      return [...permisosActuales, idPermiso];
    });
  };

  const guardarPermisosRol = async () => {
    if (!rolSeleccionado) return;

    if (permisosRolSeleccionados.length === 0) {
      alert('Debes seleccionar al menos un permiso.');
      return;
    }

    try {
      setGuardandoPermisos(true);

      await actualizarPermisosRol(rolSeleccionado.idRol, {
        permisos: permisosRolSeleccionados,
      });

      await seleccionarRol(rolSeleccionado.idRol);
      await cargarDatos();

      alert('Permisos actualizados correctamente');
    } catch (error) {
      console.error('Error actualizando permisos:', error);
      alert(
        error.response?.data?.message ||
          'No se pudieron actualizar los permisos'
      );
    } finally {
      setGuardandoPermisos(false);
    }
  };


  const handleChangeCrear = (e) => {
    const { name, value } = e.target;

    setNuevoUsuario({
      ...nuevoUsuario,
      [name]: value,
    });
  };

  const cerrarModalCrear = () => {
    setNuevoUsuario({
      nombreCompleto: '',
      usuarioLogin: '',
      nombreRol: 'VENDEDOR',
      password: '',
    });

    setMostrarModalCrear(false);
  };

  const handleCrearUsuario = async (e) => {
    e.preventDefault();

    try {
      await crearUsuario(nuevoUsuario);

      cerrarModalCrear();
      await cargarDatos();
      alert('Usuario creado correctamente');
    } catch (error) {
      console.error('Error creando usuario:', error);
      console.error('Respuesta backend:', error.response?.data);

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
        nombreRol: detalle.nombreRol || detalle.rol || 'VENDEDOR',
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
        nombreRol: usuario.nombreRol || usuario.rol || 'VENDEDOR',
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
      nombreRol: 'VENDEDOR',
      estado: 'ACTIVO',
    });
  };

  const handleChangeEditar = (e) => {
    const { name, value } = e.target;

    setUsuarioEditando({
      ...usuarioEditando,
      [name]: value,
    });
  };

  const handleEditarUsuario = async (e) => {
    e.preventDefault();

    try {
      const request = {
        nombreCompleto: usuarioEditando.nombreCompleto,
        usuarioLogin: usuarioEditando.usuarioLogin,
        nombreRol: usuarioEditando.nombreRol,
        estado: usuarioEditando.estado,
      };

      await editarUsuario(usuarioEditando.id, request);

      cerrarModalEditar();
      await cargarDatos();
      alert('Usuario actualizado correctamente');
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

  const cerrarModalPassword = () => {
    setMostrarModalPassword(false);
    setUsuarioPassword(null);
    setNuevaPassword('');
  };

  const handleCambiarPassword = async (e) => {
    e.preventDefault();

    try {
      const id = obtenerIdUsuario(usuarioPassword);

      await cambiarPasswordUsuario(id, nuevaPassword);

      cerrarModalPassword();
      alert('Contraseña actualizada correctamente');
    } catch (error) {
      console.error('Error cambiando contraseña:', error);
      alert(
        error.response?.data?.message ||
          'No se pudo actualizar la contraseña'
      );
    }
  };

  if (cargando) {
    return (
      <MainLayout>
        <div className="d-flex align-items-center gap-2">
          <div className="spinner-border spinner-border-sm text-primary" />
          <span>Cargando usuarios y roles...</span>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout>
      <div className="mb-4">
        <p className="text-muted mb-1">
          <i className="bi bi-house-door me-1"></i>
          Inicio &gt; Usuarios y Roles
        </p>

        <h4 className="page-title mb-1">
          <i className="bi bi-person-gear me-2 text-primary"></i>
          USUARIOS Y ROLES
        </h4>

        <p className="page-subtitle mb-0">
          Administración de accesos y permisos
        </p>
      </div>

      {error && (
        <div className="alert alert-danger">
          <i className="bi bi-exclamation-triangle me-2"></i>
          {error}
        </div>
      )}

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
            value={dashboard?.rolesDefinidos ?? roles.length}
            description="Roles del sistema"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Módulos Disponibles"
            value={permisos.length}
            description="Módulos con permisos"
            color="info"
          />
        </div>
      </div>

      <div className="d-flex justify-content-between align-items-center mb-3 gap-3 flex-wrap">
        <div className="app-tabs d-flex gap-1 flex-wrap">
          <button
            type="button"
            className={`app-tab-btn ${
              tabActivo === 'usuarios' ? 'active' : ''
            }`}
            onClick={() => setTabActivo('usuarios')}
          >
            <i className="bi bi-people me-2"></i>
            Usuarios
          </button>

          <button
            type="button"
            className={`app-tab-btn ${tabActivo === 'roles' ? 'active' : ''}`}
            onClick={() => setTabActivo('roles')}
          >
            <i className="bi bi-shield-check me-2"></i>
            Roles y Permisos
          </button>

          <button
            type="button"
            className={`app-tab-btn ${
              tabActivo === 'historial' ? 'active' : ''
            }`}
            onClick={() => setTabActivo('historial')}
          >
            <i className="bi bi-clock-history me-2"></i>
            Historial de Accesos
          </button>
        </div>

        {tabActivo === 'usuarios' && (
          <button
            type="button"
            className="btn btn-primary btn-sm px-4 py-2 fw-semibold app-btn-primary"
            onClick={() => setMostrarModalCrear(true)}
          >
            <i className="bi bi-person-plus me-2"></i>
            Nuevo Usuario
          </button>
        )}
      </div>

      {tabActivo === 'usuarios' && (
        <>
          <form onSubmit={buscarUsuarios} className="mb-3">
            <div className="row g-2">
              <div className="col-12 col-md-10">
                <div className="input-group">
                  <span className="input-group-text bg-white">
                    <i className="bi bi-search text-muted"></i>
                  </span>

                  <input
                    type="text"
                    className="form-control app-input"
                    placeholder="Buscar usuario"
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                  />
                </div>
              </div>

              <div className="col-12 col-md-2">
                <button
                  className="btn btn-outline-primary w-100"
                  type="submit"
                >
                  <i className="bi bi-search me-2"></i>
                  Buscar
                </button>
              </div>
            </div>
          </form>

          <div className="row g-3">
            {usuarios.length === 0 ? (
              <div className="col-12">
                <div className="alert alert-info">
                  <i className="bi bi-info-circle me-2"></i>
                  No se encontraron usuarios registrados.
                </div>
              </div>
            ) : (
              usuarios.map((usuario) => (
                <div
                  className="col-12 col-md-6"
                  key={obtenerIdUsuario(usuario)}
                >
                  <div className="app-card app-card-hover h-100 p-3">
                    <div className="d-flex justify-content-between align-items-start mb-3">
                      <div className="d-flex align-items-center gap-3">
                        <div
                          className="rounded-circle bg-primary text-white d-flex align-items-center justify-content-center fw-bold"
                          style={{ width: '46px', height: '46px' }}
                        >
                          {obtenerIniciales(usuario.nombreCompleto)}
                        </div>

                        <div>
                          <h6 className="fw-bold mb-1">
                            {usuario.nombreCompleto}
                          </h6>

                          <p className="text-muted small mb-1">
                            <i className="bi bi-envelope me-1"></i>
                            {usuario.usuarioLogin}
                          </p>

                          <span className="badge bg-primary bg-opacity-10 text-primary app-badge">
                            {obtenerNombreRolVisible(
                              usuario.nombreRol || usuario.rol
                            )}
                          </span>
                        </div>
                      </div>

                      <BadgeStatus
                        variant={
                          usuario.estado === 'ACTIVO' ? 'success' : 'danger'
                        }
                      >
                        {usuario.estado}
                      </BadgeStatus>
                    </div>

                    <hr />

                    <div className="d-flex justify-content-end gap-2 flex-wrap">
                      <button
                        type="button"
                        className="btn btn-sm btn-outline-secondary"
                        onClick={() => abrirModalEditar(usuario)}
                      >
                        <i className="bi bi-pencil-square me-1"></i>
                        Editar
                      </button>

                      <button
                        type="button"
                        className="btn btn-sm btn-outline-primary"
                        onClick={() => {
                          setUsuarioPassword(usuario);
                          setMostrarModalPassword(true);
                        }}
                      >
                        <i className="bi bi-key me-1"></i>
                        Contraseña
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
                        <i
                          className={`bi ${
                            usuario.estado === 'ACTIVO'
                              ? 'bi-person-x'
                              : 'bi-person-check'
                          } me-1`}
                        ></i>
                        {usuario.estado === 'ACTIVO' ? 'Suspender' : 'Activar'}
                      </button>
                    </div>
                  </div>
                </div>
              ))
            )}
          </div>
        </>
      )}

      {tabActivo === 'roles' && (
        <div className="row g-3">
          <div className="col-12 col-xl-6">
            <h6 className="fw-bold mb-3">
              <i className="bi bi-shield-check me-2 text-primary"></i>
              Roles definidos en el sistema
            </h6>

            {roles.length === 0 ? (
              <div className="alert alert-info">
                <i className="bi bi-info-circle me-2"></i>
                No se pudieron cargar los roles.
              </div>
            ) : (
              roles.map((rolItem) => (
                <div
                  className={`app-card app-card-hover p-3 mb-3 ${
                    rolSeleccionado?.idRol === rolItem.idRol
                      ? 'border border-primary'
                      : ''
                  }`}
                  key={rolItem.idRol}
                  style={{ cursor: 'pointer' }}
                  onClick={() => seleccionarRol(rolItem.idRol)}
                >
                  <div className="d-flex justify-content-between align-items-start">
                    <div>
                      <h6 className="fw-bold mb-1">
                        <i className="bi bi-person-badge me-2 text-primary"></i>
                        {obtenerNombreRolVisible(
                          rolItem.nombreRol || rolItem.nombre
                        )}
                      </h6>

                      <span className="badge bg-success bg-opacity-10 text-success app-badge">
                        ACTIVO
                      </span>
                    </div>

                    <span className="text-muted small">
                      <i className="bi bi-eye me-1"></i>
                      Ver permisos
                    </span>
                  </div>

                  <p className="text-muted small mt-3 mb-0">
                    {rolItem.descripcion || 'Rol predeterminado del sistema'}
                  </p>
                </div>
              ))
            )}

            <div className="alert alert-secondary">
              <i className="bi bi-lock me-2"></i>
              Los roles son predeterminados. No se crearán nuevos roles.
            </div>
          </div>

          <div className="col-12 col-xl-6">
            <h6 className="fw-bold mb-3">
              <i className="bi bi-list-check me-2 text-primary"></i>
              Permisos del rol seleccionado
            </h6>

            {!rolSeleccionado ? (
              <div
                className="app-card d-flex align-items-center justify-content-center text-muted p-4"
                style={{
                  minHeight: '240px',
                  borderStyle: 'dashed',
                }}
              >
                <div className="text-center">
                  <i className="bi bi-shield fs-1 d-block mb-2"></i>
                  Selecciona un rol de la lista para ver sus permisos
                </div>
              </div>
            ) : (
              <div className="app-card p-3">
                <h5 className="fw-bold mb-1">
                  <i className="bi bi-person-badge me-2 text-primary"></i>
                  {obtenerNombreRolVisible(
                    rolSeleccionado.nombreRol || rolSeleccionado.nombre
                  )}
                </h5>

                <p className="text-muted">
                  {rolSeleccionado.descripcion || 'Detalle de permisos del rol'}
                </p>

                <hr />

                <div className="row g-2">
                  {permisos.map((permiso) => {
                    const tienePermiso = permisosRolSeleccionados.includes(
                      permiso.idPermiso
                    );

                    return (
                      <div className="col-12" key={permiso.idPermiso}>
                        <div
                          className={`border rounded p-3 d-flex justify-content-between align-items-center ${
                            tienePermiso
                              ? 'bg-success bg-opacity-10 border-success'
                              : 'bg-light'
                          }`}
                        >
                          <div className="form-check">
                            <input
                              className="form-check-input"
                              type="checkbox"
                              id={`permiso-${permiso.idPermiso}`}
                              checked={tienePermiso}
                              onChange={() =>
                                cambiarSeleccionPermiso(permiso.idPermiso)
                              }
                            />

                            <label
                              className="form-check-label"
                              htmlFor={`permiso-${permiso.idPermiso}`}
                            >
                              <strong>
                                <i className="bi bi-check2-square me-2"></i>
                                {permiso.modulo} - {permiso.accion}
                              </strong>

                              <p className="text-muted small mb-0">
                                {permiso.descripcion || 'Permiso del sistema'}
                              </p>
                            </label>
                          </div>

                          <div>
                            <span
                              className={`badge app-badge ${
                                tienePermiso ? 'bg-success' : 'bg-secondary'
                              }`}
                            >
                              {tienePermiso ? 'Asignado' : 'No asignado'}
                            </span>
                          </div>
                        </div>
                      </div>
                    );
                  })}
                </div>

                <div className="d-flex justify-content-end mt-3">
                  <button
                    type="button"
                    className="btn btn-primary app-btn-primary"
                    onClick={guardarPermisosRol}
                    disabled={guardandoPermisos}
                  >
                    {guardandoPermisos ? (
                      <>
                        <span className="spinner-border spinner-border-sm me-2" />
                        Guardando...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-save me-2"></i>
                        Guardar permisos
                      </>
                    )}
                  </button>
                </div>

                <div className="alert alert-info mt-3 mb-0">
                  <i className="bi bi-info-circle me-2"></i>
                  Marca o desmarca permisos y luego guarda los cambios del rol.
                </div>
              </div>
            )}
          </div>
        </div>
      )}

      {tabActivo === 'historial' && (
        <div className="app-card p-3">
          <h6 className="fw-bold mb-1">
            <i className="bi bi-clock-history me-2 text-primary"></i>
            Historial de Accesos al Sistema
          </h6>

          <p className="text-muted small mb-4">
            Registro de inicio/cierre de sesión y acciones de usuarios.
          </p>

          <div className="alert alert-warning">
            <i className="bi bi-exclamation-triangle me-2"></i>
            Vista temporal. Se conectará al endpoint real cuando esté
            disponible.
          </div>

          <div className="table-responsive">
            <table className="table align-middle app-table">
              <thead>
                <tr>
                  <th>Fecha y Hora</th>
                  <th>Usuario</th>
                  <th>Acción registrada</th>
                  <th>IP de acceso</th>
                </tr>
              </thead>

              <tbody>
                {historialAccesos.map((item, index) => (
                  <tr key={index}>
                    <td>
                      <strong>{item.fecha}</strong>
                      <br />
                      <span className="text-muted small">{item.hora}</span>
                    </td>

                    <td>
                      <i className="bi bi-person-circle me-2 text-primary"></i>
                      {item.usuario}
                    </td>

                    <td>{item.accion}</td>

                    <td>
                      <code>{item.ip}</code>
                    </td>
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
              <form onSubmit={handleCrearUsuario}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-person-plus me-2 text-primary"></i>
                    Nuevo Usuario
                  </h5>

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
                      className="form-control app-input"
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
                      className="form-control app-input"
                      value={nuevoUsuario.usuarioLogin}
                      onChange={handleChangeCrear}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Rol asignado</label>
                    <select
                      name="nombreRol"
                      className="form-select app-select"
                      value={nuevoUsuario.nombreRol}
                      onChange={handleChangeCrear}
                      required
                    >
                      {ROLES.map((rol) => (
                        <option key={rol.idRol} value={rol.nombre}>
                          {obtenerNombreRolVisible(rol.nombre)}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Contraseña inicial</label>
                    <input
                      type="password"
                      name="password"
                      className="form-control app-input"
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
                    <i className="bi bi-x-circle me-2"></i>
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-check-circle me-2"></i>
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
                  <h5 className="modal-title">
                    <i className="bi bi-pencil-square me-2 text-primary"></i>
                    Editar Usuario
                  </h5>

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
                      className="form-control app-input"
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
                      className="form-control app-input"
                      value={usuarioEditando.usuarioLogin}
                      onChange={handleChangeEditar}
                      required
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Rol asignado</label>
                    <select
                      name="nombreRol"
                      className="form-select app-select"
                      value={usuarioEditando.nombreRol}
                      onChange={handleChangeEditar}
                      required
                    >
                      {ROLES.map((rol) => (
                        <option key={rol.idRol} value={rol.nombre}>
                          {obtenerNombreRolVisible(rol.nombre)}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Estado</label>
                    <select
                      name="estado"
                      className="form-select app-select"
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
                    <i className="bi bi-x-circle me-2"></i>
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-save me-2"></i>
                    Guardar Cambios
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      )}

      {mostrarModalPassword && (
        <div
          className="modal d-block"
          tabIndex="-1"
          style={{ background: 'rgba(0, 0, 0, 0.45)' }}
        >
          <div className="modal-dialog modal-md modal-dialog-centered">
            <div className="modal-content">
              <form onSubmit={handleCambiarPassword}>
                <div className="modal-header">
                  <h5 className="modal-title">
                    <i className="bi bi-key me-2 text-primary"></i>
                    Cambiar contraseña
                  </h5>

                  <button
                    type="button"
                    className="btn-close"
                    onClick={cerrarModalPassword}
                  ></button>
                </div>

                <div className="modal-body">
                  <p className="text-muted">
                    Usuario:{' '}
                    <strong>{usuarioPassword?.nombreCompleto}</strong>
                  </p>

                  <label className="form-label">Nueva contraseña</label>
                  <input
                    type="password"
                    className="form-control app-input"
                    value={nuevaPassword}
                    onChange={(e) => setNuevaPassword(e.target.value)}
                    minLength={8}
                    required
                  />

                  <small className="text-muted">
                    La contraseña debe tener mínimo 8 caracteres.
                  </small>
                </div>

                <div className="modal-footer">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={cerrarModalPassword}
                  >
                    <i className="bi bi-x-circle me-2"></i>
                    Cancelar
                  </button>

                  <button
                    type="submit"
                    className="btn btn-primary app-btn-primary"
                  >
                    <i className="bi bi-save me-2"></i>
                    Guardar contraseña
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
