import { useEffect, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

function EnrollmentsPage() {
    const [enrollments, setEnrollments] = useState([]);
    const [filteredEnrollments, setFilteredEnrollments] = useState([]);
    const [students, setStudents] = useState([]);
    const [courses, setCourses] = useState([]);

    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [formLoading, setFormLoading] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editingEnrollmentId, setEditingEnrollmentId] = useState(null);

    const [formData, setFormData] = useState({
        studentId: "",
        courseId: "",
        enrollmentDate: "",
        status: "ACTIVE",
    });

    const resetForm = () => {
        setFormData({
            studentId: "",
            courseId: "",
            enrollmentDate: "",
            status: "ACTIVE",
        });
        setIsEditMode(false);
        setEditingEnrollmentId(null);
    };

    const fetchEnrollments = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await axios.get("/enrollments");
            const enrollmentData = Array.isArray(response.data)
                ? response.data
                : response.data.content || [];

            setEnrollments(enrollmentData);
            setFilteredEnrollments(enrollmentData);
        } catch (err) {
            console.error("Enrollments fetch error:", err);

            if (err.response) {
                setError(`Failed to load enrollments. Status: ${err.response.status}`);
            } else {
                setError("Failed to load enrollments.");
            }
        } finally {
            setLoading(false);
        }
    };

    const fetchStudentsAndCourses = async () => {
        try {
            setFormLoading(true);

            const [studentsRes, coursesRes] = await Promise.all([
                axios.get("/students"),
                axios.get("/courses"),
            ]);

            const studentsData = Array.isArray(studentsRes.data)
                ? studentsRes.data
                : studentsRes.data.content || [];

            const coursesData = Array.isArray(coursesRes.data)
                ? coursesRes.data
                : coursesRes.data.content || [];

            setStudents(studentsData);
            setCourses(coursesData);
        } catch (err) {
            console.error("Students/Courses fetch error:", err);
        } finally {
            setFormLoading(false);
        }
    };

    useEffect(() => {
        fetchEnrollments();
        fetchStudentsAndCourses();
    }, []);

    const getStudentDisplay = (enrollment) => {
        if (enrollment.student) {
            const firstName = enrollment.student.firstName || "";
            const lastName = enrollment.student.lastName || "";
            const studentNumber = enrollment.student.studentNumber || "";
            return `${firstName} ${lastName}`.trim() || studentNumber || "Student";
        }

        if (enrollment.studentName) return enrollment.studentName;
        if (enrollment.studentNumber) return enrollment.studentNumber;

        if (enrollment.studentId) {
            const student = students.find((s) => s.id === enrollment.studentId);
            if (student) {
                return `${student.firstName} ${student.lastName}`.trim();
            }
            return `Student ID: ${enrollment.studentId}`;
        }

        return "-";
    };

    const getCourseDisplay = (enrollment) => {
        if (enrollment.course) {
            return (
                enrollment.course.courseName ||
                enrollment.course.courseCode ||
                "Course"
            );
        }

        if (enrollment.courseName) return enrollment.courseName;
        if (enrollment.courseCode) return enrollment.courseCode;

        if (enrollment.courseId) {
            const course = courses.find((c) => c.id === enrollment.courseId);
            if (course) {
                return `${course.courseCode} - ${course.courseName}`;
            }
            return `Course ID: ${enrollment.courseId}`;
        }

        return "-";
    };

    const getEnrollmentDate = (enrollment) => {
        return enrollment.enrollmentDate || enrollment.createdAt || enrollment.date || "-";
    };

    const getStatus = (enrollment) => {
        return enrollment.status || "-";
    };

    useEffect(() => {
        const filtered = enrollments.filter((enrollment) => {
            const studentText = getStudentDisplay(enrollment).toLowerCase();
            const courseText = getCourseDisplay(enrollment).toLowerCase();
            const dateText = String(getEnrollmentDate(enrollment)).toLowerCase();
            const statusText = String(getStatus(enrollment)).toLowerCase();

            return (
                String(enrollment.id || "").includes(searchTerm) ||
                studentText.includes(searchTerm.toLowerCase()) ||
                courseText.includes(searchTerm.toLowerCase()) ||
                dateText.includes(searchTerm.toLowerCase()) ||
                statusText.includes(searchTerm.toLowerCase())
            );
        });

        setFilteredEnrollments(filtered);
    }, [searchTerm, enrollments, students, courses]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleEditEnrollment = (enrollment) => {
        setIsEditMode(true);
        setEditingEnrollmentId(enrollment.id);
        setShowForm(true);
        setError("");
        setSuccessMessage("");

        setFormData({
            studentId: String(enrollment.studentId || enrollment.student?.id || ""),
            courseId: String(enrollment.courseId || enrollment.course?.id || ""),
            enrollmentDate: enrollment.enrollmentDate || "",
            status: enrollment.status || "ACTIVE",
        });
    };

    const handleSubmitEnrollment = async (e) => {
        e.preventDefault();
        setError("");
        setSuccessMessage("");

        try {
            const payload = {
                studentId: Number(formData.studentId),
                courseId: Number(formData.courseId),
                enrollmentDate: formData.enrollmentDate,
                status: formData.status,
            };

            if (isEditMode && editingEnrollmentId) {
                await axios.put(`/enrollments/${editingEnrollmentId}`, payload);
                setSuccessMessage("Enrollment updated successfully.");
            } else {
                await axios.post("/enrollments", payload);
                setSuccessMessage("Enrollment added successfully.");
            }

            resetForm();
            setShowForm(false);
            fetchEnrollments();
        } catch (err) {
            console.error("Submit enrollment error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    JSON.stringify(err.response.data);

                setError(
                    isEditMode
                        ? `Failed to update enrollment. ${backendMessage}`
                        : `Failed to create enrollment. ${backendMessage}`
                );
            } else {
                setError(
                    isEditMode
                        ? "Failed to update enrollment."
                        : "Failed to create enrollment."
                );
            }
        }
    };

    const handleDeleteEnrollment = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this enrollment?"
        );

        if (!confirmed) return;

        try {
            setError("");
            setSuccessMessage("");

            await axios.delete(`/enrollments/${id}`);

            setSuccessMessage("Enrollment deleted successfully.");
            fetchEnrollments();
        } catch (err) {
            console.error("Delete enrollment error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    `Status: ${err.response.status}`;

                setError(`Failed to delete enrollment. ${backendMessage}`);
            } else {
                setError("Failed to delete enrollment.");
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
                        <h1 className="page-title">Enrollments</h1>
                        <p className="page-subtitle">
                            Manage student-course enrollments in your system.
                        </p>
                    </div>

                    <div className="action-group">
                        <button className="primary-btn" onClick={handleToggleForm}>
                            {showForm ? "Close Form" : "Add Enrollment"}
                        </button>

                        <button className="secondary-btn" onClick={fetchEnrollments}>
                            Refresh
                        </button>
                    </div>
                </section>

                {showForm && (
                    <section className="content-card form-card">
                        <h2>{isEditMode ? "Edit Enrollment" : "Add New Enrollment"}</h2>

                        <form className="modern-form" onSubmit={handleSubmitEnrollment}>
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Student</label>
                                    <select
                                        name="studentId"
                                        value={formData.studentId}
                                        onChange={handleChange}
                                        required
                                        className="modern-select"
                                        disabled={formLoading}
                                    >
                                        <option value="">Select student</option>
                                        {students.map((student) => (
                                            <option key={student.id} value={student.id}>
                                                {student.studentNumber} - {student.firstName} {student.lastName}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Course</label>
                                    <select
                                        name="courseId"
                                        value={formData.courseId}
                                        onChange={handleChange}
                                        required
                                        className="modern-select"
                                        disabled={formLoading}
                                    >
                                        <option value="">Select course</option>
                                        {courses.map((course) => (
                                            <option key={course.id} value={course.id}>
                                                {course.courseCode} - {course.courseName}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Enrollment Date</label>
                                    <input
                                        type="date"
                                        name="enrollmentDate"
                                        value={formData.enrollmentDate}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Status</label>
                                    <select
                                        name="status"
                                        value={formData.status}
                                        onChange={handleChange}
                                        className="modern-select"
                                        required
                                    >
                                        <option value="ACTIVE">ACTIVE</option>
                                        <option value="COMPLETED">COMPLETED</option>
                                        <option value="CANCELLED">CANCELLED</option>
                                    </select>
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="primary-btn">
                                    {isEditMode ? "Update Enrollment" : "Save Enrollment"}
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
                            placeholder="Search by student, course, date, status..."
                            className="search-input"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>

                    {successMessage && <p className="success-text">{successMessage}</p>}
                    {loading && <p className="info-text">Loading enrollments...</p>}
                    {error && <p className="error-text">{error}</p>}

                    {!loading && !error && filteredEnrollments.length === 0 && (
                        <p className="info-text">No enrollments found.</p>
                    )}

                    {!loading && !error && filteredEnrollments.length > 0 && (
                        <div className="table-wrapper">
                            <table className="modern-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Student</th>
                                    <th>Course</th>
                                    <th>Enrollment Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredEnrollments.map((enrollment) => (
                                    <tr key={enrollment.id}>
                                        <td>{enrollment.id}</td>
                                        <td>{getStudentDisplay(enrollment)}</td>
                                        <td>{getCourseDisplay(enrollment)}</td>
                                        <td>{getEnrollmentDate(enrollment)}</td>
                                        <td>{getStatus(enrollment)}</td>
                                        <td>
                                            <div className="table-actions">
                                                <button
                                                    className="edit-btn"
                                                    onClick={() => handleEditEnrollment(enrollment)}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className="danger-btn"
                                                    onClick={() => handleDeleteEnrollment(enrollment.id)}
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

export default EnrollmentsPage;