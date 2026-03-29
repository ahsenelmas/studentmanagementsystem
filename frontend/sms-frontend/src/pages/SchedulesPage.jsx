import { useEffect, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

function SchedulesPage() {
    const [schedules, setSchedules] = useState([]);
    const [courses, setCourses] = useState([]);
    const [filteredSchedules, setFilteredSchedules] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [showForm, setShowForm] = useState(false);

    const [formData, setFormData] = useState({
        dayOfWeek: "",
        startTime: "",
        endTime: "",
        classroom: "",
        courseId: ""
    });

    const fetchSchedules = async () => {
        try {
            setLoading(true);
            const res = await axios.get("/schedules");

            const data = Array.isArray(res.data)
                ? res.data
                : res.data.content || [];

            setSchedules(data);
            setFilteredSchedules(data);
        } catch (err) {
            setError("Failed to load schedules");
        } finally {
            setLoading(false);
        }
    };

    const fetchCourses = async () => {
        try {
            const res = await axios.get("/courses");
            setCourses(res.data);
        } catch (err) {
            console.error("Courses fetch error", err);
        }
    };

    useEffect(() => {
        fetchSchedules();
        fetchCourses();
    }, []);

    useEffect(() => {
        const filtered = schedules.filter((s) =>
            s.dayOfWeek.toLowerCase().includes(searchTerm.toLowerCase()) ||
            s.classroom.toLowerCase().includes(searchTerm.toLowerCase())
        );

        setFilteredSchedules(filtered);
    }, [searchTerm, schedules]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const handleCreate = async (e) => {
        e.preventDefault();

        try {
            await axios.post("/schedules", formData);

            setSuccessMessage("Schedule created successfully");
            setShowForm(false);
            fetchSchedules();

        } catch (err) {
            setError("Failed to create schedule");
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Delete this schedule?")) return;

        try {
            await axios.delete(`/schedules/${id}`);
            fetchSchedules();
        } catch (err) {
            setError("Failed to delete schedule");
        }
    };

    return (
        <div className="app-shell">
            <Navbar />

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">Management</p>
                        <h1 className="page-title">Schedules</h1>
                        <p className="page-subtitle">
                            Manage course schedules
                        </p>
                    </div>

                    <div className="action-group">
                        <button className="primary-btn" onClick={() => setShowForm(!showForm)}>
                            {showForm ? "Close Form" : "Add Schedule"}
                        </button>

                        <button className="secondary-btn" onClick={fetchSchedules}>
                            Refresh
                        </button>
                    </div>
                </section>

                {showForm && (
                    <section className="content-card form-card">
                        <h2>Add Schedule</h2>

                        <form className="modern-form" onSubmit={handleCreate}>
                            <div className="form-grid">

                                <div className="form-group">
                                    <label>Day</label>
                                    <select name="dayOfWeek" onChange={handleChange} required>
                                        <option value="">Select</option>
                                        <option>MONDAY</option>
                                        <option>TUESDAY</option>
                                        <option>WEDNESDAY</option>
                                        <option>THURSDAY</option>
                                        <option>FRIDAY</option>
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label>Start Time</label>
                                    <input type="time" name="startTime" onChange={handleChange} required />
                                </div>

                                <div className="form-group">
                                    <label>End Time</label>
                                    <input type="time" name="endTime" onChange={handleChange} required />
                                </div>

                                <div className="form-group">
                                    <label>Classroom</label>
                                    <input type="text" name="classroom" onChange={handleChange} required />
                                </div>

                                <div className="form-group full-width">
                                    <label>Course</label>
                                    <select name="courseId" onChange={handleChange} required>
                                        <option value="">Select Course</option>
                                        {courses.map(c => (
                                            <option key={c.id} value={c.id}>
                                                {c.courseCode} - {c.courseName}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                            </div>

                            <div className="form-actions">
                                <button type="submit" className="primary-btn">Save</button>
                                <button type="button" className="secondary-btn" onClick={() => setShowForm(false)}>Cancel</button>
                            </div>
                        </form>
                    </section>
                )}

                <section className="content-card">
                    <input
                        className="search-input"
                        placeholder="Search..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                    />

                    {error && <p className="error-text">{error}</p>}
                    {successMessage && <p className="success-text">{successMessage}</p>}

                    <table className="modern-table">
                        <thead>
                        <tr>
                            <th>ID</th>
                            <th>Day</th>
                            <th>Time</th>
                            <th>Classroom</th>
                            <th>Course</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        {filteredSchedules.map(s => (
                            <tr key={s.id}>
                                <td>{s.id}</td>
                                <td>{s.dayOfWeek}</td>
                                <td>{s.startTime} - {s.endTime}</td>
                                <td>{s.classroom}</td>
                                <td>{s.courseName || s.course?.courseName}</td>
                                <td>
                                    <button
                                        className="danger-btn"
                                        onClick={() => handleDelete(s.id)}
                                    >
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                </section>
            </main>
        </div>
    );
}

export default SchedulesPage;