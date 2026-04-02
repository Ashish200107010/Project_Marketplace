import React, { useState } from "react";
import axios from "axios";

const ForgotPasswordForm = ({ onBack }) => {
  const [email, setEmail] = useState("");
  const [otpSent, setOtpSent] = useState(false);
  const [otp, setOtp] = useState("");

  const handleSendOtp = async (e) => {
    e.preventDefault();
    // Your API call to /auth/forgot-password-otp
    setOtpSent(true);
  };

  return (
    <div className="fade-in">
      <h3 className="fw-bold text-center">Reset Password</h3>
      <p className="text-muted small text-center mb-4">
        {otpSent ? "Enter the code sent to your email" : "We'll send you an OTP to reset your password"}
      </p>

      <form onSubmit={handleSendOtp}>
        <div className="mb-3">
          <label className="form-label small fw-bold">Email Address</label>
          <input type="email" className="form-control custom-input" value={email} onChange={(e) => setEmail(e.target.value)} required disabled={otpSent}/>
        </div>

        {otpSent && (
          <div className="mb-3">
            <label className="form-label small fw-bold">Enter OTP</label>
            <input type="text" className="form-control custom-input" value={otp} onChange={(e) => setOtp(e.target.value)} placeholder="6-digit code" required />
          </div>
        )}

        <button type="submit" className="btn btn-primary-green w-100 mb-3">
          {otpSent ? "Verify OTP" : "Send Reset Link"}
        </button>
      </form>

      <div className="text-center">
        <span className="auth-toggle-link small" onClick={onBack}>← Back to Login</span>
      </div>
    </div>
  );
};

export default ForgotPasswordForm;