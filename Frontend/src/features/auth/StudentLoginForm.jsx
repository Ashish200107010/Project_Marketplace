import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../../context/AuthContext"; // ✅ Import the useAuth hook

const StudentLoginForm = ({ onSwitch, onForgot }) => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const { login } = useAuth(); // ✅ get login function from context

  const handleLogin = async (e) => {
    e.preventDefault();
    setError(""); // Clear previous errors
    try {
      const res = await axios.post(
        "http://localhost:8080/auth/student/login",
        { email, password },
        { withCredentials: true }
      );
      if (res.status === 200) {
        login(res.data.student); // ✅ Update context with logged-in user
        localStorage.setItem("user", JSON.stringify(res.data.student));
        localStorage.setItem("user_expiry", Date.now() + 60 * 60 * 1000); // 1 hour expiry
        localStorage.setItem("role", res.data.student.role);
        navigate("/student/dashboard");
      }
    } catch (err) {
      setError("Invalid credentials. Please try again.");
    }
  };

  return (
    <div className="fade-in">
      <div className="form-box">
        <div className="text-center mb-4">
          <h3 className="fw-bold" style={{ color: "var(--text-main)" }}>
            Login here
          </h3>
          <p className="text-muted small">Enter your details to continue</p>
        </div>

        {error && (
          <div className="alert alert-danger py-2 small border-0 shadow-sm mb-4">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin}>
          <div className="mb-3">
            <label className="form-label small fw-bold" style={{ color: "var(--text-main)" }}>
              Email Address
            </label>
            <input
              type="email"
              className="form-control custom-input"
              placeholder="email@college.edu"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="mb-2">
            <label className="form-label small fw-bold" style={{ color: "var(--text-main)" }}>
              Password
            </label>
            <input
              type="password"
              className="form-control custom-input"
              placeholder="••••••••"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>

          <div className="text-end mb-4">
            <span className="forgot-password-link" onClick={onForgot}>
              Forgot Password?
            </span>
          </div>

          <button type="submit" className="btn-primary-green w-100">
            Sign In
          </button>
        </form>

        <div className="text-center mt-4">
          <p className="small text-muted">
            Don't have an account?{" "}
            <span className="auth-toggle-link" onClick={onSwitch}>
              Register Now
            </span>
          </p>
        </div>
      </div>
    </div>
  );
};

export default StudentLoginForm;