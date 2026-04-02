import React, { useState } from "react";
import { Link } from "react-router-dom";
import "../styles/service.css";

const Service = () => {
  const [message, setMessage] = useState("");
  const [chatHistory, setChatHistory] = useState([
    { sender: "admin", text: "Welcome to Remotask Support. How can we assist with your upskilling journey today?" }
  ]);

  const handleSendMessage = (e) => {
    e.preventDefault();
    if (!message.trim()) return;
    
    const newChat = [...chatHistory, { sender: "user", text: message }];
    setChatHistory(newChat);
    setMessage("");

    setTimeout(() => {
      setChatHistory(prev => [...prev, { 
        sender: "admin", 
        text: "Your query regarding platform features has been logged. An expert will be with you shortly." 
      }]);
    }, 1500);
  };

  return (
    <div className="service-page-wrapper">
      <div className="container py-5">
        <div className="row g-4">
          
          {/* LEFT SECTION: UPSKILLING & VALIDATION */}
          <div className="col-lg-7">
            <div className="service-card main-service">
              <span className="badge-core">Upskilling Ecosystem</span>
              <h2 className="fw-bold mt-2">Professional Competency Validation</h2>
              <p className="text-secondary">
                Remotask.in focuses on empowering remote professionals through rigorous project-based learning and skills verification. We bridge the gap between learning and industry-standard competency.
              </p>
              
              <div className="service-list mt-4">
                <div className="service-item">
                  <i className="bi bi-graph-up-arrow"></i>
                  <div>
                    <h6 className="mb-0">Career Advancement Tools</h6>
                    <small>Access to high-level projects designed to sharpen your technical expertise.</small>
                  </div>
                </div>
                <div className="service-item">
                  <i className="bi bi-cpu-fill"></i>
                  <div>
                    <h6 className="mb-0">Skill Infrastructure</h6>
                    <small>Advanced digital tools for real-time tracking of your professional growth milestones.</small>
                  </div>
                </div>
                <div className="service-item">
                  <i className="bi bi-check2-all"></i>
                  <div>
                    <h6 className="mb-0">Credential Integrity</h6>
                    <small>Immutable validation of your project contributions and technical proficiencies.</small>
                  </div>
                </div>
              </div>

              {/* Maintenance Fee Redirect Box */}
              <div className="payment-gateway-box mt-5">
                <h5>Platform Infrastructure Access</h5>
                <p className="small">
                  To sustain our secure validation servers and ensure 24/7 global accessibility for your professional profile, 
                  a nominal infrastructure maintenance fee is required to finalize your account records.
                </p>
                <div className="d-flex align-items-center justify-content-between mt-4">
                  <div>
                    <span className="fee-amount">₹499</span>
                    <span className="text-muted small ms-2"> / Infrastructure Fee</span>
                  </div>
                  <Link to="/payments" className="btn btn-payment">
                    Complete Validation & Pay
                  </Link>
                </div>
              </div>
            </div>
          </div>

          {/* RIGHT SECTION: HELP & SUPPORT CHAT */}
          <div className="col-lg-5">
            <div className="support-card">
              <div className="support-header">
                <div className="d-flex align-items-center">
                  <div className="online-indicator"></div>
                  <h5 className="mb-0 ms-2">Platform Support</h5>
                </div>
                <p className="small mb-0 opacity-75">Connect with our support team</p>
              </div>

              <div className="chat-window">
                {chatHistory.map((chat, index) => (
                  <div key={index} className={`chat-bubble ${chat.sender}`}>
                    <div className="bubble-content">{chat.text}</div>
                  </div>
                ))}
              </div>

              <form className="chat-input-area" onSubmit={handleSendMessage}>
                <input 
                  type="text" 
                  placeholder="Type your message here..." 
                  className="form-control chat-input"
                  value={message}
                  onChange={(e) => setMessage(e.target.value)}
                />
                <button type="submit" className="btn-send">
                   ➤ <i className="bi bi-send-fill"></i>
                </button>
              </form>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default Service;