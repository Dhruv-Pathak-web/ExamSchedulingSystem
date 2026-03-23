ExamSchedulingSystem - Local Run Guide

1) Start the app
   - Double click `start-app.bat`
   - Or run in terminal:
     `.\start-app.bat`

2) Open in browser
   - `http://localhost:8080/ExamSchedulingSystem/login.jsp`

3) Stop the app
   - Double click `stop-app.bat`
   - Or run:
     `.\stop-app.bat`

Database requirements (MySQL)
-----------------------------
The app expects:
- URL: `jdbc:mysql://localhost:3306/exam_system`
- User: `root`
- Password: `admin123`

It also accepts env overrides:
- `EXAM_DB_URL`
- `EXAM_DB_USER`
- `EXAM_DB_PASSWORD`

Minimum SQL setup:
`CREATE DATABASE exam_system;`
`USE exam_system;`
`CREATE TABLE admin (id INT PRIMARY KEY AUTO_INCREMENT, username VARCHAR(50), password VARCHAR(100));`
`INSERT INTO admin (username, password) VALUES ('admin', 'admin123');`
