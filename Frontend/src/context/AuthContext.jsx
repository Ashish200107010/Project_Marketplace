// context/AuthContext.js
import React, { createContext, useContext, useState, useEffect } from "react";

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);

  // Load from localStorage only once on mount, and check expiry
  useEffect(() => {
    const stored = localStorage.getItem("user");
    const expiry = localStorage.getItem("user_expiry");

    if (stored && expiry && Date.now() < parseInt(expiry, 10)) {
      setUser(JSON.parse(stored));
    } else {
      localStorage.removeItem("user");
      localStorage.removeItem("user_expiry");
    }
  }, []);

  const login = (userData) => {
    const expiryTime = Date.now() + 60 * 60 * 1000; // 1 hour
    setUser(userData);

    // backup only
    localStorage.setItem("user", JSON.stringify(userData));
    localStorage.setItem("user_expiry", expiryTime.toString());
    localStorage.setItem("role", userData.role);
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem("user");
    localStorage.removeItem("user_expiry");
    localStorage.removeItem("role"); 
  };

  // Auto-expire check every minute
  useEffect(() => {
    if (!user) return;

    const interval = setInterval(() => {
      const expiry = localStorage.getItem("user_expiry");
      if (expiry && Date.now() > parseInt(expiry, 10)) {
        logout();
      }
    }, 60 * 1000);

    return () => clearInterval(interval);
  }, [user]);

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
