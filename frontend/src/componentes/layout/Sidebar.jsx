import SidebarItem from './SidebarItem';
import { useAuth } from '../../context/AuthContext';

const Sidebar = () => {
  const { rol } = useAuth();

  const esAdmin = rol === 'ADMIN';
  const esVendedor = rol === 'VENDEDOR';
  const esAlmacen = rol === 'ALMACEN';
  const esCompras = rol === 'COMPRAS';
  const esCajero = rol === 'CAJERO';

  const obtenerNombreRol = () => {
    if (rol === 'ADMIN') return 'Administrador';
    if (rol === 'VENDEDOR') return 'Vendedor';
    if (rol === 'ALMACEN') return 'Almacén';
    if (rol === 'COMPRAS') return 'Compras';
    if (rol === 'CAJERO') return 'Cajero';

    return 'Usuario';
  };

  return (
    <aside
      className="text-white d-flex flex-column"
      style={{
        width: '260px',
        height: '100vh',
        background: '#08224a',
        position: 'fixed',
        top: 0,
        left: 0,
        zIndex: 3000,
        overflowY: 'auto',
      }}
    >
      <div className="px-4 py-4">
        <h4 className="fw-bold mb-0">
          <i className="bi bi-box-seam me-2"></i>
          ProvePeru
        </h4>
      </div>

      <div className="px-3">
        <p className="text-uppercase text-white-50 small mb-2">Principal</p>

        <SidebarItem
          label="Panel Principal"
          to="/home"
          icon="bi-speedometer2"
        />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Operaciones
        </p>

        {(esAdmin || esVendedor || esCajero) && (
          <SidebarItem label="Ventas" to="/ventas" icon="bi-cart-check" />
        )}

        {(esAdmin || esAlmacen || esCompras) && (
          <SidebarItem label="Inventario" to="/inventario" icon="bi-boxes" />
        )}

        {(esAdmin || esCompras || esAlmacen) && (
          <SidebarItem label="Compras" to="/compras" icon="bi-bag-check" />
        )}

        {(esAdmin || esCajero) && (
          <SidebarItem label="Caja / Pagos" to="/caja" icon="bi-cash-stack" />
        )}

        {esAdmin && (
          <>
            <p className="text-uppercase text-white-50 small mt-4 mb-2">
              Análisis Gerencial
            </p>

            <SidebarItem label="Reportes" to="/reportes" icon="bi-bar-chart" />
          </>
        )}

        {(esAdmin || esVendedor) && (
          <>
            <p className="text-uppercase text-white-50 small mt-4 mb-2">
              Gestión de Clientes
            </p>

            <SidebarItem label="Clientes" to="/clientes" icon="bi-people" />

            <SidebarItem
              label="Historial Compras"
              to="/historial-compras"
              icon="bi-clock-history"
            />
          </>
        )}

        {esAdmin && (
          <>
            <p className="text-uppercase text-white-50 small mt-4 mb-2">
              Administración
            </p>

            <SidebarItem
              label="Usuarios y Roles"
              to="/usuarios-roles"
              icon="bi-person-gear"
            />
          </>
        )}
      </div>

      <div className="mt-auto px-4 py-4">
        <div className="fw-bold">
          <i className="bi bi-person-badge me-2"></i>
          {obtenerNombreRol()}
        </div>
      </div>
    </aside>
  );
};

export default Sidebar;