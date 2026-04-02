import React from "react";
import { Link } from "react-router-dom";
// import Lottie from "lottie-react"; // Optional: for Lottie animations
// import learningAnimation from "./assets/learning-animation.json"; 
import "./home.css";

const Home = () => {
  return (
    <div className="home-container d-flex flex-column min-vh-100">
      <main className="container my-auto flex-grow-1 py-5">
        <div className="row align-items-center">
          
          {/* Left Column: Content */}
          <div className="col-md-6 mb-5 mb-md-0 z-index-10">
            <h2 className="display-5 fw-bold text-dark mb-4">
              Validate your learning. <br />
              <span className="text-accent">Elevate your advantage.</span>
            </h2>
            <p className="lead text-muted mb-4">
              Empower your career with verified credentials and real-world projects.
              Join thousands of learners building their future.
            </p>
            <div className="d-flex gap-3">
              <Link to="/about" className="btn btn-outline-custom btn-lg px-5 shadow-sm rounded-pill">
              Explore
            </Link>
              <Link to="/student/login" className="btn btn-primary btn-lg px-5 shadow-sm rounded-pill">
                Sign In
              </Link>
            </div>
          </div>

          {/* Right Column: Animated Visuals */}
          <div className="col-md-6 position-relative d-flex justify-content-center">
            <div className="hero-visual-area">
              {/* Background Shapes peeking behind the animation */}
              <div className="blob blob-1"></div>
              <div className="blob blob-2"></div>
              <div className="blob blob-3"></div>

                <div className="animation-container shadow-2xl">
                  <div className="image-slider">
                    <img src="/assets/home/slide1.png" alt="Slide 1" className="slider-image" />
                    <img src="/assets/home/slide2.png" alt="Slide 2" className="slider-image" />
                    <img src="/assets/home/slide3.png" alt="Slide 3" className="slider-image" />
                    <img src="/assets/home/slide4.png" alt="Slide 4" className="slider-image" />
                    <img src="/assets/home/slide5.png" alt="Slide 5" className="slider-image" />
                  </div>
                {/* </div> */}


                {/* OPTION B: Lottie Animation (Uncomment if using lottie-react) */}
                {/* <Lottie animationData={learningAnimation} loop={true} /> */}
              </div>
            </div>
          </div>

        </div>
      </main>
    </div>
  );
};

export default Home;