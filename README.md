# Project_Marketplace a Virtual Internship & Certification Platform
==========================================================
A full-stack web application that enables students to browse virtual internship projects, enroll, submit work, go through a review workflow, and receive verifiable PDF certificates upon completion. Includes Razorpay payment integration, email-based OTP authentication, and an admin dashboard for project and user management.

Tech Stack
----------
  Layer              Technologies
  -----------------  -----------------------------------------------------------
  Frontend           React 18, Vite 5, React Router DOM 7, Bootstrap 5, Axios
  Backend            Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA
  Database           MySQL 8
  Cache              Redis (token blacklist)
  Object Storage     Cloudflare R2 (S3-compatible)
  Payments           Razorpay (UPI / QR)
  Email              Spring Mail via SendGrid SMTP
  PDF Generation     iText 7
  Auth               JWT in HttpOnly cookies, OTP verification
  
Project Structure
-----------------
  Internship/
  |
  +-- Backend/
  |   +-- pom.xml
  |   +-- src/main/
  |       +-- java/com/certplatform/
  |       |   +-- admin/           Admin controllers & services
  |       |   +-- auth/            JWT filter, OTP, login/register
  |       |   +-- certificate/     PDF generation & signed URLs
  |       |   +-- common/          Shared entities, repos, exception handler
  |       |   +-- config/          SecurityConfig, WebConfig, CORS
  |       |   +-- job/             Background review job queue
  |       |   +-- notification/    Email services
  |       |   +-- payment/         Razorpay initiation & webhooks
  |       |   +-- project/         Project CRUD & file access
  |       |   +-- review/          Submission review workflow
  |       |   +-- submission/      Student submission handling
  |       |   +-- user/            User profile & role management
  |       +-- resources/
  |           +-- application.yml  App configuration
  |
  +-- Frontend/
      +-- index.html
      +-- package.json
      +-- src/
          +-- App.jsx              Route definitions
          +-- components/          Shared UI components
          +-- constants/           App-wide constants
          +-- context/             AuthContext, ProjectContext
          +-- features/
          |   +-- admin/           Admin dashboard & management
          |   +-- auth/            Login, register, OTP screens
          |   +-- payment/         Payment flow UI
          |   +-- project/         Project listing & detail pages
          |   +-- user/            Student dashboard & API helpers
          +-- pages/               Static pages (About, Privacy, etc.)
          +-- styles/              Global stylesheets

Prerequisites
-------------
  - Java 17+
  - Maven 3.8+
  - Node.js 18+ and npm
  - MySQL 8
  - Redis server
  - Cloudflare R2 bucket (or any S3-compatible store)
  - Razorpay account (for payment integration)
  - SendGrid account (for transactional email)
Getting Started
---------------
1. Database Setup
   Create a MySQL database:
     CREATE DATABASE certify;
   Hibernate will auto-create tables on first run (ddl-auto: update).
   
3. Backend Configuration
   Edit Backend/src/main/resources/application.yml and set values for:
   Property                        Description
   ------------------------------  -----------------------------------------------
   spring.datasource.url           MySQL JDBC URL (default: jdbc:mysql://localhost:3306/certify)
   spring.datasource.username      Database username
   spring.datasource.password      Database password
   spring.data.redis.host          Redis host (default: localhost)
   spring.mail.username            SendGrid API key username
   spring.mail.password            SendGrid API key
   jwt.secret                      Secret key for signing JWTs
   jwt.expiration                  Token expiry in milliseconds
   cloudinary.*                    Cloudinary credentials (cloud name, API key, secret)
   razorpay.key / razorpay.secret  Razorpay API credentials
   r2.*                            Cloudflare R2 bucket names, account ID, endpoint, access/secret keys
   
5. Run the Backend
     cd Backend
     mvn spring-boot:run
   The API server starts at http://localhost:8080.
   
7. Run the Frontend
     cd Frontend
     npm install
     npm run dev
   The dev server starts at http://localhost:5173.

   Note: In frontend code "http://localhost:8080" this base url needs to be replace with actuall backend service url.

Authentication Flow
-------------------
  1. Student registers with email and receives an OTP.
  2. OTP is verified; a JWT is issued as an HttpOnly cookie
     ("token" for students, "admin_token" for admins).
  3. All subsequent API calls include the cookie automatically
     (withCredentials: true).
  4. On logout, the token is blacklisted in Redis and the cookie is cleared.
Key Features
------------
  - Project Discovery     Students can browse, filter, and enroll in virtual
                           internship projects.
  - Submission & Review    Students submit work (e.g., Git links); a background
                           job queue processes reviews.
  - PDF Certificates       Auto-generated certificates stored on Cloudflare R2
                           with presigned download URLs.
  - Payments               Razorpay-powered UPI and QR code payment flows with
                           webhook verification.
  - Admin Dashboard        Manage projects, view enrolled users, track earnings,
                           and oversee certificates.
  - Email Notifications    OTP verification and transactional emails via SendGrid.
