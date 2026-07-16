import MainLayout from '../../componentes/layout/MainLayout';
import SummaryCard from '../../componentes/ui/SummaryCard';
import SectionCard from '../../componentes/ui/SectionCard';
import BadgeStatus from '../../componentes/ui/BadgeStatus';

const HomePage = () => {
  return (
    <MainLayout>
      <div className="mb-4">
        <h4 className="fw-bold mb-1">PANEL PRINCIPAL</h4>
        <p className="text-muted mb-0">Resumen general del negocio</p>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Ventas de hoy"
            value="S/ 1,124.00"
            description="Total vendido en el día"
            color="primary"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Saldo en Caja"
            value="S/ 1,494.00"
            description="Saldo actual disponible"
            color="success"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Alertas de Stock"
            value="4"
            description="1 sin stock · 3 bajo mínimo"
            color="warning"
          />
        </div>

        <div className="col-12 col-md-6 col-xl-3">
          <SummaryCard
            title="Clientes Activos"
            value="7"
            description="Clientes registrados"
            color="info"
          />
        </div>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-12 col-xl-8">
          <SectionCard title="Ventas - Últimos 7 días" actionText="Mayo 2028">
            <div
              className="d-flex align-items-center justify-content-center bg-light rounded border"
              style={{ height: '220px' }}
            >
              <div className="text-center text-muted">
                <h5>Gráfico de ventas</h5>
                <p className="mb-0">Aquí luego se integrará un gráfico real</p>
              </div>
            </div>
          </SectionCard>
        </div>

        <div className="col-12 col-xl-4">
          <SectionCard title="Últimas Ventas" actionText="Ver ventas">
            {[1, 2, 3, 4].map((item) => (
              <div
                className="d-flex align-items-center justify-content-between border-bottom py-2"
                key={item}
              >
                <div>
                  <strong className="small d-block">V-2026-00{item}</strong>
                  <span className="text-muted small">
                    Cliente · Transferencia
                  </span>
                </div>

                <div className="text-end">
                  <BadgeStatus>Completada</BadgeStatus>
                  <strong className="small d-block mt-1">S/ 000.00</strong>
                </div>
              </div>
            ))}
          </SectionCard>
        </div>
      </div>

      <div className="row g-3">
        <div className="col-12 col-xl-8">
          <SectionCard title="Productos Más Vendidos">
            <div className="mb-3">
              <span className="small">Prod. 1</span>
              <div className="progress">
                <div
                  className="progress-bar"
                  style={{ width: '100%' }}
                ></div>
              </div>
            </div>

            <div className="mb-3">
              <span className="small">Prod. 2</span>
              <div className="progress">
                <div
                  className="progress-bar"
                  style={{ width: '85%' }}
                ></div>
              </div>
            </div>

            <div className="mb-3">
              <span className="small">Prod. 3</span>
              <div className="progress">
                <div
                  className="progress-bar"
                  style={{ width: '70%' }}
                ></div>
              </div>
            </div>

            <div>
              <span className="small">Prod. 4</span>
              <div className="progress">
                <div
                  className="progress-bar"
                  style={{ width: '55%' }}
                ></div>
              </div>
            </div>
          </SectionCard>
        </div>

        <div className="col-12 col-xl-4">
          <SectionCard title="Alertas de Inventario" actionText="Ver inventario">
            <div className="alert alert-danger py-2 mb-2">
              <strong className="d-block small">Triplay Lupuna 1.2x2.4x4mm</strong>
              <span className="small">Sin stock disponible</span>
            </div>

            <div className="alert alert-warning py-2 mb-2">
              <strong className="d-block small">Espuma HR-40 4 pulgadas</strong>
              <span className="small">Stock: 6 / Mín: 8</span>
            </div>

            <div className="alert alert-warning py-2 mb-0">
              <strong className="d-block small">Fibra Napa Siliconada 1kg</strong>
              <span className="small">Stock: 3 / Mín: 10</span>
            </div>
          </SectionCard>
        </div>
      </div>
    </MainLayout>
  );
};

export default HomePage;