CrisisMap – Disaster Management & Emergency Response System

A web-based application designed to help people report disasters, request emergency assistance, and enable quick response by volunteers, NGOs, and government authorities.

🚨 Overview

CrisisMap is a centralized platform built to handle real-time disaster reporting and resource management.
It helps citizens and authorities coordinate during emergencies such as floods, storms, fires, earthquakes, or accidents.

The system allows users to:

Report disaster incidents with location info

Request essential services (food, water, medical help, rescue)

Assign requests automatically to nearby NGOs or volunteers

Allow authorities to monitor, manage, and respond effectively

Maintain a live crisis dashboard for decision-making

👥 Users in the System

The application supports five types of users, each with a specific role:

General User – Reports incidents & requests help

Crisis Volunteer – Responds to assigned requests

NGO / Non-Government Organizations – Handles large-scale assistance

Government Officials – Supervises all crisis events & resource allocation

Admin – Manages users, roles, and system operations

🛠️ Technology Stack
Frontend:

HTML

CSS

JavaScript

Bootstrap

Backend:

Java Spring Boot

REST APIs

MySQL (or your DB)

Deployment (Optional):

GitHub

AWS / Render / Local server

⚙️ Key Features

🌍 Real-time disaster reporting

📍 Location-based service assignment

🎯 Auto-assignment to nearest volunteers or NGOs

📊 Crisis dashboard for authorities

🔐 Role-based authentication and login

📝 Track status of requests

📢 Notification/alert system (if applicable)

🔑 Backend Logic (Authentication)

Users register with their email/phone

Spring Security handles login authentication

JWT (or session-based auth) used for secure API calls

Role-based access controls ensure each user sees correct dashboard

📁 Project Structure
CrisisMap/
 ├── frontend/
 │   ├── index.html
 │   ├── login.html
 │   ├── dashboard.html
 │   └── assets/
 ├── backend/
 │   ├── src/main/java/...
 │   ├── controllers/
 │   ├── services/
 │   ├── models/
 │   └── repository/
 └── README.md

🚀 How to Run the Project
Frontend

Open the HTML files in your browser or run using a local server.

Backend

Import the Spring Boot project in IntelliJ/Eclipse

Configure the database (application.properties)

Run the Spring Boot application

Access APIs via Postman or integrate with frontend

🤝 Contributions
Contributions, issues, and feature requests are welcome!
Feel free to open a pull request.

📄 License

This project is open-source. You may modify and use it for learning or development.

🙌 Author
Lavu Sai Anusha
