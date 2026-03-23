package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Student;
import util.DBConnection;
import util.DatabaseInitializer;

public class StudentDao {

    public List<Student> findAll() throws SQLException {
        DatabaseInitializer.ensureInitialized();
        List<Student> students = new ArrayList<>();

        String sql = "SELECT id, roll_no, full_name, email, phone, department, semester FROM students ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setId(rs.getInt("id"));
                student.setRollNo(rs.getString("roll_no"));
                student.setFullName(rs.getString("full_name"));
                student.setEmail(rs.getString("email"));
                student.setPhone(rs.getString("phone"));
                student.setDepartment(rs.getString("department"));
                student.setSemester(rs.getInt("semester"));
                students.add(student);
            }
        }

        return students;
    }

    public boolean add(Student student) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        if (existsByRollOrEmail(student.getRollNo(), student.getEmail())) {
            throw new SQLException("Student roll number or email already exists.", "23000");
        }

        String sql = "INSERT INTO students(roll_no, full_name, email, phone, department, semester) VALUES(?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRollNo());
            ps.setString(2, student.getFullName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            ps.setString(5, student.getDepartment());
            ps.setInt(6, student.getSemester());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    public int countByDepartmentAndSemester(String department, int semester) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        String sql = "SELECT COUNT(*) FROM students WHERE department = ? AND semester = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, department);
            ps.setInt(2, semester);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    private boolean existsByRollOrEmail(String rollNo, String email) throws SQLException {
        String sql = "SELECT 1 FROM students WHERE roll_no = ? OR email = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
