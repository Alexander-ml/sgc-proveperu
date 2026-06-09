import SidebarItem from './SidebarItem';

const Sidebar = () => {
  return (
    <aside
      className="text-white d-flex flex-column"
      style={{
        width: '260px',
        minHeight: '100vh',
        background: '#08224a',
      }}
    >
      <div className="px-4 py-4">
        <h4 className="fw-bold mb-0">ProvePeru</h4>
      </div>

      <div className="px-3">
        <p className="text-uppercase text-white-50 small mb-2">Principal</p>
        <SidebarItem label="Panel Principal" active />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Operaciones
        </p>
        <SidebarItem label="Ventas" />
        <SidebarItem label="Inventario" />
        <SidebarItem label="Compras" />
        <SidebarItem label="Caja / Pagos" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Análisis Gerencial
        </p>
        <SidebarItem label="Reportes" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Gestión de Clientes
        </p>
        <SidebarItem label="Clientes" />
        <SidebarItem label="Historial Compras" />

        <p className="text-uppercase text-white-50 small mt-4 mb-2">
          Administración
        </p>
        <SidebarItem label="Usuarios y Roles" />
      </div>

      <div className="mt-auto px-4 py-4">
        <div className="fw-semibold">Administrador</div>
      </div>
    </aside>
  );
};

export default Sidebar;