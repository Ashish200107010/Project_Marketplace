import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import axios from "axios";
import { rolesList, skillsList } from "../constants/projectOptions";
import Select from "react-select";
import { useProjects } from "../context/ProjectContext";

const Header = () => {
  const { user, logout, login} = useAuth();
  const { invalidateProjects } = useProjects();
  const navigate = useNavigate();
  const [isOpen, setIsOpen] = useState(false);

  //edit mode states 
  const [editingRoles, setEditingRoles] = useState(false);
  const [editingSkills, setEditingSkills] = useState(false);
  // State initialization
  const [selectedRoles, setSelectedRoles] = useState(
    user?.rolesLookingFor?.map(r => ({ label: r, value: r })) || []
  );

  const [selectedSkills, setSelectedSkills] = useState(
    user?.skills?.map(s => ({ label: s, value: s })) || []
  );


  useEffect(() => {
    if (user?.rolesLookingFor) {
      setSelectedRoles(user.rolesLookingFor.map(r => ({ label: r, value: r })));
    }
    if (user?.skills) {
      setSelectedSkills(user.skills.map(s => ({ label: s, value: s })));
    }
  }, [user]);


  const handleLogout = async () => {
    try {
      // Call backend to expire token
      const res = await axios.post("http://localhost:8080/auth/student/logout", {}, { withCredentials: true });

      if (res.status === 200) { 
        console.log("Logged out successfully");
      }
      // Atomic clear: context + localStorage
      logout();
      invalidateProjects("enrolled");
      invalidateProjects("relevant");
      invalidateProjects("available");
      invalidateProjects("completed");
      localStorage.removeItem("user");

      // Close offcanvas
      setIsOpen(false);

      // Redirect
      navigate("/student/login");
    } catch (err) {
      console.error("Logout failed", err);
      alert("Failed to logout. Please try again.");
    }
  };


  // Update roles
  const updateRoles = async (newRoles) => {
    try {
      const res = await axios.put(
        `http://localhost:8080/student/${user.id}/roles`,
        { roles: newRoles },
        { withCredentials: true }
      );

      if (res.status === 200) {
        const updatedUser = { ...user, rolesLookingFor: res.data.roles };

        // ✅ Try updating all three together
        try {
          // 1. Update context
          login(updatedUser);

          // 2. Update local state
          setSelectedRoles(res.data.roles.map(r => ({ label: r, value: r })));

          // 3. Update localStorage
          localStorage.setItem("user", JSON.stringify(updatedUser));
        } catch (innerErr) {
          console.error("Atomic update failed", innerErr);

          // ❌ Rollback: reset everything back to old user
          login(user);
          setSelectedRoles(user?.rolesLookingFor?.map(r => ({ label: r, value: r })) || []);
          localStorage.setItem("user", JSON.stringify(user));

          alert("Failed to update roles. Please try again.");
        }
      }
    } catch (err) {
      console.error("Failed to update roles", err);
      // ❌ API failed: reset back to old user
      setSelectedRoles(user?.rolesLookingFor?.map(r => ({ label: r, value: r })) || []);
      alert("Failed to update roles. Please try again.");
    }
  };


  // Update skills
  const updateSkills = async (newSkills) => {
    try {
      const res = await axios.put(
        `http://localhost:8080/student/${user.id}/skills`,
        { skills: newSkills },
        { withCredentials: true }
      );
      if (res.status === 200) {
        try{
          setSelectedSkills(res.data.skills.map(s => ({ label: s, value: s })));
          const updatedUser = { ...user, skills: res.data.skills };
          // ✅ Success: update context
          login(updatedUser);
          // ✅ also update localStorage
          localStorage.setItem("user", JSON.stringify(updatedUser));
        }
        catch(err){
            console.error("Failed to update skills", err);
        }
      } 
    }catch (err) {
      console.error("Failed to update skills", err);
      // ❌ Failure: reset back to user data
      setSelectedSkills(user?.skills?.map(s => ({ label: s, value: s })) || []);
      alert("Failed to update skills. Please try again.");
    }
  };



  return (
    <>
      <header className="d-flex justify-content-between align-items-center py-3 px-4 border-bottom bg-white shadow-sm sticky-top">
        <div className="d-flex align-items-center">
          <Link to="/" className="text-decoration-none text-dark d-flex align-items-center gap-2">
            <video
              src="/assets/logo/logo.mp4"
              autoPlay
              muted
              loop
              playsInline
              controls={false}
              disablePictureInPicture
              controlsList="nodownload nofullscreen noremoteplayback"
              style={{ height: "50px", width: "280px", borderRadius: "8px", objectFit: "cover" }}
            />
          </Link>
        </div>

        <nav className="d-flex align-items-center gap-4">
          <Link to="/about" className="text-decoration-none text-dark fw-medium">About</Link>
          <Link to="/service" className="text-decoration-none text-dark fw-medium">Service</Link>
          {user ? (
            <Link to="/student/dashboard" className="text-decoration-none text-dark fw-medium">Dashboard</Link>
          ) : (
            <a></a>
          )}
          {user ? (
            <div className="ms-2 border-start ps-4">
              <div 
                onClick={() => setIsOpen(true)}
                style={{
                  width: "24px",
                  cursor: "pointer",
                  display: "flex",
                  flexDirection: "column",
                  gap: "5px", // Adjust this to make the icon taller or shorter
                  padding: "5px 0" // Adds a bit of vertical hit-area
                }}
              >
                <div style={{ width: "100%", height: "3px", backgroundColor: "#333" }}></div>
                <div style={{ width: "100%", height: "3px", backgroundColor: "#333" }}></div>
                <div style={{ width: "100%", height: "3px", backgroundColor: "#333" }}></div>
              </div>
            </div>
          ) : (
            <Link to="/student/login" className="btn btn-success rounded-pill px-4 fw-bold shadow-sm">
              Sign In
            </Link>
          )}
        </nav>
      </header>

      {/* TASKPANE (OFFCANVAS) */}
      <div className={`offcanvas offcanvas-end ${isOpen ? "show" : ""}`} 
           style={{ visibility: isOpen ? "visible" : "hidden", transition: "0.3s", width: '350px' }} 
           tabIndex="-1">
        
        <div className="offcanvas-header border-bottom bg-light">
          <h5 className="offcanvas-title fw-bold">My Profile</h5>
          <button type="button" className="btn-close" onClick={() => setIsOpen(false)}></button>
        </div>
        
        <div className="offcanvas-body">
          {/* Header Info */}
          <div className="text-center mb-4">
            <div className="bg-success text-white rounded-circle d-inline-flex align-items-center justify-content-center mb-2" style={{width: '70px', height: '70px', fontSize: '1.8rem'}}>
              {user?.name?.charAt(0).toUpperCase()}
            </div>
            <h5 className="mb-0 fw-bold">{user?.name}</h5>
            <span className="badge bg-light text-success border border-success small">{user?.role}</span>
          </div>

          {/* User Details List */}
          <div className="list-group list-group-flush border-top">
            <div className="list-group-item px-0 py-3">
              <label className="text-muted small d-block mb-1 text-uppercase fw-bold" style={{fontSize: '0.65rem'}}>Email</label>
              <div className="text-dark">{user?.email}</div>
            </div>

            <div className="list-group-item px-0 py-3">
              <label className="text-muted small d-block mb-1 text-uppercase fw-bold" style={{fontSize: '0.65rem'}}>College</label>
              <div className="text-dark">{user?.college}</div>
            </div>

            {/* Roles Section with Edit Icon */}
            <div className="list-group-item px-0 py-3">
              <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="text-muted small text-uppercase fw-bold" style={{fontSize: '0.65rem'}}>
                  Roles Looking For
                </label>
                <button
                  className="btn btn-sm p-0 text-primary"
                  title="Edit Roles"
                  onClick={() => setEditingRoles(true)}
                >
                  ✎
                </button>
              </div>

              {editingRoles ? (
                <>
                  <Select
                    isMulti
                    options={rolesList.map(role => ({ label: role, value: role }))}
                    value={selectedRoles}
                    onChange={(selected) => setSelectedRoles(selected)}
                    className="basic-multi-select"
                    classNamePrefix="select"
                  />

                  <div className="d-flex gap-2 mt-2">

                    <button
                      className="btn btn-success btn-sm"
                      onClick={() => {
                        updateRoles(selectedRoles.map(r => r.value));
                        setEditingRoles(false);
                      }}
                    >
                      Save
                    </button>

                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => {
                        setSelectedRoles(user?.rolesLookingFor?.map(r => ({ label: r, value: r })) || []);
                        setEditingRoles(false);
                      }}
                    >
                      Cancel
                    </button>

                  </div>
                </>
              ) : (
                <div className="d-flex flex-wrap gap-1">
                  {user?.rolesLookingFor?.length > 0 ? (
                    user.rolesLookingFor.map((role, idx) => (
                      <span key={idx} className="badge bg-light text-dark border fw-normal">{role}</span>
                    ))
                  ) : <span className="text-muted small">Not specified</span>}
                </div>
              )}
            </div>


            {/* Skills Section with Edit Icon */}
            <div className="list-group-item px-0 py-3">
              <div className="d-flex justify-content-between align-items-center mb-2">
                <label className="text-muted small text-uppercase fw-bold" style={{fontSize: '0.65rem'}}>
                  Skills
                </label>
                <button
                  className="btn btn-sm p-0 text-primary"
                  title="Edit Skills"
                  onClick={() => setEditingSkills(true)}
                >
                  ✎
                </button>
              </div>

              {editingSkills ? (
                <>
                  <Select
                    isMulti
                    options={skillsList.map(skill => ({ label: skill, value: skill }))}
                    value={selectedSkills}
                    onChange={(selected) => setSelectedSkills(selected)}
                    className="basic-multi-select"
                    classNamePrefix="select"
                  />

                  <div className="d-flex gap-2 mt-2">

                    <button
                      className="btn btn-success btn-sm"
                      onClick={() => {
                        updateSkills(selectedSkills.map(s => s.value));
                        setEditingSkills(false);
                      }}
                    >
                      Save
                    </button>

                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={() => {
                        setSelectedSkills(user?.skills?.map(s => ({ label: s, value: s })) || []);
                        setEditingSkills(false);
                      }}
                    >
                      Cancel
                    </button>

                  </div>
                </>
              ) : (
                <div className="d-flex flex-wrap gap-1">
                  {user?.skills?.length > 0 ? (
                    user.skills.map((skill, idx) => (
                      <span key={idx} className="badge bg-info-subtle text-info-emphasis border border-info-subtle">{skill}</span>
                    ))
                  ) : <span className="text-muted small italic">Add your skills...</span>}
                </div>
              )}
            </div>

         </div>

          {/* Logout at the bottom */}
          <div className="d-grid mt-5">
            <button className="btn btn-outline-danger btn-sm" onClick={handleLogout}>
              Logout Account
            </button>
          </div>
        </div>
      </div>

      {/* Backdrop */}
      {isOpen && <div className="offcanvas-backdrop fade show" onClick={() => setIsOpen(false)}></div>}
    </>
  );
};

export default Header;