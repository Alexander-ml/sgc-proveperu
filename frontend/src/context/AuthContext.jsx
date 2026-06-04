import { createContext, useContext, useState } from 'react';
import { loginRequest } from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [rol, setRol] = useState(localStorage.getItem('rol'));
  const [usuario, setUsuario] = useState(
    JSON.parse(localStorage.getItem('usuario')) || null
  );

  const login = async (usuarioLogin, password) => {
    const response = await loginRequest({
      usuarioLogin,
      password,
    });

    const data = response.data;

    const tokenRecibido = data.token;
    const rolRecibido = data.rol;

    const usuarioRecibido = {
      usuarioLogin: data.usuarioLogin,
      nombreCompleto: data.nombreCompleto,
      rol: data.rol,
    };

    localStorage.setItem('token', tokenRecibido);
    localStorage.setItem('rol', rolRecibido);
    localStorage.setItem('usuario', JSON.stringify(usuarioRecibido));

    setToken(tokenRecibido);
    setRol(rolRecibido);
    setUsuario(usuarioRecibido);

    return {
      token: tokenRecibido,
      rol: rolRecibido,
      usuario: usuarioRecibido,
    };
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');
    localStorage.removeItem('usuario');

    setToken(null);
    setRol(null);
    setUsuario(null);
  };

  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider
      value={{
        token,
        rol,
        usuario,
        login,
        logout,
        isAuthenticated,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);