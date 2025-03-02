package com.example.studentinformationmanagementsystem.entity;

public class Course {
    private long courseId;     // 课程ID
    private String courseName; // 课程名称
    private int credit;       // 学分
    private long teacherId;   // 授课老师ID

    // 构造方法
    public Course() {}

    public Course(long courseId, String courseName, int credit, long teacherId) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credit = credit;
        this.teacherId = teacherId;
    }

    // Getter 和 Setter 方法
    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }
}
