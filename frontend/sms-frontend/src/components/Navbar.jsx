import { Link, useNavigate } from "react-router-dom";

function Navbar() {
    const navigate = useNavigate();

    const handleLogout = () => {
        localStorage.removeItem("token");
        navigate("/login");
    };

    return (
        <nav className="navbar">
            <h2>Student Management System</h2>

            <div className="nav-links">
                <Link to="/dashboard">Dashboard</Link>
                <Link to="/students">Students</Link>
                <button onClick={handleLogout}>Logout</button>
            </div>
        </nav>
    );
}

export default Navbar;