import React, { useState, useEffect, useRef } from "react";
import StudentRegisterForm from "./StudentRegisterForm";
import StudentLoginForm from "./StudentLoginForm"; // We'll move the login logic here
import ForgotPasswordForm from "./ForgotPasswordForm"; // New Component
import "./AuthStyle.css";

const AuthPage = () => {
  const [view, setView] = useState("login");
  const leftPupilRef = useRef(null);
  const rightPupilRef = useRef(null);

  useEffect(() => {
    const handleMouseMove = (e) => {
      const pupils = [leftPupilRef, rightPupilRef];
      pupils.forEach((pupilRef) => {
        if (!pupilRef.current) return;
        
        const rect = pupilRef.current.getBoundingClientRect();
        const eyeCenterX = rect.left + rect.width / 2;
        const eyeCenterY = rect.top + rect.height / 2;
        
        const angle = Math.atan2(e.clientY - eyeCenterY, e.clientX - eyeCenterX);
        
        // Adjust distance so pupil stays within the white circle
        const distance = 5; 
        const x = Math.cos(angle) * distance;
        const y = Math.sin(angle) * distance;
        
        // Note: Because the parent is flipped with scaleX(-1), 
        // we multiply x by -1 to keep tracking natural.
        pupilRef.current.style.transform = `translate(${-x}px, ${y}px)`;
      });
    };
    
    window.addEventListener("mousemove", handleMouseMove);
    return () => window.removeEventListener("mousemove", handleMouseMove);
  }, []);

  return (
    <div className="auth-page-wrapper">
      <div className={`auth-main-card shadow-lg mode-${view}`}>
        <div className="auth-inner-container"> 
          
          <div className="bg-mascot-side">
            <div className="character-container">
              {/* The Tortoise Image */}
              <div className="cute-character">
                <div className="eyes-overlay">
                  <div className="eye-socket eye-left">
                    <div ref={leftPupilRef} className="pupil"></div>
                  </div>
                  <div className="eye-socket eye-right">
                    <div ref={rightPupilRef} className="pupil"></div>
                  </div>
                </div>
              </div>

              <h4 className="mt-4 fw-bold">
                {view === "login" ? "Welcome Back!" : view === "register" ? "Join Us!" : "No Worries!"}
              </h4>
              <div className="mascot-bubble mt-3 px-4 text-center">
                <p className="fw-medium text-muted mb-0" style={{ fontSize: '0.95rem', fontStyle: 'italic' }}>
                  "I'm Turbo! I move slow, but I track your progress fast!"
                </p>
              </div>
            </div>
          </div>

          <div className="form-content-area">
            <div className="form-box-wrapper" style={{ width: view === "register" ? "100%" : "450px" }}>
              {view === "login" && (
                <StudentLoginForm 
                  onSwitch={() => setView("register")} 
                  onForgot={() => setView("forgot")} 
                />
              )}
              {view === "register" && (
                <StudentRegisterForm onSwitch={() => setView("login")} />
              )}
              {view === "forgot" && (
                <ForgotPasswordForm onBack={() => setView("login")} />
              )}
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default AuthPage;