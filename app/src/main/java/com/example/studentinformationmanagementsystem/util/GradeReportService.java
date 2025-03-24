package com.example.studentinformationmanagementsystem.util;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Student;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class GradeReportService {

    private Context context;
    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private TranscriptDAO transcriptDAO;
    private TeacherDAO teacherDAO;

    public GradeReportService(Context context) {
        this.context = context;
        this.studentDAO = new StudentDAO(context);
        this.courseDAO = new CourseDAO(context);
        this.transcriptDAO = new TranscriptDAO(context);
        this.teacherDAO = new TeacherDAO(context);
    }

    /**
     * 导出学生成绩单为CSV文件并保存到共享存储（Downloads目录）
     *
     * @param studentId 学生ID
     * @return 返回生成的CSV文件路径，如果导出失败返回null
     */
    public String exportGradeReportToCSV(long studentId) {
        studentDAO.open();
        courseDAO.open();
        transcriptDAO.open();
        teacherDAO.open();

        try {
            // 获取学生信息
            Student student = studentDAO.findById(studentId);
            if (student == null) {
                Log.e("GradeReportService", "Student not found with ID: " + studentId);
                return null;
            }

            // 获取学生所选课程及成绩
            List<Course> courses = courseDAO.findCoursesByStudentId(studentId);

            // 创建CSV文件内容
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("Course Name,Credit,Teacher,Score\n"); // CSV文件头

            for (Course course : courses) {
                String score = transcriptDAO.findGradeByStudentAndCourse(studentId, course.getCourseId());
                // 通过 TeacherDAO 获取老师名字
                String teacherName = teacherDAO.findById(course.getTeacherId()).getName();
                csvContent.append(course.getCourseName())
                        .append(",")
                        .append(course.getCredit())
                        .append(",")
                        .append(teacherName) // 使用老师名字替换老师ID
                        .append(",")
                        .append(score != null ? score : "N/A")
                        .append("\n");
            }

            // 保存CSV文件到共享存储（Downloads目录）
            String fileName = "GradeReport_" + student.getName() + ".csv";
            Uri fileUri = saveFileToDownloads(context, fileName, csvContent.toString());

            if (fileUri != null) {
                Log.i("GradeReportService", "Grade report exported to: " + fileUri.toString());
                return fileUri.toString();
            } else {
                Log.e("GradeReportService", "Failed to export grade report");
                return null;
            }
        } finally {
            // 确保关闭数据库连接
            studentDAO.close();
            courseDAO.close();
            transcriptDAO.close();
            teacherDAO.close();
        }
    }

    /**
     * 使用 MediaStore API 将文件保存到 Downloads 目录
     *
     * @param context  上下文
     * @param fileName 文件名
     * @param content  文件内容
     * @return 返回文件的 Uri，如果保存失败返回 null
     */
    private Uri saveFileToDownloads(Context context, String fileName, String content) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, "text/csv");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        Uri uri = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            uri = context.getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        }
        if (uri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    outputStream.write(content.getBytes());
                    outputStream.flush();
                    return uri; // 返回文件的 Uri
                }
            } catch (IOException e) {
                Log.e("GradeReportService", "Error saving file to Downloads", e);
            }
        }
        return null; // 保存失败
    }
}
