import React from "react";
import ProjectCard from "./ProjectCard";

const DifficultyColumn = ({ title, difficulty, projects, onRefresh, studentId }) => {
  const filtered = projects.filter(p => p.difficulty === difficulty);

  const headerColors = {
    EASY: "#28a745",
    MEDIUM: "#fd7e14",
    HARD: "#dc3545"
  };

  return (
    <div className="col-md-4 h-100"> {/* Added h-100 */}
      <div className="d-flex align-items-center gap-2 mb-3 bg-light p-2 rounded-3">
        <div style={{ width: "8px", height: "8px", borderRadius: "50%", backgroundColor: headerColors[difficulty] }}></div>
        <h6 className="fw-bold m-0 text-dark">
          {title} <span className="text-muted fw-normal ms-1">({filtered.length})</span>
        </h6>
      </div>

      {/* The independent scrollable area */}
      <div className="difficulty-scroll-track d-flex flex-column gap-3">
        {filtered.length > 0 ? (
          filtered.map(project => (
            <ProjectCard
              key={project.id}
              project={project}
              studentId={studentId}
              onRefresh={onRefresh}
            />
          ))
        ) : (
          <div className="text-center py-5 border rounded-4 border-dashed bg-white">
            <p className="text-muted small">No projects available.</p>
          </div>
        )}
      </div>
    </div>
  );
};

export default DifficultyColumn;
