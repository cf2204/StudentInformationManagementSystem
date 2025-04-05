package com.example.studentinformationmanagementsystem.activity.teacher;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.studentinformationmanagementsystem.R;
import com.example.studentinformationmanagementsystem.dao.CourseDAO;
import com.example.studentinformationmanagementsystem.dao.StudentDAO;
import com.example.studentinformationmanagementsystem.dao.TeacherDAO;
import com.example.studentinformationmanagementsystem.dao.TranscriptDAO;
import com.example.studentinformationmanagementsystem.entity.Course;
import com.example.studentinformationmanagementsystem.entity.Student;
import com.example.studentinformationmanagementsystem.entity.Teacher;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CourseDetailActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1001;
    private static final String CSV_MIME_TYPE = "text/csv";
    private static final String EXPORT_DIR = "StudentManagementExport";

    private CourseDAO courseDAO;
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private TranscriptDAO transcriptDAO;
    private long courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        courseId = getIntent().getLongExtra("course_id", -1);
        if (courseId == -1) {
            Toast.makeText(this, "无效的课程ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        courseDAO = new CourseDAO(this);
        studentDAO = new StudentDAO(this);
        teacherDAO = new TeacherDAO(this);
        transcriptDAO = new TranscriptDAO(this);

        courseDAO.open();
        studentDAO.open();
        teacherDAO.open();
        transcriptDAO.open();

        Button btnViewStudents = findViewById(R.id.btn_view_students);
        Button btnExportGrades = findViewById(R.id.btn_export_grades);

        displayCourseDetails();

        btnViewStudents.setOnClickListener(v -> {
            Intent intent = new Intent(CourseDetailActivity.this, CourseStudentsActivity.class);
            intent.putExtra("course_id", courseId);
            startActivity(intent);
        });

        btnExportGrades.setOnClickListener(v -> exportGradesToFile());
    }

    private void displayCourseDetails() {
        Course course = courseDAO.findById(courseId);
        if (course != null) {
            TextView tvCourseName = findViewById(R.id.tv_course_name);
            TextView tvCredit = findViewById(R.id.tv_credit);
            TextView tvTeacher = findViewById(R.id.tv_teacher);

            tvCourseName.setText(course.getCourseName());
            tvCredit.setText(String.valueOf(course.getCredit()));

            // 获取教师姓名
            Teacher teacher = teacherDAO.findById(course.getTeacherId());
            String teacherName = teacher != null ? teacher.getName() : "未知教师";
            tvTeacher.setText(teacherName);
        }
    }

    private void exportGradesToFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            exportWithMediaStore();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST);
            } else {
                exportWithLegacyMethod();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void exportWithMediaStore() {
        Course course = courseDAO.findById(courseId);
        if (course == null) {
            Toast.makeText(this, "课程信息获取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = String.format(Locale.getDefault(),
                "grades_%s_%s.csv",
                sanitizeFilename(course.getCourseName()),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));

        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, CSV_MIME_TYPE);
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + EXPORT_DIR);

        ContentResolver resolver = getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

        if (uri == null) {
            Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try (OutputStream outputStream = resolver.openOutputStream(uri)) {
            if (outputStream != null) {
                writeCourseGradesToStream(outputStream, course);
                Toast.makeText(this,
                        String.format("成绩已导出到 Downloads/%s/%s", EXPORT_DIR, fileName),
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("CourseDetail", "导出成绩失败", e);
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            try {
                resolver.delete(uri, null, null);
            } catch (Exception deleteEx) {
                Log.e("CourseDetail", "删除失败文件出错", deleteEx);
            }
        }
    }

    private void exportWithLegacyMethod() {
        Course course = courseDAO.findById(courseId);
        if (course == null) {
            Toast.makeText(this, "课程信息获取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        File exportDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                EXPORT_DIR);

        if (!exportDir.exists() && !exportDir.mkdirs()) {
            Toast.makeText(this, "创建目录失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = String.format(Locale.getDefault(),
                "grades_%s_%s.csv",
                sanitizeFilename(course.getCourseName()),
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()));

        File file = new File(exportDir, fileName);

        try (FileWriter writer = new FileWriter(file)) {
            writeCourseGradesToWriter(writer, course);

            MediaScannerConnection.scanFile(this,
                    new String[]{file.getAbsolutePath()},
                    new String[]{CSV_MIME_TYPE},
                    null);

            Toast.makeText(this,
                    String.format("成绩已导出到 Downloads/%s/%s", EXPORT_DIR, fileName),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("CourseDetail", "导出成绩失败", e);
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeCourseGradesToStream(OutputStream outputStream, Course course) throws IOException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream)) {
            writeCourseGrades(writer, course);
        }
    }

    private void writeCourseGradesToWriter(FileWriter writer, Course course) throws IOException {
        writeCourseGrades(writer, course);
    }

    private void writeCourseGrades(Appendable writer, Course course) throws IOException {
        // 写入CSV头部
        writer.append("学号,姓名,班级,专业,成绩\n");

        List<Student> students = studentDAO.findStudentsByCourseId(course.getCourseId());

        for (Student student : students) {
            String score = transcriptDAO.findGradeByStudentAndCourse(
                    student.getStudentId(), course.getCourseId());

            writer.append(String.format(Locale.getDefault(),
                    "%d,%s,%s,%s,%s\n",
                    student.getStudentId(),
                    escapeCsv(student.getName()),
                    escapeCsv(student.getStudentClass()),
                    escapeCsv(student.getMajor()),
                    score != null ? escapeCsv(score) : "未评分"));
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9-_.]", "_");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                exportGradesToFile();
            } else {
                Toast.makeText(this, "需要存储权限才能导出成绩", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        courseDAO.close();
        studentDAO.close();
        teacherDAO.close();
        transcriptDAO.close();
        super.onDestroy();
    }
}