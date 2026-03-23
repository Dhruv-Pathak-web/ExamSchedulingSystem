<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Add Student</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/app.css">
</head>
<body class="app-shell">

<header class="topbar">
    <div class="brand-wrap">
        <p class="brand-title">Exam Scheduling System</p>
        <p class="brand-subtitle">Admin Control Panel</p>
    </div>
    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/logout">Sign Out</a>
</header>

<div class="shell-body">
    <aside class="sidebar">
        <p class="sidebar-label">Navigation</p>
        <nav class="side-nav">
            <a class="side-link" href="<%= request.getContextPath() %>/admin/dashboard.jsp">Dashboard</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/students/viewStudents.jsp">Students</a>
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/students/addStudent.jsp">Add Student</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/subjects/index.jsp">Subjects</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">Rooms</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <div>
                <p class="eyebrow">Students</p>
                <h1 class="hero-title">Add Student</h1>
                <p class="hero-subtitle">Create a student record stored in the database.</p>
            </div>

            <% if ("missing".equals(error)) { %>
                <p class="status status-error">Please fill all required fields correctly.</p>
            <% } else if ("duplicate".equals(error)) { %>
                <p class="status status-error">Roll number or email already exists.</p>
            <% } %>

            <form class="form-grid" action="<%= request.getContextPath() %>/student-action/add" method="post">
                <div class="form-grid-2">
                    <div class="field">
                        <label for="rollNo">Roll Number</label>
                        <input id="rollNo" name="rollNo" type="text" placeholder="e.g. CSE-2026-001" required>
                    </div>

                    <div class="field">
                        <label for="fullName">Full Name</label>
                        <input id="fullName" name="fullName" type="text" placeholder="Student full name" required>
                    </div>

                    <div class="field">
                        <label for="email">Email</label>
                        <input id="email" name="email" type="email" placeholder="name@example.com" required>
                    </div>

                    <div class="field">
                        <label for="phone">Phone</label>
                        <input id="phone" name="phone" type="tel" placeholder="10-digit phone number" required>
                    </div>

                    <div class="field">
                        <label for="department">Department</label>
                        <select id="department" name="department" required>
                            <option value="">Select Department</option>
                            <option>CSE</option>
                            <option>ECE</option>
                            <option>ME</option>
                            <option>CE</option>
                            <option>EE</option>
                        </select>
                    </div>

                    <div class="field">
                        <label for="semester">Semester</label>
                        <select id="semester" name="semester" required>
                            <option value="">Select Semester</option>
                            <option value="1">1</option>
                            <option value="2">2</option>
                            <option value="3">3</option>
                            <option value="4">4</option>
                            <option value="5">5</option>
                            <option value="6">6</option>
                            <option value="7">7</option>
                            <option value="8">8</option>
                        </select>
                    </div>
                </div>

                <div class="toolbar">
                    <button class="btn btn-primary" type="submit">Save Student</button>
                    <a class="btn btn-secondary" href="<%= request.getContextPath() %>/admin/students/viewStudents.jsp">View Students</a>
                </div>
            </form>
        </section>
    </main>
</div>

</body>
</html>
