import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";

const VerifyOtp = () => {
  const { state } = useLocation();
  const navigate = useNavigate();
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");

  const handleVerify = async () => {
    try {
        const res = await axios.post("http://localhost:8080/auth/student/verify-otp", {
            email: state.email,
            otp
        }, {withcredential: true});
        
        if (res.status === 200) {
            const { student } = res.data;

            localStorage.setItem("role", student.role);
            localStorage.setItem("userId", student.id);
            localStorage.setItem("user", JSON.stringify(student));

            navigate("/student/dashboard");
        } else {
            setError("Unexpected response. Please try again.");
        }

        } catch (err) {
            console.error("OTP verification failed:", err);
            setError(err.response?.data?.message || "Invalid OTP. Please try again.");
        }

  };

  return (
    <div className="card p-4 shadow-sm" style={{ maxWidth: "400px", margin: "auto" }}>
      <h4 className="text-center mb-3">Verify OTP</h4>
      {error && <div className="alert alert-danger">{error}</div>}
      <input
        type="text"
        className="form-control mb-3"
        placeholder="Enter OTP"
        value={otp}
        onChange={(e) => setOtp(e.target.value)}
      />
      <button className="btn btn-primary w-100" onClick={handleVerify}>Confirm OTP</button>
    </div>
  );
};

export default VerifyOtp;
