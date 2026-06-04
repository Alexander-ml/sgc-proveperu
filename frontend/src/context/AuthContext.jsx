import { createContext, useContext, useState } from 'react';
import { loginRequest } from '../services/authService';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [rol, setRol] = useState(localStorage.getItem('rol'));

  const login = async (username, password) => {
    const data = await loginRequest({
      username,
      password,
    });

    const tokenRecibido = data.token || data.jwt || data.accessToken;
    const rolRecibido = data.rol || data.role;

    localStorage.setItem('token', tokenRecibido);
    localStorage.setItem('rol', rolRecibido);

    setToken(tokenRecibido);
    setRol(rolRecibido);

    return {
      token: tokenRecibido,
      rol: rolRecibido,
    };
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('rol');

    setToken(null);
    setRol(null);
  };

  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider
      value={{
        token,
        rol,
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