import { Link, useLocation, useNavigate } from "react-router-dom";

function Navbar() {
    const location = useLocation();
    const navigate = useNavigate();
    const role = localStorage.getItem("role");

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("role");
        localStorage.removeItem("username");
        sessionStorage.clear();
        navigate("/login");
    };

    const isActive = (path) => location.pathname === path;

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <span className="brand-bear">🐻</span>
                <span>Student Management System</span>
            </div>

            <div className="navbar-links">
                <Link
                    to="/dashboard"
                    className={`nav-link ${isActive("/dashboard") ? "active-link" : ""}`}
                >
                    Dashboard
                </Link>

                {role === "ADMIN" && (
                    <>
                        <Link
                            to="/students"
                            className={`nav-link ${isActive("/students") ? "active-link" : ""}`}
                        >
                            Students
                        </Link>

                        <Link
                            to="/courses"
                            className={`nav-link ${isActive("/courses") ? "active-link" : ""}`}
                        >
                            Courses
                        </Link>

                        <Link
                            to="/enrollments"
                            className={`nav-link ${isActive("/enrollments") ? "active-link" : ""}`}
                        >
                            Enrollments
                        </Link>

                        <Link
                            to="/schedules"
                            className={`nav-link ${isActive("/schedules") ? "active-link" : ""}`}
                        >
                            Schedules
                        </Link>
                    </>
                )}

                {role === "STUDENT" && (
                    <>

                        <Link
                            to="/my-courses"
                            className={`nav-link ${isActive("/my-courses") ? "active-link" : ""}`}
                        >
                            My Courses
                        </Link>

                        <Link
                            to="/schedules"
                            className={`nav-link ${isActive("/schedules") ? "active-link" : ""}`}
                        >
                            Schedule
                        </Link>
                    </>
                )}

                <button onClick={handleLogout} className="logout-btn">
                    Logout
                </button>
            </div>
        </nav>
    );
}

export default Navbar;