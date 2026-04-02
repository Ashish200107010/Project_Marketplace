// import React, { useEffect, useState } from "react";
// import Sidebar from "./Components/Sidebar";
// import FiltersBar from "./Components/FiltersBar";
// import DifficultyColumn from "./Components/DifficultyColumn";
// import { fetchAllProjects } from "./utils/api";
// import "./StudentDashboard.css"; // ✅ Added the CSS import

// const StudentDashboard = () => {
//   const [projects, setProjects] = useState([]);
//   const [filteredProjects, setFilteredProjects] = useState([]);
//   const [activeSection, setActiveSection] = useState("relevant");
//   const [duration, setDuration] = useState("All");

//   const storedUser = localStorage.getItem("user");
//   const studentId = storedUser ? JSON.parse(storedUser).id : null;

//   const refreshProjects = async () => {
//     const marked = await fetchAllProjects(activeSection);
//     // Apply duration filter immediately after fetching
//     let filtered = marked;
//     if (duration !== "All") {
//       const daysMap = { "30": 30, "60": 60, "90": 90, "180": 180 };
//       filtered = filtered.filter(p => p.duration_days === daysMap[duration]);
//     }
//     setProjects(marked);
//     setFilteredProjects(filtered);
//   };

//   useEffect(() => {
//     refreshProjects();
//   }, [activeSection]);

//   return (
//     <div className="d-flex min-vh-100" style={{ backgroundColor: "#f8f9fa" }}>
//       <Sidebar active={activeSection} onSelect={setActiveSection} />
      
//       <div className="flex-grow-1 p-4 overflow-hidden">
//           {/* <h4 className="fw-bold text-dark">Project Marketplace</h4> */}
//           <p className="text-danger">Choose your project wisely, you can't change it once you've enrolled. And you can enroll for only one project at a time.</p>

//         {/* <FiltersBar projects={projects} onFilter={setFilteredProjects} /> */}

//         <FiltersBar
//           projects={projects}
//           onFilter={setFilteredProjects}
//           duration={duration}
//           setDuration={setDuration}
//         />
        
//         <div className="row g-4 dashboard-content-area">
//           {["Easy", "Medium", "Hard"].map((level) => (
//             <DifficultyColumn
//               key={level}
//               title={level}
//               difficulty={level.toUpperCase()}
//               projects={filteredProjects}
//               onRefresh={refreshProjects}
//               studentId={studentId}
//             />
//           ))}
//         </div>
//       </div>
//     </div>
//   );
// };

// export default StudentDashboard;

import React, { useEffect, useState } from "react";
import Sidebar from "./Components/Sidebar";
import FiltersBar from "./Components/FiltersBar";
import DifficultyColumn from "./Components/DifficultyColumn";
import { fetchProjects } from "./utils/api";
import { useProjects } from "../../context/ProjectContext";
import { useAuth } from "../../context/AuthContext";
import "./StudentDashboard.css";

const StudentDashboard = () => {
  const [filteredProjects, setFilteredProjects] = useState([]);
  const [activeSection, setActiveSection] = useState("relevant");
  const [duration, setDuration] = useState("All");
  const { user } = useAuth();

  const { getProjects, setProjects, isDirty, invalidateProjects } = useProjects();

  const storedUser = user ? JSON.stringify(user) : null;
  if (!storedUser) {  
      // window.location.href = "student/login";
      // return null;
  }
  const studentId = storedUser ? JSON.parse(storedUser).id : null;

  const refreshProjects = async () => {
    // --- Enrolled projects ---
    let enrolled = getProjects("enrolled");

    if (isDirty("enrolled") || !enrolled.length) {
      enrolled = await fetchProjects("enrolled", { userId: studentId });
      setProjects("enrolled", enrolled);
    }

    const enrolledIds = new Set(enrolled.map(p => p.id));

    // --- Active section projects ---
    let data = getProjects(activeSection);

    if (isDirty(activeSection) || !data.length) {
      data = await fetchProjects(activeSection, { userId: studentId });
      setProjects(activeSection, data);
    }

    // Mark with enrollment status
    const marked = data.map(p => ({
      ...p,
      id: p.id || p.projectId,
      isEnrolled: enrolledIds.has(p.id || p.projectId),
    }));

    // Apply duration filter
    let filtered = marked;
    if (duration !== "All") {
      const daysMap = { "30": 30, "60": 60, "90": 90, "180": 180 };
      filtered = filtered.filter(p => p.duration_days === daysMap[duration]);
    }

    setFilteredProjects(filtered);
  };

  useEffect(() => {
    refreshProjects();
  }, [activeSection, duration]);

  const handleEnroll = async (projectId, startDate) => {
    await fetchProjects("enroll", { userId: studentId, projectId, startDate });
    invalidateProjects("enrolled"); // mark enrolled dirty
    await refreshProjects();
  };

  const handleUnenroll = async (projectId) => {
    await fetchProjects("unenroll", { userId: studentId, projectId });
    invalidateProjects("enrolled"); // mark enrolled dirty
    await refreshProjects();
  };

  return (
    <div className="d-flex min-vh-100" style={{ backgroundColor: "#f8f9fa" }}>
      <Sidebar active={activeSection} onSelect={setActiveSection} />
      
      <div className="flex-grow-1 p-4 overflow-hidden">


        <FiltersBar
          projects={getProjects(activeSection)}
          onFilter={setFilteredProjects}
          duration={duration}
          setDuration={setDuration}
        />
        
        <div className="row g-4 dashboard-content-area">
          {["Easy", "Medium", "Hard"].map((level) => (
            <DifficultyColumn
              key={level}
              title={level}
              difficulty={level.toUpperCase()}
              projects={filteredProjects}
              onEnroll={handleEnroll}
              onUnenroll={handleUnenroll}
              studentId={studentId}
            />
          ))}
        </div>
      </div>
    </div>
  );
};

export default StudentDashboard;
