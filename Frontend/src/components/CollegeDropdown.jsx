import React from "react";
import Select from "react-select";
import { collegeList } from "../constants/collegeOptions";

const CollegeDropdown = ({ form, handleSelectChange }) => {
  return (
    <div className="mb-3">
      <label className="form-label">College</label>
      <Select
        options={collegeList}
        value={form.college ? { label: form.college, value: form.college } : null}
        onChange={(selected) => handleSelectChange(selected, "college")}
        placeholder="Search or select your college..."
        isClearable
        isSearchable
        noOptionsMessage={() => "No matching college found"}
        styles={{ menu: (provided) => ({ ...provided, zIndex: 9999 }) }}
      />
    </div>
  );
};

export default CollegeDropdown;
