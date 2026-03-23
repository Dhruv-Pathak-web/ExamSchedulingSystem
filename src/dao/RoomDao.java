package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Room;
import util.DBConnection;
import util.DatabaseInitializer;

public class RoomDao {

    public List<Room> findAll() throws SQLException {
        DatabaseInitializer.ensureInitialized();
        List<Room> rooms = new ArrayList<>();

        String sql = "SELECT id, room_code, capacity FROM rooms ORDER BY room_code";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Room room = new Room();
                room.setId(rs.getInt("id"));
                room.setRoomCode(rs.getString("room_code"));
                room.setCapacity(rs.getInt("capacity"));
                rooms.add(room);
            }
        }

        return rooms;
    }

    public boolean add(Room room) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        if (existsByRoomCode(room.getRoomCode())) {
            throw new SQLException("Room code already exists.", "23000");
        }

        String sql = "INSERT INTO rooms(room_code, capacity) VALUES(?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getRoomCode());
            ps.setInt(2, room.getCapacity());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean delete(int id) throws SQLException {
        DatabaseInitializer.ensureInitialized();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE id = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        }
    }

    private boolean existsByRoomCode(String roomCode) throws SQLException {
        String sql = "SELECT 1 FROM rooms WHERE room_code = ? LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
