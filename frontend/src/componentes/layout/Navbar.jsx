import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
  const { usuario, rol, logout } = useAuth();

  const cerrarSesion = async () => {
    await logout();
    window.location.replace('/login');
  };

  return (
    <div className="bg-white rounded shadow-sm px-4 py-3 mb-4 d-flex justify-content-between align-items-center">
      <div>
        <span className="text-muted">
          <i className="bi bi-house-door me-2"></i>
          Inicio
        </span>
      </div>

      <div className="d-flex align-items-center gap-3">
        <div className="text-end">
          <div className="fw-bold">
            <i className="bi bi-person-circle me-2"></i>
            {usuario?.nombreCompleto || 'Usuario'}
          </div>

          <small className="text-muted">{rol}</small>
        </div>

        <button
          type="button"
          className="btn btn-outline-danger btn-sm"
          onClick={cerrarSesion}
        >
          <i className="bi bi-box-arrow-right me-1"></i>
          Salir
        </button>
      </div>
    </div>
  );
};

export default Navbar;