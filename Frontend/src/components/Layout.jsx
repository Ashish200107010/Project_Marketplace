// src/components/Layout.jsx
import React from "react";
import { Link, Outlet } from "react-router-dom";
import Header from "./Header";
import Footer from "./Footer";

const Layout = () => {
  return (
    <div>
      {/* Header */}
      <Header />

      {/* Page Content */}
      <main>
        <Outlet />
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
};

export default Layout;
