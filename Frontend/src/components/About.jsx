// import "../styles/about.css";
// import { useNavigate } from "react-router-dom"; // 1. Import the hook
// import React, { useState } from "react"; // Added useState

// const About = () => {

//   const [showPrivacy, setShowPrivacy] = useState(false); // State for toggle
//   const navigate = useNavigate(); // 2. Initialize the navigate function

//   const handleRedirect = () => {
//     navigate("/student/dashboard"); // 3. Define the path to your dashboard
//   };

//   return (
//     <div className="about-container d-flex flex-column min-vh-100">
//       <main className="container my-5 py-5">
//         {/* Hero Section */}
//         <section className="text-center mb-5">
//           <h1 className="display-4 fw-bold text-dark">
//             Our Mission: <span className="text-green">Closing the Opportunity Gap</span>
//           </h1>
//           <p className="lead text-muted mx-auto" style={{ maxWidth: "800px" }}>
//             Bridging the gap between academic theory and industry reality for every ambitious student, regardless of their college tier.
//           </p>
//         </section>

//         <div className="row g-5">
//           {/* Detailed Narrative Section */}
//           <div className="col-lg-8">
//             <h3 className="fw-bold mb-4">Who We Are</h3>
//             <p className="text-secondary lh-lg">
//               At <strong>Remotask.in</strong>, we recognize a silent crisis in the modern education landscape. Thousands of brilliant, hardworking students from Tier 2 and Tier 3 colleges possess the intellect but lack the ecosystem to secure high-stakes internships during their academic tenure. Traditional hiring cycles often overlook these institutions, leaving a massive pool of talent without the "real-world" experience required to break into the workforce.
//             </p>
//             <p className="text-secondary lh-lg">
//               We aren't just an EdTech platform; we are a <strong>Virtual Experience Incubator</strong>. We provide a simulated, high-fidelity professional atmosphere where students can undertake complex projects that mirror the exact requirements of industry-leading companies.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">The "Experience First" Philosophy</h3>
//             <p className="text-secondary lh-lg">
//               Our unique model allows students to select specific project paths tailored to their career goals. Unlike traditional courses that focus on passive watching, our platform demands active building. Once a project is submitted, it undergoes a rigorous review process. We don't just hand out certificates; we validate skillsets. Only when a student meets the industry-standard requirements of the project do they receive their <strong>Project Internship Certification</strong>.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">Our Commitment to Accessibility</h3>
//             <p className="text-secondary lh-lg">
//               Education should not be a luxury. The core of our platform—the projects, the learning materials, and the review process—is provided <strong>free of cost</strong> to the student. To ensure the longevity of our platform, the upkeep of our servers, and the fair compensation of our dedicated reviewers and employees, we implement a nominal platform maintenance fee. This allows us to scale our impact while remaining the most affordable gateway to career readiness in the market.
//             </p>
//           </div>

//           {/* Sidebar Info / Highlights */}
//           <div className="col-lg-4">
//             <div className="stats-card p-4 rounded-4 shadow-sm border mb-4">
//               <h5 className="fw-bold text-green mb-3">Why Remotask.in?</h5>
//               <ul className="list-unstyled">
//                 <li className="mb-3"><strong>Industry-Aligned Projects:</strong> No busy work. Only projects that matter.</li>
//                 <li className="mb-3"><strong>Peerless Review System:</strong> Get feedback that actually helps you grow.</li>
//                 <li className="mb-3"><strong>Curated Reference Section:</strong> Access high-quality materials for every tech stack.</li>
//                 <li className="mb-3"><strong>Verified Credentials:</strong> Earn certificates that prove your hands-on ability.</li>
//               </ul>
//             </div>

//             <div className="cta-box p-4 rounded-4 bg-green text-white text-center">
//               <h5>Start Your Journey</h5>
//               <p className="small">Ready to build your portfolio and gain the experience you deserve?</p>
//               <button 
//               onClick={handleRedirect}
//               className="btn btn-light text-green fw-bold rounded-pill px-4">Browse Projects</button>
//             </div>
//           </div>
//         </div>
//       </main>
//     </div>
//   );
// };

// export default About;

// import React, { useState } from "react";
// import { useNavigate } from "react-router-dom";
// import Privacy from "./Privacy"; // Assuming Privacy.jsx is in the same folder
// import "../styles/about.css";

// const About = () => {
//   const navigate = useNavigate();
//   const [showPrivacy, setShowPrivacy] = useState(false);

//   const handleRedirect = () => {
//     navigate("/student/dashboard");
//   };

//   return (
//     <div className="about-container d-flex flex-column min-vh-100 bg-white">
//       <main className="container my-5 py-5">
        
//         {/* --- HERO SECTION --- */}
//         <section className="text-center mb-5">
//           <h1 className="display-4 fw-bold text-dark">
//             Our Mission: <span className="text-success">Closing the Opportunity Gap</span>
//           </h1>
//           <p className="lead text-muted mx-auto" style={{ maxWidth: "800px" }}>
//             Bridging the gap between academic theory and industry reality for every ambitious student, regardless of their college tier.
//           </p>
//         </section>

//         <div className="row g-5 mb-5">
//           {/* --- MAIN CONTENT --- */}
//           <div className="col-lg-8">
//             <h3 className="fw-bold mb-4">Who We Are</h3>
//             <p className="text-secondary lh-lg">
//               At <strong>Remotask.in</strong>, we recognize a silent crisis in the modern education landscape. 
//               Thousands of brilliant students from Tier 2 and Tier 3 colleges possess the intellect but lack the ecosystem 
//               to secure high-stakes internships. Traditional hiring cycles often overlook these institutions, leaving 
//               talent without the experience required to break into the workforce.
//             </p>
//             <p className="text-secondary lh-lg">
//               We aren't just an EdTech platform; we are a <strong>Virtual Experience Incubator</strong>. 
//               We provide a simulated, high-fidelity professional atmosphere where students undertake complex projects 
//               mirroring the exact requirements of industry-leading companies.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">The "Experience First" Philosophy</h3>
//             <p className="text-secondary lh-lg">
//               Our unique model demands active building over passive watching. Once a project is submitted, 
//               it undergoes a rigorous review process. We don't just hand out certificates; we validate skillsets. 
//               Only when a student meets industry standards do they receive their <strong>Project Internship Certification</strong>.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">Our Commitment to Accessibility</h3>
//             <p className="text-secondary lh-lg">
//               Education should not be a luxury. The core of our platform is provided <strong>free of cost</strong>. 
//               To ensure longevity and fair compensation for reviewers, we implement a nominal platform maintenance fee, 
//               remaining the most affordable gateway to career readiness.
//             </p>
//           </div>

//           {/* --- SIDEBAR --- */}
//           <div className="col-lg-4">
//             <div className="p-4 rounded-4 shadow-sm border mb-4 bg-light">
//               <h5 className="fw-bold text-success mb-3">Why Remotask.in?</h5>
//               <ul className="list-unstyled mb-0">
//                 <li className="mb-3 d-flex gap-2">
//                   <span className="text-success">✔</span>
//                   <span><strong>Industry-Aligned Projects:</strong> No busy work.</span>
//                 </li>
//                 <li className="mb-3 d-flex gap-2">
//                   <span className="text-success">✔</span>
//                   <span><strong>Peerless Review:</strong> Feedback that helps you grow.</span>
//                 </li>
//                 <li className="mb-3 d-flex gap-2">
//                   <span className="text-success">✔</span>
//                   <span><strong>Curated References:</strong> High-quality learning materials.</span>
//                 </li>
//                 <li className="d-flex gap-2">
//                   <span className="text-success">✔</span>
//                   <span><strong>Verified Credentials:</strong> Prove your hands-on ability.</span>
//                 </li>
//               </ul>
//             </div>

//             <div className="p-4 rounded-4 bg-success text-white text-center shadow">
//               <h5 className="fw-bold">Start Your Journey</h5>
//               <p className="small mb-4">Ready to build your portfolio and gain the experience you deserve?</p>
//               <button 
//                 onClick={handleRedirect}
//                 className="btn btn-light text-success fw-bold rounded-pill px-4"
//               >
//                 Browse Projects
//               </button>
//             </div>
//           </div>
//         </div>

//         {/* --- PRIVACY TOGGLE SECTION --- */}
//         <div className="mt-5 pt-5 border-top">
//           <div className="d-flex align-items-center justify-content-center gap-3">
//             <span className={`small fw-bold ${!showPrivacy ? "text-dark" : "text-muted"}`}>ABOUT COMPANY</span>
            
//             <div className="form-check form-switch m-0">
//               <input 
//                 className="form-check-input hover-pointer" 
//                 type="checkbox" 
//                 role="switch" 
//                 id="privacyToggle"
//                 checked={showPrivacy}
//                 onChange={() => setShowPrivacy(!showPrivacy)}
//                 style={{ width: "2.5em", height: "1.25em", cursor: "pointer" }}
//               />
//             </div>
            
//             <span className={`small fw-bold ${showPrivacy ? "text-success" : "text-muted"}`}>PRIVACY POLICY</span>
//           </div>

//           {/* --- CONDITIONAL PRIVACY COMPONENT --- */}
//           {showPrivacy && (
//             <div className="mt-5 fade-in-up">
//               <Privacy />
//             </div>
//           )}
//         </div>

//       </main>
//     </div>
//   );
// };

// export default About;

// import React, { useState } from "react";
// import { useNavigate } from "react-router-dom";
// import Privacy from "./Privacy"; 
// import "../styles/about.css";

// const About = () => {
//   const navigate = useNavigate();
//   const [showPrivacy, setShowPrivacy] = useState(false);

//   const handleRedirect = () => {
//     navigate("/student/dashboard");
//   };

//   return (
//     <div className="about-container d-flex flex-column min-vh-100">
//       <main className="container my-5 py-5">
        
//         {/* Hero Section */}
//         <section className="text-center mb-5">
//           <h1 className="display-4 fw-bold text-dark">
//             Our Mission: <span className="text-green">Closing the Opportunity Gap</span>
//           </h1>
//           <p className="lead text-muted mx-auto" style={{ maxWidth: "800px" }}>
//             Bridging the gap between academic theory and industry reality for every ambitious student, regardless of their college tier.
//           </p>
//         </section>

//         <div className="row g-5">
//           {/* Detailed Narrative Section */}
//           <div className="col-lg-8">
//             <h3 className="fw-bold mb-4">Who We Are</h3>
//             <p className="text-secondary lh-lg">
//               At <strong>Remotask.in</strong>, we recognize a silent crisis in the modern education landscape. Thousands of brilliant, hardworking students from Tier 2 and Tier 3 colleges possess the intellect but lack the ecosystem to secure high-stakes internships during their academic tenure. Traditional hiring cycles often overlook these institutions, leaving a massive pool of talent without the "real-world" experience required to break into the workforce.
//             </p>
//             <p className="text-secondary lh-lg">
//               We aren't just an EdTech platform; we are a <strong>Virtual Experience Incubator</strong>. We provide a simulated, high-fidelity professional atmosphere where students can undertake complex projects that mirror the exact requirements of industry-leading companies.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">The "Experience First" Philosophy</h3>
//             <p className="text-secondary lh-lg">
//               Our unique model allows students to select specific project paths tailored to their career goals. Unlike traditional courses that focus on passive watching, our platform demands active building. Once a project is submitted, it undergoes a rigorous review process. We don't just hand out certificates; we validate skillsets. Only when a student meets the industry-standard requirements of the project do they receive their <strong>Project Internship Certification</strong>.
//             </p>

//             <h3 className="fw-bold mt-5 mb-4">Our Commitment to Accessibility</h3>
//             <p className="text-secondary lh-lg">
//               Education should not be a luxury. The core of our platform—the projects, the learning materials, and the review process—is provided <strong>free of cost</strong> to the student. To ensure the longevity of our platform, the upkeep of our servers, and the fair compensation of our dedicated reviewers and employees, we implement a nominal platform maintenance fee. This allows us to scale our impact while remaining the most affordable gateway to career readiness in the market.
//             </p>
//           </div>

//           {/* Sidebar Info / Highlights */}
//           <div className="col-lg-4">
//             <div className="stats-card p-4 rounded-4 shadow-sm border mb-4">
//               <h5 className="fw-bold text-green mb-3">Why Remotask.in?</h5>
//               <ul className="list-unstyled">
//                 <li className="mb-3"><strong>Industry-Aligned Projects:</strong> No busy work. Only projects that matter.</li>
//                 <li className="mb-3"><strong>Peerless Review System:</strong> Get feedback that actually helps you grow.</li>
//                 <li className="mb-3"><strong>Curated Reference Section:</strong> Access high-quality materials for every tech stack.</li>
//                 <li className="mb-3"><strong>Verified Credentials:</strong> Earn certificates that prove your hands-on ability.</li>
//               </ul>
//             </div>

//             <div className="cta-box p-4 rounded-4 bg-green text-white text-center">
//               <h5>Start Your Journey</h5>
//               <p className="small">Ready to build your portfolio and gain the experience you deserve?</p>
//               <button 
//                 onClick={handleRedirect}
//                 className="btn btn-light text-green fw-bold rounded-pill px-4">
//                 Browse Projects
//               </button>
//             </div>
//           </div>
//         </div>

//         {/* Privacy Toggle Section */}
//         <div className="mt-5 pt-5 border-top">
//           <div className="d-flex align-items-center justify-content-center gap-3">
//             <span className={`small fw-bold ${!showPrivacy ? "text-dark" : "text-muted"}`}>
//               ABOUT COMPANY
//             </span>
            
//             <div className="form-check form-switch m-0">
//               <input 
//                 className="form-check-input" 
//                 type="checkbox" 
//                 role="switch" 
//                 id="privacyToggle"
//                 checked={showPrivacy}
//                 onChange={() => setShowPrivacy(!showPrivacy)}
//                 style={{ width: "2.5em", height: "1.25em", cursor: "pointer" }}
//               />
//             </div>
            
//             <span className={`small fw-bold ${showPrivacy ? "text-green" : "text-muted"}`}>
//               PRIVACY POLICY
//             </span>
//           </div>

//           {/* Conditional Privacy View */}
//           {showPrivacy && (
//             <div className="mt-5 pt-4 border-top-dotted animate-fade-in">
//               <Privacy />
//             </div>
//           )}
//         </div>
//       </main>
//     </div>
//   );
// };

// export default About;

import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Privacy from "./Privacy"; 
import "../styles/about.css";

const About = () => {
  const navigate = useNavigate();
  const [showPrivacy, setShowPrivacy] = useState(false);

  const handleRedirect = () => {
    navigate("/student/dashboard");
  };

  return (
    <div className="about-container d-flex flex-column min-vh-100">
      <main className="container my-5 py-5">
        
        {/* Hero Section */}
        <section className="text-center mb-5">
          <h1 className="display-4 fw-bold text-dark">
            Our Mission: <span className="text-green">Closing the Opportunity Gap</span>
          </h1>
          <p className="lead text-muted mx-auto" style={{ maxWidth: "800px" }}>
            Bridging the gap between academic theory and industry reality for every ambitious student, regardless of their college tier.
          </p>
        </section>

        <div className="row g-5">
          {/* Detailed Narrative Section */}
          <div className="col-lg-8">
            <h3 className="fw-bold mb-4">Who We Are</h3>
            <p className="text-secondary lh-lg">
              At <strong>Remotask.in</strong>, we recognize a silent crisis in the modern education landscape. Thousands of brilliant, hardworking students from Tier 2 and Tier 3 colleges possess the intellect but lack the ecosystem to secure high-stakes internships during their academic tenure. Traditional hiring cycles often overlook these institutions, leaving a massive pool of talent without the "real-world" experience required to break into the workforce.
            </p>
            <p className="text-secondary lh-lg">
              We aren't just an EdTech platform; we are a <strong>Virtual Experience Incubator</strong>. We provide a simulated, high-fidelity professional atmosphere where students can undertake complex projects that mirror the exact requirements of industry-leading companies.
            </p>

            <h3 className="fw-bold mt-5 mb-4">The "Experience First" Philosophy</h3>
            <p className="text-secondary lh-lg">
              Our unique model allows students to select specific project paths tailored to their career goals. Unlike traditional courses that focus on passive watching, our platform demands active building. Once a project is submitted, it undergoes a rigorous review process. We don't just hand out certificates; we validate skillsets. Only when a student meets the industry-standard requirements of the project do they receive their <strong>Project Internship Certification</strong>.
            </p>

            <h3 className="fw-bold mt-5 mb-4">Our Commitment to Accessibility</h3>
            <p className="text-secondary lh-lg">
              Education should not be a luxury. The core of our platform—the projects, the learning materials, and the review process—is provided <strong>free of cost</strong> to the student. To ensure the longevity of our platform, the upkeep of our servers, and the fair compensation of our dedicated reviewers and employees, we implement a nominal platform maintenance fee. This allows us to scale our impact while remaining the most affordable gateway to career readiness in the market.
            </p>
          </div>

          {/* Sidebar Info / Highlights */}
          <div className="col-lg-4">
            <div className="stats-card p-4 rounded-4 shadow-sm border mb-4">
              <h5 className="fw-bold text-green mb-3">Why Remotask.in?</h5>
              <ul className="list-unstyled">
                <li className="mb-3"><strong>Industry-Aligned Projects:</strong> No busy work. Only projects that matter.</li>
                <li className="mb-3"><strong>Peerless Review System:</strong> Get feedback that actually helps you grow.</li>
                <li className="mb-3"><strong>Curated Reference Section:</strong> Access high-quality materials for every tech stack.</li>
                <li className="mb-3"><strong>Verified Credentials:</strong> Earn certificates that prove your hands-on ability.</li>
              </ul>
            </div>

            <div className="cta-box p-4 rounded-4 bg-green text-white text-center">
              <h5>Start Your Journey</h5>
              <p className="small">Ready to build your portfolio and gain the experience you deserve?</p>
              <button 
                onClick={handleRedirect}
                className="btn btn-light text-green fw-bold rounded-pill px-4">
                Browse Projects
              </button>
            </div>
          </div>
        </div>

        {/* --- Text Toggle Link --- */}
        <div className="mt-5 pt-5 border-top text-center">
          <span 
            onClick={() => setShowPrivacy(!showPrivacy)}
            className="text-uppercase fw-bold"
            style={{ 
              cursor: "pointer", 
              fontSize: "0.85rem", 
              letterSpacing: "1px",
              color: showPrivacy ? "#28a745" : "#6c757d", // Uses green when active
              transition: "all 0.3s ease",
              borderBottom: `2px solid ${showPrivacy ? "#28a745" : "transparent"}`
            }}
          >
            {showPrivacy ? "✕ Close Privacy Policy" : "Privacy Policy"}
          </span>

          {/* Conditional Privacy View */}
          {showPrivacy && (
            <div className="mt-4 pt-4 border-top border-light animate-fade-in">
              <Privacy />
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default About;