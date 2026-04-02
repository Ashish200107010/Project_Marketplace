import React, { useState } from "react";

// const FiltersBar = ({ projects, onFilter }) => {
//   const [search, setSearch] = useState("");
//   const [duration, setDuration] = useState("All");

//   const handleSearch = (e) => {
//     const term = e.target.value.toLowerCase();
//     setSearch(term);
//     applyFilters(term, duration);
//   };

//   const handleDuration = (e) => {
//     const value = e.target.value;
//     setDuration(value);
//     applyFilters(search, value);
//   };

//   const applyFilters = (term, durationValue) => {
//     let filtered = projects;

//     // text search
//     if (term) {
//       filtered = filtered.filter(
//         (p) =>
//           p.title.toLowerCase().includes(term) ||
//           p.skillsAssociated?.some((skill) =>
//             skill.toLowerCase().includes(term)
//           )
//       );
//     }

//     // duration filter
//     if (durationValue !== "All") {
//       const daysMap = {
//         "30": 30,
//         "60": 60,
//         "90": 90,
//         "180": 180,
//       };
//       const targetDays = daysMap[durationValue];
//       filtered = filtered.filter((p) => p.duration_days === targetDays);
//     }

//     onFilter(filtered);
//   };

//   return (
//     <div className="bg-white p-1 rounded-4 shadow-sm border mb-2 d-flex align-items-center gap-3">
//       <div className="flex-grow-1 position-relative">
//         <span className="position-absolute top-50 start-0 translate-middle-y ms-3 text-muted">
//           🔍
//         </span>
//         <input
//           type="text"
//           className="form-control border-0 bg-light ps-5 py-2 rounded-3"
//           placeholder="Search projects, tech stacks, or skills..."
//           value={search}
//           onChange={handleSearch}
//           style={{ fontSize: "0.95rem" }}
//         />
//       </div>

//       <select
//         className="form-select border-0 bg-light w-auto rounded-3 small text-muted cursor-pointer"
//         value={duration}
//         onChange={handleDuration}
//       >
//         <option value="All">All Durations</option>
//         <option value="30">1 Month</option>
//         <option value="60">2 Months</option>
//         <option value="90">3 Months</option>
//         <option value="180">6 Months</option>
//       </select>
//     </div>
//   );
// };

// export default FiltersBar;

const FiltersBar = ({ projects, onFilter, duration, setDuration }) => {
  const [search, setSearch] = useState("");

  const applyFilters = (term, durationValue) => {
    let filtered = projects;

    if (term) {
      filtered = filtered.filter(
        p =>
          p.title.toLowerCase().includes(term) ||
          p.skillsAssociated?.some(skill =>
            skill.toLowerCase().includes(term)
          )
      );
    }

    if (durationValue !== "All") {
      const daysMap = { "30": 30, "60": 60, "90": 90, "180": 180 };
      filtered = filtered.filter(p => p.duration_days === daysMap[durationValue]);
    }

    onFilter(filtered);
  };

  const handleSearch = (e) => {
    const term = e.target.value.toLowerCase();
    setSearch(term);
    applyFilters(term, duration);
  };

  const handleDuration = (e) => {
    const value = e.target.value;
    setDuration(value);
    applyFilters(search, value);
  };

  return (
    <div className="bg-white p-1 rounded-4 shadow-sm border mb-2 d-flex align-items-center gap-3">
      <div className="flex-grow-1 position-relative">
        <span className="position-absolute top-50 start-0 translate-middle-y ms-3 text-muted">
          🔍
        </span>
        <input
          type="text"
          className="form-control border-0 bg-light ps-5 py-2 rounded-3"
          placeholder="Search projects, tech stacks, or skills..."
          value={search}
          onChange={handleSearch}
          style={{ fontSize: "0.95rem" }}
        />
      </div>

      <select
        className="form-select border-0 bg-light w-auto rounded-3 small text-muted cursor-pointer"
        value={duration}
        onChange={handleDuration}
      >
        <option value="All">All Durations</option>
        <option value="30">1 Month</option>
        <option value="60">2 Months</option>
        <option value="90">3 Months</option>
        <option value="180">6 Months</option>
      </select>
    </div>
  );
};

export default FiltersBar;