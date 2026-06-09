const SummaryCard = ({ title, value, description, color = 'primary' }) => {
  return (
    <div className="card border-0 shadow-sm h-100">
      <div className="card-body">
        <div
          className={`bg-${color} bg-opacity-10 text-${color} rounded d-inline-flex align-items-center justify-content-center mb-3`}
          style={{
            width: '42px',
            height: '42px',
            fontWeight: 'bold',
          }}
        >
          ●
        </div>

        <p className="text-muted mb-1 small">{title}</p>

        <h4 className="fw-semibold mb-2">{value}</h4>

        {description && (
          <p className="text-muted small mb-0">{description}</p>
        )}
      </div>
    </div>
  );
};

export default SummaryCard;