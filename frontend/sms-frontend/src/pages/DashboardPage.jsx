import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import axios from "../api/axios";

function DashboardPage() {
    const role = localStorage.getItem("role");
    const username = localStorage.getItem("username") || "User";
    const isAdmin = role === "ADMIN";

    const [stats, setStats] = useState({
        students: 0,
        courses: 0,
        enrollments: 0,
        schedules: 0,
    });

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const fetchDashboardStats = async () => {
        try {
            setLoading(true);
            setError("");

            if (isAdmin) {
                const [studentsRes, coursesRes, enrollmentsRes, schedulesRes] =
                    await Promise.all([
                        axios.get("/students"),
                        axios.get("/courses"),
                        axios.get("/enrollments"),
                        axios.get("/schedules"),
                    ]);

                const studentsData = Array.isArray(studentsRes.data)
                    ? studentsRes.data
                    : studentsRes.data.content || [];

                const coursesData = Array.isArray(coursesRes.data)
                    ? coursesRes.data
                    : coursesRes.data.content || [];

                const enrollmentsData = Array.isArray(enrollmentsRes.data)
                    ? enrollmentsRes.data
                    : enrollmentsRes.data.content || [];

                const schedulesData = Array.isArray(schedulesRes.data)
                    ? schedulesRes.data
                    : schedulesRes.data.content || [];

                setStats({
                    students: studentsData.length,
                    courses: coursesData.length,
                    enrollments: enrollmentsData.length,
                    schedules: schedulesData.length,
                });
            } else {
                const [coursesRes, enrollmentsRes, schedulesRes] = await Promise.all([
                    axios.get("/courses/my"),
                    axios.get("/enrollments/my"),
                    axios.get("/schedules/my"),
                ]);

                const coursesData = Array.isArray(coursesRes.data)
                    ? coursesRes.data
                    : coursesRes.data.content || [];

                const enrollmentsData = Array.isArray(enrollmentsRes.data)
                    ? enrollmentsRes.data
                    : enrollmentsRes.data.content || [];

                const schedulesData = Array.isArray(schedulesRes.data)
                    ? schedulesRes.data
                    : schedulesRes.data.content || [];

                setStats({
                    students: 0,
                    courses: coursesData.length,
                    enrollments: enrollmentsData.length,
                    schedules: schedulesData.length,
                });
            }
        } catch (err) {
            console.error("Dashboard stats error:", err);

            const backendMessage =
                err.response?.data?.message ||
                err.response?.data?.error ||
                `Status: ${err.response?.status || "unknown"}`;

            setError(`Failed to load dashboard data. ${backendMessage}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboardStats();
    }, [isAdmin]);

    return (
        <div className="app-shell">

            <main className="page-wrapper">
                <section className="hero-card">
                    <div>
                        <p className="section-badge">
                            {isAdmin ? "Admin Panel" : "Student Portal"}
                        </p>

                        <h1 className="page-title">Dashboard</h1>

                        <p className="page-subtitle">
                            {isAdmin
                                ? "Welcome to your Student Management System. Here you can manage students, courses, enrollments, and schedules in one place."
                                : `Welcome, ${username}. Here you can explore your courses, enrollments, and schedule.`}
                        </p>
                    </div>
                </section>

                {error && (
                    <section className="content-card" style={{ marginBottom: "24px" }}>
                        <p className="error-text">{error}</p>
                    </section>
                )}

                {isAdmin ? (
                    <>
                        <section className="stats-grid">
                            <div className="stat-card">
                                <h3>Total Students</h3>
                                <p className="stat-number">{loading ? "..." : stats.students}</p>
                                <span className="stat-note">Current registered students</span>
                            </div>

                            <div className="stat-card">
                                <h3>Total Courses</h3>
                                <p className="stat-number">{loading ? "..." : stats.courses}</p>
                                <span className="stat-note">Courses available in the system</span>
                            </div>

                            <div className="stat-card">
                                <h3>Total Enrollments</h3>
                                <p className="stat-number">{loading ? "..." : stats.enrollments}</p>
                                <span className="stat-note">Student-course records</span>
                            </div>

                            <div className="stat-card">
                                <h3>Total Schedules</h3>
                                <p className="stat-number">{loading ? "..." : stats.schedules}</p>
                                <span className="stat-note">Scheduled course sessions</span>
                            </div>
                        </section>

                        <section className="content-card">
                            <h2>System Overview</h2>
                            <p>
                                This admin dashboard displays live backend totals for students,
                                courses, enrollments, and schedules. It gives you a quick overview
                                of the whole system and helps you manage academic data efficiently.
                            </p>
                        </section>
                    </>
                ) : (
                    <>
                        <section className="stats-grid">
                            <div className="stat-card">
                                <h3>My Courses</h3>
                                <p className="stat-number">{loading ? "..." : stats.courses}</p>
                                <span className="stat-note">Courses assigned to you</span>
                            </div>

                            <div className="stat-card">
                                <h3>My Enrollments</h3>
                                <p className="stat-number">{loading ? "..." : stats.enrollments}</p>
                                <span className="stat-note">Your active enrollment records</span>
                            </div>

                            <div className="stat-card">
                                <h3>Schedule Entries</h3>
                                <p className="stat-number">{loading ? "..." : stats.schedules}</p>
                                <span className="stat-note">Visible scheduled course sessions</span>
                            </div>
                        </section>

                        <section className="content-card">
                            <h2>Student Overview</h2>
                            <p>
                                From here, you can browse your enrolled courses, view your
                                enrollments, and check your personal weekly schedule.
                            </p>
                        </section>
                    </>
                )}
            </main>
        </div>
    );
}

export default DashboardPage;