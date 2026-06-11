import SidebarItem from './SidebarItem';

const Sidebar = () => {
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
        <h4 className="fw-bold mb-0">ProvePeru</h4>
      </div>

      <div className="px-3">
        <p className="text-uppercase text-white-50 small mb-2">Principal</p>
        <SidebarItem label="Panel Principal" to="/home" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Operaciones
        </p>
        <SidebarItem label="Ventas" to="/ventas" />
        <SidebarItem label="Inventario" to="/inventario" />
        <SidebarItem label="Compras" to="/compras" />
        <SidebarItem label="Caja / Pagos" to="/caja" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Análisis Gerencial
        </p>
        <SidebarItem label="Reportes" to="/reportes" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Gestión de Clientes
        </p>
        <SidebarItem label="Clientes" to="/clientes" />
        <SidebarItem label="Historial Compras" to="/historial-compras" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Administración
        </p>
        <SidebarItem label="Usuarios y Roles" to="/usuarios-roles" />
      </div>

      <div className="mt-auto px-4 py-4">
        <div className="fw-semibold">Administrador</div>
      </div>
    </aside>
  );
};

export default Sidebar;