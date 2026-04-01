import { useEffect, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

function StudentsPage() {
    const [students, setStudents] = useState([]);
    const [filteredStudents, setFilteredStudents] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [isEditMode, setIsEditMode] = useState(false);
    const [editingStudentId, setEditingStudentId] = useState(null);

    const [formData, setFormData] = useState({
        studentNumber: "",
        firstName: "",
        lastName: "",
        birthDate: "",
        phone: "",
        department: "",
    });

    const resetForm = () => {
        setFormData({
            studentNumber: "",
            firstName: "",
            lastName: "",
            birthDate: "",
            phone: "",
            department: "",
        });
        setIsEditMode(false);
        setEditingStudentId(null);
    };

    const fetchStudents = async () => {
        try {
            setLoading(true);
            setError("");

            const response = await axios.get("/students");

            const studentData = Array.isArray(response.data)
                ? response.data
                : response.data.content || [];

            setStudents(studentData);
            setFilteredStudents(studentData);
        } catch (err) {
            console.error("Students fetch error:", err);

            if (err.response) {
                setError(`Failed to load students. Status: ${err.response.status}`);
            } else {
                setError("Failed to load students.");
            }
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchStudents();
    }, []);

    useEffect(() => {
        const filtered = students.filter((student) => {
            const fullName =
                `${student.firstName || ""} ${student.lastName || ""}`.toLowerCase();

            return (
                String(student.id || "").includes(searchTerm) ||
                String(student.studentNumber || "")
                    .toLowerCase()
                    .includes(searchTerm.toLowerCase()) ||
                fullName.includes(searchTerm.toLowerCase()) ||
                String(student.department || "")
                    .toLowerCase()
                    .includes(searchTerm.toLowerCase())
            );
        });

        setFilteredStudents(filtered);
    }, [searchTerm, students]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleEditStudent = (student) => {
        setIsEditMode(true);
        setEditingStudentId(student.id);
        setShowForm(true);
        setError("");
        setSuccessMessage("");

        setFormData({
            studentNumber: student.studentNumber || "",
            firstName: student.firstName || "",
            lastName: student.lastName || "",
            birthDate: student.birthDate || "",
            phone: student.phone || "",
            department: student.department || "",
        });
    };

    const handleSubmitStudent = async (e) => {
        e.preventDefault();
        setError("");
        setSuccessMessage("");

        try {
            const payload = {
                studentNumber: formData.studentNumber,
                firstName: formData.firstName,
                lastName: formData.lastName,
                birthDate: formData.birthDate,
                phone: formData.phone,
                department: formData.department,
            };

            if (isEditMode && editingStudentId) {
                await axios.put(`/students/${editingStudentId}`, payload);
                setSuccessMessage("Student updated successfully.");
            } else {
                await axios.post("/students", payload);
                setSuccessMessage("Student added successfully.");
            }

            resetForm();
            setShowForm(false);
            fetchStudents();
        } catch (err) {
            console.error("Submit student error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    JSON.stringify(err.response.data);

                setError(
                    isEditMode
                        ? `Failed to update student. ${backendMessage}`
                        : `Failed to create student. ${backendMessage}`
                );
            } else {
                setError(
                    isEditMode
                        ? "Failed to update student."
                        : "Failed to create student."
                );
            }
        }
    };

    const handleDeleteStudent = async (id) => {
        const confirmed = window.confirm(
            "Are you sure you want to delete this student?"
        );

        if (!confirmed) return;

        try {
            setError("");
            setSuccessMessage("");

            await axios.delete(`/students/${id}`);

            setSuccessMessage("Student deleted successfully.");
            fetchStudents();
        } catch (err) {
            console.error("Delete student error:", err);

            if (err.response) {
                const backendMessage =
                    err.response.data?.message ||
                    err.response.data?.error ||
                    `Status: ${err.response.status}`;

                setError(`Failed to delete student. ${backendMessage}`);
            } else {
                setError("Failed to delete student.");
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

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">Management</p>
                        <h1 className="page-title">Students</h1>
                        <p className="page-subtitle">
                            View and manage the student list in your system.
                        </p>
                    </div>

                    <div className="action-group">
                        <button className="primary-btn" onClick={handleToggleForm}>
                            {showForm ? "Close Form" : "Add Student"}
                        </button>

                        <button className="secondary-btn" onClick={fetchStudents}>
                            Refresh
                        </button>
                    </div>
                </section>

                {showForm && (
                    <section className="content-card form-card">
                        <h2>{isEditMode ? "Edit Student" : "Add New Student"}</h2>

                        <form className="modern-form" onSubmit={handleSubmitStudent}>
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Student Number</label>
                                    <input
                                        type="text"
                                        name="studentNumber"
                                        value={formData.studentNumber}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>First Name</label>
                                    <input
                                        type="text"
                                        name="firstName"
                                        value={formData.firstName}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Last Name</label>
                                    <input
                                        type="text"
                                        name="lastName"
                                        value={formData.lastName}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Birth Date</label>
                                    <input
                                        type="date"
                                        name="birthDate"
                                        value={formData.birthDate}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Phone</label>
                                    <input
                                        type="text"
                                        name="phone"
                                        value={formData.phone}
                                        onChange={handleChange}
                                    />
                                </div>

                                <div className="form-group full-width">
                                    <label>Department</label>
                                    <input
                                        type="text"
                                        name="department"
                                        value={formData.department}
                                        onChange={handleChange}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="primary-btn">
                                    {isEditMode ? "Update Student" : "Save Student"}
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
                            placeholder="Search by name, student number, department..."
                            className="search-input"
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </div>

                    {successMessage && <p className="success-text">{successMessage}</p>}
                    {loading && <p className="info-text">Loading students...</p>}
                    {error && <p className="error-text">{error}</p>}

                    {!loading && !error && filteredStudents.length === 0 && (
                        <p className="info-text">No students found.</p>
                    )}

                    {!loading && !error && filteredStudents.length > 0 && (
                        <div className="table-wrapper">
                            <table className="modern-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Student Number</th>
                                    <th>First Name</th>
                                    <th>Last Name</th>
                                    <th>Birth Date</th>
                                    <th>Phone</th>
                                    <th>Department</th>
                                    <th>Actions</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filteredStudents.map((student) => (
                                    <tr key={student.id}>
                                        <td>{student.id}</td>
                                        <td>{student.studentNumber}</td>
                                        <td>{student.firstName}</td>
                                        <td>{student.lastName}</td>
                                        <td>{student.birthDate || "-"}</td>
                                        <td>{student.phone || "-"}</td>
                                        <td>{student.department}</td>
                                        <td>
                                            <div className="table-actions">
                                                <button
                                                    className="edit-btn"
                                                    onClick={() => handleEditStudent(student)}
                                                >
                                                    Edit
                                                </button>

                                                <button
                                                    className="danger-btn"
                                                    onClick={() => handleDeleteStudent(student.id)}
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

export default StudentsPage;