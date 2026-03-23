<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/app.css">
</head>
<body>

<main class="page page-narrow">
    <section class="card">
        <p class="eyebrow">Exam Scheduling System</p>
        <h1 class="hero-title">Admin Login</h1>
        <p class="hero-subtitle">Sign in to manage students, rooms, subjects, and timetable schedules.</p>

        <%
            String error = request.getParameter("error");
            if ("invalid".equals(error)) {
        %>
        <p class="status status-error">Invalid username or password.</p>
        <%
            } else if ("missing".equals(error)) {
        %>
        <p class="status status-error">Please enter both username and password.</p>
        <%
            }
        %>

        <form action="login" method="post" class="form-grid mt-1">
            <div class="field">
                <label for="username">Username</label>
                <input id="username" type="text" name="username" autocomplete="username" required>
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input id="password" type="password" name="password" autocomplete="current-password" required>
            </div>

            <button type="submit" class="btn btn-primary">Log In</button>
        </form>
    </section>
</main>

</body>
</html>
