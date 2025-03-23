package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UserDAO {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public UserDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回用户角色（"student" 或 "teacher"），如果账号密码错误返回 null
     */
    public String login(String username, String password) {
        Cursor cursor = null;
        try {
            // 查询用户表，检查账号和密码
            cursor = db.query(
                    "User",
                    new String[]{"role"}, // 只查询角色字段
                    "username = ? AND password = ?", // 条件
                    new String[]{username, password}, // 参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // 获取角色
                int roleIndex = cursor.getColumnIndex("role");
                if (roleIndex >= 0) {
                    return cursor.getString(roleIndex); // 返回角色（"student" 或 "teacher"）
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 关闭 Cursor
            }
        }
        return null; // 账号密码错误
    }

    /**
     * 根据账号和密码获取对应的学生或老师 ID
     * @param username 用户名
     * @param password 密码
     * @return 返回用户 ID（学生 ID 或老师 ID），如果账号密码错误返回 -1
     */
    public long getUserIdByPassword(String username, String password) {
        Cursor cursor = null;
        try {
            // 查询用户表，检查账号和密码
            cursor = db.query(
                    "User",
                    new String[]{"user_id", "role"}, // 查询用户ID和角色
                    "username = ? AND password = ?", // 条件
                    new String[]{username, password}, // 参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // 获取用户ID和角色
                int idIndex = cursor.getColumnIndex("user_id");
                int roleIndex = cursor.getColumnIndex("role");
                long userId = cursor.getLong(idIndex);
                String role = cursor.getString(roleIndex);
                // 根据角色查询学生或老师ID
                if (role.equals("student")) {
                    return getStudentIdByUserId(userId);
                } else if (role.equals("teacher")) {
                    return getTeacherIdByUserId(userId);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 关闭 Cursor
            }
        }
        return -1; // 账号密码错误
    }

    /**
     * 根据用户ID获取学生ID
     * @param userId 用户ID
     * @return 学生ID，如果未找到返回 -1
     */
    private long getStudentIdByUserId(long userId) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Student",
                    new String[]{"student_id"}, // 查询学生ID
                    "user_id = ?", // 条件
                    new String[]{String.valueOf(userId)}, // 参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("student_id");
                if(idIndex >= 0){
                    return cursor.getLong(idIndex);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 关闭 Cursor
            }
        }
        return -1; // 未找到学生ID
    }

    /**
     * 根据用户ID获取老师ID
     * @param userId 用户ID
     * @return 老师ID，如果未找到返回 -1
     */
    private long getTeacherIdByUserId(long userId) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Teacher",
                    new String[]{"teacher_id"}, // 查询老师ID
                    "user_id = ?", // 条件
                    new String[]{String.valueOf(userId)}, // 参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex("teacher_id");
                if(idIndex >= 0){
                    return cursor.getLong(idIndex);
                }
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 关闭 Cursor
            }
        }
        return -1; // 未找到老师ID
    }

    // 插入用户,测试用
    public long insertUser(String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        return db.insert("User", null, values);
    }
}
