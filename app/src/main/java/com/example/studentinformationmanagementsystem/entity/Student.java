package com.example.studentinformationmanagementsystem.entity;

public class Student {
    private long studentId;    // 学生ID
    private String name;       // 姓名
    private String gender;     // 性别
    private String birthDate;  // 出生日期
    private String studentClass; // 班级
    private String major;      // 专业
    private long userId;       // 关联的用户ID

    // 构造方法
    public Student() {}

    public Student(long studentId, String name, String gender, String birthDate, String studentClass, String major, long userId) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.birthDate = birthDate;
        this.studentClass = studentClass;
        this.major = major;
        this.userId = userId;
    }

    // Getter 和 Setter 方法
    public long getStudentId() {
        return studentId;
    }

    public void setStudentId(long studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
