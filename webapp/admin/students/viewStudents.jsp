<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dao.StudentDao" %>
<%@ page import="model.Student" %>
<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Student> students = new ArrayList<>();
    String loadError = null;
    String success = request.getParameter("success");
    String error = request.getParameter("error");

    try {
        students = new StudentDao().findAll();
    } catch (Exception ex) {
        loadError = ex.getMessage();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>View Students</title>
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
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/students/viewStudents.jsp">Students</a>
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
                <p class="eyebrow">Students</p>
                <h1 class="hero-title">Student Records</h1>
                <p class="hero-subtitle">View and manage all student entries.</p>
            </div>

            <% if ("added".equals(success)) { %>
                <p class="status status-ok">Student added successfully.</p>
            <% } else if ("deleted".equals(success)) { %>
                <p class="status status-ok">Student deleted successfully.</p>
            <% } %>

            <% if ("invalid".equals(error) || "missing".equals(error)) { %>
                <p class="status status-error">Invalid student operation request.</p>
            <% } else if ("not_found".equals(error)) { %>
                <p class="status status-error">Student record not found.</p>
            <% } else if ("db".equals(error)) { %>
                <p class="status status-error">Database error while processing students.</p>
            <% } %>

            <% if (loadError != null) { %>
                <p class="status status-error">Failed to load students: <%= loadError %></p>
            <% } else if (students.isEmpty()) { %>
                <p class="status status-ok">No students found. Start by adding a student.</p>
            <% } else { %>
                <div class="data-table-wrap">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Roll No</th>
                            <th>Name</th>
                            <th>Email</th>
                            <th>Phone</th>
                            <th>Department</th>
                            <th>Semester</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Student s : students) { %>
                            <tr>
                                <td><%= s.getRollNo() %></td>
                                <td><%= s.getFullName() %></td>
                                <td><%= s.getEmail() %></td>
                                <td><%= s.getPhone() %></td>
                                <td><span class="badge"><%= s.getDepartment() %></span></td>
                                <td><%= s.getSemester() %></td>
                                <td>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/student-action/delete" method="post" onsubmit="return confirm('Delete this student?');">
                                        <input type="hidden" name="id" value="<%= s.getId() %>">
                                        <button class="btn btn-danger" type="submit">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>

            <div class="toolbar">
                <a class="btn btn-primary" href="<%= request.getContextPath() %>/admin/students/addStudent.jsp">Add New Student</a>
            </div>
        </section>
    </main>
</div>

</body>
</html>

