import { NavLink } from 'react-router-dom';

const SidebarItem = ({ label, to }) => {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        `d-block px-3 py-2 rounded mb-2 text-decoration-none ${
          isActive ? 'bg-primary text-white' : 'text-white'
        }`
      }
      style={{
        cursor: 'pointer',
        fontSize: '15px',
        width: '100%',
        minHeight: '40px',
        lineHeight: '24px',
        display: 'block',
      }}
    >
      {label}
    </NavLink>
  );
};

export default SidebarItem;