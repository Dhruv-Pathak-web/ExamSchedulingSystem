package servlet;

import java.io.IOException;
import java.sql.SQLException;

import dao.StudentDao;
import model.Student;
import util.AuthUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StudentServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private final StudentDao studentDao = new StudentDao();

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

            response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?error=invalid_action");
        } catch (SQLException e) {
            if (isDuplicateKey(e)) {
                response.sendRedirect(request.getContextPath() + "/admin/students/addStudent.jsp?error=duplicate");
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?error=db");
        }
    }

    private void handleAdd(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String rollNo = trim(request.getParameter("rollNo"));
        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));
        String department = trim(request.getParameter("department"));
        int semester = parseInt(request.getParameter("semester"));

        if (rollNo == null || fullName == null || email == null || phone == null || department == null || semester < 1) {
            response.sendRedirect(request.getContextPath() + "/admin/students/addStudent.jsp?error=missing");
            return;
        }

        Student student = new Student(rollNo, fullName, email, phone, department, semester);
        studentDao.add(student);

        response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?success=added");
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = parseInt(request.getParameter("id"));
        if (id < 1) {
            response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?error=invalid");
            return;
        }

        boolean deleted = studentDao.delete(id);
        if (deleted) {
            response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?success=deleted");
            return;
        }

        response.sendRedirect(request.getContextPath() + "/admin/students/viewStudents.jsp?error=not_found");
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
