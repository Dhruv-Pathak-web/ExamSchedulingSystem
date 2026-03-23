package model;

public class Subject {

    private int id;
    private String subjectCode;
    private String subjectName;
    private String department;
    private int semester;
    private int examDurationMinutes;

    public Subject() {
    }

    public Subject(String subjectCode, String subjectName, String department, int semester, int examDurationMinutes) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.department = department;
        this.semester = semester;
        this.examDurationMinutes = examDurationMinutes;
    }

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

    public int getExamDurationMinutes() {
        return examDurationMinutes;
    }

    public void setExamDurationMinutes(int examDurationMinutes) {
        this.examDurationMinutes = examDurationMinutes;
    }
}