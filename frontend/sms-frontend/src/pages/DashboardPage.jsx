import Navbar from "../components/Navbar.jsx";

function DashboardPage() {
    return (
        <div>
            <Navbar />

            <div className="content-container">
                <div className="content-card">
                    <h2>Dashboard</h2>
                    <p>Welcome to the Student Management System frontend.</p>
                    <p>You can now navigate to the Students page.</p>
                </div>
            </div>
        </div>
    );
}

export default DashboardPage;