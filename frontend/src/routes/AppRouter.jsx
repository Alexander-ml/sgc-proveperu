/*Estructura de rutas mínima. Solo define la ruta raíz. Las rutas reales se agregan en la siguiente fase*/
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/auth/LoginPage';
import HomePage from '../pages/home/HomePage';
import UsuariosRolesPage from '../pages/admin/UsuariosRolesPage';
import RoleRoute from './RoleRoute';
import VentasPage from '../pages/ventas/VentasPage';
import InventarioPage from '../pages/inventario/InventarioPage';
import ComprasPage from '../pages/compras/ComprasPage';
import CajaPage from '../pages/caja/CajaPage';
import ClientesPage from '../pages/clientes/ClientesPage';
import ReportesPage from '../pages/reportes/ReportesPage';
import HistorialComprasPage from '../pages/clientes/HistorialComprasPage';

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
              <InventarioPage />
            </RoleRoute>
          }
        />

        <Route
          path="/compras"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'COMPRAS', 'ALMACEN']}>
              <ComprasPage />
            </RoleRoute>
          }
        />

        <Route
          path="/caja"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'CAJERO']}>
              <CajaPage />
            </RoleRoute>
          }
        />

        <Route
          path="/reportes"
          element={
            <RoleRoute allowedRoles={['ADMIN']}>
              <ReportesPage />
            </RoleRoute>
          }
        />

        <Route
          path="/clientes"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'VENDEDOR']}>
              <ClientesPage />
            </RoleRoute>
          }
        />

        <Route
          path="/historial-compras"
          element={
            <RoleRoute allowedRoles={['ADMIN', 'VENDEDOR']}>
              <HistorialComprasPage />
            </RoleRoute>
          }
        />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;