<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="dao.RoomDao" %>
<%@ page import="model.Room" %>
<%
    if (session.getAttribute("adminUser") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Room> rooms = new ArrayList<>();
    String loadError = null;
    String success = request.getParameter("success");
    String error = request.getParameter("error");

    try {
        rooms = new RoomDao().findAll();
    } catch (Exception ex) {
        loadError = ex.getMessage();
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Rooms</title>
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
            <a class="side-link side-link-active" href="<%= request.getContextPath() %>/admin/rooms/index.jsp">Rooms</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/timetable/index.jsp">Timetable</a>
            <a class="side-link" href="<%= request.getContextPath() %>/admin/test.jsp">System Test</a>
        </nav>
    </aside>

    <main class="content-stack">
        <section class="card stack">
            <div>
                <p class="eyebrow">Rooms</p>
                <h1 class="hero-title">Manage Rooms</h1>
                <p class="hero-subtitle">Add rooms and seating capacity for scheduling.</p>
            </div>

            <% if ("added".equals(success)) { %>
                <p class="status status-ok">Room added successfully.</p>
            <% } else if ("deleted".equals(success)) { %>
                <p class="status status-ok">Room deleted successfully.</p>
            <% } %>

            <% if ("missing".equals(error) || "invalid".equals(error)) { %>
                <p class="status status-error">Please enter valid room details.</p>
            <% } else if ("duplicate".equals(error)) { %>
                <p class="status status-error">Room code already exists.</p>
            <% } else if ("db".equals(error)) { %>
                <p class="status status-error">Database error while processing rooms.</p>
            <% } %>

            <div class="panel stack">
                <h2 class="panel-title">Add Room</h2>
                <form class="form-grid" action="<%= request.getContextPath() %>/room-action/add" method="post">
                    <div class="form-grid-2">
                        <div class="field">
                            <label for="roomCode">Room Code</label>
                            <input id="roomCode" name="roomCode" type="text" required>
                        </div>
                        <div class="field">
                            <label for="capacity">Capacity</label>
                            <input id="capacity" name="capacity" type="number" min="1" required>
                        </div>
                    </div>
                    <div class="toolbar">
                        <button class="btn btn-primary" type="submit">Add Room</button>
                    </div>
                </form>
            </div>

            <% if (loadError != null) { %>
                <p class="status status-error">Failed to load rooms: <%= loadError %></p>
            <% } else if (rooms.isEmpty()) { %>
                <p class="status status-ok">No rooms available yet.</p>
            <% } else { %>
                <div class="data-table-wrap">
                    <table class="data-table">
                        <thead>
                        <tr>
                            <th>Room Code</th>
                            <th>Capacity</th>
                            <th>Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        <% for (Room r : rooms) { %>
                            <tr>
                                <td><%= r.getRoomCode() %></td>
                                <td><%= r.getCapacity() %></td>
                                <td>
                                    <form class="inline-form" action="<%= request.getContextPath() %>/room-action/delete" method="post" onsubmit="return confirm('Delete this room?');">
                                        <input type="hidden" name="id" value="<%= r.getId() %>">
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

