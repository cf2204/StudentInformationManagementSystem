package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.util.ArrayList;
import java.util.List;

public class TeacherDAO implements BaseDAO<Teacher>{
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public TeacherDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * 已经被方法insertTeacherWithAccount取代
     * 请不用调用该方法
     * @param teacher 对应实体对象
     * @return 插入位置
     */
    @Override
    @Deprecated
    public long insert(Teacher teacher) {
        ContentValues values = new ContentValues();
        values.put("name", teacher.getName());
        values.put("department", teacher.getDepartment());
        values.put("user_id", teacher.getUserId());
        return db.insert("Teacher", null, values);
    }

    @Override
    public int update(Teacher teacher) {
        ContentValues values = new ContentValues();
        values.put("name", teacher.getName());
        values.put("department", teacher.getDepartment());
        values.put("user_id", teacher.getUserId());
        return db.update("Teacher", values, "teacher_id=?", new String[]{String.valueOf(teacher.getTeacherId())});
    }

    /**
     * 删除老师信息，并删除 User 表中对应的账号
     * @param id 老师ID
     * @return 返回删除的行数，如果删除失败返回 -1
     */
    @Override
    public int delete(long id) {
        // 获取老师对应的 user_id
        long userId = getUserIdByTeacherId(id);
        if (userId == -1) {
            return -1; // 未找到对应的 user_id
        }
        // 删除 User 表中的记录
        int userDeleted = db.delete("User", "user_id = ?", new String[]{String.valueOf(userId)});
        if (userDeleted <= 0) {
            return -1; // 删除 User 表记录失败
        }
        // 删除 Teacher 表中的记录
        return db.delete("Teacher", "teacher_id = ?", new String[]{String.valueOf(id)});
    }

    @Override
    public Teacher findById(long id) {
        Cursor cursor = db.query("Teacher", null, "teacher_id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Teacher teacher = new Teacher(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getLong(3)
            );
            cursor.close();
            return teacher;
        }
        return null;
    }

    @Override
    public long getIdByName(String name) {
        Cursor cursor = db.query("Teacher", null, "name=?", new String[]{String.valueOf(name)}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return 0;
    }

    @Override
    public List<Teacher> findAll() {
        List<Teacher> teacherList;
        Cursor cursor = db.query("Teacher", null, null, null, null, null, null);
        teacherList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return teacherList;
    }

    @Override
    public List<Teacher> ChangeToList(Cursor cursor) {
        List<Teacher> teacherList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取列索引
                int teacherIdIndex = cursor.getColumnIndex("teacher_id");
                int nameIndex = cursor.getColumnIndex("name");
                int departmentIndex = cursor.getColumnIndex("department");
                int userIdIndex = cursor.getColumnIndex("user_id");

                // 检查列索引是否有效
                if (teacherIdIndex >= 0 && nameIndex >= 0 && departmentIndex >= 0 && userIdIndex >= 0) {
                    Teacher teacher = new Teacher(
                            cursor.getLong(teacherIdIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(departmentIndex),
                            cursor.getLong(userIdIndex)
                    );
                    teacherList.add(teacher);
                } else {
                    System.err.println("Invalid column index detected in Teacher table!");
                }
            }
            cursor.close();
        }
        return teacherList;
    }

    /**
     * 插入老师信息，并同时向 User 表中插入账号和密码
     * @param teacher  老师信息
     * @param username 用户名
     * @param password 密码
     * @return 返回老师ID，如果插入失败返回 -1
     */
    public long insertTeacherWithAccount(Teacher teacher, String username, String password) {
        long userId = insertUser(username, password);
        if (userId == -1) {
            return -1; // 插入用户失败
        }
        ContentValues values = new ContentValues();
        values.put("name", teacher.getName());
        values.put("department", teacher.getDepartment());
        values.put("user_id", userId);
        return db.insert("Teacher", null, values);
    }

    /**
     * 向 User 表中插入账号和密码
     *
     * @param username 用户名
     * @param password 密码
     * @return 返回用户ID，如果插入失败返回 -1
     */
    private long insertUser(String username, String password) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", "teacher");
        return db.insert("User", null, values);
    }

    /**
     * 根据老师ID获取对应的 user_id
     *
     * @param teacherId 老师ID
     * @return 返回 user_id，如果未找到返回 -1
     */
    private long getUserIdByTeacherId(long teacherId) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Teacher",
                    new String[]{"user_id"}, // 查询 user_id
                    "teacher_id = ?", // 条件
                    new String[]{String.valueOf(teacherId)}, // 参数
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex("user_id");
                return cursor.getLong(index);
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close(); // 关闭 Cursor
            }
        }
        return -1; // 未找到 user_id
    }
}
