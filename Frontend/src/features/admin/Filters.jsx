import React from "react";

const Filters = ({ filters, setFilters }) => {
  return (
    <div className="d-flex gap-3 mb-4">
      <select
        className="form-select"
        value={filters.sortBy}
        onChange={(e) => setFilters({ ...filters, sortBy: e.target.value })}
      >
        <option value="date">Sort by Date</option>
        <option value="name">Sort by Name</option>
        <option value="status">Sort by Status</option>
      </select>

      <select
        className="form-select"
        value={filters.role}
        onChange={(e) => setFilters({ ...filters, role: e.target.value })}
      >
        <option value="all">All Roles</option>
        <option value="student">Student</option>
        <option value="admin">Admin</option>
      </select>

      <input
        type="date"
        className="form-control"
        onChange={(e) => setFilters({ ...filters, dateRange: { ...filters.dateRange, start: e.target.value } })}
      />
      <input
        type="date"
        className="form-control"
        onChange={(e) => setFilters({ ...filters, dateRange: { ...filters.dateRange, end: e.target.value } })}
      />
    </div>
  );
};

export default Filters;
