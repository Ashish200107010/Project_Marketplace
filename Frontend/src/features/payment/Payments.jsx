// import React, { useState } from "react";

// const Payments = () => {
//   // Global variables for pricing
//   const actualPrice = 499;
//   const discountPercentage = 30; // static discount

//   const [statusMessage, setStatusMessage] = useState("");
//   const [paymentData, setPaymentData] = useState(null);

//   // Calculate discount
//   const discountAmount = (actualPrice * discountPercentage) / 100;
//   const totalPayable = (actualPrice - discountAmount).toFixed(2);

//   // Unified continue flow
//   const handleContinuePayment = async () => {
//     try {
//       const storedUser = localStorage.getItem("user");
//       const studentId = storedUser ? JSON.parse(storedUser).id : null;

//       const res = await fetch("http://localhost:8080/api/student/payments/initiate-qr", {
//         method: "POST",
//         headers: { "Content-Type": "application/json" },
//         body: JSON.stringify({ amount: totalPayable, studentId }), // send updated amount
//         credentials: "include",
//       });

//       const data = await res.json();
//       if (data.qrCodeUrl) {
//         setPaymentData(data);
//         window.open(data.qrCodeUrl, "_blank");
//       } else {
//         setStatusMessage("Failed to initiate payment.");
//       }
//     } catch (err) {
//       console.error("Error initiating payment:", err);
//       setStatusMessage("Something went wrong. Please try again later.");
//     }
//   };

//   return (
//     <div className="container my-5">
//       <div className="row g-4 justify-content-center">
//         {/* Payment Summary */}
//         <div className="col-md-6">
//           <div className="card h-100 shadow-sm p-4">
//             <h5 className="card-title">🧾 Payment Summary</h5>
//             <p className="text-muted small">
//               This fee helps us maintain platform quality and support certificate generation.
//             </p>

//             <div className="d-flex justify-content-between">
//               <span>Platform Maintenance Fee</span>
//               <span>₹{actualPrice}</span>
//             </div>
//             <div className="d-flex justify-content-between">
//               <span>Discount ({discountPercentage}%)</span>
//               <span>-₹{discountAmount.toFixed(2)}</span>
//             </div>
//             <hr />
//             <div className="d-flex justify-content-between fw-bold">
//               <span>Total Payable</span>
//               <span>₹{totalPayable}</span>
//             </div>

//             {/* Static Discount Banner */}
//             <div className="mt-4 p-3 bg-light border rounded text-center">
//               <h6>✨ Special Offer</h6>
//               <p className="mb-0 text-success fw-bold">Flat {discountPercentage}% Discount Applied!</p>
//             </div>

//             {/* Continue Button */}
//             <div className="mt-4">
//               <button className="btn btn-success w-100" onClick={handleContinuePayment}>
//                 Continue to Payment
//               </button>
//             </div>
//           </div>
//         </div>
//       </div>

//       {/* Status Message */}
//       {statusMessage && (
//         <div className="alert alert-info text-center mt-4">{statusMessage}</div>
//       )}
//     </div>
//   );
// };

// export default Payments;
import React, { useState } from "react";
import "./payments.css";

const Payments = () => {
  const actualPrice = 499;
  const discountPercentage = 30; 
  const [statusMessage, setStatusMessage] = useState("");

  const discountAmount = (actualPrice * discountPercentage) / 100;
  const totalPayable = (actualPrice - discountAmount).toFixed(2);

  const handleContinuePayment = async () => {
    try {
      const storedUser = localStorage.getItem("user");
      const studentId = storedUser ? JSON.parse(storedUser).id : null;

      const res = await fetch("http://localhost:8080/api/student/payments/initiate-qr", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ amount: totalPayable, studentId }),
        credentials: "include",
      });

      const data = await res.json();
      if (data.qrCodeUrl) {
        window.open(data.qrCodeUrl, "_blank");
      } else {
        setStatusMessage("Failed to initiate payment.");
      }
    } catch (err) {
      console.error("Error initiating payment:", err);
      setStatusMessage("Something went wrong. Please try again later.");
    }
  };

  return (
    <div className="payments-page-wrapper">
      <div className="container py-5">
        <div className="row g-0 payment-main-card">
          
          {/* LEFT SECTION: IMAGE/GRAPHIC AREA */}
          <div className="col-lg-6 d-none d-lg-flex align-items-center justify-content-center bg-white border-end">
            <div className="text-center p-5">
              <div className="upskill-icon-box mb-4">
                <i className="bi bi-shield-check"></i>
              </div>
              <h3 className="fw-bold">Secure Infrastructure</h3>
              <p className="text-muted">
                Finalizing your professional competency records through our secure encrypted gateway. 
                Your validation metadata will be accessible 24/7 post-processing.
              </p>
              {/* Placeholder for your actual image asset */}
              <div className="graphic-placeholder mt-4">
                 <img src="https://img.freepik.com/free-vector/digital-lifestyle-concept-illustration_114360-7307.jpg" alt="Secure Payment" className="img-fluid" style={{maxWidth: '300px'}} />
              </div>
            </div>
          </div>

          {/* RIGHT SECTION: PAYMENT CALCULATION */}
          <div className="col-lg-6">
            <div className="payment-form-section p-4 p-md-5">
              <div className="d-flex justify-content-between align-items-center mb-4">
                <h4 className="fw-bold mb-0">Order Summary</h4>
                <span className="badge bg-light text-dark border">Ref: RT-PAY-2026</span>
              </div>

              <div className="pricing-details">
                <div className="d-flex justify-content-between mb-3">
                  <span className="text-secondary">Platform Maintenance Fee</span>
                  <span className="fw-semibold">₹{actualPrice.toFixed(2)}</span>
                </div>
                
                <div className="d-flex justify-content-between mb-3 text-success">
                  <span>Discount ({discountPercentage}%)</span>
                  <span>-₹{discountAmount.toFixed(2)}</span>
                </div>

                <div className="offer-tag mb-4">
                  <i className="bi bi-patch-check me-2"></i>
                  Special Offer Applied: Flat 30% Off
                </div>

                <hr className="my-4" />

                <div className="d-flex justify-content-between align-items-center mb-5">
                  <div>
                    <h5 className="mb-0 fw-bold">Total Payable</h5>
                    <small className="text-muted">Inclusive of all infrastructure costs</small>
                  </div>
                  <h3 className="total-amount mb-0">₹{totalPayable}</h3>
                </div>

                <button className="btn btn-payment-green w-100 mb-3" onClick={handleContinuePayment}>
                  Pay & Authorize Records
                </button>
                
                <p className="text-center text-muted small">
                  <i className="bi bi-lock-fill me-1"></i> 
                  Encrypted Transaction
                </p>

                {statusMessage && (
                  <div className="alert alert-danger small py-2 mt-3 text-center">
                    {statusMessage}
                  </div>
                )}
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default Payments;