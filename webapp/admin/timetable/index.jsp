<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="dao.TimetableDao" %>
<%@ page import="model.ScheduleEntry" %>
<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<ScheduleEntry> entries = new ArrayList<>();
    String loadError = null;
    String success = request.getParameter("success");
    String error = request.getParameter("error");
    String rows = request.getParameter("rows");
    String subjects = request.getParameter("subjects");
    String ruleMessage = request.getParameter("message");

    String startDateValue = request.getParameter("startDate");
    if (startDateValue == null || startDateValue.isBlank()) {
        startDateValue = LocalDate.now().plusDays(1).toString();
    }

    String slotsPerDayValue = request.getParameter("slotsPerDay");
    if (slotsPerDayValue == null || slotsPerDayValue.isBlank()) {
        slotsPerDayValue = "2";
    }

    String durationHoursValue = request.getParameter("durationHours");
    if (durationHoursValue == null || durationHoursValue.isBlank()) {
        durationHoursValue = "3";
    }

    try {
        entries = new TimetableDao().findAll();
    } catch (Exception ex) {
        loadError = ex.getMessage();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Timetable</title>
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
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <div>
                <p class="eyebrow">Planning</p>
                <h1 class="hero-title">Exam Timetable</h1>
                <p class="hero-subtitle">Rules applied: one exam/day per department-semester, maximum 3 courses per shift, slot-based scheduling using your slots/day and duration, multi-room allocation per subject, and full regenerate on each run.</p>
            </div>

            <% if ("generated".equals(success)) { %>
                <p class="status status-ok">Schedule generated successfully. Subjects scheduled: <%= subjects == null ? "0" : subjects %>, room allocations: <%= rows == null ? "0" : rows %>.</p>
            <% } else if ("cleared".equals(success)) { %>
                <p class="status status-ok">Existing schedule cleared.</p>
            <% } %>

            <% if ("invalid_input".equals(error)) { %>
                <p class="status status-error">Please enter valid start date, slots/day, and duration.</p>
            <% } else if ("rules".equals(error)) { %>
                <p class="status status-error"><%= ruleMessage == null ? "Unable to generate schedule with current rules." : ruleMessage %></p>
            <% } else if ("db".equals(error)) { %>
                <p class="status status-error">Database error while generating schedule.</p>
            <% } %>

            <div class="panel stack">
                <h2 class="panel-title">Generate Settings</h2>
                <form class="form-grid" action="<%= request.getContextPath() %>/timetable-action/generate" method="post">
                    <div class="form-grid-3">
                        <div class="field">
                            <label for="startDate">Start Date</label>
                            <input id="startDate" type="date" name="startDate" value="<%= startDateValue %>" required>
                        </div>
                        <div class="field">
                            <label for="slotsPerDay">Slots Per Day</label>
                            <input id="slotsPerDay" type="number" min="1" max="8" name="slotsPerDay" value="<%= slotsPerDayValue %>" required>
                        </div>
                        <div class="field">
                            <label for="durationHours">Exam Duration (hours)</label>
                            <input id="durationHours" type="number" min="0.5" max="8" step="0.5" name="durationHours" value="<%= durationHoursValue %>" required>
                        </div>
                    </div>
                    <div class="toolbar">
                        <button class="btn btn-primary" type="submit">Generate Schedule</button>
                    </div>
                </form>
                <form class="inline-form" action="<%= request.getContextPath() %>/timetable-action/clear" method="post" onsubmit="return confirm('Clear current schedule?');">
                    <button class="btn btn-danger" type="submit">Clear Schedule</button>
                </form>
            </div>

            <% if (loadError != null) { %>
                <p class="status status-error">Failed to load schedule: <%= loadError %></p>
            <% } else if (entries.isEmpty()) { %>
                <p class="status status-ok">No schedule generated yet.</p>
            <% } else { %>
                <div class="data-table-wrap">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Slot #</th>
                            <th>Time</th>
                            <th>Subject</th>
                            <th>Dept/Sem</th>
                            <th>Room</th>
                            <th>Allocated</th>
                            <th>Expected</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (ScheduleEntry e : entries) { %>
                            <tr>
                                <td><%= e.getExamDate() %></td>
                                <td><%= e.getSlotNumber() %></td>
                                <td><%= e.getExamSlot() %></td>
                                <td>
                                    <strong><%= e.getSubjectCode() %></strong><br>
                                    <span class="muted"><%= e.getSubjectName() %></span>
                                </td>
                                <td><span class="badge"><%= e.getDepartment() %> - Sem <%= e.getSemester() %></span></td>
                                <td><%= e.getRoomCode() %> (Cap: <%= e.getRoomCapacity() %>)</td>
                                <td><%= e.getAllocatedStudents() %></td>
                                <td><%= e.getExpectedStudents() %></td>
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
