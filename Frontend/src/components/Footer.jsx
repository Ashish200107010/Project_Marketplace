// import React from "react";

// const Footer = () => {
//   return (
//     <footer className="text-center py-4 border-top bg-white">
//       <p className="mb-2">© 2025 Remotask.in. All rights reserved.</p>
//       <div className="d-flex justify-content-center gap-3">
//         <img src="/assets/twitter.png" alt="Twitter" style={{ height: "24px" }} />
//         <img src="/assets/linkedin.png" alt="LinkedIn" style={{ height: "24px" }} />
//       </div>
//     </footer>
//   );
// };

// export default Footer;
import React from "react";
import "../styles/footer.css";

const Footer = () => {
  return (
    <footer className="footer-section">
      <div className="container">
        <div className="row align-items-center py-4">
          
          {/* Left Side: Brand Info */}
          <div className="col-md-6 text-center text-md-start mb-3 mb-md-0">
            <h5 className="fw-bold text-dark mb-1">
              Remotask<span className="text-green">.in</span>
            </h5>
            <p className="text-muted small mb-0">
              Validate your learning. Elevate your advantage.
            </p>
          </div>

          {/* Center Side: Copyright */}
          <div className="col-md-6 text-center">
            <p className="small text-muted mb-0">
              © {new Date().getFullYear()} Remotask.in. All rights reserved.
            </p>
          </div>

        </div>
      </div>
    </footer>
  );
};

export default Footer;