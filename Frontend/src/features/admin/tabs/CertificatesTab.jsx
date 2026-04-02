import React from "react";

const CertificatesTab = () => {
  return (
    <div className="row">
      {["Today", "Week", "Month", "Year"].map(period => (
        <div className="col-md-3 mb-3" key={period}>
          <div className="card p-3 shadow-sm">
            <h6>{period}'s Issued Certificates</h6>
            <p className="fs-5">{Math.floor(Math.random() * 300)} issued</p>
          </div>
        </div>
      ))}
      <div className="col-12 mt-3">
        <label>Custom Range</label>
        <div className="d-flex gap-2">
          <input type="date" className="form-control" />
          <input type="date" className="form-control" />
          <button className="btn btn-outline-primary">Load</button>
        </div>
      </div>
    </div>
  );
};

export default CertificatesTab;
