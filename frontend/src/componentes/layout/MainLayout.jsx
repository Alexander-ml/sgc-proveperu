import Sidebar from './Sidebar';
import Navbar from './Navbar';

const MainLayout = ({ children }) => {
  return (
    <div
      style={{
        minHeight: '100vh',
        background: '#f8f9fa',
      }}
    >
      <Sidebar />

      <main
        style={{
          marginLeft: '260px',
          minHeight: '100vh',
          padding: '24px',
          position: 'relative',
          zIndex: 1,
        }}
      >
        <Navbar />
        {children}
      </main>
    </div>
  );
};

export default MainLayout;