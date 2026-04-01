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
import Layout from "./components/Layout";

function App() {
    return (
        <Routes>
            {/* PUBLIC */}
            <Route path="/" element={<Navigate to="/login" replace />} />
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />

            {/* PROTECTED + LAYOUT */}
            <Route
                path="/dashboard"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <Layout>
                            <DashboardPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/students"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <Layout>
                            <StudentsPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/courses"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <Layout>
                            <CoursesPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/enrollments"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN"]}>
                        <Layout>
                            <EnrollmentsPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/my-courses"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <Layout>
                            <MyCoursesPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/schedules"
                element={
                    <ProtectedRoute allowedRoles={["ADMIN", "STUDENT"]}>
                        <Layout>
                            <SchedulesPage />
                        </Layout>
                    </ProtectedRoute>
                }
            />
        </Routes>
    );
}

export default App;