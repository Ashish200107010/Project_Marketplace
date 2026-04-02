import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Home from "./pages/Home";
import AdminDashboard from "./features/admin/AdminDashboard";
import StudentDashboard from "./features/user/StudentDashboard";
import NotFound from "./pages/NotFound";
import Layout from "./components/Layout";
// REMOVED: StudentLoginForm (It's now inside AuthPage)
// REMOVED: StudentRegister (It's now inside AuthPage)
import AdminLogin from "./features/auth/AdminLogin";
import VerifyOtp from "./features/auth/VerifyOtp";
import ProjectDetailPage from "./features/project/ProjectDetailPage";
import About from "./components/About";
import Privacy from "./components/Privacy";
import Service from "./components/Service";
import Payments from "./features/payment/Payments";
import AuthPage from "./features/auth/AuthPage";
import { AuthProvider } from "./context/AuthContext";
import { ProjectProvider } from "./context/ProjectContext";

function App() {
  return (
    <AuthProvider> {/* ✅ Wrap everything here */}
      <ProjectProvider> {/* ✅ Wrap everything here */}
        <Router>
          <Routes>
            <Route element={<Layout />}>
              {/* Public routes */}
              <Route path="/" element={<Home />} />
              
              <Route path="/student/login" element={<AuthPage />} />
              <Route path="/admin/login" element={<AdminLogin />} />
              <Route path="/verify-otp" element={<VerifyOtp />} />
              <Route path="/admin/dashboard" element={<AdminDashboard />} />
              <Route path="/student/dashboard" element={<StudentDashboard />} />
              <Route path="/projects/:id" element={<ProjectDetailPage />} />
              <Route path="/about" element={<About />} />
              <Route path="/privacy" element={<Privacy />} />
              <Route path="/service" element={<Service />} />
              <Route path="/payments" element={<Payments />} />
              
              <Route path="*" element={<NotFound />} />
            </Route>
          </Routes>
        </Router>
      </ProjectProvider>
    </AuthProvider>
  );
}

export default App;