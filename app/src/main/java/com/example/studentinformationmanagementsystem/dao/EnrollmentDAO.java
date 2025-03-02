package com.example.studentinformationmanagementsystem.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EnrollmentDAO{
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public EnrollmentDAO(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     *  插入选课信息
     *
     * @param student_id 学生id
     * @param course_id 课程id
     * @param score 成绩
     * @return 选课信息在表中的id
     */
    public long insert(long student_id,long course_id,String score) {
        ContentValues values = new ContentValues();
        values.put("student_id", student_id);
        values.put("course_id", course_id);
        values.put("score", score);
        return db.insert("Enrollment", null, values);
    }

    /**
     *  更新选课成绩
     *
     * @param student_id 学生id
     * @param course_id 课程id
     * @param score 成绩
     * @return 被更新的行数(>0则表示更新成功)
     */
    public int update(long student_id,long course_id,String score) {
        ContentValues values = new ContentValues();
        values.put("score", score);
        return db.update("Enrollment", values, "student_id=? AND course_id=?",
                new String[]{String.valueOf(student_id), String.valueOf(course_id)}
        );
    }

    /**
     *  删除选课信息
     *
     * @param studentId 学生id
     * @param courseId 课程id
     * @return 被删除的行数(>0则表示删除成功)
     */
    public int delete(long studentId, long courseId) {
        return db.delete("Enrollment", "student_id=? AND course_id=?",
                new String[]{String.valueOf(studentId), String.valueOf(courseId)}
        );
    }

    /**
     *  根据学生id和课程id获得成绩
     *
     * @param studentId 学生id
     * @param courseId 课程id
     * @return 成绩
     */
    public String findGradeByStudentAndCourse(long studentId, long courseId) {
        String score = null;
        Cursor cursor = db.rawQuery(
                "SELECT grade FROM Enrollment " +
                        "WHERE student_id = ? AND course_id = ?",
                new String[]{String.valueOf(studentId), String.valueOf(courseId)}
        );

        if (cursor != null) {
            int gradeIndex = cursor.getColumnIndex("score");
            if (gradeIndex >= 0) {
                cursor.moveToFirst();
                score = cursor.getString(gradeIndex);
            }
            cursor.close();
        }
        return score;
    }


}
