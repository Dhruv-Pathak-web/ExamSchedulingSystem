package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_DB_URL =
            "jdbc:mysql://localhost:3306/exam_system?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "admin123";

    private DBConnection() {
        // Utility class
    }

    public static Connection getConnection() throws SQLException {
        String dbUrl = System.getenv().getOrDefault("EXAM_DB_URL", DEFAULT_DB_URL);
        String dbUser = System.getenv().getOrDefault("EXAM_DB_USER", DEFAULT_DB_USER);
        String dbPassword = System.getenv().getOrDefault("EXAM_DB_PASSWORD", DEFAULT_DB_PASSWORD);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver not found.", e);
        }

        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}
