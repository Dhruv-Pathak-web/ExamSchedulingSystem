package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Subject;
import util.DBConnection;
import util.DatabaseInitializer;

public class SubjectDao {

    public List<Subject> findAll() throws SQLException {
        DatabaseInitializer.ensureInitialized();
        List<Subject> subjects = new ArrayList<>();

        String sql = "SELECT id, subject_code, subject_name, department, semester, exam_duration_minutes FROM subjects ORDER BY department, semester, subject_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Subject subject = new Subject();
                subject.setId(rs.getInt("id"));
                subject.setSubjectCode(rs.getString("subject_code"));
                subject.setSubjectName(rs.getString("subject_name"));
                subject.setDepartment(rs.getString("department"));
                subject.setSemester(rs.getInt("semester"));
                subject.setExamDurationMinutes(rs.getInt("exam_duration_minutes"));
                subjects.add(subject);
            }
        }

        return subjects;
    }

    public boolean add(Subject subject) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        if (existsBySubjectCode(subject.getSubjectCode())) {
            throw new SQLException("Subject code already exists.", "23000");
        }

        String sql = "INSERT INTO subjects(subject_code, subject_name, department, semester, exam_duration_minutes) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subject.getSubjectCode());
            ps.setString(2, subject.getSubjectName());
            ps.setString(3, subject.getDepartment());
            ps.setInt(4, subject.getSemester());
            ps.setInt(5, subject.getExamDurationMinutes());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM subjects WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private boolean existsBySubjectCode(String subjectCode) throws SQLException {
        String sql = "SELECT 1 FROM subjects WHERE subject_code = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subjectCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
