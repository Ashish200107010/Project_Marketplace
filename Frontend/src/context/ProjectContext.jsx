import React, { createContext, useState, useContext } from "react";

const ProjectContext = createContext();

export const ProjectProvider = ({ children }) => {
  const [projectsCache, setProjectsCache] = useState({
    relevant: { data: [], lastFetched: null, isDirty: true },
    available: { data: [], lastFetched: null, isDirty: true },
    enrolled: { data: [], lastFetched: null, isDirty: true },
    completed: { data: [], lastFetched: null, isDirty: true }
  });

  const setProjects = (type, data) => {
    setProjectsCache(prev => ({
      ...prev,
      [type]: { data, lastFetched: Date.now(), isDirty: false }
    }));
  };

  const getProjects = (type) => projectsCache[type]?.data || [];
  const getLastFetched = (type) => projectsCache[type]?.lastFetched;
  const isDirty = (type) => projectsCache[type]?.isDirty;

  const invalidateProjects = (type) => {
    setProjectsCache(prev => ({
      ...prev,
      [type]: { ...prev[type], isDirty: true }
    }));
  };

  return (
    <ProjectContext.Provider
      value={{ setProjects, getProjects, getLastFetched, isDirty, invalidateProjects }}
    >
      {children}
    </ProjectContext.Provider>
  );
};

export const useProjects = () => useContext(ProjectContext);
