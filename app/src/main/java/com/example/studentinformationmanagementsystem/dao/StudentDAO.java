package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.appcompat.view.menu.MenuBuilder;

import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StudentDAO implements BaseDAO<Student> {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public StudentDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public long insert(Student student) {
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("gender", student.getGender());
        values.put("birth_date", student.getBirthDate());
        values.put("class", student.getStudentClass());
        values.put("major", student.getMajor());
        values.put("user_id", student.getUserId());
        return db.insert("Student", null, values);
    }

    @Override
    public int update(Student student) {
        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("gender", student.getGender());
        values.put("birth_date", student.getBirthDate());
        values.put("class", student.getStudentClass());
        values.put("major", student.getMajor());
        return db.update("Student", values, "student_id=?", new String[]{String.valueOf(student.getStudentId())});
    }

    @Override
    public int delete(long id) {
        return db.delete("Student", "student_id=?", new String[]{String.valueOf(id)});
    }

    @Override
    public Student findById(long id) {
        Cursor cursor = db.query("Student", null, "student_id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Student student = new Student(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getLong(6)
            );
            cursor.close();
            return student;
        }
        return null;
    }

    @Override
    public long getIdByName(String name) {
        Cursor cursor = db.query("Student", null, "name=?", new String[]{String.valueOf(name)}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return 0;
    }


    /**
     * 根据课程id获得本课学生对象列表
     *
     * @param courseId 课程id
     * @return 学生对象数组(List<Student>对象）
     */
    public List<Student> findStudentsByCourseId(long courseId) {
        List<Student> studentList = new ArrayList<>();
        // 查询选课表，获取选了该课程的学生ID
        Cursor cursor = db.rawQuery(
                "SELECT s.* FROM Student s " +
                        "INNER JOIN Enrollment e ON s.student_id = e.student_id " +
                        "WHERE e.course_id = ?", new String[]{String.valueOf(courseId)}
        );

        studentList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return studentList;
    }

    // 获得所有学生
    @Override
    public List<Student> findAll() {
        List<Student> studentList = new ArrayList<>();
        Cursor cursor = db.query("Student", null, null, null, null, null, null);

        studentList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return studentList;
    }

    // 本类使用，将游标对象转化为列表
    @Override
    public List<Student> ChangeToList(Cursor cursor) {
        List<Student> studentList = new ArrayList<>();
        if (cursor != null) {

            while (cursor.moveToNext()) {
                // 获取列索引
                int studentIdIndex = cursor.getColumnIndex("student_id");
                int nameIndex = cursor.getColumnIndex("name");
                int genderIndex = cursor.getColumnIndex("gender");
                int birthDateIndex = cursor.getColumnIndex("birth_date");
                int classIndex = cursor.getColumnIndex("class");
                int majorIndex = cursor.getColumnIndex("major");
                int userIdIndex = cursor.getColumnIndex("user_id");

                // 检查列索引是否有效
                if (studentIdIndex >= 0 && nameIndex >= 0 && genderIndex >= 0 &&
                        birthDateIndex >= 0 && classIndex >= 0 && majorIndex >= 0 && userIdIndex >= 0) {
                    Student student = new Student(
                            cursor.getLong(studentIdIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(genderIndex),
                            cursor.getString(birthDateIndex),
                            cursor.getString(classIndex),
                            cursor.getString(majorIndex),
                            cursor.getLong(userIdIndex)
                    );

                    studentList.add(student);
                } else {
                    System.err.println("Invalid column index detected in Student table!");
                }
            }
            cursor.close();
        }
        return studentList;
    }


}
