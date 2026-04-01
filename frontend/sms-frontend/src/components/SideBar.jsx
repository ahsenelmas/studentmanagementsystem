import { Link, useLocation } from "react-router-dom";

function Sidebar() {
    const location = useLocation();
    const role = localStorage.getItem("role");

    const isActive = (path) => location.pathname === path;

    return (
        <div className="sidebar">
            <div className="sidebar-logo">🎓 SMS</div>

            <div className="sidebar-links">
                <Link className={isActive("/dashboard") ? "active" : ""} to="/dashboard">
                    Dashboard
                </Link>

                {role === "ADMIN" && (
                    <>
                        <Link to="/students">Students</Link>
                        <Link to="/courses">Courses</Link>
                        <Link to="/enrollments">Enrollments</Link>
                        <Link to="/schedules">Schedules</Link>
                    </>
                )}

                {role === "STUDENT" && (
                    <>
                        <Link to="/my-courses">My Courses</Link>
                        <Link to="/schedules">Schedule</Link>
                    </>
                )}
            </div>
        </div>
    );
}

export default Sidebar;