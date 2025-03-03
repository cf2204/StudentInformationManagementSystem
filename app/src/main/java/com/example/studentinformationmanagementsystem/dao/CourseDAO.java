package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.studentinformationmanagementsystem.entity.Course;

import java.util.ArrayList;
import java.util.List;

public class CourseDAO implements BaseDAO<Course> {
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public CourseDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    @Override
    public long insert(Course course) {
        ContentValues values = new ContentValues();
        values.put("course_name", course.getCourseName());
        values.put("credit", course.getCredit());
        values.put("teacher_id", course.getTeacherId());
        return db.insert("Course", null, values);
    }

    @Override
    public int update(Course course) {
        ContentValues values = new ContentValues();
        values.put("course_name", course.getCourseName());
        values.put("credit", course.getCredit());
        values.put("teacher_id", course.getTeacherId());
        return db.update("Course", values, "course_id=?", new String[]{String.valueOf(course.getCourseId())});
    }

    @Override
    public int delete(long id) {
        return db.delete("Course", "course_id=?", new String[]{String.valueOf(id)});
    }

    @Override
    public Course findById(long id) {
        Cursor cursor = db.query("Course", null, "course_id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            Course course = new Course(
                    cursor.getLong(0),
                    cursor.getString(1),
                    cursor.getInt(2),
                    cursor.getLong(3)
            );
            cursor.close();
            return course;
        }
        return null;
    }

    @Override
    public long getIdByName(String name) {
        Cursor cursor = db.query("Course", null, "course_name=?", new String[]{String.valueOf(name)}, null, null, null);
        if(cursor != null && cursor.moveToFirst()){
            long id = cursor.getLong(0);
            cursor.close();
            return id;
        }
        return 0;
    }

    @Override
    public List<Course> findAll() {
        List<Course> courseList;
        Cursor cursor = db.query("Course", null, null, null, null, null, null);

        courseList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return courseList;
    }

    // 本类使用，将游标对象转化为列表
    @Override
    public List<Course> ChangeToList(Cursor cursor) {
        List<Course> courseList = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取列索引
                int courseIdIndex = cursor.getColumnIndex("course_id");
                int courseNameIndex = cursor.getColumnIndex("course_name");
                int creditIndex = cursor.getColumnIndex("credit");
                int teacherIdIndex = cursor.getColumnIndex("teacher_id");

                // 检查列索引是否有效
                if (courseIdIndex >= 0 && courseNameIndex >= 0 && creditIndex >= 0 && teacherIdIndex >= 0) {
                    Course course = new Course(
                            cursor.getLong(courseIdIndex),
                            cursor.getString(courseNameIndex),
                            cursor.getInt(creditIndex),
                            cursor.getLong(teacherIdIndex)
                    );
                    courseList.add(course);
                } else {
                    System.err.println("Invalid column index detected!");
                }
            }
            cursor.close();
        }
        return courseList;
    }

    /**
     * 根据学生 ID 获取已选课程对象列表
     *
     * @param studentId 学生id
     * @return 课程对象数组(List<Student>对象）
     */
    public List<Course> findCoursesByStudentId(long studentId) {
        List<Course> courseList;
        // 查询选课表，获取该学生选的课程ID
        Cursor cursor = db.rawQuery(
                "SELECT c.* FROM Course c " +
                        "INNER JOIN Enrollment e ON c.course_id = e.course_id " +
                        "WHERE e.student_id = ?", new String[]{String.valueOf(studentId)}
        );

        courseList = ChangeToList(cursor);
        if(!cursor.isClosed()){
            cursor.close();
        }
        return courseList;
    }

}
