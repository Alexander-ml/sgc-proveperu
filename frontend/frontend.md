FRONTEND
 
### Función de la Carpeta `frontend/`
 
Es la aplicación web que ve y usa el usuario. Genera una interfaz de usuario dinámica, consume la API REST del backend, y controla qué ve cada usuario según su rol.
 
---
 
### Por Qué Cada Tecnología
 
**React:** La interfaz del sistema tiene estado complejo. Cuando el vendedor agrega un producto a la venta, el subtotal se actualiza instantáneamente. Cuando el stock baja del mínimo, el badge de alertas cambia en tiempo real. React maneja ese estado de forma eficiente y predecible.
 
**Vite:** Es el build tool que reemplaza a Create React App. Inicia el servidor de desarrollo en menos de un segundo (frente a los 20-30 segundos de CRA). La recarga en caliente al cambiar código es prácticamente instantánea.
 
**JavaScript:** Es el lenguaje de ejecución de React. Toda la lógica del frontend — llamadas a la API, validaciones, cálculos de subtotales, manejo del token JWT — se escribe en JS. No se usa TypeScript para mantener la curva de aprendizaje manejable para el equipo.
 
**Bootstrap 5:** Proporciona componentes UI listos (tablas, formularios, modales, alertas, tarjetas, navbars, badges) y una cuadrícula de 12 columnas para layouts responsivos.
 
**Nginx:** Sirve los archivos compilados de React como archivos estáticos, que es la forma correcta de servir una SPA en entorno Docker. Resuelve además el problema del enrutamiento de React Router: si el usuario presiona F5 en `/ventas/nueva`, Nginx sabe que debe devolver `index.html` y dejar que React maneje esa ruta. Sin Nginx, eso genera un 404. También hace de proxy inverso enviando las peticiones `/api/*` al backend, evitando problemas de CORS dentro del entorno Docker.
 
---
 
### Herramientas Adicionales Recomendadas
 
**Axios:** Cliente HTTP para llamadas a la API. Su ventaja sobre `fetch` nativo son los interceptores: se configura una vez para adjuntar automáticamente el JWT en cada petición, y para detectar respuestas 401 y redirigir al login sin código adicional en cada componente.
 
**React Router DOM v6:** Navegación entre páginas sin recargar el browser. Permite definir rutas protegidas: si el usuario no está autenticado, cualquier ruta protegida lo redirige al login. Si el rol del usuario no tiene permiso, lo redirige al dashboard.
 
**React Hook Form:** Para formularios con validación. El formulario de nueva venta (seleccionar cliente, agregar múltiples productos, calcular totales, elegir método de pago) es complejo. React Hook Form reduce el código de manejo de estado del formulario y valida los campos antes de enviarlos al backend.
 
**React-Toastify o SweetAlert2:** Notificaciones de éxito y error visibles al usuario. "Venta registrada" en verde, "Stock insuficiente" en rojo, confirmación antes de anular una venta.
 
**Recharts:** Para los gráficos del dashboard del administrador. Ventas por día, productos con bajo stock, comparación de ingresos y egresos. Ligero y con buena integración en React.
 
---
 
### Estructura de Carpetas: Explicación
 
```
src/
├── assets/
├── components/
│   ├── ui/
│   └── layout/
├── pages/
├── services/
├── hooks/
├── context/
├── routes/
├── utils/
│   ├── helpers/
│   └── constants/
├── App.jsx
└── main.jsx
```
 
---
 
#### `assets/`
 
**Qué guarda:** Imágenes, íconos en SVG, fuentes tipográficas, y el archivo de variables CSS globales (colores de la marca, tipografía base).
 
**Organización interna:** `assets/images/`, `assets/icons/`.
 
**Buenas prácticas:** Siempre importar los activos como módulos de JavaScript, nunca desde rutas absolutas del sistema de archivos. Una ruta absoluta funciona en una laptop pero rompe en otra.
 
**Error a evitar:** Poner aquí archivos de lógica o componentes. Assets son solo recursos estáticos.
 
---
 
#### `components/ui/`
 
**Qué guarda:** Los bloques de construcción más pequeños de la interfaz, sin lógica de negocio. Ejemplos: `Button`, `Input`, `Select`, `Modal`, `Badge`, `Spinner`, `Tooltip`, `ConfirmDialog`, `Pagination`, `EmptyState`, `Alert`.
 
**Para qué sirve:** Son componentes completamente reutilizables en cualquier parte del sistema. Un `Button` no sabe si está en ventas o en compras. Recibe sus datos por props y se renderiza.
 
**Buenas prácticas:** Un componente UI no hace llamadas a la API. No conoce conceptos del negocio. Si lo hace, pertenece a otro lugar.
 
**Error a evitar:** Poner aquí componentes que contienen lógica de negocio, llamadas HTTP, o estado complejo. Eso los hace imposibles de reutilizar.
 
---
 
#### `components/layout/`
 
**Qué guarda:** La estructura visual global de la aplicación. `Sidebar` (menú lateral con los módulos), `Navbar` (barra superior con usuario y cerrar sesión), `MainLayout` (envuelve todas las páginas autenticadas con Sidebar + Navbar), `AuthLayout` (envuelve solo el login, sin sidebar).
 
**Para qué sirve:** Define la envoltura visual que aparece en todas las páginas. Con un layout como componente contenedor, el Sidebar aparece una vez y todas las páginas lo heredan automáticamente. No se repite código.
 
**Buenas prácticas:** El `Sidebar` debe renderizar solo los menús que el rol del usuario tiene permitidos. Un vendedor no debe ver el menú de "Usuarios y Seguridad".
 
**Error a evitar:** Repetir el Sidebar y la Navbar en cada página individual.
 
---
 
#### `pages/`
 
**Qué guarda:** Las páginas completas de cada módulo. Cada carpeta dentro de `pages/` corresponde a un módulo funcional del sistema.
 
**Estructura interna recomendada:**
 
```
pages/
├── auth/          ← Login
├── dashboard/     ← Vista ejecutiva del negocio
├── ventas/        ← Lista, Nueva Venta, Detalle
├── inventario/    ← Stock actual, Ajustes, Movimientos
├── compras/       ← Órdenes de compra, Recepciones, Proveedores
├── clientes/      ← Lista, Detalle, Historial de compras
├── caja/          ← Apertura, Movimientos del día, Cierre
└── admin/         ← Usuarios y Roles (solo administrador)
```
 
**Para qué sirve:** Cada página es responsable de orquestar sus componentes, llamar a los servicios, y manejar el estado local de esa vista.
 
**Buenas prácticas:** Las páginas usan componentes de `ui/` y de `layout/`. No definen estilos visuales propios que ya existen en Bootstrap. Delegan las llamadas HTTP a `services/`.
 
**Error a evitar:** Poner lógica de llamadas HTTP directamente dentro del JSX de la página. Eso mezcla responsabilidades y hace el componente imposible de probar.
 
---
 
#### `services/`
 
**Qué guarda:** Un archivo JavaScript por módulo de negocio, que encapsula todas las llamadas HTTP a la API. Ejemplo: `ventasService.js`, `comprasService.js`, `inventarioService.js`, `clientesService.js`, `cajaService.js`, `authService.js`.
 
**Para qué sirve:** Centraliza toda la comunicación con el backend. Ningún componente o página llama directamente a Axios con una URL hardcodeada. Siempre llaman a una función del servicio.
 
**Buenas prácticas:** Crear una instancia central de Axios configurada con: la URL base del backend leída de la variable de entorno, el interceptor de request que adjunta el JWT, y el interceptor de response que maneja los 401. Todos los servicios usan esa instancia.
 
**Error a evitar:** Escribir `axios.get('http://localhost:8080/api/ventas')` directamente en un componente. Cuando el backend cambia de puerto o de host, hay que actualizar decenas de archivos.
 
---
 
#### `hooks/`
 
**Qué guarda:** Custom Hooks que encapsulan lógica reutilizable con estado. Ejemplos:
 
- `useAuth()` — acceso al usuario autenticado, su rol y permisos
- `useVenta()` — toda la lógica del formulario de nueva venta: agregar productos, calcular subtotales, validar stock, gestionar pagos
- `usePagination()` — lógica compartida de paginación para tablas
- `useDebounce()` — retrasar la búsqueda mientras el usuario escribe
**Para qué sirve:** Extraer lógica compleja de los componentes para que sean más simples y legibles. El componente `NuevaVentaPage` no tiene 200 líneas de lógica — tiene 30 líneas que usan `useVenta()` donde vive la lógica real.
 
**Buenas prácticas:** Un hook tiene una responsabilidad única. Si un hook hace demasiadas cosas, es una señal de que debe dividirse.
 
**Error a evitar:** Poner toda la lógica de negocio directamente en los componentes de página.
 
---
 
#### `context/`
 
**Qué guarda:** Los proveedores de estado global usando Context API. Para este sistema:
 
- `AuthContext` — el usuario autenticado, su token JWT, su rol, sus permisos. Se inicializa al cargar la app.
**Para qué sirve:** Compartir el estado de autenticación entre todos los componentes sin pasarlo como prop a través de múltiples niveles de la jerarquía.
 
**Buenas prácticas:** El Context es para estado verdaderamente global. El estado local de un formulario no es global — vive en el componente o en su hook.
 
**Error a evitar:** Convertir el Context en "almacén de todo". Estado que solo usa un componente debe vivir en ese componente.
 
---
 
#### `routes/`
 
**Qué guarda:** La configuración de navegación de la aplicación. `AppRouter.jsx` define todas las rutas. `PrivateRoute.jsx` verifica sesión antes de renderizar una ruta protegida. `RoleRoute.jsx` verifica además que el rol tenga permiso.
 
**Para qué sirve:** Controlar el acceso a cada página según autenticación y rol. Un vendedor que escribe `/admin/usuarios` manualmente debe ser redirigido, no ver un error del servidor.
 
**Buenas prácticas:** Las rutas públicas (solo login) y las rutas protegidas están claramente separadas en la configuración.
 
**Error a evitar:** Confiar solo en que el menú lateral no muestra las opciones no permitidas. La protección de rutas debe ser independiente del menú.
 
---
 
#### `utils/helpers/`
 
**Qué guarda:** Funciones puras sin estado que transforman o formatean datos.
 
Ejemplos concretos para este sistema:
- `formatCurrency(amount)` → `"S/ 1,250.00"`
- `formatDate(date)` → `"27/04/2026"`
- `formatDateTime(datetime)` → `"27/04/2026 14:35"`
- `validateDNI(dni)` → `true` o `false`
- `validateRUC(ruc)` → `true` o `false`
- `calculateChange(total, paid)` → monto de vuelto
**Por qué separado de constants:** Son lógica (funciones), no valores. Tienen propósito diferente.
 
---
 
#### `utils/constants/`
 
**Qué guarda:** Valores fijos del sistema que no cambian en tiempo de ejecución.
 
Ejemplos:
- `ROLES` → objeto con los nombres de roles del sistema
- `ESTADOS_VENTA` → `{ REGISTRADA: 'REGISTRADA', ANULADA: 'ANULADA' }`
- `ESTADOS_COMPRA` → `{ PENDIENTE, PARCIAL, RECIBIDO, ANULADO }`
- `METODOS_PAGO` → los métodos aceptados
- `PAGINATION` → página por defecto, tamaño por defecto
- `STOCK_MINIMO_ALERTA` → umbral para mostrar alerta visual
**Por qué separado de helpers:** Son valores (constantes), no funciones. Agruparlos evita strings mágicos dispersos por el código que son difíciles de buscar y actualizar.
 
---
 
#### `App.jsx`
 
El componente raíz de la aplicación. Envuelve todo con los providers de Context necesarios (AuthContext) y renderiza el `AppRouter`. Es deliberadamente simple.
 
---
 
#### `main.jsx`
 
El punto de entrada de la aplicación. Monta `App` en el elemento HTML raíz. También es donde se configura cualquier herramienta global (tema de Bootstrap, configuración de Toastify).
 
---
 