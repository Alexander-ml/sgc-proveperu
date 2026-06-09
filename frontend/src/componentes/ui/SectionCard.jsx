const SectionCard = ({ title, actionText, children }) => {
  return (
    <div className="card border-0 shadow-sm h-100">
      <div className="card-body">
        <div className="d-flex justify-content-between align-items-center mb-3">
          <h6 className="fw-bold mb-0">{title}</h6>

          {actionText && (
            <button className="btn btn-sm btn-link text-decoration-none">
              {actionText}
            </button>
          )}
        </div>

        {children}
      </div>
    </div>
  );
};

export default SectionCard;