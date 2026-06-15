import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../componentes/layout/MainLayout';

/* Datos de prueba (frontend). Luego se reemplazan por el servicio real. */
const CLIENTES = [
  { id: '', nombre: '— Sin cliente (venta general) —' },
  { id: '1', nombre: 'Tapicería El Buen Mueble' },
  { id: '2', nombre: 'Carpintería Hnos. García' },
  { id: '3', nombre: 'Muebles Modernos S.A.C.' },
  { id: '4', nombre: 'Fernández Mobiliario E.I.R.L.' },
];

const TIPOS_COMPROBANTE = ['Boleta de Venta', 'Factura', 'Nota de Venta'];

const METODOS_PAGO = ['Efectivo', 'Transferencia', 'Yape', 'POS'];

const CATALOGO = [
  { codigo: 'TRP-001', nombre: 'Triplay Lupuna 1.2x2.4x4mm', precio: 45.0 },
  { codigo: 'ESP-040', nombre: 'Espuma HR-40 4 pulgadas', precio: 120.0 },
  { codigo: 'FNS-001', nombre: 'Fibra Napa Siliconada 1kg', precio: 28.5 },
  { codigo: 'TEL-220', nombre: 'Tela Chenille (metro)', precio: 32.0 },
  { codigo: 'GRP-015', nombre: 'Grapa tapicería 8mm (caja)', precio: 18.0 },
];

const formatearMonto = (valor) =>
  valor.toLocaleString('es-PE', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

const NuevaVentaPage = () => {
  const navigate = useNavigate();

  const [cliente, setCliente] = useState('');
  const [tipoComprobante, setTipoComprobante] = useState(TIPOS_COMPROBANTE[0]);
  const [busquedaProducto, setBusquedaProducto] = useState('');
  const [items, setItems] = useState([]);
  const [pagos, setPagos] = useState([{ metodo: 'Efectivo', monto: '' }]);

  const resultadosBusqueda = useMemo(() => {
    const texto = busquedaProducto.trim().toLowerCase();
    if (!texto) return [];

    return CATALOGO.filter(
      (producto) =>
        producto.nombre.toLowerCase().includes(texto) ||
        producto.codigo.toLowerCase().includes(texto)
    );
  }, [busquedaProducto]);

  const agregarProducto = (producto) => {
    setItems((prev) => {
      const existente = prev.find((item) => item.codigo === producto.codigo);

      if (existente) {
        return prev.map((item) =>
          item.codigo === producto.codigo
            ? { ...item, cantidad: item.cantidad + 1 }
            : item
        );
      }

      return [...prev, { ...producto, cantidad: 1 }];
    });

    setBusquedaProducto('');
  };

  const cambiarCantidad = (codigo, cantidad) => {
    const valor = Math.max(1, Number(cantidad) || 1);

    setItems((prev) =>
      prev.map((item) => (item.codigo === codigo ? { ...item, cantidad: valor } : item))
    );
  };

  const quitarProducto = (codigo) => {
    setItems((prev) => prev.filter((item) => item.codigo !== codigo));
  };

  const cambiarPago = (index, campo, valor) => {
    setPagos((prev) =>
      prev.map((pago, i) => (i === index ? { ...pago, [campo]: valor } : pago))
    );
  };

  const agregarPago = () => {
    setPagos((prev) => [...prev, { metodo: 'Efectivo', monto: '' }]);
  };

  const quitarPago = (index) => {
    setPagos((prev) => prev.filter((_, i) => i !== index));
  };

  const subtotal = useMemo(
    () => items.reduce((acc, item) => acc + item.precio * item.cantidad, 0),
    [items]
  );

  const totalPagado = useMemo(
    () => pagos.reduce((acc, pago) => acc + (Number(pago.monto) || 0), 0),
    [pagos]
  );

  const total = subtotal;

  const confirmarVenta = () => {
    if (items.length === 0) {
      alert('Agrega al menos un producto a la venta.');
      return;
    }

    if (totalPagado < total) {
      alert('El monto pagado es menor al total de la venta.');
      return;
    }

    /* Aquí luego se llamará al servicio de ventas (backend). */
    alert('Venta registrada correctamente (demo frontend).');
    navigate('/ventas');
  };

  return (
    <MainLayout>
      <div className="mb-4">
        <h4 className="fw-bold mb-1">Nueva Venta</h4>
        <p className="text-muted mb-0">Registrar una nueva transacción de venta</p>
      </div>

      <div className="card border-0 shadow-sm mb-3">
        <div className="card-body d-flex align-items-center gap-3">
          <button
            type="button"
            className="btn btn-outline-secondary btn-sm"
            onClick={() => navigate('/ventas')}
          >
            ← Volver
          </button>
          <span className="text-muted">
            Completa el formulario para registrar la nueva venta
          </span>
        </div>
      </div>

      <div className="row g-3">
        <div className="col-12 col-xl-8">
          <div className="card border-0 shadow-sm mb-3">
            <div className="card-body">
              <h6 className="fw-bold text-primary mb-3">Datos de la Venta</h6>

              <div className="row g-3">
                <div className="col-12 col-md-6">
                  <label className="form-label">Cliente</label>
                  <select
                    className="form-select"
                    value={cliente}
                    onChange={(e) => setCliente(e.target.value)}
                  >
                    {CLIENTES.map((c) => (
                      <option key={c.id} value={c.id}>
                        {c.nombre}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="col-12 col-md-6">
                  <label className="form-label">Tipo de Comprobante</label>
                  <select
                    className="form-select"
                    value={tipoComprobante}
                    onChange={(e) => setTipoComprobante(e.target.value)}
                  >
                    {TIPOS_COMPROBANTE.map((tipo) => (
                      <option key={tipo} value={tipo}>
                        {tipo}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
            </div>
          </div>

          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <h6 className="fw-bold text-primary mb-3">Productos</h6>

              <div className="position-relative mb-3">
                <input
                  type="text"
                  className="form-control"
                  placeholder="Buscar producto por nombre o código..."
                  value={busquedaProducto}
                  onChange={(e) => setBusquedaProducto(e.target.value)}
                />

                {resultadosBusqueda.length > 0 && (
                  <ul
                    className="list-group position-absolute w-100 shadow-sm"
                    style={{ zIndex: 1000 }}
                  >
                    {resultadosBusqueda.map((producto) => (
                      <li
                        key={producto.codigo}
                        className="list-group-item list-group-item-action d-flex justify-content-between align-items-center"
                        role="button"
                        onClick={() => agregarProducto(producto)}
                      >
                        <span>
                          <strong className="small">{producto.codigo}</strong> ·{' '}
                          {producto.nombre}
                        </span>
                        <span className="text-muted small">
                          S/ {formatearMonto(producto.precio)}
                        </span>
                      </li>
                    ))}
                  </ul>
                )}
              </div>

              {items.length === 0 ? (
                <div
                  className="d-flex align-items-center justify-content-center text-muted border border-dashed rounded"
                  style={{ height: '180px' }}
                >
                  Busca y agrega productos a la venta
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="table align-middle">
                    <thead>
                      <tr className="text-muted small">
                        <th>Producto</th>
                        <th className="text-end">Precio</th>
                        <th style={{ width: '120px' }}>Cantidad</th>
                        <th className="text-end">Subtotal</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {items.map((item) => (
                        <tr key={item.codigo}>
                          <td>
                            <div className="fw-semibold small">{item.nombre}</div>
                            <div className="text-muted small">{item.codigo}</div>
                          </td>
                          <td className="text-end">S/ {formatearMonto(item.precio)}</td>
                          <td>
                            <input
                              type="number"
                              min={1}
                              className="form-control form-control-sm"
                              value={item.cantidad}
                              onChange={(e) =>
                                cambiarCantidad(item.codigo, e.target.value)
                              }
                            />
                          </td>
                          <td className="text-end fw-semibold">
                            S/ {formatearMonto(item.precio * item.cantidad)}
                          </td>
                          <td className="text-end">
                            <button
                              type="button"
                              className="btn btn-sm btn-link text-danger"
                              title="Quitar"
                              onClick={() => quitarProducto(item.codigo)}
                            >
                              ✕
                            </button>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          </div>
        </div>

        <div className="col-12 col-xl-4">
          <div className="card border-0 shadow-sm mb-3">
            <div className="card-body">
              <h6 className="fw-bold text-primary mb-3">Pago</h6>

              {pagos.map((pago, index) => (
                <div className="mb-3" key={index}>
                  <label className="form-label">Método de pago</label>
                  <select
                    className="form-select mb-2"
                    value={pago.metodo}
                    onChange={(e) => cambiarPago(index, 'metodo', e.target.value)}
                  >
                    {METODOS_PAGO.map((metodo) => (
                      <option key={metodo} value={metodo}>
                        {metodo}
                      </option>
                    ))}
                  </select>

                  <label className="form-label">Monto (S/)</label>
                  <div className="d-flex gap-2">
                    <input
                      type="number"
                      min={0}
                      step="0.01"
                      className="form-control"
                      placeholder="0.00"
                      value={pago.monto}
                      onChange={(e) => cambiarPago(index, 'monto', e.target.value)}
                    />

                    {pagos.length > 1 && (
                      <button
                        type="button"
                        className="btn btn-outline-danger"
                        title="Quitar método"
                        onClick={() => quitarPago(index)}
                      >
                        ✕
                      </button>
                    )}
                  </div>
                </div>
              ))}

              <button
                type="button"
                className="btn btn-outline-secondary w-100"
                onClick={agregarPago}
              >
                + Agregar otro método de pago
              </button>
            </div>
          </div>

          <div className="card border-0 shadow-sm">
            <div className="card-body">
              <p className="text-uppercase text-muted small fw-bold mb-3">Resumen</p>

              <div className="d-flex justify-content-between mb-2">
                <span className="text-muted">Subtotal</span>
                <span>S/ {formatearMonto(subtotal)}</span>
              </div>

              <hr />

              <div className="d-flex justify-content-between mb-2">
                <strong>TOTAL</strong>
                <strong className="text-primary">S/ {formatearMonto(total)}</strong>
              </div>

              <div className="d-flex justify-content-between mb-3">
                <span className="text-muted small">Monto pagado</span>
                <span className="text-muted small">S/ {formatearMonto(totalPagado)}</span>
              </div>

              <button
                type="button"
                className="btn btn-primary w-100"
                onClick={confirmarVenta}
              >
                Confirmar Venta
              </button>
            </div>
          </div>
        </div>
      </div>
    </MainLayout>
  );
};

export default NuevaVentaPage;
