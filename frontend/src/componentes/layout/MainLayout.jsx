import Sidebar from './Sidebar';
import Navbar from './Navbar';

const MainLayout = ({ children }) => {
  return (
    <div className="d-flex bg-light" style={{ minHeight: '100vh' }}>
      <Sidebar />

      <main className="flex-grow-1 p-4">
        <Navbar />
        {children}
      </main>
    </div>
  );
};

export default MainLayout;