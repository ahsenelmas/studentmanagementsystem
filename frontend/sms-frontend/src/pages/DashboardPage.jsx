import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import axios from "../api/axios";

function DashboardPage() {
    const [stats, setStats] = useState({
        students: 0,
        courses: 0,
        enrollments: 0,
        schedules: 0,
    });

    const [loading, setLoading] = useState(true);

    const fetchDashboardStats = async () => {
        try {
            setLoading(true);

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
        } catch (error) {
            console.error("Dashboard stats error:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchDashboardStats();
    }, []);

    return (
        <div className="app-shell">
            <Navbar />

            <main className="page-wrapper">
                <section className="hero-card">
                    <div>
                        <p className="section-badge">Admin Panel</p>
                        <h1 className="page-title">Dashboard</h1>
                        <p className="page-subtitle">
                            Welcome to your Student Management System. Here you can manage
                            students, courses, enrollments, and schedules in one place.
                        </p>
                    </div>
                </section>

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
                        Your frontend now displays live backend totals for students,
                        courses, enrollments, and schedules. This makes the dashboard much
                        more dynamic and presentation-ready.
                    </p>
                </section>
            </main>
        </div>
    );
}

export default DashboardPage;