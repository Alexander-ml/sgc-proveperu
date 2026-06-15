/*Estructura de rutas mínima. Solo define la ruta raíz. Las rutas reales se agregan en la siguiente fase*/
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/auth/LoginPage';
import HomePage from '../pages/home/HomePage';
import UsuariosRolesPage from '../pages/admin/UsuariosRolesPage';
import RoleRoute from './RoleRoute';
import VentasPage from '../pages/ventas/VentasPage';

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route path="/login" element={<LoginPage />} />

        <Route path="/home" element={<HomePage />} />

        <Route
          path="/usuarios-roles"
          element={
            <RoleRoute allowedRoles={['ADMIN']}>
              <UsuariosRolesPage />
            </RoleRoute>
          }
        />

        <Route
          path="/ventas"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'VENDEDOR', 'CAJERO']}>
              <VentasPage />
            </RoleRoute>
          }
        />

        <Route
          path="/inventario"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'ALMACEN', 'COMPRAS']}>
              <HomePage />
            </RoleRoute>
          }
        />

        <Route
          path="/compras"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'COMPRAS', 'ALMACEN']}>
              <HomePage />
            </RoleRoute>
          }
        />

        <Route
          path="/caja"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'CAJERO']}>
              <HomePage />
            </RoleRoute>
          }
        />

        <Route
          path="/reportes"
          element={
            <RoleRoute allowedRoles={['ADMIN']}>
              <HomePage />
            </RoleRoute>
          }
        />

        <Route
          path="/clientes"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'VENDEDOR']}>
              <HomePage />
            </RoleRoute>
          }
        />

        <Route
          path="/historial-compras"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'VENDEDOR']}>
              <HomePage />
            </RoleRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;