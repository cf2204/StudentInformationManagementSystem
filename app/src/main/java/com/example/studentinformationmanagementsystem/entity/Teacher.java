package com.example.studentinformationmanagementsystem.entity;

public class Teacher {
    private long teacherId;    // 老师ID
    private String name;       // 姓名
    private String department; // 部门
    private long userId;       // 关联的用户ID

    // 构造方法
    public Teacher() {}

    public Teacher(long teacherId, String name, String department, long userId) {
        this.teacherId = teacherId;
        this.name = name;
        this.department = department;
        this.userId = userId;
    }

    // Getter 和 Setter 方法
    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
