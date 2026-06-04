import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const RoleRoute = ({ children, allowedRoles }) => {
  const { rol } = useAuth();

  if (!allowedRoles.includes(rol)) {
    return <Navigate to="/login" replace />;
  }

  return children;
};

export default RoleRoute;