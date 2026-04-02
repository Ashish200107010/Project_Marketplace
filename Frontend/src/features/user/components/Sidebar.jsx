import React from "react";

const sections = [
  { key: "relevant", label: "Relevant Work" },
  { key: "available", label: "Available Work" },
  { key: "enrolled", label: "Enrolled Work" },
  { key: "completed", label: "Completed Work" },
];

const Sidebar = ({ active, onSelect }) => (
  <div className="bg-white border-end p-4" style={{ width: "240px", minWidth: "240px" }}>
    <div className="mb-5 px-2">
      <h6 className="text-uppercase text-muted fw-bold small" style={{ letterSpacing: "1px" }}>
        My Workspace
      </h6>
    </div>
    
    <nav className="nav flex-column gap-2">
      {sections.map(({ key, label }) => (
        <button
          key={key}
          onClick={() => onSelect(key)}
          className={`btn border-0 text-start px-3 py-2 rounded-3 transition-all ${
            active === key 
              ? "bg-green text-white shadow-sm" 
              : "text-secondary hover-bg-light"
          }`}
          style={{
            fontWeight: active === key ? "600" : "500",
            backgroundColor: active === key ? "" : "transparent"
          }}
        >
          {label}
        </button>
      ))}
    </nav>
  </div>
);

export default Sidebar;