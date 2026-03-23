<%@ page import="dao.StudentDao" %>
<%@ page import="dao.SubjectDao" %>
<%@ page import="dao.RoomDao" %>
<%@ page import="dao.TimetableDao" %>
<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    int studentCount = 0;
    int subjectCount = 0;
    int roomCount = 0;
    int scheduleCount = 0;
    String dashboardError = null;

    try {
        studentCount = new StudentDao().findAll().size();
        subjectCount = new SubjectDao().findAll().size();
        roomCount = new RoomDao().findAll().size();
        scheduleCount = new TimetableDao().findAll().size();
    } catch (Exception ex) {
        dashboardError = ex.getMessage();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
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
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/dashboard.jsp">Dashboard</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/students/viewStudents.jsp">Students</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/students/addStudent.jsp">Add Student</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/subjects/index.jsp">Subjects</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">Rooms</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <div>
                <p class="eyebrow">Control Center</p>
                <h1 class="hero-title">Admin Dashboard</h1>
                <p class="hero-subtitle">All modules are now wired to database-backed operations.</p>
            </div>

            <% if (dashboardError != null) { %>
                <p class="status status-error">Failed to load dashboard data: <%= dashboardError %></p>
            <% } %>

            <div class="dashboard-grid">
                <a class="dashboard-tile" href="<%= request.getContextPath() %>/admin/students/viewStudents.jsp">
                    <h2 class="tile-title">Students</h2>
                    <p class="tile-desc"><span class="badge"><%= studentCount %></span> total records</p>
                </a>

                <a class="dashboard-tile" href="<%= request.getContextPath() %>/admin/subjects/index.jsp">
                    <h2 class="tile-title">Subjects</h2>
                    <p class="tile-desc"><span class="badge"><%= subjectCount %></span> configured subjects</p>
                </a>

                <a class="dashboard-tile" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">
                    <h2 class="tile-title">Rooms</h2>
                    <p class="tile-desc"><span class="badge"><%= roomCount %></span> available rooms</p>
                </a>

                <a class="dashboard-tile" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">
                    <h2 class="tile-title">Exam Schedule</h2>
                    <p class="tile-desc"><span class="badge"><%= scheduleCount %></span> scheduled exams</p>
                </a>
            </div>
        </section>
    </main>
</div>

</body>
</html>