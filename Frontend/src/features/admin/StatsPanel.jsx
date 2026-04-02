import React from "react";

const StatsPanel = () => {
  return (
    <div className="row mb-4">
      <div className="col-md-3">
        <div className="card p-3">
          <h5>Today's Earnings</h5>
          <p>₹12,500</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card p-3">
          <h5>Last Week</h5>
          <p>₹85,000</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card p-3">
          <h5>Last Month</h5>
          <p>₹3,20,000</p>
        </div>
      </div>
      <div className="col-md-3">
        <div className="card p-3">
          <h5>Total Users</h5>
          <p>1,240</p>
        </div>
      </div>
    </div>
  );
};

export default StatsPanel;
