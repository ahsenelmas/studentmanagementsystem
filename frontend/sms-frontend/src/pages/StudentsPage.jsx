import { useEffect, useState } from "react";
import api from "../api/axios.js";
import Navbar from "../components/Navbar.jsx";

function StudentsPage() {
    const [students, setStudents] = useState([]);
    const [error, setError] = useState("");

    useEffect(() => {
        fetchStudents();
    }, []);

    const fetchStudents = async () => {
        try {
            const response = await api.get("/api/students");
            setStudents(response.data);
        } catch (err) {
            setError(err.response?.data?.message || "Failed to load students");
        }
    };

    return (
        <div>
            <Navbar />

            <div className="content-container">
                <div className="content-card">
                    <h2>Students</h2>

                    {error && <p className="error">{error}</p>}

                    {students.length === 0 ? (
                        <p>No students found.</p>
                    ) : (
                        <table className="data-table">
                            <thead>
                            <tr>
                                <th>ID</th>
                                <th>Student Number</th>
                                <th>First Name</th>
                                <th>Last Name</th>
                                <th>Department</th>
                            </tr>
                            </thead>
                            <tbody>
                            {students.map((student) => (
                                <tr key={student.id}>
                                    <td>{student.id}</td>
                                    <td>{student.studentNumber}</td>
                                    <td>{student.firstName}</td>
                                    <td>{student.lastName}</td>
                                    <td>{student.department}</td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    )}
                </div>
            </div>
        </div>
    );
}

export default StudentsPage;