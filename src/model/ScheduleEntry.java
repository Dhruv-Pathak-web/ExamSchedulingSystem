package model;

public class ScheduleEntry {

    private int id;
    private String subjectCode;
    private String subjectName;
    private String department;
    private int semester;
    private String roomCode;
    private int roomCapacity;
    private String examDate;
    private String examSlot;
    private int slotNumber;
    private int allocatedStudents;
    private int expectedStudents;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public int getRoomCapacity() {
        return roomCapacity;
    }

    public void setRoomCapacity(int roomCapacity) {
        this.roomCapacity = roomCapacity;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamSlot() {
        return examSlot;
    }

    public void setExamSlot(String examSlot) {
        this.examSlot = examSlot;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getAllocatedStudents() {
        return allocatedStudents;
    }

    public void setAllocatedStudents(int allocatedStudents) {
        this.allocatedStudents = allocatedStudents;
    }

    public int getExpectedStudents() {
        return expectedStudents;
    }

    public void setExpectedStudents(int expectedStudents) {
        this.expectedStudents = expectedStudents;
    }
}
