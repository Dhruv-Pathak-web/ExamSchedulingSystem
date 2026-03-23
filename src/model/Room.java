package model;

public class Room {

    private int id;
    private String roomCode;
    private int capacity;

    public Room() {
    }

    public Room(String roomCode, int capacity) {
        this.roomCode = roomCode;
        this.capacity = capacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}