import React, { useEffect, useState, useCallback } from "react";
import axios from "axios";
import Cookies from "js-cookie";
import ProjectForm from "./ProjectForm";
import ProjectList from "./ProjectList";

const API_BASE = "http://localhost:8080/admin/projects";
const axiosConfig = {
  withCredentials: true,
  headers: {
    "Content-Type": "application/json"
  }
};

const ProjectsTab = () => {
  const [projects, setProjects] = useState([]);
  const [editingProject, setEditingProject] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchProjects = useCallback(async () => {
    setLoading(true);
    setError("");

    try {
      const res = await axios.get(`${API_BASE}/list`, axiosConfig);
      console.log(res)
      if (res.status === 200 && Array.isArray(res.data)) {
        setProjects(res.data);
      } else if (res.status === 401 && message === "TOKEN_MISSING") {
        window.location.href = "/login";
      }
      else {
        throw new Error("Unexpected response format");
      }
    } catch (err) {
      console.error("Failed to fetch projects", err);
      setError(
        err.response?.data?.message || "Unable to load projects. Please try again."
      );
      setProjects([]);
    } finally {
      setLoading(false);
    }
  }, []);


  useEffect(() => {
    fetchProjects();
  }, [fetchProjects]);


  const handleCreate = async (projectData) => {
    setError("");
    try {
      const formData = new FormData();
      formData.append("file", projectData.file); // PDF
      formData.append("title", projectData.title);
      formData.append("description", projectData.description);
      formData.append("difficulty", projectData.difficulty);
      formData.append("duration_days", projectData.duration_days);
      formData.append("review_mode", projectData.review_mode);
      projectData.skills_associated.forEach(s => formData.append("skills_associated", s));
      projectData.roles_associated.forEach(r => formData.append("roles_associated", r));

      const res = await axios.post(`${API_BASE}/create-with-file`, formData, {
        withCredentials: true,
      });

      if (res.status === 201 || res.status === 200) {
        setProjects(prev => [...prev, res.data]);
        setEditingProject(null);
      } else {
        throw new Error("Unexpected response format");
      }
    } catch (err) {
      console.error("Project creation failed", err);
      setError(err.response?.data?.message || "Failed to create project.");
    }
  };


  const handleUpdate = async (updatedProject) => {
    setError("");

    try {
      // const res = await axios.put(`${API_BASE}/update`, updatedProject, axiosConfig);
      const res = await axios.put( `${API_BASE}/update/${updatedProject.id}`, updatedProject, axiosConfig );
      if (res.status === 200 || res.status === 204) {
        setProjects(prev =>
          prev.map(p => (p.id === updatedProject.id ? res.data : p))
        );
        setEditingProject(null);
      } else if (res.status === 401 && message === "TOKEN_MISSING") {
        window.location.href = "/login";
      } else {
        throw new Error("Unexpected response format");
      }
    } catch (err) {
      console.error("Update failed", err);
      setError(
        err.response?.data?.message || "Failed to update project."
      );
    }
  };


  const handleDelete = async (id) => {
    setError("");

    try {
      // const res = await axios.delete(`${API_BASE}/delete`, { id }, axiosConfig);
      const res = await axios.delete(`${API_BASE}/delete/${id}`, axiosConfig); // ✅ ID in path
      if (res.status === 200 || res.status === 204) {
        setProjects(prev => prev.filter(p => p.id !== id));
      } 
      else if (res.status === 401 && message === "TOKEN_MISSING") {
        window.location.href = "/login";
      } else {
        throw new Error("Unexpected response format");
      }
    } catch (err) {
      console.error("Delete failed", err);
      setError(
        err.response?.data?.message || "Failed to delete project."
      );
    }
  };



  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h5 className="mb-0">Projects</h5>
        <button className="btn btn-success" onClick={() => setEditingProject({})}>
          + Create Project
        </button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}
      {loading && <div className="text-muted">Loading projects...</div>}

      {editingProject && (
        <ProjectForm
          project={editingProject}
          onSave={editingProject.id ? handleUpdate : handleCreate}
          onCancel={() => setEditingProject(null)}
        />
      )}

      <ProjectList
        projects={projects}
        onEdit={setEditingProject}
        onDelete={handleDelete}
      />
    </div>
  );
};

export default ProjectsTab;
