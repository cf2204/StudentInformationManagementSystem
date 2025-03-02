package com.example.studentinformationmanagementsystem.entity;

public class User {
    private long userId;       // 用户ID
    private String username;  // 用户名
    private String password;  // 密码
    private String role;       // 角色（student 或 teacher）

    // 构造方法
    public User() {}

    public User(long userId, String username, String password, String role) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter 和 Setter 方法
    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
