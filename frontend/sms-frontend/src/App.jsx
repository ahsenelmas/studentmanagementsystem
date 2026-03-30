import { Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RegisterPage from "./pages/RegisterPage";
import DashboardPage from "./pages/DashboardPage";
import StudentsPage from "./pages/StudentsPage";
import CoursesPage from "./pages/CoursesPage";
import EnrollmentsPage from "./pages/EnrollmentsPage";
import SchedulesPage from "./pages/SchedulesPage";
import ProtectedRoute from "./components/ProtectedRoute";
import MyCoursesPage from "./pages/MyCoursesPage";

function App() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <DashboardPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/students"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <StudentsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/courses"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <CoursesPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/enrollments"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <EnrollmentsPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/my-courses"
                element={
                    <ProtectedRoute>
                        <MyCoursesPage />
                    </ProtectedRoute>
                }
            />

            <Route
                path="/schedules"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <SchedulesPage />
                    </ProtectedRoute>
                }
            />
        </Routes>
    );
}

export default App;