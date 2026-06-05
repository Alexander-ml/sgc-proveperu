import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const HomePage = () => {
  const navigate = useNavigate();
  const { usuario, token, rol, logout } = useAuth();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div style={{ padding: '30px', fontFamily: 'Arial, sans-serif' }}>
      <h1>Información del usuario autenticado</h1>

      <div
        style={{
          background: '#f1f5f9',
          padding: '20px',
          borderRadius: '10px',
          maxWidth: '800px',
          marginTop: '20px',
        }}
      >
        <p>
          <strong>Nombre completo:</strong> {usuario?.nombreCompleto}
        </p>

        <p>
          <strong>Usuario login:</strong> {usuario?.usuarioLogin}
        </p>

        <p>
          <strong>Rol:</strong> {rol}
        </p>

        <p>
          <strong>Tipo:</strong> Bearer
        </p>

        <p>
          <strong>Token:</strong>
        </p>

        <textarea
          value={token || ''}
          readOnly
          rows="7"
          style={{
            width: '100%',
            padding: '10px',
            borderRadius: '8px',
            border: '1px solid #cbd5e1',
            resize: 'none',
          }}
        />
      </div>

      <button
        onClick={handleLogout}
        style={{
          marginTop: '20px',
          padding: '12px 20px',
          background: '#dc2626',
          color: 'white',
          border: 'none',
          borderRadius: '8px',
          cursor: 'pointer',
        }}
      >
        Cerrar sesión
      </button>
    </div>
  );
};

export default HomePage;