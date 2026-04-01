import { useEffect, useMemo, useState } from "react";
import axios from "../api/axios";
import Navbar from "../components/Navbar";

const initialFormData = {
    dayOfWeek: "",
    startTime: "",
    endTime: "",
    room: "",
    semester: "",
    courseId: "",
};

const WEEK_DAYS = [
    "MONDAY",
    "TUESDAY",
    "WEDNESDAY",
    "THURSDAY",
    "FRIDAY",
    "SATURDAY",
    "SUNDAY",
];

const HOURS = Array.from({ length: 24 }, (_, i) =>
    String(i).padStart(2, "0")
);

const MINUTES = ["00", "10", "20", "30", "40", "50"];

function formatTime(time) {
    if (!time) return "";
    return String(time).slice(0, 5);
}

function getHourFromTime(time) {
    if (!time) return "";
    const parts = time.split(":");
    return parts[0] || "";
}

function getMinuteFromTime(time) {
    if (!time) return "";
    const parts = time.split(":");
    return parts[1] || "";
}

function updateTimeValue(currentTime, part, newValue) {
    const currentHour = getHourFromTime(currentTime);
    const currentMinute = getMinuteFromTime(currentTime);

    const hour = part === "hour" ? newValue : currentHour;
    const minute = part === "minute" ? newValue : currentMinute;

    return `${hour}:${minute}`;
}

function TimeSelect({ label, value, onChange }) {
    const selectedHour = getHourFromTime(value);
    const selectedMinute = getMinuteFromTime(value);

    return (
        <div className="form-group">
            <label>{label}</label>

            <div className="time-select-row">
                <select
                    className="modern-select"
                    value={selectedHour}
                    onChange={(e) =>
                        onChange(updateTimeValue(value, "hour", e.target.value))
                    }
                    required
                >
                    <option value="">Hour</option>
                    {HOURS.map((hour) => (
                        <option key={hour} value={hour}>
                            {hour}
                        </option>
                    ))}
                </select>

                <span className="time-separator">:</span>

                <select
                    className="modern-select"
                    value={selectedMinute}
                    onChange={(e) =>
                        onChange(updateTimeValue(value, "minute", e.target.value))
                    }
                    required
                >
                    <option value="">Min</option>
                    {MINUTES.map((minute) => (
                        <option key={minute} value={minute}>
                            {minute}
                        </option>
                    ))}
                </select>
            </div>
        </div>
    );
}

function SchedulesPage() {
    const role = localStorage.getItem("role");
    const isAdmin = role === "ADMIN";

    const [schedules, setSchedules] = useState([]);
    const [courses, setCourses] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [selectedDay, setSelectedDay] = useState("ALL");
    const [loading, setLoading] = useState(true);
    const [submitting, setSubmitting] = useState(false);
    const [error, setError] = useState("");
    const [successMessage, setSuccessMessage] = useState("");
    const [showForm, setShowForm] = useState(false);
    const [editingScheduleId, setEditingScheduleId] = useState(null);
    const [formData, setFormData] = useState(initialFormData);

    const isEditing = editingScheduleId !== null;

    const clearMessages = () => {
        setError("");
        setSuccessMessage("");
    };

    const fetchSchedules = async () => {
        try {
            setLoading(true);
            clearMessages();

            const endpoint = isAdmin ? "/schedules" : "/schedules/my";
            const res = await axios.get(endpoint);
            const data = Array.isArray(res.data) ? res.data : [];

            setSchedules(data);
        } catch (err) {
            setError("Failed to load schedules.");
        } finally {
            setLoading(false);
        }
    };

    const fetchCourses = async () => {
        try {
            const endpoint = isAdmin ? "/courses" : "/courses/my";
            const res = await axios.get(endpoint);
            const data = Array.isArray(res.data) ? res.data : [];

            setCourses(data);
        } catch (err) {
            setError("Failed to load courses.");
        }
    };

    useEffect(() => {
        fetchSchedules();
        fetchCourses();
    }, [isAdmin]);

    const filteredSchedules = useMemo(() => {
        const term = searchTerm.trim().toLowerCase();

        const result = schedules.filter((schedule) => {
            const day = schedule.dayOfWeek?.toLowerCase() || "";
            const room = schedule.room?.toLowerCase() || "";
            const semester = schedule.semester?.toLowerCase() || "";
            const courseName = schedule.courseName?.toLowerCase() || "";
            const courseCode = schedule.courseCode?.toLowerCase() || "";

            const matchesSearch =
                !term ||
                day.includes(term) ||
                room.includes(term) ||
                semester.includes(term) ||
                courseName.includes(term) ||
                courseCode.includes(term);

            const matchesDay =
                selectedDay === "ALL" || schedule.dayOfWeek === selectedDay;

            return matchesSearch && matchesDay;
        });

        result.sort((a, b) => {
            if (a.dayOfWeek !== b.dayOfWeek) {
                return WEEK_DAYS.indexOf(a.dayOfWeek) - WEEK_DAYS.indexOf(b.dayOfWeek);
            }
            return String(a.startTime).localeCompare(String(b.startTime));
        });

        return result;
    }, [searchTerm, selectedDay, schedules]);

    const timetableData = useMemo(() => {
        const grouped = {};
        WEEK_DAYS.forEach((day) => {
            grouped[day] = [];
        });

        filteredSchedules.forEach((schedule) => {
            if (grouped[schedule.dayOfWeek]) {
                grouped[schedule.dayOfWeek].push(schedule);
            }
        });

        WEEK_DAYS.forEach((day) => {
            grouped[day].sort((a, b) =>
                String(a.startTime).localeCompare(String(b.startTime))
            );
        });

        return grouped;
    }, [filteredSchedules]);

    const resetForm = () => {
        setFormData(initialFormData);
        setEditingScheduleId(null);
        setShowForm(false);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;

        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const validateForm = () => {
        if (
            !formData.dayOfWeek ||
            !formData.startTime ||
            !formData.endTime ||
            !formData.room ||
            !formData.semester ||
            !formData.courseId
        ) {
            setError("Please fill in all fields.");
            return false;
        }

        const startHour = getHourFromTime(formData.startTime);
        const startMinute = getMinuteFromTime(formData.startTime);
        const endHour = getHourFromTime(formData.endTime);
        const endMinute = getMinuteFromTime(formData.endTime);

        if (!startHour || !startMinute || !endHour || !endMinute) {
            setError("Please select both hour and minute for start time and end time.");
            return false;
        }

        if (formData.startTime >= formData.endTime) {
            setError("End time must be later than start time.");
            return false;
        }

        return true;
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        clearMessages();

        if (!validateForm()) return;

        try {
            setSubmitting(true);

            await axios.post("/schedules", {
                dayOfWeek: formData.dayOfWeek,
                startTime: formData.startTime,
                endTime: formData.endTime,
                room: formData.room,
                semester: formData.semester,
                courseId: Number(formData.courseId),
            });

            setSuccessMessage("Schedule created successfully.");
            resetForm();
            await fetchSchedules();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to create schedule.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleEditClick = (schedule) => {
        if (!isAdmin) return;

        clearMessages();
        setShowForm(true);
        setEditingScheduleId(schedule.id);

        setFormData({
            dayOfWeek: schedule.dayOfWeek || "",
            startTime: schedule.startTime ? schedule.startTime.slice(0, 5) : "",
            endTime: schedule.endTime ? schedule.endTime.slice(0, 5) : "",
            room: schedule.room || "",
            semester: schedule.semester || "",
            courseId: schedule.courseId ? String(schedule.courseId) : "",
        });

        window.scrollTo({ top: 0, behavior: "smooth" });
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        clearMessages();

        if (!validateForm()) return;

        try {
            setSubmitting(true);

            await axios.put(`/schedules/${editingScheduleId}`, {
                dayOfWeek: formData.dayOfWeek,
                startTime: formData.startTime,
                endTime: formData.endTime,
                room: formData.room,
                semester: formData.semester,
                courseId: Number(formData.courseId),
            });

            setSuccessMessage("Schedule updated successfully.");
            resetForm();
            await fetchSchedules();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to update schedule.");
        } finally {
            setSubmitting(false);
        }
    };

    const handleDelete = async (id) => {
        if (!isAdmin) return;

        clearMessages();

        const confirmed = window.confirm("Are you sure you want to delete this schedule?");
        if (!confirmed) return;

        try {
            await axios.delete(`/schedules/${id}`);
            setSuccessMessage("Schedule deleted successfully.");
            await fetchSchedules();
        } catch (err) {
            setError(err.response?.data?.message || "Failed to delete schedule.");
        }
    };

    return (
        <div className="app-shell">
            <Navbar />

            <main className="page-wrapper">
                <section className="page-header-row">
                    <div>
                        <p className="section-badge">
                            {isAdmin ? "Management" : "Student Portal"}
                        </p>
                        <h1 className="page-title">Schedules</h1>
                        <p className="page-subtitle">
                            {isAdmin
                                ? "Create, update, and manage course schedules."
                                : "Browse your personal weekly timetable and course schedule information."}
                        </p>
                    </div>

                    <div className="action-group">
                        {isAdmin && (
                            <button
                                className="primary-btn"
                                onClick={() => {
                                    clearMessages();
                                    if (showForm && isEditing) {
                                        resetForm();
                                    } else {
                                        setShowForm(!showForm);
                                        if (showForm) resetForm();
                                    }
                                }}
                            >
                                {showForm ? "Close Form" : "Add Schedule"}
                            </button>
                        )}

                        <button className="secondary-btn" onClick={fetchSchedules}>
                            Refresh
                        </button>
                    </div>
                </section>

                {showForm && isAdmin && (
                    <section className="content-card form-card">
                        <h2>{isEditing ? "Edit Schedule" : "Add Schedule"}</h2>

                        <form
                            className="modern-form"
                            onSubmit={isEditing ? handleUpdate : handleCreate}
                        >
                            <div className="form-grid">
                                <div className="form-group">
                                    <label>Day</label>
                                    <select
                                        className="modern-select"
                                        name="dayOfWeek"
                                        value={formData.dayOfWeek}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="">Select day</option>
                                        {WEEK_DAYS.map((day) => (
                                            <option key={day} value={day}>
                                                {day}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <TimeSelect
                                    label="Start Time"
                                    value={formData.startTime}
                                    onChange={(newTime) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            startTime: newTime,
                                        }))
                                    }
                                />

                                <TimeSelect
                                    label="End Time"
                                    value={formData.endTime}
                                    onChange={(newTime) =>
                                        setFormData((prev) => ({
                                            ...prev,
                                            endTime: newTime,
                                        }))
                                    }
                                />

                                <div className="form-group">
                                    <label>Room</label>
                                    <input
                                        type="text"
                                        name="room"
                                        value={formData.room}
                                        onChange={handleChange}
                                        placeholder="e.g. A101"
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label>Semester</label>
                                    <input
                                        type="text"
                                        name="semester"
                                        value={formData.semester}
                                        onChange={handleChange}
                                        placeholder="e.g. Spring 2026"
                                        required
                                    />
                                </div>

                                <div className="form-group full-width">
                                    <label>Course</label>
                                    <select
                                        className="modern-select"
                                        name="courseId"
                                        value={formData.courseId}
                                        onChange={handleChange}
                                        required
                                    >
                                        <option value="">Select course</option>
                                        {courses.map((course) => (
                                            <option key={course.id} value={course.id}>
                                                {course.courseCode} - {course.courseName}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="primary-btn" disabled={submitting}>
                                    {submitting
                                        ? isEditing
                                            ? "Updating..."
                                            : "Saving..."
                                        : isEditing
                                            ? "Update Schedule"
                                            : "Save Schedule"}
                                </button>

                                <button
                                    type="button"
                                    className="secondary-btn"
                                    onClick={resetForm}
                                    disabled={submitting}
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </section>
                )}

                <section className="content-card" style={{ marginBottom: "24px" }}>
                    <div className="schedule-toolbar">
                        <input
                            className="search-input"
                            placeholder="Search by day, room, semester, course..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />

                        <select
                            className="modern-select day-filter"
                            value={selectedDay}
                            onChange={(e) => setSelectedDay(e.target.value)}
                        >
                            <option value="ALL">All Days</option>
                            {WEEK_DAYS.map((day) => (
                                <option key={day} value={day}>
                                    {day}
                                </option>
                            ))}
                        </select>
                    </div>

                    {error && <p className="error-text">{error}</p>}
                    {successMessage && <p className="success-text">{successMessage}</p>}
                </section>

                <section className="content-card" style={{ marginBottom: "24px" }}>
                    <div className="view-header">
                        <h2>Weekly Timetable</h2>
                        <p className="info-text">
                            A visual weekly view of your schedule entries.
                        </p>
                    </div>

                    {loading ? (
                        <p className="info-text">Loading timetable...</p>
                    ) : (
                        <div className="timetable-grid">
                            {WEEK_DAYS.map((day) => (
                                <div key={day} className="day-column">
                                    <div className="day-column-header">{day}</div>

                                    <div className="day-column-body">
                                        {timetableData[day]?.length > 0 ? (
                                            timetableData[day].map((schedule) => (
                                                <div key={schedule.id} className="schedule-block">
                                                    <div className="schedule-block-top">
                                                        <span className="schedule-course-code">
                                                            {schedule.courseCode}
                                                        </span>
                                                        <span className="schedule-room">
                                                            {schedule.room}
                                                        </span>
                                                    </div>

                                                    <h3 className="schedule-course-name">
                                                        {schedule.courseName}
                                                    </h3>

                                                    <p className="schedule-time">
                                                        {formatTime(schedule.startTime)} - {formatTime(schedule.endTime)}
                                                    </p>

                                                    <p className="schedule-semester">
                                                        {schedule.semester}
                                                    </p>

                                                    {isAdmin && (
                                                        <div className="schedule-card-actions">
                                                            <button
                                                                className="edit-btn"
                                                                onClick={() => handleEditClick(schedule)}
                                                            >
                                                                Edit
                                                            </button>
                                                            <button
                                                                className="danger-btn"
                                                                onClick={() => handleDelete(schedule.id)}
                                                            >
                                                                Delete
                                                            </button>
                                                        </div>
                                                    )}
                                                </div>
                                            ))
                                        ) : (
                                            <div className="empty-day-card">No classes</div>
                                        )}
                                    </div>
                                </div>
                            ))}
                        </div>
                    )}
                </section>

                <section className="content-card">
                    <div className="view-header">
                        <h2>Table View</h2>
                        <p className="info-text">
                            A structured list view for quick review and management.
                        </p>
                    </div>

                    {loading ? (
                        <p className="info-text">Loading schedules...</p>
                    ) : filteredSchedules.length === 0 ? (
                        <p className="info-text">No schedules found.</p>
                    ) : (
                        <div className="table-wrapper">
                            <table className="modern-table">
                                <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Day</th>
                                    <th>Time</th>
                                    <th>Room</th>
                                    <th>Semester</th>
                                    <th>Course Code</th>
                                    <th>Course Name</th>
                                    {isAdmin && <th>Actions</th>}
                                </tr>
                                </thead>
                                <tbody>
                                {filteredSchedules.map((schedule) => (
                                    <tr key={schedule.id}>
                                        <td>{schedule.id}</td>
                                        <td>{schedule.dayOfWeek}</td>
                                        <td>
                                            {formatTime(schedule.startTime)} - {formatTime(schedule.endTime)}
                                        </td>
                                        <td>{schedule.room}</td>
                                        <td>{schedule.semester}</td>
                                        <td>{schedule.courseCode}</td>
                                        <td>{schedule.courseName}</td>

                                        {isAdmin && (
                                            <td>
                                                <div className="table-actions">
                                                    <button
                                                        className="edit-btn"
                                                        onClick={() => handleEditClick(schedule)}
                                                    >
                                                        Edit
                                                    </button>

                                                    <button
                                                        className="danger-btn"
                                                        onClick={() => handleDelete(schedule.id)}
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            </td>
                                        )}
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

export default SchedulesPage;