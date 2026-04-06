import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios.js";

function RegisterPage() {
    const navigate = useNavigate();

    const [formData, setFormData] = useState({
        username: "",
        email: "",
        password: "",
        studentId: "",
    });

    const [students, setStudents] = useState([]);
    const [message, setMessage] = useState("");
    const [error, setError] = useState("");
    const [loadingStudents, setLoadingStudents] = useState(true);

    useEffect(() => {
        fetchStudents();
    }, []);

    const fetchStudents = async () => {
        try {
            const response = await api.get("/auth/student-options");
            setStudents(response.data);
        } catch (err) {
            console.error("Failed to load students:", err);
            setError("Could not load student list.");
        } finally {
            setLoadingStudents(false);
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        try {
            const payload = {
                username: formData.username,
                email: formData.email,
                password: formData.password,
                studentId: Number(formData.studentId),
            };

            const response = await api.post("/auth/register", payload);
            setMessage(typeof response.data === "string" ? response.data : "Registration successful!");

            setTimeout(() => {
                navigate("/login");
            }, 1200);
        } catch (err) {
            setError(
                err.response?.data?.message ||
                err.response?.data ||
                "Registration failed"
            );
        }
    };

    return (
        <div className="page-container">
            <div className="card">
                <h2>Register</h2>

                <form onSubmit={handleSubmit} className="form">
                    <input
                        type="text"
                        name="username"
                        placeholder="Username"
                        value={formData.username}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="email"
                        name="email"
                        placeholder="Email"
                        value={formData.email}
                        onChange={handleChange}
                        required
                    />

                    <input
                        type="password"
                        name="password"
                        placeholder="Password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                    />

                    <select
                        name="studentId"
                        value={formData.studentId}
                        onChange={handleChange}
                        required
                        disabled={loadingStudents}
                    >
                        <option value="">
                            {loadingStudents ? "Loading students..." : "Select Student"}
                        </option>

                        {students.map((student) => (
                            <option key={student.id} value={student.id}>
                                {student.fullName} ({student.studentNumber})
                            </option>
                        ))}
                    </select>

                    <button type="submit">Register</button>
                </form>

                {message && <p className="success">{message}</p>}
                {error && <p className="error">{error}</p>}

                <p>
                    Already have an account? <Link to="/login">Login</Link>
                </p>
            </div>
        </div>
    );
}

export default RegisterPage;