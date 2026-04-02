import React, { useState } from "react";
import { enrollProject, unenrollProject } from "../utils/api";
import { useProjects } from "../../../context/ProjectContext";  

const ProjectCard = ({ project, studentId, onRefresh }) => {
  const [isEnrolled, setIsEnrolled] = useState(project.isEnrolled);
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [startDate, setStartDate] = useState("");
  const { invalidateProjects } = useProjects();

  const handleEnrollClick = (e) => {
    e.stopPropagation();
    setShowDatePicker(true);
  };

  const handleConfirmEnroll = async () => {
    if (!startDate) {
      alert("Please select a valid start date.");
      return;
    }

    const today = new Date();
    const highestPossibleDate = new Date();
    highestPossibleDate.setMonth(highestPossibleDate.getMonth() + 1);
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(today.getMonth() - 6);

    const chosen = new Date(startDate);

    if (chosen > highestPossibleDate) {
      alert("Start date cannot be in the future.");
      return;
    }
    if (chosen < sixMonthsAgo) {
      alert("Start date cannot be older than 6 months.");
      return;
    }

    try {
      await enrollProject(studentId, project.id, startDate);
      setIsEnrolled(true);
      invalidateProjects("enrolled");
      setShowDatePicker(false);
      onRefresh?.();
    } catch (err) {
      alert("Enrollment failed");
    }
  };

  const handleUnenroll = async (e) => {
    e.stopPropagation();
    const confirmed = window.confirm(
      "Are you sure you want to unenroll?\n\nYou will lose access to submissions and certificates."
    );

    if (!confirmed) return;

    try {
      const result = await unenrollProject(studentId, project.id);
      alert(result);
      setIsEnrolled(false);
      invalidateProjects("enrolled");
      onRefresh?.();
    } catch (err) {
      alert("Unenrollment failed");
    }
  };

  const openInNewTab = () => {
    if (project.id) {
      window.open(`/projects/${project.id}`, "_blank");
    }
  };

  return (
    <div
      className="project-card border rounded-4 p-3 bg-white shadow-sm hover-shadow transition-all"
      style={{ cursor: "pointer", position: "relative" }}
      onClick={openInNewTab}
    >
      <div className="d-flex justify-content-between align-items-start mb-2">
        <h6 className="fw-bold text-dark mb-0 pe-3" style={{ fontSize: "1.05rem" }}>
          {project.title}
        </h6>
        {project.reviewMode && (
          <span className="badge-soft text-uppercase" style={{ fontSize: '0.65rem' }}>
            {project.reviewMode}
          </span>
        )}
      </div>

      <p className="text-muted small mb-3 line-clamp-2">
        {project.description}
      </p>

      <div className="mb-3 d-flex flex-wrap gap-1">
        {project.skillsAssociated?.slice(0, 3).map((skill, idx) => (
          <span key={idx} className="skill-tag">{skill}</span>
        ))}
        {project.skillsAssociated?.length > 3 && (
          <span className="skill-tag">+{project.skillsAssociated.length - 3}</span>
        )}
      </div>

      <div className="d-flex justify-content-between align-items-center mt-auto pt-2 border-top">
        <div className="small text-secondary">
          <span className="me-2">🕒 {project.duration_days || "14"} Days</span>
        </div>

        {isEnrolled ? (
          <button className="btn-unenroll" onClick={handleUnenroll}>
            Unenroll
          </button>
        ) : (
          <button className="btn-enroll" onClick={handleEnrollClick}>
            Start Now
          </button>
        )}
      </div>

      {/* Date Picker Overlay */}
      {showDatePicker && (
        <div 
          className="date-picker-overlay animate-fade-in"
          onClick={(e) => e.stopPropagation()}
        >
          <label className="small fw-bold text-dark mb-1">Select Start Date</label>
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            className="form-control form-control-sm mb-2 shadow-none"
          />
          <div className="d-flex gap-2">
            <button className="btn btn-sm btn-green flex-grow-1" onClick={handleConfirmEnroll}>Confirm</button>
            <button className="btn btn-sm btn-light border flex-grow-1" onClick={() => setShowDatePicker(false)}>Cancel</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProjectCard;