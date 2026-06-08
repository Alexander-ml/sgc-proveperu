const SidebarItem = ({ label, active = false }) => {
  return (
    <div
      className={`px-3 py-2 rounded mb-1 ${
        active ? 'bg-primary text-white' : 'text-white'
      }`}
      style={{
        cursor: 'pointer',
        fontSize: '15px',
      }}
    >
      {label}
    </div>
  );
};

export default SidebarItem;