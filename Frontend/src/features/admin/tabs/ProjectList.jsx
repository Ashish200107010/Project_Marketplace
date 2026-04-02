import React from "react";

const ProjectList = ({ projects, onEdit, onDelete }) => {
  return (
    <table className="table table-bordered">
      <thead>
        <tr>
          <th>Title</th>
          <th>Difficulty</th>
          <th>Review Mode</th>
          <th>Duration</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {projects.map((project) => (
          <tr key={project.id}>
            <td>{project.title}</td>
            <td>{project.difficulty}</td>
            <td>{project.reviewMode}</td>
            <td>{project.durationDays} days</td>
            <td>
              <button className="btn btn-sm btn-warning me-2" onClick={() => onEdit(project)}>
                Edit
              </button>
              <button className="btn btn-sm btn-danger" onClick={() => onDelete(project.id)}>
                Delete
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default ProjectList;
