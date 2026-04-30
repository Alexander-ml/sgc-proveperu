/*Estructura de rutas mínima. Solo define la ruta raíz. Las rutas reales se agregan en la siguiente fase*/
import { BrowserRouter, Routes, Route } from 'react-router-dom';

function AppRouter() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={
          <h1>Manos a la obra</h1>
          } />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRouter;