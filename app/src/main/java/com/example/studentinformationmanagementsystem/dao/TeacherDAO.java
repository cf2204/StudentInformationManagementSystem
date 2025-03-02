package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.util.ArrayList;
import java.util.Collections;
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

    @Override
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

    @Override
    public int delete(long id) {
        return db.delete("Teacher", "teacher_id=?", new String[]{String.valueOf(id)});
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
        List<Teacher> teacherList = new ArrayList<>();
        Cursor cursor = db.query("Teacher", null, null, null, null, null, null);

        teacherList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return teacherList;
    }

    @Override
    public List ChangeToList(Cursor cursor) {
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
}
