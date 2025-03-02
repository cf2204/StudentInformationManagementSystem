package com.example.studentinformationmanagementsystem.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "student_management.db";
    private static final int DATABASE_VERSION = 1;

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
}
