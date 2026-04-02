import React, { useState } from "react";
import EarningTab from "./tabs/EarningTab";
import UsersTab from "./tabs/UsersTab";
import CertificatesTab from "./tabs/CertificatesTab";
import ProjectsTab from "./tabs/ProjectsTab";

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState("earnings");

  const renderTab = () => {
    switch (activeTab) {
      case "earnings": return <EarningTab />;
      case "users": return <UsersTab />;
      case "certificates": return <CertificatesTab />;
      case "projects": return <ProjectsTab />;
      default: return null;
    }
  };

  return (
    <div className="container py-4">
      <h2 className="mb-4">Admin Dashboard</h2>

      <ul className="nav nav-tabs mb-4">
        {["earnings", "users", "certificates", "projects", "load"].map(tab => (
          <li className="nav-item" key={tab}>
            <button
              className={`nav-link ${activeTab === tab ? "active" : ""}`}
              onClick={() => setActiveTab(tab)}
            >
              {tab.charAt(0).toUpperCase() + tab.slice(1)}
            </button>
          </li>
        ))}
      </ul>

      {renderTab()}
    </div>
  );
};

export default AdminDashboard;
