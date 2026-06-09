const BadgeStatus = ({ children, variant = 'success' }) => {
  return (
    <span className={`badge bg-${variant} bg-opacity-10 text-${variant}`}>
      {children}
    </span>
  );
};

export default BadgeStatus;