package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student_management.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表
        db.execSQL("CREATE TABLE User (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "role TEXT NOT NULL);");
        // 创建学生表
        db.execSQL("CREATE TABLE Student (" +
                "student_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "gender TEXT, " +
                "birth_date TEXT, " +
                "class TEXT, " +
                "major TEXT, " +
                "user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES User(user_id));");
        // 创建老师表
        db.execSQL("CREATE TABLE Teacher (" +
                "teacher_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "department TEXT, " +
                "user_id INTEGER, " +
                "FOREIGN KEY (user_id) REFERENCES User(user_id));");
        // 创建课程表
        db.execSQL("CREATE TABLE Course (" +
                "course_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "course_name TEXT NOT NULL, " +
                "credit INTEGER, " +
                "teacher_id INTEGER, " +
                "FOREIGN KEY (teacher_id) REFERENCES Teacher(teacher_id));");
        // 创建成绩单表
        db.execSQL("CREATE TABLE Transcript (" +
                "transcript_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "course_id INTEGER, " +
                "score TEXT, " +
                "FOREIGN KEY (student_id) REFERENCES Student(student_id), " +
                "FOREIGN KEY (course_id) REFERENCES Course(course_id));");
        //初始化数值
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 升级数据库时删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS User");
        db.execSQL("DROP TABLE IF EXISTS Student");
        db.execSQL("DROP TABLE IF EXISTS Teacher");
        db.execSQL("DROP TABLE IF EXISTS Course");
        db.execSQL("DROP TABLE IF EXISTS Transcript");
        onCreate(db);
    }

    // 插入初始数据
    private void insertInitialData(SQLiteDatabase db) {
        // 插入老师信息
        insertTeacher(db, "王老师", "计算机学院");
        insertTeacher(db, "李老师", "数学学院");
        insertTeacher(db, "张老师", "物理学院");
        insertTeacher(db, "赵老师", "化学学院");
        insertTeacher(db, "刘老师", "外语学院");
        // 插入课程信息
        insertCourse(db, "数据结构", 4, 1);
        insertCourse(db, "高等数学", 3, 2);
        insertCourse(db, "大学物理", 3, 3);
        insertCourse(db, "有机化学", 2, 4);
        insertCourse(db, "英语写作", 2, 5);
        // 插入学生信息
        for (int i = 1; i <= 20; i++) {
            insertStudent(db, "学生" + i, i % 2 == 0 ? "男" : "女", "2000-01-" + (i < 10 ? "0" + i : i), "班级" + (i % 5 + 1), "专业" + (i % 3 + 1));
        }
    }

    // 插入老师信息
    private void insertTeacher(SQLiteDatabase db, String name, String department) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("department", department);

        // 插入 User 表
        long userId = insertUser(db, name.toLowerCase(), "123456", "teacher");

        if (userId != -1) {
            values.put("user_id", userId);
            db.insert("Teacher", null, values);
        }
    }

    // 插入课程信息
    private void insertCourse(SQLiteDatabase db, String courseName, int credit, long teacherId) {
        ContentValues values = new ContentValues();
        values.put("course_name", courseName);
        values.put("credit", credit);
        values.put("teacher_id", teacherId);
        db.insert("Course", null, values);
    }

    // 插入学生信息
    private void insertStudent(SQLiteDatabase db, String name, String gender, String birthDate, String studentClass, String major) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("gender", gender);
        values.put("birth_date", birthDate);
        values.put("class", studentClass);
        values.put("major", major);
        // 插入 User 表
        long userId = insertUser(db, name.toLowerCase(), "123456", "student");

        if (userId != -1) {
            values.put("user_id", userId);
            db.insert("Student", null, values);
        }
    }

    // 插入用户信息
    private long insertUser(SQLiteDatabase db, String username, String password, String role) {
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("password", password);
        values.put("role", role);
        return db.insert("User", null, values);
    }
}
