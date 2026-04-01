import { useState } from "react";
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

    const [message, setMessage] = useState("");
    const [error, setError] = useState("");

    const handleChange = (e) => {
        setFormData((prev) => ({
            ...prev,
            [e.target.name]: e.target.value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage("");
        setError("");

        try {
            const payload = {
                ...formData,
                studentId: Number(formData.studentId),
            };

            const response = await api.post("/auth/register", payload);
            setMessage(response.data);
            setTimeout(() => navigate("/login"), 1200);
        } catch (err) {
            setError(err.response?.data?.message || "Registration failed");
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

                    <input
                        type="number"
                        name="studentId"
                        placeholder="Student ID"
                        value={formData.studentId}
                        onChange={handleChange}
                        required
                    />

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