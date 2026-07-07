const CLIENTES_MOCK = [
  {
    idCliente: 1,
    nombre: 'Carpintería Hnos. García',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '20456789123',
    telefono: '944-123456',
    correo: 'garcia@carpinteria.pe',
    direccion: 'Av. Los Álamos 234, Chiclayo',
    numeroCompras: 15,
    montoTotal: 4580.0,
    ultimaCompra: '2026-05-04',
    estado: 'ACTIVO',
    frecuente: true,
  },
  {
    idCliente: 2,
    nombre: 'Tapicería El Buen Mueble',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '20123456789',
    telefono: '923-234567',
    correo: 'buenmueble@gmail.com',
    direccion: 'Jr. Bolívar 567, Chiclayo',
    numeroCompras: 22,
    montoTotal: 7230.5,
    ultimaCompra: '2026-05-05',
    estado: 'ACTIVO',
    frecuente: true,
  },
  {
    idCliente: 3,
    nombre: 'Muebles Modernos S.A.C.',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '20987654321',
    telefono: '965-345678',
    correo: 'ventas@mueblesmodernos.pe',
    direccion: 'Calle Las Flores 89, Lambayeque',
    numeroCompras: 31,
    montoTotal: 12400.0,
    ultimaCompra: '2026-05-02',
    estado: 'ACTIVO',
    frecuente: true,
  },
  {
    idCliente: 4,
    nombre: 'Juan Pérez Ramírez',
    tipoCliente: 'PERSONA',
    tipoDocumento: 'DNI',
    numeroDocumento: '12345678',
    telefono: '915-456789',
    correo: 'jperez@gmail.com',
    direccion: 'Urb. Primavera B-12, Chiclayo',
    numeroCompras: 5,
    montoTotal: 780.0,
    ultimaCompra: '2026-04-20',
    estado: 'ACTIVO',
    frecuente: false,
  },
  {
    idCliente: 5,
    nombre: 'Fernández Mobiliario E.I.R.L.',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '20345678901',
    telefono: '937-567890',
    correo: 'fernandez.mobiliario@outlook.com',
    direccion: 'Av. Grau 1200, Chiclayo',
    numeroCompras: 18,
    montoTotal: 6890.0,
    ultimaCompra: '2026-05-01',
    estado: 'ACTIVO',
    frecuente: true,
  },
  {
    idCliente: 6,
    nombre: 'Artesanías Chiclayo S.R.L.',
    tipoCliente: 'EMPRESA',
    tipoDocumento: 'RUC',
    numeroDocumento: '20234567890',
    telefono: '948-678901',
    correo: 'artchic@gmail.com',
    direccion: 'Calle San Martín 456, Chiclayo',
    numeroCompras: 9,
    montoTotal: 2340.0,
    ultimaCompra: '2026-04-28',
    estado: 'ACTIVO',
    frecuente: false,
  },
  {
    idCliente: 7,
    nombre: 'María López Torres',
    tipoCliente: 'PERSONA',
    tipoDocumento: 'DNI',
    numeroDocumento: '87654321',
    telefono: '912-789456',
    correo: 'mlopez@gmail.com',
    direccion: 'Av. Salaverry 321, Chiclayo',
    numeroCompras: 3,
    montoTotal: 420.0,
    ultimaCompra: '2026-04-10',
    estado: 'ACTIVO',
    frecuente: false,
  },
];

const obtenerClientesGuardados = () => {
  const clientesGuardados = localStorage.getItem('clientes_mock');

  if (!clientesGuardados) {
    localStorage.setItem('clientes_mock', JSON.stringify(CLIENTES_MOCK));
    return CLIENTES_MOCK;
  }

  return JSON.parse(clientesGuardados);
};

const guardarClientes = (clientes) => {
  localStorage.setItem('clientes_mock', JSON.stringify(clientes));
};

export const obtenerResumenClientes = async () => {
  const clientes = obtenerClientesGuardados();

  const totalClientes = clientes.length;
  const empresas = clientes.filter(
    (cliente) => cliente.tipoCliente === 'EMPRESA'
  ).length;
  const personasNaturales = clientes.filter(
    (cliente) => cliente.tipoCliente === 'PERSONA'
  ).length;
  const clientesFrecuentes = clientes.filter(
    (cliente) => Number(cliente.numeroCompras || 0) > 10
  ).length;

  return {
    data: {
      totalClientes,
      empresas,
      personasNaturales,
      clientesFrecuentes,
    },
  };
};

export const listarClientes = async ({ busqueda = '', tipoCliente = '' } = {}) => {
  let clientes = obtenerClientesGuardados();

  if (busqueda) {
    const texto = busqueda.toLowerCase();

    clientes = clientes.filter(
      (cliente) =>
        cliente.nombre.toLowerCase().includes(texto) ||
        cliente.numeroDocumento.toLowerCase().includes(texto) ||
        cliente.correo.toLowerCase().includes(texto) ||
        cliente.telefono.toLowerCase().includes(texto)
    );
  }

  if (tipoCliente) {
    clientes = clientes.filter((cliente) => cliente.tipoCliente === tipoCliente);
  }

  return {
    data: clientes,
  };
};

export const crearCliente = async (cliente) => {
  const clientes = obtenerClientesGuardados();

  const nuevoCliente = {
    idCliente: Date.now(),
    nombre: cliente.nombre,
    tipoCliente: cliente.tipoCliente,
    tipoDocumento: cliente.tipoDocumento,
    numeroDocumento: cliente.numeroDocumento,
    telefono: cliente.telefono,
    correo: cliente.correo,
    direccion: cliente.direccion,
    numeroCompras: 0,
    montoTotal: 0,
    ultimaCompra: '-',
    estado: 'ACTIVO',
    frecuente: false,
  };

  const actualizados = [nuevoCliente, ...clientes];

  guardarClientes(actualizados);

  return {
    data: nuevoCliente,
    message: 'Cliente registrado temporalmente',
  };
};

export const obtenerClientePorId = async (idCliente) => {
  const clientes = obtenerClientesGuardados();

  const cliente = clientes.find(
    (item) => Number(item.idCliente) === Number(idCliente)
  );

  return {
    data: cliente || null,
  };
};