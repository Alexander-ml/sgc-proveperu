import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const Navbar = () => {
  const navigate = useNavigate();
  const { usuario, rol, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="d-flex justify-content-between align-items-center mb-4">
      <div>
        <h5 className="mb-0">Inicio</h5>
      </div>

      <div className="d-flex align-items-center gap-3">
        <div className="text-end">
          <strong className="d-block">
            {usuario?.nombreCompleto || 'Usuario'}
          </strong>
          <span className="text-muted">{rol || 'Administrador'}</span>
        </div>

        <button
          className="btn btn-outline-danger btn-sm"
          onClick={handleLogout}
        >
          Cerrar sesión
        </button>
      </div>
    </header>
  );
};

export default Navbar;