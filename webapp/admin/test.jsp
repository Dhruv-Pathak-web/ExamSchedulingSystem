<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>System Test</title>
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
            <a class="side-link" href="<%= request.getContextPath() %>/admin/students/addStudent.jsp">Add Student</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/subjects/index.jsp">Subjects</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">Rooms</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <p class="eyebrow">Diagnostics</p>
            <h1 class="hero-title">System Test Page</h1>
            <p class="status status-ok">Application routes and admin session are active.</p>
            <div class="toolbar">
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/admin/dashboard.jsp">Open Dashboard</a>
            </div>
        </section>
    </main>
</div>

</body>
</html>