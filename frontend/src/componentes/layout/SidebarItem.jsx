import { NavLink } from 'react-router-dom';

const SidebarItem = ({ label, to, icon }) => {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `d-flex align-items-center gap-2 px-3 py-2 rounded mb-2 text-decoration-none ${
          isActive ? 'bg-primary text-white' : 'text-white'
        }`
      }
      style={{
        cursor: 'pointer',
        fontSize: '15px',
        width: '100%',
        minHeight: '40px',
        lineHeight: '24px',
      }}
    >
      {icon && <i className={`bi ${icon}`}></i>}
      <span>{label}</span>
    </NavLink>
  );
};

export default SidebarItem;