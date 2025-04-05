package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentinformationmanagementsystem.entity.Student;

import java.util.ArrayList;
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

    /**
     * 已经被方法insertTeacherWithAccount取代
     * 请不用调用该方法
     * @param student 对应实体对象
     * @return 插入位置
     */
    @Override
    @Deprecated
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

    /**
     * 删除学生信息，并删除 User 表中对应的账号
     * @param id 学生ID
     * @return 返回删除的行数，如果删除失败返回 -1
     */
    @Override
    public int delete(long id) {
        // 获取学生对应的 user_id
        long userId = getUserIdByStudentId(id);
        if (userId == -1) {
            return -1; // 未找到对应的 user_id
        }

        // 删除 User 表中的记录
        int userDeleted = db.delete("User", "user_id = ?", new String[]{String.valueOf(userId)});
        if (userDeleted <= 0) {
            return -1; // 删除 User 表记录失败
        }

        // 删除 Student 表中的记录
        return db.delete("Student", "student_id = ?", new String[]{String.valueOf(id)});
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
     * @param courseId 课程id
     * @return 学生对象数组(List<Student>对象）
     */
    public List<Student> findStudentsByCourseId(long courseId) {
        List<Student> studentList;
        // 查询选课表，获取选了该课程的学生ID
        Cursor cursor = db.rawQuery(
                "SELECT s.* FROM Student s " +
                        "INNER JOIN Transcript t ON s.student_id = t.student_id " +
                        "WHERE t.course_id = ?", new String[]{String.valueOf(courseId)}
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
        List<Student> studentList;
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

    /**
     * 插入学生信息，并同时向 User 表中插入账号和密码
     * @param student  学生信息
     * @param username 用户名
     * @param password 密码
     * @return 返回学生ID，如果插入失败返回 -1
     */
    public long insertStudentWithAccount(Student student, String username, String password) {
        long userId = insertUser(username, password);
        if (userId == -1) {
            return -1; // 插入用户失败
        }

        ContentValues values = new ContentValues();
        values.put("name", student.getName());
        values.put("gender", student.getGender());
        values.put("birth_date", student.getBirthDate());
        values.put("class", student.getStudentClass());
        values.put("major", student.getMajor());
        values.put("user_id", userId);

        return db.insert("Student", null, values);
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
        values.put("role", "student");
        return db.insert("User", null, values);
    }

    /**
     * 根据学生ID获取对应的 user_id
     * @param studentId 学生ID
     * @return 返回 user_id，如果未找到返回 -1
     */
    private long getUserIdByStudentId(long studentId) {
        Cursor cursor = null;
        try {
            cursor = db.query(
                    "Student",
                    new String[]{"user_id"}, // 查询 user_id
                    "student_id = ?", // 条件
                    new String[]{String.valueOf(studentId)}, // 参数
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
