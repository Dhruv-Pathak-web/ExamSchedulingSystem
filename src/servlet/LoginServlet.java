package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import util.DBConnection;
import util.DatabaseInitializer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = trimToNull(request.getParameter("username"));
        String password = request.getParameter("password");
        String contextPath = request.getContextPath();

        if (username == null || password == null || password.isBlank()) {
            response.sendRedirect(contextPath + "/login.jsp?error=missing");
            return;
        }

        try {
            DatabaseInitializer.ensureInitialized();
        } catch (SQLException e) {
            throw new ServletException("Database initialization failed.", e);
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT 1 FROM admin WHERE username = ? AND password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("adminUser", username);
                    response.sendRedirect(contextPath + "/admin/dashboard.jsp");
                } else {
                    response.sendRedirect(contextPath + "/login.jsp?error=invalid");
                }
            }
        } catch (SQLException e) {
            throw new ServletException("Login failed due to a database error.", e);
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
