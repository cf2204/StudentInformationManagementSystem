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

    // 插入用户（可选，用于测试）
    public long insertUser(String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        return db.insert("User", null, values);
    }
}
