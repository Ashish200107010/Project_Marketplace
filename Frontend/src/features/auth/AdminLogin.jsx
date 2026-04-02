import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const AdminLogin = () => {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
    setError("");
  };

  const validateForm = () => {
    const { email, password } = form;
    if (!email || !password) return "All fields are required.";
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) return "Invalid email format.";
    return null;
  };

  const handleLogin = async () => {
    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post("http://localhost:8080/auth/admin/login", form, {
        withCredentials: true
      });

      // ✅ Check if login was successful
      if (res.status === 200 && res.data && res.data.admin) {
        navigate("/admin/dashboard");
      } else if (res.status === 401 && message === "TOKEN_MISSING") {
        window.location.href = "/login";
      } else {
        setError("Unexpected response. Please try again.");
      }
    } catch (err) {
      console.error("Login error:", err);
      const message =
        err.response?.data?.message 
        || err.response?.data?.error 
        || err.message 
        || "Login failed. Please check your credentials.";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center min-vh-100 bg-light">
      <div className="card shadow p-4" style={{ maxWidth: "400px", width: "100%" }}>
        <h3 className="text-center mb-4">Admin Login</h3>

        {error && <div className="alert alert-danger">{error}</div>}

        <div className="mb-3">
          <label className="form-label">Email address</label>
          <input
            type="email"
            name="email"
            className="form-control"
            value={form.email}
            onChange={handleChange}
            placeholder="admin@example.com"
            autoComplete="email"
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Password</label>
          <input
            type="password"
            name="password"
            className="form-control"
            value={form.password}
            onChange={handleChange}
            placeholder="••••••••"
            autoComplete="current-password"
          />
        </div>

        <div className="d-grid">
          <button
            className="btn btn-primary"
            onClick={handleLogin}
            disabled={loading}
          >
            {loading ? "Logging in..." : "Login"}
          </button>
        </div>

        <div className="text-center mt-3">
          <a href="/forgot-password" className="text-decoration-none">Forgot Password?</a>
        </div>
      </div>
    </div>
  );
};

export default AdminLogin;
