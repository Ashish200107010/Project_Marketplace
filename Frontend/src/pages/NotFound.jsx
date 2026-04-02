import React from "react";
import { Link } from "react-router-dom";

const NotFound = () => {
  return (
    <div style={{ padding: "2rem" }}>
      <h2>404 - Page Not Found</h2>
      <Link to="/">Go Home</Link>
    </div>
  );
};

export default NotFound;
