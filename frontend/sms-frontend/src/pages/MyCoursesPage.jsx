import { useEffect, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

function MyCoursesPage() {
    const [courses, setCourses] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");

    const fetchCourses = async () => {
        try {
            setLoading(true);
            setError("");

            const res = await axios.get("/courses/my");
            const data = Array.isArray(res.data) ? res.data : res.data.content || [];

            setCourses(data);
        } catch (err) {
            console.error("My courses fetch error:", err);

            const backendMessage =
                err.response?.data?.message ||
                err.response?.data?.error ||
                `Status: ${err.response?.status || "unknown"}`;

            setError(`Failed to load courses. ${backendMessage}`);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCourses();
    }, []);

    return (
        <div className="app-shell">

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">Student Portal</p>
                        <h1 className="page-title">My Courses</h1>
                        <p className="page-subtitle">
                            Browse your enrolled courses in a modern view.
                        </p>
                    </div>
                </section>

                {loading && <p className="info-text">Loading courses...</p>}
                {error && <p className="error-text">{error}</p>}

                {!loading && !error && courses.length === 0 && (
                    <p className="info-text">No courses found for your account.</p>
                )}

                {!loading && !error && courses.length > 0 && (
                    <div className="course-grid">
                        {courses.map((course) => (
                            <div key={course.id} className="course-card">
                                <div className="course-header">
                                    <span className="course-code">{course.courseCode}</span>
                                    <span className="course-credit">{course.credit} ECTS</span>
                                </div>

                                <h2 className="course-title">{course.courseName}</h2>

                                <p className="course-description">
                                    {course.description || "No description available."}
                                </p>

                                <div className="course-footer">
                                    <span className="course-tag">My Course</span>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </main>
        </div>
    );
}

export default MyCoursesPage;