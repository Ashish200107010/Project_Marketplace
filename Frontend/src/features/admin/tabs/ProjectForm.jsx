import React, { useState, useEffect } from "react";
import Select from "react-select";
import { skillsList, rolesList } from "../../../constants/projectOptions";

const ProjectForm = ({ project, onSave, onCancel }) => {
  const [form, setForm] = useState({
    title: "",
    description: "",
    difficulty: "MEDIUM",
    duration_days: 30,
    review_mode: "AI",
    skills_associated: [],
    roles_associated: [],
  });

  const [touched, setTouched] = useState({});
  const [errors, setErrors] = useState({});
  const [file, setFile] = useState(null); // ✅ new state for requirement PDF


  const handleBlur = (e) => {
    const { name } = e.target;
    setTouched(prev => ({ ...prev, [name]: true }));

    // Validate on blur
    validateField(name, form[name]);
  };
  
  const validateField = (name, value) => {
    let error = "";

    if (!value || (typeof value === "string" && value.trim() === "")) {
        error = `${name.replace(/_/g, " ")} is required`;
    }

    if (name === "duration_days" && value < 1) {
        error = "Duration must be at least 1 day";
    }

    setErrors(prev => ({ ...prev, [name]: error }));
    };


  useEffect(() => {
    if (project) {
      setForm({
        ...project,
        review_mode: project.review_mode || "AI",
        skills_associated: Array.isArray(project.skills_associated)
          ? project.skills_associated.map(skill => ({ label: skill, value: skill }))
          : [],
        roles_associated: Array.isArray(project.roles_associated)
          ? project.roles_associated.map(role => ({ label: role, value: role }))
          : [],
      });
    }
  }, [project]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({ ...prev, [name]: value }));
  };

  const handleSelectChange = (selected, field) => {
    setForm(prev => ({ ...prev, [field]: selected }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    // Validate all required fields
    if (!form.title.trim()) {
        alert("Project title is required.");
        return;
    }

    if (!form.description.trim()) {
        alert("Project description is required.");
        return;
    }

    if (!file) {
      alert("Please upload a requirement PDF.");
      return;
    }


    if (!form.difficulty) {
        alert("Please select a difficulty level.");
        return;
    }

    if (!form.review_mode) {
        alert("Please select a review mode.");
        return;
    }

    if (!form.duration_days || form.duration_days < 1) {
        alert("Duration must be at least 1 day.");
        return;
    }

    if (form.skills_associated.length === 0) {
        alert("Please select at least one skill.");
        return;
    }

    if (form.roles_associated.length === 0) {
        alert("Please select at least one role.");
        return;
    }

    // Prepare payload
    const payload = {
        ...form,
        skills_associated: form.skills_associated.map(s => s.value),
        roles_associated: form.roles_associated.map(r => r.value),
    };

    console.log("Validated payload:", payload);
    onSave({
      ...payload,
      file
    });

  };


  return (
    <form onSubmit={handleSubmit} className="card p-4 mb-4 shadow-sm">
  {/* Title */}
  <div className="mb-3">
    <label className="form-label">Project Title <span className="text-danger">*</span></label>
    <input
      type="text"
      name="title"
      className={`form-control ${touched.title && errors.title ? "is-invalid" : ""}`}
      value={form.title}
      onChange={handleChange}
      onBlur={handleBlur}
    />
    {touched.title && errors.title && (
      <div className="invalid-feedback">{errors.title}</div>
    )}
  </div>

  {/* Description */}
  <div className="mb-3">
    <label className="form-label">Project Description <span className="text-danger">*</span></label>
    <textarea
      name="description"
      className={`form-control ${touched.description && errors.description ? "is-invalid" : ""}`}
      value={form.description}
      onChange={handleChange}
      onBlur={handleBlur}
      rows={3}
    />
    {touched.description && errors.description && (
      <div className="invalid-feedback">{errors.description}</div>
    )}
  </div>

  {/* Description PDF */}
  <div className="mb-3">
    <label className="form-label">Description PDF <span className="text-danger">*</span></label>
    <input
      type="file"
      accept="application/pdf"
      className="form-control"
      onChange={(e) => setFile(e.target.files[0])}
    />
  </div>


  {/* Difficulty */}
  <div className="mb-3">
    <label className="form-label">Difficulty <span className="text-danger">*</span></label>
    <select
      name="difficulty"
      className={`form-select ${touched.difficulty && errors.difficulty ? "is-invalid" : ""}`}
      value={form.difficulty}
      onChange={handleChange}
      onBlur={handleBlur}
    >
      <option value="">Select difficulty</option>
      <option value="EASY">Easy</option>
      <option value="MEDIUM">Medium</option>
      <option value="HARD">Hard</option>
    </select>
    {touched.difficulty && errors.difficulty && (
      <div className="invalid-feedback">{errors.difficulty}</div>
    )}
  </div>

  {/* Duration */}
  <div className="mb-3">
    <label className="form-label">Duration (days) <span className="text-danger">*</span></label>
    <input
      type="number"
      name="duration_days"
      className={`form-control ${touched.duration_days && errors.duration_days ? "is-invalid" : ""}`}
      value={form.duration_days}
      onChange={handleChange}
      onBlur={handleBlur}
      min={1}
    />
    {touched.duration_days && errors.duration_days && (
      <div className="invalid-feedback">{errors.duration_days}</div>
    )}
  </div>

  {/* Review Mode */}
  <div className="mb-3">
    <label className="form-label">Review Mode <span className="text-danger">*</span></label>
    <select
      name="review_mode"
      className={`form-select ${touched.review_mode && errors.review_mode ? "is-invalid" : ""}`}
      value={form.review_mode}
      onChange={handleChange}
      onBlur={handleBlur}
    >
      <option value="">Select review mode</option>
      <option value="AI">AI</option>
      <option value="MANUAL">Manual</option>
    </select>
    {touched.review_mode && errors.review_mode && (
      <div className="invalid-feedback">{errors.review_mode}</div>
    )}
  </div>

  {/* Skills */}
  <div className="mb-3">
    <label className="form-label">Skills Required <span className="text-danger">*</span></label>
    <Select
      isMulti
      options={skillsList.map(skill => ({ label: skill, value: skill }))}
      value={form.skills_associated}
      onChange={(selected) => handleSelectChange(selected, "skills_associated")}
      onBlur={() => setTouched(prev => ({ ...prev, skills_associated: true }))}
      className={touched.skills_associated && errors.skills_associated ? "is-invalid" : ""}
    />
    {touched.skills_associated && errors.skills_associated && (
      <div className="invalid-feedback d-block">{errors.skills_associated}</div>
    )}
  </div>

  {/* Roles */}
  <div className="mb-3">
    <label className="form-label">Target Roles <span className="text-danger">*</span></label>
    <Select
      isMulti
      options={rolesList.map(role => ({ label: role, value: role }))}
      value={form.roles_associated}
      onChange={(selected) => handleSelectChange(selected, "roles_associated")}
      onBlur={() => setTouched(prev => ({ ...prev, roles_associated: true }))}
      className={touched.roles_associated && errors.roles_associated ? "is-invalid" : ""}
    />
    {touched.roles_associated && errors.roles_associated && (
      <div className="invalid-feedback d-block">{errors.roles_associated}</div>
    )}
  </div>

  {/* Buttons */}
  <div className="d-flex gap-2">
    <button type="submit" className="btn btn-primary">
      {project?.id ? "Update Project" : "Create Project"}
    </button>
    <button type="button" className="btn btn-secondary" onClick={onCancel}>
      Cancel
    </button>
  </div>
</form>

    );
};

export default ProjectForm;
