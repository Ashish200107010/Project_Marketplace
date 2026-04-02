import React, { useState } from "react";
import Select from "react-select";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { skillsList, rolesList } from "../../constants/projectOptions";
import CollegeDropdown from "../../components/CollegeDropdown";

const StudentRegisterForm = ({ onSwitch }) => {
  const navigate = useNavigate();

  // --- NEW STATES FOR VERIFICATION ---
  const [showOtpField, setShowOtpField] = useState(false);
  const [otp, setOtp] = useState("");
  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const [isSendingOtp, setIsSendingOtp] = useState(false);

  const [form, setForm] = useState({
    email: "",
    password: "",
    name: "",
    college: "",
    skills: [],
    rolesLookingFor: []
  });

  const [touched, setTouched] = useState({});
  const [errors, setErrors] = useState({});
  const [submitError, setSubmitError] = useState("");

  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));
    validateField(name, form[name]);
  };

  const validateField = (name, value) => {
    let error = "";
    if (!value || (typeof value === "string" && value.trim() === "")) {
      error = `${name.replace(/([A-Z])/g, " $1")} is required`;
    }
    if (name === "email" && value) {
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(value)) error = "Invalid email format";
    }
    setErrors(prev => ({ ...prev, [name]: error }));
    return error;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (selected, field) => {
    const value = Array.isArray(selected) ? selected : selected?.value || "";
    setForm(prev => ({ ...prev, [field]: value }));
    setTouched(prev => ({ ...prev, [field]: true }));
    validateField(field, value);
  };

  // --- NEW HANDLERS ---
  const handleSendOtp = async () => {
    const emailError = validateField("email", form.email);
    if (emailError) return;

    setIsSendingOtp(true);
    try {
      // Adjust endpoint as per your backend
      await axios.post("http://localhost:8080/auth/student/send-otp", { email: form.email });
      setShowOtpField(true);
      setSubmitError("");
    } catch (err) {
      setSubmitError(err.response?.data?.message || "Failed to send OTP.");
    } finally {
      setIsSendingOtp(false);
    }
  };

  const handleVerifyOtp = async () => {
    try {
      const res = await axios.post("http://localhost:8080/auth/student/verify-otp", { 
        email: form.email, 
        otp 
      });
      if (res.status === 200) {
        setIsEmailVerified(true);
        setShowOtpField(false);
        setSubmitError("");

        localStorage.setItem("role", student.role);
        localStorage.setItem("userId", student.id);
        localStorage.setItem("user", JSON.stringify(student));

      }
    } catch (err) {
      setErrors(prev => ({ ...prev, otp: "Invalid OTP" }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!isEmailVerified) {
      setSubmitError("Please verify your email first.");
      return;
    }

    const requiredFields = ["email", "password", "name", "rolesLookingFor"];
    let hasError = false;

    requiredFields.forEach((field) => {
      const value = form[field];
      if (validateField(field, value)) hasError = true;
      if (!value || (Array.isArray(value) && value.length === 0)) hasError = true;
    });

    if (hasError) return;

    try {
      const payload = {
        ...form,
        skills: form.skills.map(s => s.value),
        rolesLookingFor: form.rolesLookingFor.map(r => r.value)
      };

      const res = await axios.post("http://localhost:8080/auth/student/register", payload, {withCredentials: true});

      if (res.status === 200) {
        localStorage.setItem("role", student.role);
        localStorage.setItem("userId", student.id);
        localStorage.setItem("user", JSON.stringify(student));
        navigate("/student/dashboard"); // Or wherever you'd like
        
      }
    } catch (err) {
      setSubmitError(err.response?.data?.message || "Registration failed.");
    }
  };

  return (
    <form onSubmit={handleSubmit} style={{ width: "100%", margin: "auto" }}>
      <h3 className="text-center mb-4">Register here</h3>
      {submitError && <div className="alert alert-danger">{submitError}</div>}

      {/* EMAIL FIELD */}
      <div className="mb-3">
        <label className="form-label">Email <span className="text-danger">*</span></label>
        <div className="position-relative d-flex align-items-center">
          <input
            type="email"
            name="email"
            className={`form-control custom-input-inner ${touched.email && errors.email ? "is-invalid" : ""}`}
            placeholder="example@college.edu"
            value={form.email}
            onChange={handleChange}
            onBlur={handleBlur}
            disabled={isEmailVerified}
            style={{ paddingRight: isEmailVerified ? "100px" : "80px" }} // Make room for the button/badge
          />
          
          <div className="inner-input-action">
            {!isEmailVerified ? (
              <button 
                className="btn-verify-link" 
                type="button" 
                onClick={handleSendOtp}
                disabled={isSendingOtp || !form.email}
              >
                {isSendingOtp ? "..." : "Verify"}
              </button>
            ) : (
              <span className="badge-verified">
                <i className="bi bi-check-circle-fill me-1"></i> Verified
              </span>
            )}
          </div>
        </div>
        {touched.email && errors.email && <div className="text-danger small mt-1">{errors.email}</div>}
      </div>

      {/* OTP FIELD (Made more compact) */}
      {showOtpField && !isEmailVerified && (
        <div className="mb-3 fade-in otp-container">
          <div className="d-flex gap-2">
            <input
              type="text"
              className="form-control text-center"
              placeholder="Enter 6-digit OTP"
              style={{ letterSpacing: "4px" }}
              value={otp}
              onChange={(e) => setOtp(e.target.value)}
            />
            <button className="btn btn-primary px-4" type="button" onClick={handleVerifyOtp}>
              Confirm
            </button>
          </div>
          {errors.otp && <div className="text-danger small mt-1">{errors.otp}</div>}
        </div>
      )}

      {/* OTHER FIELDS (Password & Name) */}
      {["password", "name"].map((field) => (
        <div className="mb-3" key={field}>
          <label className="form-label">
            {field.charAt(0).toUpperCase() + field.slice(1)} <span className="text-danger">*</span>
          </label>
          <input
            type={field === "password" ? "password" : "text"}
            name={field}
            className={`form-control ${touched[field] && errors[field] ? "is-invalid" : ""}`}
            value={form[field]}
            onChange={handleChange}
            onBlur={handleBlur}
          />
          {touched[field] && errors[field] && <div className="invalid-feedback">{errors[field]}</div>}
        </div>
      ))}

      <CollegeDropdown form={form} handleSelectChange={handleSelectChange} />

      <div className="mb-3">
        <label className="form-label">Skills</label>
        <Select
          isMulti
          options={skillsList.map(skill => ({ label: skill, value: skill }))}
          value={form.skills}
          onChange={(selected) => handleSelectChange(selected, "skills")}
        />
      </div>

      <div className="mb-3">
        <label className="form-label">Roles Looking For <span className="text-danger">*</span></label>
        <Select
          isMulti
          options={rolesList.map(role => ({ label: role, value: role }))}
          value={form.rolesLookingFor}
          onChange={(selected) => handleSelectChange(selected, "rolesLookingFor")}
        />
        {touched.rolesLookingFor && errors.rolesLookingFor && (
          <div className="text-danger small mt-1">{errors.rolesLookingFor}</div>
        )}
      </div>

      <div className="d-grid gap-2 mt-4">
        <button 
          type="submit" 
          className="btn btn-register-submit py-3 shadow-sm" 
          disabled={!isEmailVerified}
        >
          {isEmailVerified ? "Complete Registration" : "Please Verify Email"}
        </button>
      </div>

      <div className="text-center mt-3">
        <p className="small text-muted">
          Already have an account?{" "}
          <span className="auth-toggle-link fw-bold" onClick={onSwitch} style={{ cursor: "pointer" }}>Login</span>
        </p>
      </div>
    </form>
  );
};

export default StudentRegisterForm;