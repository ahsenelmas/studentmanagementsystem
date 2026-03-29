import { useEffect, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

function CoursesPage() {
    const [courses, setCourses] = useState([]);
    const [filteredCourses, setFilteredCourses] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editingCourseId, setEditingCourseId] = useState(null);

    const [formData, setFormData] = useState({
        courseCode: "",
        courseName: "",
        credit: "",
        description: "",
    });

    const resetForm = () => {
        setFormData({
            courseCode: "",
            courseName: "",
            credit: "",
            description: "",
        });
        setIsEditMode(false);
        setEditingCourseId(null);
    };

    const fetchCourses = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await axios.get("/courses");
            const courseData = Array.isArray(response.data)
                ? response.data
                : response.data.content || [];

            setCourses(courseData);
            setFilteredCourses(courseData);
        } catch (err) {
            console.error("Courses fetch error:", err);

            if (err.response) {
                setError(`Failed to load courses. Status: ${err.response.status}`);
            } else {
                setError("Failed to load courses.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCourses();
    }, []);

    useEffect(() => {
        const filtered = courses.filter((course) => {
            return (
                String(course.id || "").includes(searchTerm) ||
                String(course.courseCode || "")
                    .toLowerCase()
                    .includes(searchTerm.toLowerCase()) ||
                String(course.courseName || "")
                    .toLowerCase()
                    .includes(searchTerm.toLowerCase()) ||
                String(course.description || "")
                    .toLowerCase()
                    .includes(searchTerm.toLowerCase())
            );
        });

        setFilteredCourses(filtered);
    }, [searchTerm, courses]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleEditCourse = (course) => {
        setIsEditMode(true);
        setEditingCourseId(course.id);
        setShowForm(true);
        setError("");
        setSuccessMessage("");

        setFormData({
            courseCode: course.courseCode || "",
            courseName: course.courseName || "",
            credit: course.credit || "",
            description: course.description || "",
        });
    };

    const handleSubmitCourse = async (e) => {
        e.preventDefault();
        setError("");
        setSuccessMessage("");

        try {
            const payload = {
                courseCode: formData.courseCode,
                courseName: formData.courseName,
                credit: Number(formData.credit),
                description: formData.description,
            };

            if (isEditMode && editingCourseId) {
                await axios.put(`/courses/${editingCourseId}`, payload);
                setSuccessMessage("Course updated successfully.");
            } else {
                await axios.post("/courses", payload);
                setSuccessMessage("Course added successfully.");
            }

            resetForm();
            setShowForm(false);
            fetchCourses();
        } catch (err) {
            console.error("Submit course error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    JSON.stringify(err.response.data);

                setError(
                    isEditMode
                        ? `Failed to update course. ${backendMessage}`
                        : `Failed to create course. ${backendMessage}`
                );
            } else {
                setError(
                    isEditMode
                        ? "Failed to update course."
                        : "Failed to create course."
                );
            }
        }
    };

    const handleDeleteCourse = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this course?"
        );

        if (!confirmed) return;

        try {
            setError("");
            setSuccessMessage("");

            await axios.delete(`/courses/${id}`);

            setSuccessMessage("Course deleted successfully.");
            fetchCourses();
        } catch (err) {
            console.error("Delete course error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    `Status: ${err.response.status}`;

                setError(`Failed to delete course. ${backendMessage}`);
            } else {
                setError("Failed to delete course.");
            }
        }
    };

    const handleToggleForm = () => {
        if (showForm) {
            setShowForm(false);
            resetForm();
            setError("");
        } else {
            setShowForm(true);
            resetForm();
            setError("");
            setSuccessMessage("");
        }
    };

    return (
        <div className="app-shell">
            <Navbar />

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">Management</p>
                        <h1 className="page-title">Courses</h1>
                        <p className="page-subtitle">
                            View and manage the course list in your system.
                        </p>
                    </div>

                    <div className="action-group">
                        <button className="primary-btn" onClick={handleToggleForm}>
                            {showForm ? "Close Form" : "Add Course"}
                        </button>

                        <button className="secondary-btn" onClick={fetchCourses}>
                            Refresh
                        </button>
                    </div>
                </section>

                {showForm && (
                    <section className="content-card form-card">
                        <h2>{isEditMode ? "Edit Course" : "Add New Course"}</h2>

                        <form className="modern-form" onSubmit={handleSubmitCourse}>
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Course Code</label>
                                    <input
                                        type="text"
                                        name="courseCode"
                                        value={formData.courseCode}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Course Name</label>
                                    <input
                                        type="text"
                                        name="courseName"
                                        value={formData.courseName}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Credit</label>
                                    <input
                                        type="number"
                                        name="credit"
                                        value={formData.credit}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group full-width">
                                    <label>Description</label>
                                    <input
                                        type="text"
                                        name="description"
                                        value={formData.description}
                                        onChange={handleChange}
                                        placeholder="Optional course description"
                                    />
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="primary-btn">
                                    {isEditMode ? "Update Course" : "Save Course"}
                                </button>

                                <button
                                    type="button"
                                    className="secondary-btn"
                                    onClick={() => {
                                        setShowForm(false);
                                        resetForm();
                                        setError("");
                                    }}
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </section>
                )}

                <section className="content-card">
                    <div className="toolbar">
                        <input
                            type="text"
                            placeholder="Search by course code, course name, description..."
                            className="search-input"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>

                    {successMessage && <p className="success-text">{successMessage}</p>}
                    {loading && <p className="info-text">Loading courses...</p>}
                    {error && <p className="error-text">{error}</p>}

                    {!loading && !error && filteredCourses.length === 0 && (
                        <p className="info-text">No courses found.</p>
                    )}

                    {!loading && !error && filteredCourses.length > 0 && (
                        <div className="table-wrapper">
                            <table className="modern-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Course Code</th>
                                    <th>Course Name</th>
                                    <th>Credit</th>
                                    <th>Description</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredCourses.map((course) => (
                                    <tr key={course.id}>
                                        <td>{course.id}</td>
                                        <td>{course.courseCode}</td>
                                        <td>{course.courseName}</td>
                                        <td>{course.credit}</td>
                                        <td>{course.description || "-"}</td>
                                        <td>
                                            <div className="table-actions">
                                                <button
                                                    className="edit-btn"
                                                    onClick={() => handleEditCourse(course)}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className="danger-btn"
                                                    onClick={() => handleDeleteCourse(course.id)}
                                                >
                                                    Delete
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>
            </main>
        </div>
    );
}

export default CoursesPage;