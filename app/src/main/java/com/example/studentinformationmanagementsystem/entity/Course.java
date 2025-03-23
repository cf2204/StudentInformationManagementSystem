package com.example.studentinformationmanagementsystem.entity;

public class Course {
    private long courseId;     // 课程ID
    private String courseName; // 课程名称
    private int credit;       // 学分
    private long teacherId;   // 授课老师ID

    // 新增字段：是否已选（不持久化到数据库）
    private transient boolean selected;

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

    // 已选状态判断方法
    public boolean isSelected() {
        return selected;
    }

    // 设置选课状态方法
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
