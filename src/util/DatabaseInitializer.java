package util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseInitializer {

    private static volatile boolean initialized = false;

    private DatabaseInitializer() {
    }

    public static void ensureInitialized() throws SQLException {
        if (initialized) {
            return;
        }

        synchronized (DatabaseInitializer.class) {
            if (initialized) {
                return;
            }

            try (Connection conn = DBConnection.getConnection();
                 Statement st = conn.createStatement()) {

                createTables(st);
                migrateLegacyColumns(conn, st);
                cleanupLegacyIndexes(conn, st);
                seedAdmin(st);
            }

            initialized = true;
        }
    }

    private static void createTables(Statement st) throws SQLException {
        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS admin (
                id INT PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(60) NOT NULL UNIQUE,
                password VARCHAR(120) NOT NULL
            )
            """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS students (
                id INT PRIMARY KEY AUTO_INCREMENT,
                roll_no VARCHAR(50),
                full_name VARCHAR(120),
                email VARCHAR(120),
                phone VARCHAR(20),
                department VARCHAR(20),
                semester INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS subjects (
                id INT PRIMARY KEY AUTO_INCREMENT,
                subject_code VARCHAR(30),
                subject_name VARCHAR(120),
                department VARCHAR(20),
                semester INT,
                exam_duration_minutes INT DEFAULT 180,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS rooms (
                id INT PRIMARY KEY AUTO_INCREMENT,
                room_code VARCHAR(40),
                capacity INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);

        st.executeUpdate("""
            CREATE TABLE IF NOT EXISTS exam_schedule (
                id INT PRIMARY KEY AUTO_INCREMENT,
                subject_id INT,
                room_id INT,
                exam_date DATE,
                exam_slot VARCHAR(80),
                slot_number INT,
                allocated_students INT,
                expected_students INT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """);
    }

    private static void migrateLegacyColumns(Connection conn, Statement st) throws SQLException {
        migrateStudents(conn, st);
        migrateSubjects(conn, st);
        migrateRooms(conn, st);
        migrateExamSchedule(conn, st);
    }

    private static void migrateStudents(Connection conn, Statement st) throws SQLException {
        renameColumnIfNeeded(conn, st, "students", "rollNo", "roll_no", "VARCHAR(50)");
        renameColumnIfNeeded(conn, st, "students", "fullName", "full_name", "VARCHAR(120)");

        ensureColumn(conn, st, "students", "roll_no", "VARCHAR(50)");
        ensureColumn(conn, st, "students", "full_name", "VARCHAR(120)");
        ensureColumn(conn, st, "students", "email", "VARCHAR(120)");
        ensureColumn(conn, st, "students", "phone", "VARCHAR(20)");
        ensureColumn(conn, st, "students", "department", "VARCHAR(20)");
        ensureColumn(conn, st, "students", "semester", "INT");
        ensureColumn(conn, st, "students", "created_at", "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP");
    }

    private static void migrateSubjects(Connection conn, Statement st) throws SQLException {
        renameColumnIfNeeded(conn, st, "subjects", "subjectCode", "subject_code", "VARCHAR(30)");
        renameColumnIfNeeded(conn, st, "subjects", "subjectName", "subject_name", "VARCHAR(120)");
        renameColumnIfNeeded(conn, st, "subjects", "examDurationMinutes", "exam_duration_minutes", "INT");
        renameColumnIfNeeded(conn, st, "subjects", "duration", "exam_duration_minutes", "INT");

        ensureColumn(conn, st, "subjects", "subject_code", "VARCHAR(30)");
        ensureColumn(conn, st, "subjects", "subject_name", "VARCHAR(120)");
        ensureColumn(conn, st, "subjects", "department", "VARCHAR(20)");
        ensureColumn(conn, st, "subjects", "semester", "INT");
        ensureColumn(conn, st, "subjects", "exam_duration_minutes", "INT DEFAULT 180");
        ensureColumn(conn, st, "subjects", "created_at", "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP");
    }

    private static void migrateRooms(Connection conn, Statement st) throws SQLException {
        renameColumnIfNeeded(conn, st, "rooms", "roomCode", "room_code", "VARCHAR(40)");

        ensureColumn(conn, st, "rooms", "room_code", "VARCHAR(40)");
        ensureColumn(conn, st, "rooms", "capacity", "INT");
        ensureColumn(conn, st, "rooms", "created_at", "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP");
    }

    private static void migrateExamSchedule(Connection conn, Statement st) throws SQLException {
        renameColumnIfNeeded(conn, st, "exam_schedule", "subjectId", "subject_id", "INT");
        renameColumnIfNeeded(conn, st, "exam_schedule", "roomId", "room_id", "INT");
        renameColumnIfNeeded(conn, st, "exam_schedule", "examDate", "exam_date", "DATE");
        renameColumnIfNeeded(conn, st, "exam_schedule", "examSlot", "exam_slot", "VARCHAR(80)");
        renameColumnIfNeeded(conn, st, "exam_schedule", "expectedStudents", "expected_students", "INT");
        renameColumnIfNeeded(conn, st, "exam_schedule", "allocatedStudents", "allocated_students", "INT");
        renameColumnIfNeeded(conn, st, "exam_schedule", "slotNumber", "slot_number", "INT");

        ensureColumn(conn, st, "exam_schedule", "subject_id", "INT");
        ensureColumn(conn, st, "exam_schedule", "room_id", "INT");
        ensureColumn(conn, st, "exam_schedule", "exam_date", "DATE");
        ensureColumn(conn, st, "exam_schedule", "exam_slot", "VARCHAR(80)");
        ensureColumn(conn, st, "exam_schedule", "slot_number", "INT");
        ensureColumn(conn, st, "exam_schedule", "allocated_students", "INT DEFAULT 0");
        ensureColumn(conn, st, "exam_schedule", "expected_students", "INT DEFAULT 0");
        ensureColumn(conn, st, "exam_schedule", "created_at", "TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP");
    }

    private static void cleanupLegacyIndexes(Connection conn, Statement st) throws SQLException {
        dropForeignKeysForColumn(conn, st, "exam_schedule", "subject_id");
        dropForeignKeysForColumn(conn, st, "exam_schedule", "room_id");
        dropSingleColumnUniqueIndex(conn, st, "exam_schedule", "subject_id");
        dropSingleColumnUniqueIndex(conn, st, "exam_schedule", "room_id");
    }

    private static void dropSingleColumnUniqueIndex(Connection conn, Statement st,
                                                    String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT INDEX_NAME
                FROM information_schema.statistics
                WHERE table_schema = DATABASE()
                  AND table_name = ?
                  AND non_unique = 0
                  AND index_name <> 'PRIMARY'
                GROUP BY INDEX_NAME
                HAVING COUNT(*) = 1
                   AND SUM(CASE WHEN COLUMN_NAME = ? THEN 1 ELSE 0 END) = 1
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String indexName = rs.getString("INDEX_NAME");
                    try {
                        st.executeUpdate("ALTER TABLE `" + tableName + "` DROP INDEX `" + indexName + "`");
                    } catch (SQLException ignored) {
                        // Keep startup resilient for mixed legacy schemas.
                    }
                }
            }
        }
    }

    private static void dropForeignKeysForColumn(Connection conn, Statement st,
                                                 String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT CONSTRAINT_NAME
                FROM information_schema.KEY_COLUMN_USAGE
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                  AND REFERENCED_TABLE_NAME IS NOT NULL
                GROUP BY CONSTRAINT_NAME
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            ps.setString(2, columnName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String constraint = rs.getString("CONSTRAINT_NAME");
                    try {
                        st.executeUpdate("ALTER TABLE `" + tableName + "` DROP FOREIGN KEY `" + constraint + "`");
                    } catch (SQLException ignored) {
                        // Ignore if already absent or protected by vendor behavior.
                    }
                }
            }
        }
    }

    private static void seedAdmin(Statement st) throws SQLException {
        st.executeUpdate("INSERT IGNORE INTO admin(username, password) VALUES('admin', 'admin123')");
    }

    private static void renameColumnIfNeeded(Connection conn, Statement st, String tableName,
                                             String oldColumn, String newColumn, String newType) throws SQLException {
        if (!tableExists(conn, tableName)) {
            return;
        }

        if (!columnExists(conn, tableName, newColumn) && columnExists(conn, tableName, oldColumn)) {
            st.executeUpdate("ALTER TABLE `" + tableName + "` CHANGE `" + oldColumn + "` `" + newColumn + "` " + newType);
        }
    }

    private static void ensureColumn(Connection conn, Statement st, String tableName,
                                     String columnName, String definition) throws SQLException {
        if (!tableExists(conn, tableName)) {
            return;
        }

        if (!columnExists(conn, tableName, columnName)) {
            st.executeUpdate("ALTER TABLE `" + tableName + "` ADD COLUMN `" + columnName + "` " + definition);
        }
    }

    private static boolean tableExists(Connection conn, String tableName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getTables(conn.getCatalog(), null, tableName, new String[] { "TABLE" })) {
            return rs.next();
        }
    }

    private static boolean columnExists(Connection conn, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = conn.getMetaData();
        try (ResultSet rs = metaData.getColumns(conn.getCatalog(), null, tableName, columnName)) {
            return rs.next();
        }
    }
}
