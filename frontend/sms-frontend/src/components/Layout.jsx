import Sidebar from "./Sidebar";
import Navbar from "./Navbar";

function Layout({ children }) {
    return (
        <div className="layout">
            <Sidebar />
            <div className="main-content">
                <Navbar />
                <div className="page-wrapper">{children}</div>
            </div>
        </div>
    );
}

export default Layout;