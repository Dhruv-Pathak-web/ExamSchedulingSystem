<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dao.SubjectDao" %>
<%@ page import="model.Subject" %>
<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Subject> subjects = new ArrayList<>();
    String loadError = null;
    String success = request.getParameter("success");
    String error = request.getParameter("error");

    try {
        subjects = new SubjectDao().findAll();
    } catch (Exception ex) {
        loadError = ex.getMessage();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Subjects</title>
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
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/subjects/index.jsp">Subjects</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">Rooms</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <div>
                <p class="eyebrow">Subjects</p>
                <h1 class="hero-title">Manage Subjects</h1>
                <p class="hero-subtitle">Add and remove exam subjects.</p>
            </div>

            <% if ("added".equals(success)) { %>
                <p class="status status-ok">Subject added successfully.</p>
            <% } else if ("deleted".equals(success)) { %>
                <p class="status status-ok">Subject deleted successfully.</p>
            <% } %>

            <% if ("missing".equals(error) || "invalid".equals(error)) { %>
                <p class="status status-error">Please enter valid subject details.</p>
            <% } else if ("duplicate".equals(error)) { %>
                <p class="status status-error">Subject code already exists.</p>
            <% } else if ("db".equals(error)) { %>
                <p class="status status-error">Database error while processing subjects.</p>
            <% } %>

            <div class="panel stack">
                <h2 class="panel-title">Add Subject</h2>
                <form class="form-grid" action="<%= request.getContextPath() %>/subject-action/add" method="post">
                    <div class="form-grid-3">
                        <div class="field">
                            <label for="subjectCode">Subject Code</label>
                            <input id="subjectCode" name="subjectCode" type="text" required>
                        </div>
                        <div class="field">
                            <label for="subjectName">Subject Name</label>
                            <input id="subjectName" name="subjectName" type="text" required>
                        </div>
                        <div class="field">
                            <label for="duration">Duration (minutes)</label>
                            <input id="duration" name="duration" type="number" min="30" value="180" required>
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
                        <button class="btn btn-primary" type="submit">Add Subject</button>
                    </div>
                </form>
            </div>

            <% if (loadError != null) { %>
                <p class="status status-error">Failed to load subjects: <%= loadError %></p>
            <% } else if (subjects.isEmpty()) { %>
                <p class="status status-ok">No subjects added yet.</p>
            <% } else { %>
                <div class="data-table-wrap">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Code</th>
                            <th>Name</th>
                            <th>Department</th>
                            <th>Semester</th>
                            <th>Duration</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Subject s : subjects) { %>
                            <tr>
                                <td><%= s.getSubjectCode() %></td>
                                <td><%= s.getSubjectName() %></td>
                                <td><span class="badge"><%= s.getDepartment() %></span></td>
                                <td><%= s.getSemester() %></td>
                                <td><%= s.getExamDurationMinutes() %> min</td>
                                <td>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/subject-action/delete" method="post" onsubmit="return confirm('Delete this subject?');">
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
        </section>
    </main>
</div>

</body>
</html>

