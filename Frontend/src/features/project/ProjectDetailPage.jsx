import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchProjects, enrollProject, unenrollProject } from "../user/utils/api";
import { BsDownload, BsGithub, BsCalendar3, BsClock, BsAward } from "react-icons/bs";
import "../user/StudentDashboard"; // Ensure theme classes are available
// import { useProjects } from "../../../context/ProjectContext";  

const ProjectDetailPage = () => {
  const { id } = useParams();
  const [project, setProject] = useState(null);
  const [isEnrolled, setIsEnrolled] = useState(false);
  const [gitLink, setGitLink] = useState("");
  const [showDatePicker, setShowDatePicker] = useState(false);
  const [startDate, setStartDate] = useState("");
  // const { invalidateProjects } = useProjects();
  

  useEffect(() => {
    const loadProject = async () => {
      const all = await fetchProjects("available");
      const found = all.find((p) => String(p.id) === id);
      setProject(found);

      const enrolled = await fetchProjects("enrolled");
      const enrolledIds = new Set(enrolled.map((p) => String(p.id)));
      setIsEnrolled(enrolledIds.has(id));
    };
    loadProject();
  }, [id]);

  const handleEnrollClick = () => {
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
    if (chosen > highestPossibleDate || chosen < sixMonthsAgo) {
      alert("Select valid start date");
      return;
    }
    try {
      const storedUser = localStorage.getItem("user");
      const studentId = storedUser ? JSON.parse(storedUser).id : null;
      await enrollProject(studentId, project.id, startDate);
      setIsEnrolled(true);
      // invalidateProjects("enrolled");
      setShowDatePicker(false);
      alert("Enrolled successfully");
    } catch (err) {
      alert("Enrollment failed");
    }
  };

  const handleUnenroll = async (e) => {
    e.stopPropagation();
    const confirmed = window.confirm(
      "Are you sure you want to unenroll from this project?\n\nUnenrolling means you will lose access to submissions, reviews, and certificates."
    );
    if (!confirmed) return;
    try {
      const storedUser = localStorage.getItem("user");
      const studentId = storedUser ? JSON.parse(storedUser).id : null;
      await unenrollProject(studentId, project.id);
      setIsEnrolled(false);
      // invalidateProjects("enrolled");
      alert("Unenrolled successfully");
    } catch (err) {
      alert("Unenrollment failed");
    }
  };

  const handleGitSubmit = async () => {
    const studentId = localStorage.getItem("userId");
    if (!studentId) {
      alert("Student ID not found. Please log in again.");
      return;
    }
    if (!gitLink || !project?.id) {
      alert("Missing GitHub link or project ID.");
      return;
    }
    const payload = { projectId: project.id, studentId, gitLink };
    try {
      const response = await fetch("http://localhost:8080/api/student/projects/submit", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
        credentials: "include",
      });
      if (!response.ok) {
        alert("Submission failed. Please check your GitHub link.");
        return;
      }
      alert("GitHub link submitted successfully!");
    } catch (error) {
      alert("Network error. Please try again later.");
    }
  };

  const handleDownloadCertificate = async () => {
    try {
      const storedUser = localStorage.getItem("user");
      const studentId = storedUser ? JSON.parse(storedUser).id : null;
      const res = await fetch("http://localhost:8080/api/student/projects/downloadCertificate", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ studentId, projectId: project.id }),
        credentials: "include",
      });
      if (!res.ok) throw new Error("Failed");
      const data = await res.json();
      const url = data.certificateUrl || data.certificatePathKey;
      window.open(url, "_blank");
    } catch (err) {
      alert("Failed to download certificate");
    }
  };

  const handleDownloadRequirement = async () => {
    try {
      const res = await fetch("http://localhost:8080/api/student/projects/files/access", {
        method: "POST",
        credentials: "include",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ projectId: id }),
      });
      if (!res.ok) throw new Error("Failed");
      const data = await res.json();
      window.open(data.accessUrl, "_blank");
    } catch (err) {
      alert("Failed to download requirement PDF");
    }
  };

  if (!project) return (
    <div className="d-flex justify-content-center align-items-center vh-100">
      <div className="spinner-border text-green" role="status"></div>
    </div>
  );

  return (
    <div className="container py-5">
      <div className="row g-5">
        {/* LEFT COLUMN: Content */}
        <div className="col-lg-8">
          <div className="d-flex align-items-center gap-2 mb-2">
            <span className="badge-soft text-uppercase small px-3">{project.difficulty}</span>
            <span className="text-muted small">•</span>
            <span className="text-muted small"><BsClock className="me-1"/> {project.duration_days} Days</span>
          </div>
          
          <h1 className="fw-bold text-dark mb-4" style={{ fontSize: '2.5rem' }}>{project.title}</h1>
          
          <div className="mb-5">
            <h5 className="fw-bold text-green mb-3">Work Description</h5>
            <p className="text-secondary leading-relaxed" style={{ fontSize: '1.1rem', lineHeight: '1.8' }}>
              {project.description}
            </p>
          </div>

          <div className="mb-5">
            <h5 className="fw-bold text-green mb-3">Steps for Completion</h5>
            <ol className="text-secondary" style={{ fontSize: '1.1rem', lineHeight: '1.8' }}>
              <li>Download the project requirements.</li>
              <li>Review and understand the problem statement thoroughly.</li>
              <li>Develop the solution using the specified technologies.</li>
              <li>Upload the completed project to GitHub and ensure the repository is set to public.</li>
              <li>Submit the link to the public GitHub repository in the submission field on the right side.</li>
            </ol>
          </div>

          <div className="row mb-5 g-4">
            <div className="col-md-6">
              <h6 className="fw-bold mb-3">Skills Involved</h6>
              <div className="d-flex flex-wrap gap-2">
                {project.skills_associated?.map((skill, i) => (
                  <span key={i} className="skill-tag px-3 py-2">{skill}</span>
                ))}
              </div>
            </div>
            <div className="col-md-6">
              <h6 className="fw-bold mb-3">Target Roles</h6>
              <div className="d-flex flex-wrap gap-2">
                {project.roles_associated?.map((role, i) => (
                  <span key={i} className="skill-tag px-3 py-2 bg-light border-0">{role}</span>
                ))}
              </div>
            </div>
          </div>

          {project.mentorReview && (
            <div className="alert border-0 rounded-4 p-4 mb-4" style={{ background: '#f0f9ff' }}>
              <h6 className="fw-bold text-primary d-flex align-items-center gap-2">
                <BsAward /> Mentor Review
              </h6>
              <p className="mb-0 text-dark small">{project.mentorReview}</p>
            </div>
          )}

          {project.reviewScheduledAt && (
            <div className="p-3 bg-light rounded-3 d-inline-flex align-items-center gap-2 text-info small mb-4">
              <BsCalendar3 /> Review Scheduled: {new Date(project.reviewScheduledAt).toLocaleString()}
            </div>
          )}
        </div>

        {/* RIGHT COLUMN: Action Sidebar */}
        <div className="col-lg-4">
          <div className="sticky-top" style={{ top: '2rem' }}>
            {/* Enrollment Card */}
            <div className="card border rounded-4 shadow-sm p-4 mb-4">
              <h6 className="fw-bold mb-3">Work Assets</h6>
              <button 
                onClick={handleDownloadRequirement}
                className="btn btn-light border w-100 py-2 d-flex align-items-center justify-content-center gap-2 mb-3 rounded-3"
              >
                <BsDownload /> Download Work Requirement
              </button>

              <hr />

              {isEnrolled ? (
                <div>
                  <label className="small fw-bold text-muted mb-2">Submission Link</label>
                  <div className="input-group mb-3">
                    <span className="input-group-text bg-white border-end-0"><BsGithub /></span>
                    <input
                      type="url"
                      className="form-control border-start-0 ps-0 shadow-none"
                      value={gitLink}
                      onChange={(e) => setGitLink(e.target.value)}
                      placeholder="Github Repo URL"
                    />
                  </div>
                  <button className="btn-green w-100 py-2 mb-3" onClick={handleGitSubmit}>
                    Submit Work
                  </button>
                  
                  {project.certificateUrl || (project.reviewStatus === "COMPLETED") ? (
                    <button className="btn btn-warning w-100 py-2 rounded-3 fw-bold" onClick={handleDownloadCertificate}>
                      🎓 Download Certificate
                    </button>
                  ) : project.reviewStatus === "FAILED" ? (
                    <button className="btn btn-danger w-100 py-2 rounded-3 fw-bold">Reupload Work</button>
                  ) : null}

                  <button className="btn btn-link text-danger w-100 mt-3 btn-sm text-decoration-none" onClick={handleUnenroll}>
                    Unenroll from Work
                  </button>
                </div>
              ) : (
                <div>
                  {!showDatePicker ? (
                    <button className="btn-enroll w-100 py-3 fs-5" onClick={handleEnrollClick}>
                      Start Now
                    </button>
                  ) : (
                    <div className="animate-fade-in p-3 bg-light rounded-3">
                      <label className="small fw-bold mb-2">Choose Start Date</label>
                      <input
                        type="date"
                        className="form-control mb-2"
                        value={startDate}
                        onChange={(e) => setStartDate(e.target.value)}
                        min={new Date(new Date().setMonth(new Date().getMonth() - 6)).toISOString().split("T")[0]}
                        max={new Date(new Date().setMonth(new Date().getMonth() + 1)).toISOString().split("T")[0]}
                      />
                      <button className="btn-green w-100 mb-2" onClick={handleConfirmEnroll}>Confirm Enrollment</button>
                      <button className="btn btn-sm btn-light w-100" onClick={() => setShowDatePicker(false)}>Cancel</button>
                    </div>
                  )}
                  <p className="text-center text-muted small mt-3">Enroll to unlock project files and submission portal.</p>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProjectDetailPage;

