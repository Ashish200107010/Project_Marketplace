import React, { useState } from "react";

const ProjectForm = ({ onCreate }) => {
  const [name, setName] = useState("");

  const handleSubmit = (e) => {
    e.preventDefault();
    const newProject = {
      id: Date.now(),
      name,
      status: "New",
      createdAt: new Date().toISOString().split("T")[0],
    };
    onCreate(newProject);
    setName("");
  };

  return (
    <form onSubmit={handleSubmit} className="mb-4">
      <div className="input-group">
        <input
          type="text"
          className="form-control"
          placeholder="Project name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <button className="btn btn-success" type="submit">Create</button>
      </div>
    </form>
  );
};

export default ProjectForm;
