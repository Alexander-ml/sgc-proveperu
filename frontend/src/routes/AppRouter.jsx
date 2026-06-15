/*Estructura de rutas mínima. Solo define la ruta raíz. Las rutas reales se agregan en la siguiente fase*/
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import LoginPage from '../pages/auth/LoginPage';
import HomePage from '../pages/home/HomePage';
import UsuariosRolesPage from '../pages/admin/UsuariosRolesPage';
import VentasPage from '../pages/ventas/VentasPage';
import NuevaVentaPage from '../pages/ventas/NuevaVentaPage';

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />

        <Route path="/login" element={<LoginPage />} />

        <Route path="/home" element={<HomePage />} />

        <Route path="/usuarios-roles" element={<UsuariosRolesPage />} />

        <Route path="/ventas" element={<VentasPage />} />
        <Route path="/ventas/nueva" element={<NuevaVentaPage />} />
        <Route path="/inventario" element={<HomePage />} />
        <Route path="/compras" element={<HomePage />} />
        <Route path="/caja" element={<HomePage />} />
        <Route path="/reportes" element={<HomePage />} />
        <Route path="/clientes" element={<HomePage />} />
        <Route path="/historial-compras" element={<HomePage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;