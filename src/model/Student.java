package model;

public class Student {

    private int id;
    private String rollNo;
    private String fullName;
    private String email;
    private String phone;
    private String department;
    private int semester;

    public Student() {
    }

    public Student(String rollNo, String fullName, String email, String phone, String department, int semester) {
        this.rollNo = rollNo;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.semester = semester;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}