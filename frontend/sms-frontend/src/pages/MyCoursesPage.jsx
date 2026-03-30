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
            const res = await axios.get("/courses");
            setCourses(Array.isArray(res.data) ? res.data : []);
        } catch (err) {
            setError("Failed to load courses.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCourses();
    }, []);

    return (
        <div className="app-shell">
            <Navbar />

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">Student Portal</p>
                        <h1 className="page-title">My Courses</h1>
                        <p className="page-subtitle">
                            Browse your available courses in a modern view.
                        </p>
                    </div>
                </section>

                {loading && <p className="info-text">Loading courses...</p>}
                {error && <p className="error-text">{error}</p>}

                <div className="course-grid">
                    {courses.map((course) => (
                        <div key={course.id} className="course-card">
                            <div className="course-header">
                                <span className="course-code">
                                    {course.courseCode}
                                </span>
                                <span className="course-credit">
                                    {course.credit} ECTS
                                </span>
                            </div>

                            <h2 className="course-title">
                                {course.courseName}
                            </h2>

                            <p className="course-description">
                                {course.description || "No description available."}
                            </p>

                            <div className="course-footer">
                                <span className="course-tag">Course</span>
                            </div>
                        </div>
                    ))}
                </div>
            </main>
        </div>
    );
}

export default MyCoursesPage;