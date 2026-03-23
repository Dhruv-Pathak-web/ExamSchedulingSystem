package servlet;

import java.io.IOException;
import java.sql.SQLException;

import dao.SubjectDao;
import model.Subject;
import util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SubjectServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final SubjectDao subjectDao = new SubjectDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!AuthUtil.requireLogin(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getPathInfo();

        try {
            if ("/add".equals(action)) {
                handleAdd(request, response);
                return;
            }

            if ("/delete".equals(action)) {
                handleDelete(request, response);
                return;
            }

            response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=invalid_action");
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=duplicate");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=db");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String subjectCode = trim(request.getParameter("subjectCode"));
        String subjectName = trim(request.getParameter("subjectName"));
        String department = trim(request.getParameter("department"));
        int semester = parseInt(request.getParameter("semester"));
        int duration = parseInt(request.getParameter("duration"));

        if (subjectCode == null || subjectName == null || department == null || semester < 1 || duration < 30) {
            response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=missing");
            return;
        }

        Subject subject = new Subject(subjectCode, subjectName, department, semester, duration);
        subjectDao.add(subject);

        response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?success=added");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = parseInt(request.getParameter("id"));
        if (id < 1) {
            response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=invalid");
            return;
        }

        boolean deleted = subjectDao.delete(id);
        if (deleted) {
            response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?success=deleted");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/subjects/index.jsp?error=not_found");
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private boolean isDuplicateKey(SQLException exception) {
        return "23000".equals(exception.getSQLState()) || exception.getMessage().toLowerCase().contains("duplicate");
    }
}
