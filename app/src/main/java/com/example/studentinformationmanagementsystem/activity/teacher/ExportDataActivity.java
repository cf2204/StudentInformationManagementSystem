package com.example.studentinformationmanagementsystem.activity.teacher;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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

public class ExportDataActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_STORAGE_REQUEST = 1;
    private static final String EXPORT_DIR = "StudentManagementExport";
    private static final String CSV_MIME_TYPE = "text/csv";

    private StudentDAO studentDAO;
    private CourseDAO courseDAO;
    private TeacherDAO teacherDAO;
    private TranscriptDAO transcriptDAO;
    private long teacherId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_data);

        teacherId = getIntent().getLongExtra("teacher_id", -1);

        studentDAO = new StudentDAO(this);
        courseDAO = new CourseDAO(this);
        teacherDAO = new TeacherDAO(this);
        transcriptDAO = new TranscriptDAO(this);

        studentDAO.open();
        courseDAO.open();
        teacherDAO.open();
        transcriptDAO.open();

        Button btnExportStudents = findViewById(R.id.btn_export_students);
        Button btnExportCourses = findViewById(R.id.btn_export_courses);
        Button btnExportGrades = findViewById(R.id.btn_export_grades);

        btnExportStudents.setOnClickListener(v -> exportData("students"));
        btnExportCourses.setOnClickListener(v -> exportData("courses"));
        btnExportGrades.setOnClickListener(v -> exportData("grades"));
    }

    private void exportData(String dataType) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ 使用 MediaStore API
            exportWithMediaStore(dataType);
        } else {
            // 旧版本使用传统文件API
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST);
            } else {
                exportWithLegacyMethod(dataType);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void exportWithMediaStore(String dataType) {
        String fileName = getExportFileName(dataType);

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
                CsvWriter writer = new OutputStreamCsvWriter(outputStream);
                switch (dataType) {
                    case "students":
                        exportStudentsToCSV(writer);
                        break;
                    case "courses":
                        exportCoursesToCSV(writer);
                        break;
                    case "grades":
                        exportGradesToCSV(writer);
                        break;
                }
                Toast.makeText(this, "文件已保存到Downloads/" + EXPORT_DIR, Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("ExportData", "导出失败", e);
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // 删除创建失败的文件
            try {
                resolver.delete(uri, null, null);
            } catch (Exception deleteEx) {
                Log.e("ExportData", "删除失败文件出错", deleteEx);
            }
        }
    }

    private void exportWithLegacyMethod(String dataType) {
        File exportDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                EXPORT_DIR);

        if (!exportDir.exists() && !exportDir.mkdirs()) {
            Toast.makeText(this, "创建目录失败", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = getExportFileName(dataType);
        File file = new File(exportDir, fileName);

        try (FileWriter fileWriter = new FileWriter(file)) {
            CsvWriter writer = new FileWriterCsvWriter(fileWriter);
            switch (dataType) {
                case "students":
                    exportStudentsToCSV(writer);
                    break;
                case "courses":
                    exportCoursesToCSV(writer);
                    break;
                case "grades":
                    exportGradesToCSV(writer);
                    break;
            }

            // 通知系统扫描新文件
            MediaScannerConnection.scanFile(this,
                    new String[]{file.getAbsolutePath()},
                    new String[]{CSV_MIME_TYPE},
                    null);

            Toast.makeText(this, "文件已保存到Downloads/" + EXPORT_DIR, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("ExportData", "导出失败", e);
            Toast.makeText(this, "导出失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private String getExportFileName(String dataType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String timestamp = sdf.format(new Date());
        return String.format(Locale.getDefault(),
                "%s_%s.csv", dataType, timestamp);
    }

    // ==================== CSV 导出方法 ====================

    private interface CsvWriter {
        void write(String content) throws IOException;
    }

    private static class OutputStreamCsvWriter implements CsvWriter {
        private final OutputStreamWriter writer;

        public OutputStreamCsvWriter(OutputStream outputStream) {
            this.writer = new OutputStreamWriter(outputStream);
        }

        @Override
        public void write(String content) throws IOException {
            writer.write(content);
        }
    }

    private static class FileWriterCsvWriter implements CsvWriter {
        private final FileWriter writer;

        public FileWriterCsvWriter(FileWriter writer) {
            this.writer = writer;
        }

        @Override
        public void write(String content) throws IOException {
            writer.write(content);
        }
    }

    private void exportStudentsToCSV(CsvWriter writer) throws IOException {
        List<Student> students = studentDAO.findAll();
        writer.write("student_id,name,gender,birth_date,class,major\n");
        for (Student student : students) {
            writer.write(String.format(Locale.getDefault(),
                    "%d,%s,%s,%s,%s,%s\n",
                    student.getStudentId(),
                    escapeCsv(student.getName()),
                    escapeCsv(student.getGender()),
                    escapeCsv(student.getBirthDate()),
                    escapeCsv(student.getStudentClass()),
                    escapeCsv(student.getMajor())));
        }
    }

    private void exportCoursesToCSV(CsvWriter writer) throws IOException {
        List<Course> courses = courseDAO.findAll();
        writer.write("course_id,course_name,credit,teacher_id,teacher_name\n");

        for (Course course : courses) {
            Teacher teacher = teacherDAO.findById(course.getTeacherId());
            String teacherName = teacher != null ? teacher.getName() : "未知教师";

            writer.write(String.format(Locale.getDefault(),
                    "%d,%s,%d,%d,%s\n",
                    course.getCourseId(),
                    escapeCsv(course.getCourseName()),
                    course.getCredit(),
                    course.getTeacherId(),
                    escapeCsv(teacherName)));
        }
    }

    private void exportGradesToCSV(CsvWriter writer) throws IOException {
        List<Course> courses = teacherId != -1 ?
                courseDAO.findCoursesByTeacherId(teacherId) :
                courseDAO.findAll();

        writer.write("student_id,student_name,course_id,course_name,score\n");

        for (Course course : courses) {
            List<Student> students = studentDAO.findStudentsByCourseId(course.getCourseId());

            for (Student student : students) {
                String score = transcriptDAO.findGradeByStudentAndCourse(
                        student.getStudentId(), course.getCourseId());

                writer.write(String.format(Locale.getDefault(),
                        "%d,%s,%d,%s,%s\n",
                        student.getStudentId(),
                        escapeCsv(student.getName()),
                        course.getCourseId(),
                        escapeCsv(course.getCourseName()),
                        score != null ? escapeCsv(score) : "未评分"));
            }
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    // ==================== 权限处理 ====================

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，可以执行导出操作
                // 这里需要知道用户点击的是哪个按钮，简单起见可以重新导出所有类型
                // 实际应用中应该保存用户操作类型
                Toast.makeText(this, "权限已授予，请重新点击导出按钮", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要存储权限才能导出数据", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        studentDAO.close();
        courseDAO.close();
        teacherDAO.close();
        transcriptDAO.close();
        super.onDestroy();
    }
}